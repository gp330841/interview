# Refactoring & Optimization Prompts

Curated prompts for code optimization, code smell elimination, and concurrency tuning.

---

## 1. Concurrency Audit Prompt
```markdown
Analyze the attached Java class for thread safety issues, race conditions, memory visibility bugs, and lock contention.
Suggest specific lock-free algorithms or ConcurrentHashMap atomic primitives to improve throughput.
```

## 2. Performance Optimization Prompt
```markdown
Review the algorithm and data structures in the attached file.
Optimize time complexity from O(N) to O(1) or O(log N) where feasible, while maintaining memory efficiency and code readability.
```

## 3. Code Smell Elimination Prompt
```markdown
Audit the provided code for anti-patterns:
- Long methods / God classes
- Deep nesting
- Swallowed exceptions
- Unsynchronized shared mutable state
Refactor into modular, clean SOLID Java code.
```
