# Collections Framework in Java

This module covers Java Collections architecture, underlying data structures, internals of hashing, concurrency in collections, and interview questions.

---

## 🌟 Core Concepts

Java Collections represent a architecture for storing and manipulating a group of objects. It has interfaces like `List`, `Set`, `Queue`, and `Map`.

```
                    Collection
         ___________/   |      \___________
        /               |                  \
      List             Set                Queue
     /    \           /   \                 |
ArrayList LinkedList HashSet TreeSet     Deque
                                            |
                                         ArrayDeque
```

---

## ❓ Frequently Asked Interview Questions

### Q1: Explain the Internal Working of `HashMap` in Java?
> [!IMPORTANT]
> This is a classic interview question. Be ready to explain buckets, hash collision, treeification, and rehashing.

1. **Hashing**: When `put(key, value)` is called, the key's `hashCode()` is calculated. A supplemental hash function is applied to distribute the hash values uniformly.
2. **Index Formula**: The bucket index is calculated using bitwise AND: `index = hash & (n - 1)`, where `n` is the bucket capacity (always a power of 2).
3. **Collision Resolution**: HashMap uses **Chaining** (Singly Linked List) inside buckets. If different keys map to the same index, they are appended to the linked list.
4. **Treeification (Java 8+)**: If the linked list size in a single bucket exceeds `TREEIFY_THRESHOLD` (8) and the total capacity is at least `MIN_TREEIFY_CAPACITY` (64), the list is converted into a **Balanced Red-Black Tree**. This improves the worst-case search complexity from $O(N)$ to $O(\log N)$.
5. **Load Factor & Rehashing**: The default load factor is `0.75`. When the element count exceeds `capacity * load_factor`, capacity doubles, and all key-value entries are re-hashed to new indexes.

---

### Q2: What is the difference between "Fail-Fast" and "Fail-Safe" Iterators?
- **Fail-Fast Iterators**: Operate directly on the original collection. They maintain a internal modification counter (`modCount`). If the collection is modified structure-wise (add/remove) while iterating, it immediately throws `ConcurrentModificationException`.
  - *Examples*: ArrayList, HashMap, HashSet iterators.
- **Fail-Safe (Weakly Consistent) Iterators**: Operate on a clone or snapshot of the collection (or lock-free structures). They do not throw exceptions if the collection is altered.
  - *Examples*: CopyOnWriteArrayList, ConcurrentHashMap iterators.

---

### Q3: What is the difference between ArrayList and LinkedList?
| Feature | ArrayList | LinkedList |
| :--- | :--- | :--- |
| **Data Structure** | Dynamic Resizable Array | Doubly Linked List |
| **Random Access** | $O(1)$ (using index) | $O(N)$ (must traverse from head/tail) |
| **Insert/Delete** | $O(N)$ (requires shifting elements) | $O(1)$ (if node pointer is known) |
| **Memory Footprint**| Low (only stores elements & indexes) | High (stores node pointers: next, prev) |

---

### Q4: Why must `equals()` and `hashCode()` be overridden together?
> [!WARNING]
> Breaking this contract will lead to erratic behavior in hashing-based collections like `HashMap`, `HashSet`, and `Hashtable`.

- **The Contract**:
  1. If two objects are equal according to `equals(Object)`, they must have the same `hashCode()`.
  2. If two objects have the same `hashCode()`, they are *not* guaranteed to be equal.
- **What happens if you fail to override?**
  If `equals()` is overridden but not `hashCode()`, two logically equal objects will yield different hashCodes. A `HashMap` will place them in different buckets, making it impossible to retrieve the stored value using a duplicate key.

---

### Q5: What is the difference between `Comparable` and `Comparator`?
- **Comparable**: Used for **natural sorting** of objects. The class itself implements `Comparable<T>` and overrides `compareTo(T)`. Changes the class definition itself.
- **Comparator**: Used for **custom sorting** orders. A separate class (or lambda) implements `Comparator<T>` and overrides `compare(T o1, T o2)`. Does not modify the target class.

---

## 🛠️ Code Examples
- **[CollectionsBasics.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/collections/CollectionsBasics.java)**: Comprehensive comparison of Lists, Sets, Maps, and sorting mechanisms.
- **[EqualsAndHashCode.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/collections/EqualsAndHashCode.java)**: Demonstrates the hashing contract and consequence of incorrect overrides.
