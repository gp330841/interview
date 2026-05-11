# Famous SQL Query Interview Problems

This section covers the most frequently asked practical SQL coding questions in interviews.

## 1. Find the Nth Highest Salary
**Problem:** Write a SQL query to find the 2nd highest salary from an `Employee` table.

**Solution 1: Using Subquery (Max)**
```sql
SELECT MAX(Salary) 
FROM Employee 
WHERE Salary < (SELECT MAX(Salary) FROM Employee);
```

**Solution 2: Using LIMIT / OFFSET (MySQL/PostgreSQL)**
```sql
SELECT DISTINCT Salary 
FROM Employee 
ORDER BY Salary DESC 
LIMIT 1 OFFSET 1; -- (N-1) for Nth highest
```

**Solution 3: Using DENSE_RANK() (Most robust, handles duplicates well)**
```sql
WITH RankedSalaries AS (
    SELECT Salary, DENSE_RANK() OVER(ORDER BY Salary DESC) as rnk
    FROM Employee
)
SELECT Salary FROM RankedSalaries WHERE rnk = 2;
```

## 2. Find Duplicate Records in a Table
**Problem:** Write a query to find all emails that appear more than once in a `Users` table.

**Solution:**
```sql
SELECT Email, COUNT(Email)
FROM Users
GROUP BY Email
HAVING COUNT(Email) > 1;
```

## 3. Delete Duplicate Records (Keeping One)
**Problem:** Delete all duplicate emails from a `Person` table, keeping only one unique email with the smallest `Id`.

**Solution (Using Self Join - MySQL):**
```sql
DELETE p1 
FROM Person p1, Person p2
WHERE p1.Email = p2.Email AND p1.Id > p2.Id;
```

**Solution (Using Window Functions - PostgreSQL/SQL Server):**
```sql
WITH CTE AS (
    SELECT id, email, ROW_NUMBER() OVER(PARTITION BY email ORDER BY id) as row_num
    FROM Person
)
DELETE FROM CTE WHERE row_num > 1;
```

## 4. Employees Earning More Than Their Managers
**Problem:** Given an `Employee` table with `Id`, `Name`, `Salary`, and `ManagerId`. Find the names of employees who earn more than their managers.

**Solution (Using Self Join):**
```sql
SELECT e.Name AS Employee
FROM Employee e
JOIN Employee m ON e.ManagerId = m.Id
WHERE e.Salary > m.Salary;
```

## 5. Fetch Top N Records for Each Group (e.g., Top 3 Salaries per Department)
**Problem:** Find the employees who earn the top 3 highest salaries in each of the departments.

**Solution (Using DENSE_RANK()):**
```sql
WITH RankedEmployees AS (
    SELECT d.Name AS Department, e.Name AS Employee, e.Salary,
           DENSE_RANK() OVER(PARTITION BY d.Id ORDER BY e.Salary DESC) as rnk
    FROM Employee e
    JOIN Department d ON e.DepartmentId = d.Id
)
SELECT Department, Employee, Salary
FROM RankedEmployees
WHERE rnk <= 3;
```

## 6. Swap Values in a Single Update
**Problem:** Given a table `Salary` with a column `sex` storing 'm' and 'f', write a single query to swap all 'm' to 'f' and 'f' to 'm' without using intermediate temp tables.

**Solution (Using CASE statement):**
```sql
UPDATE Salary 
SET sex = CASE 
    WHEN sex = 'm' THEN 'f'
    ELSE 'm'
END;
```

## 7. Find the Consecutive Empty Seats
**Problem:** Given a `Cinema` table with `seat_id` and `free` (1 for free, 0 for occupied). Find all consecutive available seats (2 or more).

**Solution (Using Window Functions - LEAD/LAG):**
```sql
WITH SeatStatus AS (
    SELECT seat_id, free,
           LAG(free, 1) OVER(ORDER BY seat_id) as prev_free,
           LEAD(free, 1) OVER(ORDER BY seat_id) as next_free
    FROM Cinema
)
SELECT seat_id 
FROM SeatStatus 
WHERE free = 1 AND (prev_free = 1 OR next_free = 1)
ORDER BY seat_id;
```

## 8. Calculate Cumulative Sum (Running Total)
**Problem:** Calculate the running total of revenue per day from a `Sales` table.

**Solution (Using Window Functions):**
```sql
SELECT SaleDate, DailyRevenue,
       SUM(DailyRevenue) OVER(ORDER BY SaleDate ASC) as CumulativeRevenue
FROM Sales;
```
