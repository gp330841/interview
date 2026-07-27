# Specification: High-Throughput Key Generator & URL Shortener

**Spec ID**: SPEC-URL-004  
**Status**: APPROVED  
**Category**: System Design / Scalability  

---

## 1. System Overview
A scalable URL Shortener service capable of encoding unique 64-bit numerical IDs into compact 7-character Base62 strings, achieving high throughput through distributed ID range pre-allocation.

---

## 2. Key Algorithms
- **Base62 Character Set**: `[0-9a-zA-Z]` (62 symbols).
- **Short Key Capacity**: $62^7 \approx 3.52 \times 10^{12}$ unique URLs.
- **Pre-Allocation**: Each instance acquires a range block of $1,000$ IDs at a time to prevent centralized database lock contention.
