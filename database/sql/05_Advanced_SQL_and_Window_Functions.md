# Advanced SQL & Window Functions Interview Questions

## 1. What are Window Functions? How are they different from GROUP BY?
Window functions perform a calculation across a set of table rows that are somehow related to the current row. 
*   **Difference from GROUP BY**: While aggregate functions with `GROUP BY` collapse multiple rows into a single summary row, window functions do not cause rows to become grouped into a single output row; the rows retain their separate identities. The result of the calculation is added as an extra column.
*   **Syntax**: They use the `OVER()` clause, which can contain `PARTITION BY` (to divide rows into groups) and `ORDER BY` (to specify the order of calculation within the partition).

## 2. Explain ROW_NUMBER(), RANK(), and DENSE_RANK().
These are window functions used to assign a sequential integer to rows.
Given the salaries: [100, 100, 90, 80]
*   **ROW_NUMBER()**: Assigns a unique, sequential number to each row, regardless of ties. 
    *   *Result*: 1, 2, 3, 4
*   **RANK()**: Assigns the same rank to duplicate values, but skips the next numbers. 
    *   *Result*: 1, 1, 3, 4 (Notice '2' is skipped).
*   **DENSE_RANK()**: Assigns the same rank to duplicate values, but does *not* skip the next numbers.
    *   *Result*: 1, 1, 2, 3

## 3. Explain LEAD() and LAG() functions.
These functions allow you to access data from a subsequent or previous row in the same result set without the need for a self-join.
*   **LAG()**: Accesses data from a previous row. Useful for calculating year-over-year growth or finding the difference between a row and the previous row.
*   **LEAD()**: Accesses data from a subsequent row.

## 4. What is a Database Trigger? What are its pros and cons?
A trigger is a special type of stored procedure that automatically executes (fires) when a specific event occurs in the database (e.g., `BEFORE INSERT`, `AFTER UPDATE`, `DELETE`).
*   **Pros**: Useful for enforcing complex business rules automatically, maintaining audit trails, or cross-table synchronization.
*   **Cons**: They execute invisibly ("magic"), making debugging and tracing application logic difficult. Heavy use of triggers can severely degrade performance.

## 5. What are Stored Procedures?
A stored procedure is a prepared SQL code that you can save, so the code can be reused over and over again. They can accept input parameters and return multiple results.
*   **Pros**: 
    *   Reduced network traffic (send a single call instead of large queries).
    *   Security (can grant execution rights without granting direct table access).
    *   Pre-compiled execution plans (in some legacy databases, though modern DBs cache plans for parameterized queries too).
*   **Cons**:
    *   Business logic gets trapped in the database layer.
    *   Harder to version control, test, and debug compared to application code.
    *   Ties the application to a specific database vendor (e.g., PL/SQL vs T-SQL).

## 6. What is a View and a Materialized View?
*   **View**: A virtual table based on the result-set of an SQL statement. It does not store data itself. Every time you query a view, the database executes the underlying query. Good for simplifying complex joins and restricting data access.
*   **Materialized View**: A view whose result set has been physically saved/stored to disk. When queried, it reads from the stored data rather than executing the query. 
    *   *Use case*: Drastically improves performance for heavy, complex analytical queries.
    *   *Trade-off*: The data can become stale. It needs to be refreshed (either synchronously on underlying table changes, or asynchronously on a schedule).

## 7. Difference between a Function and a Stored Procedure?
*   **Function**: MUST return a value. Can be used in `SELECT` statements (`SELECT my_function(id)`). Cannot alter database state (DML operations like INSERT/UPDATE are usually restricted).
*   **Stored Procedure**: Does not necessarily have to return a value (can return output parameters). Cannot be used directly in a `SELECT` statement. Designed to perform actions/DML operations.
