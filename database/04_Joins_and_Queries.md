# SQL Joins & Queries Interview Questions

## 1. Explain the different types of SQL Joins.
Joins are used to combine rows from two or more tables based on a related column between them.
*   **INNER JOIN**: Returns only the rows where there is a match in *both* tables.
*   **LEFT (OUTER) JOIN**: Returns all rows from the left table, and the matched rows from the right table. The result is NULL from the right side if there is no match.
*   **RIGHT (OUTER) JOIN**: Returns all rows from the right table, and the matched rows from the left table. Result is NULL from the left side when there is no match.
*   **FULL (OUTER) JOIN**: Returns all rows when there is a match in *either* left or right table. (Union of Left and Right joins).
*   **CROSS JOIN**: Returns the Cartesian product of the two tables (every row in the left table paired with every row in the right table).
*   **SELF JOIN**: A regular join, but the table is joined with itself. Requires the use of table aliases.

## 2. What is the difference between WHERE and HAVING clauses?
*   **WHERE**: Used to filter records *before* any grouping takes place. It cannot contain aggregate functions (like `SUM`, `COUNT`).
*   **HAVING**: Used to filter groups *after* the `GROUP BY` clause has been applied. It is specifically designed to be used with aggregate functions.
*   *Order of execution*: `FROM` -> `WHERE` -> `GROUP BY` -> `HAVING` -> `SELECT`.

## 3. What is a Subquery? Explain Correlated vs. Non-Correlated Subqueries.
A subquery is a query nested inside another query.
*   **Non-Correlated Subquery**: Can be evaluated independently of the outer query. It runs once, and its result is used by the outer query.
    *   *Example*: `SELECT * FROM Employees WHERE DepartmentID IN (SELECT ID FROM Departments WHERE Name = 'IT')`
*   **Correlated Subquery**: References columns from the outer query. It cannot be evaluated independently. It must be executed repeatedly, once for every row evaluated by the outer query. This is generally much slower.
    *   *Example*: `SELECT * FROM Employees e1 WHERE Salary > (SELECT AVG(Salary) FROM Employees e2 WHERE e1.DepartmentID = e2.DepartmentID)`

## 4. What is the difference between UNION and UNION ALL?
Both operators combine the result sets of two or more `SELECT` statements.
*   **UNION**: Removes duplicate rows from the combined result set. It requires a distinct sort operation behind the scenes, making it slower.
*   **UNION ALL**: Includes all duplicate rows. It simply appends the result sets, making it significantly faster than `UNION`. Always use `UNION ALL` unless you specifically need to eliminate duplicates.

## 5. What is the difference between TRUNCATE, DELETE, and DROP?
*   **DELETE**: A DML (Data Manipulation Language) command. Deletes rows one by one and records each deletion in the transaction log. Can be rolled back. Can use a `WHERE` clause. Slower.
*   **TRUNCATE**: A DDL (Data Definition Language) command. Removes all rows by deallocating the data pages. Minimal logging. Cannot be rolled back in most databases (though some like SQL Server allow it within a transaction). Faster than `DELETE`. Resets identity columns. Cannot use a `WHERE` clause.
*   **DROP**: A DDL command. Completely removes the table structure and its data from the database. Cannot be rolled back.

## 6. What are CTEs (Common Table Expressions)?
A CTE is a temporary named result set that you can reference within a `SELECT`, `INSERT`, `UPDATE`, or `DELETE` statement. Defined using the `WITH` keyword.
*   **Pros**: Improves query readability, allows for recursive queries (Recursive CTEs are useful for hierarchical data like organizational charts or graphs), and can be referenced multiple times within the main query.
