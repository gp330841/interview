# Indexing & B-Trees Interview Questions

## 1. What is an Index in a database and how does it work?
An index is a data structure (usually a B-Tree or Hash table) associated with a table that improves the speed of data retrieval operations. It works similarly to an index in a book. Without an index, the database must perform a "full table scan," reading every row to find the matching ones. With an index, the database traverses the index structure to quickly locate the disk addresses of the required rows.

## 2. What is the difference between a Clustered and a Non-Clustered Index?
*   **Clustered Index**: Determines the physical order of data in a table. Because the data rows can be sorted in only one way, there can be **only one** clustered index per table (usually the Primary Key). The leaf nodes of a clustered index contain the actual data pages.
*   **Non-Clustered Index**: Does not alter the physical order of the table. It creates a separate structure from the data rows. The leaf nodes contain the index key values and a pointer (row locator) to the actual data row. A table can have **multiple** non-clustered indexes.

## 3. How does a B-Tree (and B+Tree) index work?
A B-Tree (Balanced Tree) keeps data sorted and allows searches, sequential access, insertions, and deletions in logarithmic time.
Most relational databases use a variant called **B+Tree**:
*   Internal nodes only contain keys for routing.
*   All actual data (or pointers to data) are stored at the leaf nodes.
*   The leaf nodes are linked together in a linked list, which makes range queries (e.g., `WHERE age BETWEEN 20 AND 30`) extremely efficient.

## 4. What is a Covering Index?
A covering index is a non-clustered index that includes all the columns required by a specific query. If a query only selects columns that are part of the index, the database can retrieve the results directly from the index without having to look up the actual data rows in the table. This drastically reduces disk I/O and improves performance.

## 5. Explain Index Scan, Index Seek, and Table Scan.
*   **Table Scan**: The database reads every row in the table from beginning to end. Very slow for large tables.
*   **Index Scan**: The database reads all rows in the index structure. Faster than a table scan if the index is smaller than the table, but still reads the whole index.
*   **Index Seek**: The database uses the B-Tree structure to navigate directly to the specific rows that match the filter criteria. This is the most efficient operation.

## 6. Why shouldn't you index every column?
Indexes come with overhead:
*   **Storage Space**: Indexes consume additional disk space.
*   **Write Penalty**: Every time a row is `INSERT`ed, `UPDATE`d, or `DELETE`d, the database must also update all corresponding indexes. This slows down write operations.
*   Therefore, indexes should be created strategically on columns frequently used in `WHERE`, `JOIN`, `ORDER BY`, and `GROUP BY` clauses.

## 7. What is a Hash Index and when is it used?
A Hash Index uses a hash function to map keys to a specific bucket. 
*   **Pros**: Extremely fast for exact match lookups (`WHERE id = 5`). Time complexity is O(1).
*   **Cons**: Cannot be used for range queries (`WHERE id > 5`) or sorting, because the hashing process randomizes the order. Often used in in-memory databases or key-value stores (like Redis).
