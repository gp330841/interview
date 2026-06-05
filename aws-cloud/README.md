# AWS Cloud Interview Guide & Concept Dashboard

Welcome to the AWS Cloud engineering guide. This section covers core global infrastructure, virtual networking, compute models, storage classes, security foundations, databases, and application integration patterns commonly asked in systems engineering, DevOps, and cloud architecture interviews.

---

## AWS Cloud Concept Dashboard

| ID | Concept | Category | Difficulty | Key Focus / Interview Questions |
|----|---------|----------|------------|---------------------------------|
| 01 | [EC2 & Auto Scaling Groups (ASG)](concepts/01-ec2-asg.md) | Compute | Medium | Virtualization (HVM vs PV), ASG Lifecycle Hooks, Scaling Policies, Instance Store vs EBS |
| 02 | [Amazon S3 Deep Dive](concepts/02-s3.md) | Storage | Medium | Partition limits (3500 PUT/5500 GET), Multipart uploads, Object Locks, Encryption (SSE-KMS), Versioning |
| 03 | [AWS Lambda Deep Dive](concepts/03-lambda.md) | Serverless | Hard | Cold Starts, MicroVM (Firecracker), Hyperplane ENI, Sync vs Async vs Event Source Mapping |
| 04 | [ECS & EKS Container Systems](concepts/04-ecs-eks.md) | Containers | Hard | Task Placement strategies/constraints, EKS Control Plane, Pod Networking (VPC CNI), IRSA (Pod IAM Roles) |
| 05 | [RDS & Aurora Databases](concepts/05-rds.md) | Database | Hard | Multi-AZ physical sync replication, Read Replica async lag, Aurora distributed storage & Quorum model |
| 06 | [DynamoDB Deep Dive](concepts/06-dynamodb.md) | Database | Hard | Partition allocation calculation, GSI vs LSI, Streams, Single-Table Design, Conditional Writes |
| 07 | [SQS & SNS Messaging Systems](concepts/07-sqs-sns.md) | Integration | Medium | Visibility Timeout, FIFO de-duplication, Long Polling vs Short Polling, Pub/Sub Fan-out architecture |
| 08 | [Secrets Manager & SSM Parameter Store](concepts/08-secrets-manager.md) | Security | Easy | Lambda-based rotation, KMS envelope integration, rate limit thresholds, SSM vs Secrets Manager |

---

## Core Infrastructure & Networking Reference

Before diving into service-specific internals, ensure you are comfortable with these networking building blocks:

### A. Infrastructure Layout
*   **Region**: Independent geographic area hosting multiple, isolated Availability Zones (AZs).
*   **Availability Zone (AZ)**: One or more data centers with redundant power, cooling, and networking. AZs in a region are separated by miles to isolate against local disasters but linked by high-speed, sub-millisecond fiber.
*   **Edge Location**: Content delivery endpoints running **Amazon CloudFront** (CDN) and **Route 53** (DNS) to cache static and dynamic assets close to clients.

### B. VPC Components
*   **Public Subnet**: Subnet with a route table entry pointing to an **Internet Gateway (IGW)** (`0.0.0.0/0 -> igw-xxxx`).
*   **Private Subnet**: Subnet with no direct route to the IGW. Outbound traffic must go through a **NAT Gateway** residing in a public subnet.
*   **Security Groups**: Stateful, instance-level firewalls. Inbound traffic allowed automatically allows return outbound traffic. Supports *Allow* rules only.
*   **Network ACLs (NACLs)**: Stateless, subnet-level firewalls. Evaluated numerically. Requires explicit *Allow* and *Deny* rules for both inbound and outbound traffic.

### C. Private Connectivity
*   **Gateway VPC Endpoints**: Free. Add routes to your route table directing traffic to **S3** and **DynamoDB** over the internal AWS network, bypassing NAT Gateways.
*   **Interface VPC Endpoints (PrivateLink)**: Paid. Deploy an Elastic Network Interface (ENI) with a private IP from your subnet into the VPC to securely access other AWS services (e.g., KMS, SQS) over the internal network.
*   **VPC Peering**: A 1-to-1 connection between two VPCs (non-transitive).
*   **Transit Gateway**: A hub-and-spoke transit router that connects thousands of VPCs and on-premises networks centrally.
