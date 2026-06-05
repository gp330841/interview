# EC2 & Auto Scaling Groups (ASG) Deep Dive

This module covers the advanced architectural internals of Amazon EC2 and Auto Scaling Groups (ASG) required for senior infrastructure and systems design interviews.

---

## 1. Amazon EC2: Virtualization & Hardware Internals

To understand EC2 performance, you must understand how AWS runs virtual machines.

### A. Virtualization Types: HVM vs. PV (Historical Context)
*   **Paravirtualization (PV)**: Virtual machines do not have access to hardware emulation. They require a modified guest OS that calls the hypervisor API directly. Slow and mostly deprecated.
*   **Hardware Virtual Machine (HVM)**: The hypervisor provides complete hardware emulation. Guest operating systems do not need modification, and they take advantage of hardware extensions (Intel VT / AMD-V) for near-bare-metal execution. All modern EC2 instances use HVM.

### B. The AWS Nitro System
Historically, hypervisors (like Xen) managed virtualization, networking, storage, and security in software on the host CPU. This created a virtualization tax, consuming up to 30% of host resources.
*   **Nitro Architecture**: AWS offloads hypervisor duties to dedicated hardware cards (ASICs). 
    *   *Nitro Card for VPC*: Handles local network routing and security group rules in hardware.
    *   *Nitro Card for EBS*: Handles encryption and NVMe storage connection.
    *   *Nitro Security Chip*: Hardware-based root of trust, protecting resources and firmware.
    *   *Nitro Hypervisor*: A lightweight hypervisor (based on KVM) that only manages CPU and memory allocation.
*   *Interview Impact*: Nitro enables bare-metal performance, hardware-enforced isolation, and faster boot times.

---

## 2. Ephemeral vs. Persistent Storage: Instance Store vs. EBS

When provisioning EC2, choosing the right storage type impacts performance, cost, and reliability.

| Feature | Instance Store (Ephemeral) | EBS (Elastic Block Store - Persistent) |
| :--- | :--- | :--- |
| **Physical Location** | Physically attached to the host hardware hosting the VM. | Network-attached storage (SAN) connected via internal network. |
| **Durability** | Data is lost if the instance stops, terminates, or the host hardware fails. Survives OS reboots. | Data persists independently of the EC2 instance lifespan. |
| **Performance** | Ultra-low latency, extremely high IOPS (million+) and throughput (GB/s). Good for temp cache. | IOPS/throughput limited by network bandwidth (EBS-Optimized) and volume tier (gp3, io2). |
| **Backup** | No native snapshots. Must handle backups at the application layer. | Native, point-in-time incremental snapshots stored in S3. |
| **Use Cases** | Temporary databases, cache pools, Distributed file systems (HDFS), NoSQL replicas (Cassandra). | Boot volumes, relational databases (MySQL/PostgreSQL), long-term file storage. |

---

## 3. Auto Scaling Groups (ASG): Internals & Scaling Mechanics

An ASG automates EC2 scale-out (adding instances) and scale-in (terminating instances) based on demand.

```
       +--------------------------------------------+
       |             Auto Scaling Group             |
       |                                            |
       |  [Instance AZ-A]  [Instance AZ-B]  ...    |
       |         ^                ^                 |
       +---------|----------------|-----------------+
                 |                |
             Scale-Out        Scale-In
                 |                |
         [Metric Alarm]     [Cooldown Timeout]
```

### A. Health Checks: EC2 vs. ELB
By default, ASG uses **EC2 status checks** (checking hardware and hypervisor availability). If your application crashes or locks up (e.g., JVM OutOfMemoryError), the EC2 instance appears healthy, but the web server fails to respond.
*   **ELB Health Checks**: If your ASG is attached to an Application Load Balancer (ALB), configure the ASG to use ELB health checks. The ALB continuously hits an application endpoint (e.g., `/health`). If the endpoint returns a `5xx` or times out, the ALB marks it unhealthy, and the ASG terminates and replaces it.

### B. Scaling Policies
*   **Target Tracking**: You define a metric and target value (e.g., "Keep average CPU utilization at 70%"). AWS manages the alarms and scaling steps.
*   **Step Scaling**: Increases/decreases capacity in steps based on the size of the metric breach (e.g., "If CPU is 70-80%, add 1 instance. If CPU is >80%, add 3 instances").
*   **Scheduled Scaling**: Scales based on predictable, known time events (e.g., "Scale out to 20 instances every Friday at 5:00 PM for weekend traffic").
*   **Predictive Scaling**: Uses machine learning to analyze historical traffic patterns and proactively launch instances *before* a spike occurs.

### C. Cooldown Periods
A cooldown period ensures the ASG does not launch or terminate additional instances before the previous scaling action takes effect.
*   *Why?* It takes a few minutes for an EC2 instance to boot, pull dependencies, and begin processing requests. Without a cooldown, a scaling policy might trigger repeatedly, launching too many instances (thrashing).

---

## 4. Advanced ASG Mechanics: Lifecycle Hooks & Termination Policies

### A. ASG Lifecycle Hooks
Lifecycle hooks allow you to pause an instance's state transitions (either at launch or termination) to perform custom setup or teardown scripts before the instance is placed in service or terminated.

```
[Pending] ---> (Pending:Wait Hook) ---> [InService]
                                             |
[Terminating] <--- (Terminating:Wait Hook) <-+
```

*   **Launch Hook (`autoscaling:EC2_INSTANCE_LAUNCHING`)**:
    *   *State Transition*: Instance is created but remains in a `Pending:Wait` state.
    *   *Action*: An SSM document or Lambda script installs software, updates configuration files, downloads seed data, or performs system checks.
    *   *Resolution*: The script sends a `CONTINUE` or `ABANDON` signal. If `CONTINUE`, the instance goes to `InService`.
*   **Termination Hook (`autoscaling:EC2_INSTANCE_TERMINATING`)**:
    *   *State Transition*: Instance is marked for termination but remains in `Terminating:Wait`.
    *   *Action*: Allows you to drain active connections, upload log files to S3, or backup local state.
    *   *Resolution*: The hook times out or is completed manually, letting the instance terminate.

### B. Instance Termination Policies
When a scale-in event occurs, the ASG must decide which specific instance to terminate. The default behavior is designed to maintain high availability across Availability Zones:
1.  Identify the Availability Zone with the **most instances**.
2.  Within that AZ, identify instances running on the **oldest Launch Configuration** or Launch Template.
3.  If multiple instances match, terminate the instance that is **closest to the next billing hour** (to maximize instance utility).
4.  If multiple instances still match, terminate one **randomly**.
*   *Customization*: You can configure policies like `OldestInstance`, `NewestInstance`, or assign **Instance Scale-In Protection** to specific nodes (e.g., nodes currently running long-running batch jobs).

---

## 5. Standard Interview Questions

#### Q1: If an EC2 instance fails its ELB health check, what happens?
The Load Balancer stops sending traffic to the instance. If the instance is part of an Auto Scaling Group configured with ELB health checks, the ASG will mark the instance as unhealthy, terminate it, and provision a new instance in its place to maintain the desired capacity.

#### Q2: What is the difference between a Reboot, a Stop, and a Terminate action on an EC2 instance?
*   **Reboot**: Soft or hard operating system restart. The instance remains on the same physical host, keeps its private/public IP address, and preserves local Instance Store data.
*   **Stop**: The instance is shut down and moved off the physical host. You stop paying for compute, but continue paying for attached EBS volumes. When started, it gets a new public IP (unless an Elastic IP is attached) and **loses any data on ephemeral Instance Stores**.
*   **Terminate**: The instance is permanently deleted. Attached EBS volumes are deleted (unless configured otherwise via `DeleteOnTermination=false`). The IP addresses are released back to the pool.

#### Q3: How do you handle scaling for a memory-intensive application if ASG CPU scaling is not triggering?
By default, CloudWatch does *not* collect memory metrics from EC2 because memory is managed inside the OS kernel, which the hypervisor cannot read.
*   *Solution*: Install the **CloudWatch Agent** on the EC2 instances to push memory metrics (e.g., `mem_used_percent`) as a Custom Metric to CloudWatch. Then, configure your Auto Scaling Group to scale using a Target Tracking Policy based on this custom memory metric.
