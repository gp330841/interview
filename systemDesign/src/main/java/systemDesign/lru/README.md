# LRU (Least Recently Used) Cache

An LRU Cache organizes items in order of use, allowing you to quickly identify which item hasn't been used for the longest amount of time. To maintain **O(1)** time complexity for both `get` and `put` operations, we combine two data structures:
1. **HashMap**: For expected $O(1)$ lookups of cache keys.
2. **Doubly Linked List (DLL)**: For $O(1)$ additions/deletions and to consistently track the most and least recently used elements.

## Class Breakdown
* **`Node.java`**: Represents a single entry in the DLL. It stores both `key` and `value`. Storing the key is crucial so that when the DLL drops the last node (eviction), we seamlessly know which key we also must wipe out from the `HashMap`.
* **`DoublyLinkedList.java`**: Custom DLL implementation routing updates. The strictly updated *head* pinpoints the Most Recently Used (MRU) whereas the *tail* outlines the Least Recently Used (LRU) element.
* **`LRUCacheBase.java`**: Abstract class defining the core structural bounds (`HashMap`, `cacheCapacity`, standard `get`/`put` signatures).
* **`LRUCacheImpl.java`**: The concrete implementation. 
  * On `get()`: It checks the map. If present, it retrieves the node and immediately relocates it to the front of the DLL (flagging it as MRU).
  * On `put()`: If the key exists, it updates the internal value and moves the node to the front. If the key is totally new, it adds it directly extending the map layout and inserting it at the front of the DLL. If capacity thresholds are breached, the physical tail node (LRU) is popped strictly from both the DLL and the underlying map.
* **`LRUCacheDriver.java`**: Demonstration component resolving test payloads and cache evictions functionality.
