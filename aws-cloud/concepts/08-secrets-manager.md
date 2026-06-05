# Secrets Manager & SSM Parameter Store Deep Dive

This module covers the advanced architecture of AWS Secrets Manager and Systems Manager (SSM) Parameter Store, focusing on secure storage, automatic rotation, caching strategies, and cross-account access.

---

## 1. AWS Secrets Manager: Advanced Architecture

Secrets Manager is a purpose-built database for credentials, API keys, and private configuration settings.

### A. Automatic Credential Rotation Architecture
One of Secrets Manager's primary advantages is native, automated secret rotation, which uses an AWS Lambda function to update credentials on a schedule.

```
       +--------------------------------------------+
       |           Secrets Manager Rotation         |
       +--------------------------------------------+
                             |
                   [Triggers on Schedule]
                             |
                             v
               +----------------------------+
               |  Rotation Lambda Function  |
               +----------------------------+
                 /          |             \
      1. Create New       2. Update DB   3. Test New
      Version in SM       with New Creds   Credentials
                 \          |             /
                  v         v            v
             [Secrets]    [RDS]      [Verify App]
```

The rotation process follows a **4-step lifecycle** to ensure the database credentials are changed without causing application downtime:
1.  **createSecret**: The rotation Lambda generates a new random password and registers it in Secrets Manager as a `AWSPENDING` version.
2.  **setSecret**: The Lambda updates the actual target database (e.g., RDS) with the new credentials. The database now accepts *both* the old and new passwords (if it supports dual-user rotation) or just the new one.
3.  **testSecret**: The Lambda attempts to authenticate against the database using the pending credentials to verify they work.
4.  **finishSecret**: The Lambda marks the pending secret version as `AWSCURRENT`, promoting it to the primary active secret. The old version is kept as `AWSPREVIOUS`.

### B. KMS Key Integration & Security
Every secret in Secrets Manager is encrypted at rest using an **AWS Key Management Service (KMS) Customer Master Key (CMK)**.
*   When you retrieve a secret via `GetSecretValue`, Secrets Manager calls the KMS Decrypt API dynamically.
*   To successfully fetch a secret, the executing IAM role must have permission to read the secret (`secretsmanager:GetSecretValue`) **and** permission to use the KMS key (`kms:Decrypt`).

### C. Cross-Account Access via Resource-Based Policies
Unlike many AWS config services, Secrets Manager supports **Resource-Based Policies** attached directly to the secret.
*   *Why this matters*: You can grant permissions to an IAM role in a *different* AWS account directly from the secret's policy. The external role can call `GetSecretValue` without having to perform an STS `AssumeRole` operation first, simplifying cross-account integrations.

### D. Client-Side Caching (Crucial for Scaling)
Calling Secrets Manager's API (`GetSecretValue`) directly inside a high-throughput application handler (e.g., on every HTTP request or database query) is a major anti-pattern.
*   *Problems*:
    1.  **Cost**: Secrets Manager charges $0.05 per 10,000 API requests. A high-traffic API will rack up thousands of dollars in requests.
    2.  **Rate Limits**: Secrets Manager has a default API limit of 10,000 requests/sec. Exceeding this throttles your app.
*   *Solution*: Implement **Client-Side Caching** using the official AWS Secrets Manager caching libraries (available for Java, Go, Python, NodeJS). The library caches the secret in local memory, checks for rotation in the background, and refreshes the cache asynchronously based on a TTL (e.g., every 5-15 minutes).

---

## 2. Secrets Manager vs. SSM Parameter Store

Systems design interviewers often ask you to choose between these two services. Use this comparison table to make an informed decision.

| Feature | AWS Secrets Manager | SSM Parameter Store |
| :--- | :--- | :--- |
| **Cost** | Paid ($0.40 per secret per month + API requests). | Free (for Standard Parameters). Paid (for Advanced Parameters). |
| **Automatic Rotation** | **Native** integration via built-in AWS Lambda templates. | **None**. Must build custom EventBridge + Lambda workflows. |
| **Parameter Limits** | Up to 40,000 secrets per region. | Up to 10,000 parameters per account (Standard). |
| **Max Payload Size** | Up to **64 KB** per secret. | **4 KB** (Standard) or **8 KB** (Advanced). |
| **Cross-Account Access** | Native support via **Resource-Based Policies**. | Requires assuming cross-account IAM Roles (no resource policies). |
| **Parameter Hierarchy** | No native folder structures. | Supports path hierarchies (e.g., `/prod/db/password`). |

---

## 3. Standard Interview Questions

#### Q1: If you store database credentials in Secrets Manager, how do you prevent your application from failing during secret rotation?
1.  **Dual-User Rotation Pattern**: (If supported by database/Lambda) Configure rotation to swap between two database users. User A is active while User B's password is rotated. The application will transparently fall back to the active user while the other rotates.
2.  **Client-Side Caching with Retry**: Ensure your application uses a caching library. If a database connection fails (due to a rotated password), the application should invalidate its local cache, force a fresh fetch from Secrets Manager, and retry the database connection.

#### Q2: Can you encrypt values in SSM Parameter Store?
Yes. SSM Parameter Store offers a parameter type called `SecureString`.
*   When you write a parameter as a `SecureString`, SSM encrypts the value using a KMS key.
*   When retrieving, the client must explicitly pass `--with-decryption` in the CLI/SDK call, and must have `kms:Decrypt` permission on the associated key.

#### Q3: When should you choose Secrets Manager over SSM Parameter Store?
*   Choose **Secrets Manager** when you need automated credential rotation (especially for RDS), cross-account access to secrets without assuming roles, or need to store large configurations (up to 64KB).
*   Choose **SSM Parameter Store** for general application configuration parameters (e.g., hostnames, feature flags, environment variables) that are not highly sensitive or do not change frequently, to save on cost.
