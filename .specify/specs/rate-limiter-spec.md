# Specification: Distributed Rate Limiter Service

**Spec ID**: SPEC-RL-002  
**Status**: APPROVED  
**Category**: Resilience & Security  

---

## 1. System Overview
A high-performance rate-limiting service providing Token Bucket and Sliding Window Log algorithms to protect APIs from abuse and denial of service attacks.

---

## 2. Requirements & Algorithms

### 2.1 Token Bucket Algorithm
- Refill rate: $R$ tokens/sec. Max capacity: $C$ tokens.
- Refill calculation: $\text{newTokens} = \min(C, \text{currentTokens} + \Delta t \times R)$
- Thread safety: Atomic compare-and-swap or lock-free timestamp updates.

### 2.2 Sliding Window Log Algorithm
- Track request timestamps within rolling window $W$.
- Evict timestamps older than $t - W$.
- Accept request if current timestamp count within window $< \text{limit}$.

---

## 3. Performance & Memory Budget
- Processing overhead < 1 ms per request.
- Zero memory leakage via background expired window cleaner.
