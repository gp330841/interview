# AWS Lambda Deep Dive

This module covers the advanced architectural internals of AWS Lambda, focusing on the execution lifecycle, microVM architecture, VPC networking mechanics, and integration models.

---

## 1. Under the Hood: Firecracker & Execution Lifecycle

To design high-performance serverless systems, you must understand how AWS runs your functions in physical hardware.

### A. Firecracker MicroVM
AWS Lambda uses **Firecracker**, an open-source virtualization technology written in Rust.
*   **What it does**: Firecracker creates lightweight virtual machines (MicroVMs) inside bare-metal EC2 instances. It combines the security and isolation of traditional virtual machines with the speed and resource efficiency of containers.
*   **Boot Time**: A Firecracker microVM can boot in **5 milliseconds**.

### B. Function Execution Lifecycle
A Lambda function execution consists of three main phases:

```
[Cold Start] ---------------------------------------------------+
|                                                               |
|  +--------------+     +-----------------+     +------------+  |
|  |  Extension   | --> | Runtime Init    | --> | Function   |  |
|  |  Init        |     | (Boot runtime)  |     | Init (Code)|  |
|  +--------------+     +-----------------+     +------------+  |
+---------------------------------------------------------------+
                                |
                         [Warm Start]
                                |
                         +--------------+
                         | Invoke       | <--- (Runs Handler)
                         +--------------+
                                |
                         +--------------+
                         | Shutdown     | (Cleans up container)
                         +--------------+
```

1.  **Init Phase (Cold Start only)**:
    *   *Extension Init*: Starts system extensions.
    *   *Runtime Init*: Bootstraps the runtime environment (e.g., NodeJS, JVM, Python VM).
    *   *Function Init*: Downloads the code package and executes static/global initialization code (outside the handler function).
2.  **Invoke Phase**:
    *   Executes the handler function (e.g., `exports.handler`).
3.  **Shutdown Phase**:
    *   Triggers when the function hasn't received requests for a while. Deallocates resources, terminates the runtime, and stops the microVM.

### C. Cold Start Optimizations
Since the Init phase takes time, optimizing it is a critical skill.
*   **Database Connections**: Define database connections, AWS clients, and SDKs **outside the handler function** (in the global scope). This code runs once during the Init phase. Subsequent warm invocations reuse the established connection.
*   **Reduce Package Size**: Exclude unnecessary dependencies. Use tree-shaking and bundlers.
*   **Provisioned Concurrency**: Pre-warms a specified number of microVMs, keeping them in the Init phase ready to execute. This eliminates cold starts entirely but introduces a flat hourly charge.

---

## 2. Serverless VPC Networking (Hyperplane)

Historically, attaching a Lambda function to a private VPC caused severe latency (up to 15-30 seconds) on cold starts because AWS had to provision and attach an Elastic Network Interface (ENI) dynamically.
*   **The Modern Solution (Hyperplane)**: AWS redesigned VPC networking for Lambda in 2019.
    *   AWS now provisions a shared, managed **Hyperplane ENI** in your VPC when you configure the Lambda function.
    *   When the Lambda function scales or cold-starts, it routes traffic through this pre-established Hyperplane ENI.
    *   *Result*: VPC cold start latency is reduced to under **1 second** and doesn't consume IP addresses from your subnets dynamically per function instance.

---

## 3. Invocation Models

AWS Lambda executes in three distinct invocation models. Selecting the correct model affects retries, throttling, and scaling.

### A. Synchronous Invocation
*   **How it Works**: The client invokes the Lambda and waits for a response. The connection remains open.
*   **Error Handling**: If the Lambda fails or throttles, it is the client's responsibility to handle retries and backoff.
*   **Common Services**: Application Load Balancer, API Gateway, Amazon Cognito.

### B. Asynchronous Invocation
*   **How it Works**: The client sends the payload to Lambda. AWS places the event in an internal SQS queue managed by Lambda, and returns an HTTP 202 (Accepted) immediately. Lambda poller picks up the message and processes it.
*   **Error Handling**: AWS automatically retries failed executions **2 times** with exponential backoff.
*   **Dead-Letter Queues (DLQ) & Destinations**: If retries fail, you can route the message to a DLQ (SQS/SNS) or use **Lambda Destinations** (routes success/failure details to SQS, SNS, EventBridge, or another Lambda).
*   **Common Services**: Amazon S3, Amazon SNS, CloudWatch Alarms.

### C. Event Source Mapping (Polling Model)
*   **How it Works**: Lambda polls an external stream or queue (e.g., SQS queue, Kinesis stream, DynamoDB stream).
*   **Batching**: Lambda reads items in batches (e.g., up to 10,000 for streams, 10 for SQS) and processes them inside a single execution.
*   **Error Handling**:
    *   *SQS*: If the batch fails, the messages return to the queue (based on visibility timeout).
    *   *Streams (Kinesis/DynamoDB)*: If an item in a batch fails, Lambda blocks the entire partition shard and retries the *entire batch* until it succeeds or the records expire (up to 7 days). This can block the pipeline (poison pill).
    *   *Mitigation*: Configure `BisectBatchOnFunctionError` (splits the batch in half and retries each half independently to find the failing record) and set a maximum retry limit.

---

## 4. Standard Interview Questions

#### Q1: If a Lambda function times out, what happens to database connections defined in the global scope?
If a function times out (e.g., reaches its 15-minute execution limit or custom timeout), the execution environment is immediately frozen. It may later be thawed for a subsequent invocation (keeping the global database connection active). If the container is destroyed, the connection is closed.
*   *Warning*: If you do not handle connection limits properly, concurrent Lambdas scaling up and down can quickly exhaust your relational database connection pool (since each Lambda instance establishes its own connection). Use **RDS Proxy** to multiplex connections.

#### Q2: Explain the difference between Reserve Concurrency and Provisioned Concurrency.
*   **Reserved Concurrency**: Sets a hard limit on the maximum number of concurrent instances your function can scale to.
    *   *Purpose*: Prevents a runaway function from consuming the entire account concurrency pool (default 1,000) and protects downstream databases from being overwhelmed.
*   **Provisioned Concurrency**: Keeps a specified number of execution environments initialized (warmed) and ready to respond.
    *   *Purpose*: Eliminates cold start latency for latency-sensitive applications.

#### Q3: How do you handle a "poison pill" message in an Event Source Mapping stream?
A poison pill is a corrupted or invalid message that fails processing every time, blocking the entire stream shard.
*   *Solution*: Configure your Event Source Mapping with:
    1.  **Maximum Record Age**: Drops the record if it is older than a certain age (e.g., 2 hours).
    2.  **Maximum Retry Attempts**: Limits retries (e.g., retry 3 times, then discard).
    3.  **On-Failure Destination**: Sends the metadata of the failing record to an SQS queue/SNS topic (acting as a DLQ) before skipping it, allowing developers to inspect it without blocking the stream.
