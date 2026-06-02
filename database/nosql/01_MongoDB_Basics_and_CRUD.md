# MongoDB Basics: A Beginner to Intermediate Guide

This guide is designed for developers transitioning to NoSQL and MongoDB. It covers core concepts, terminology mapping, and copy-pasteable command-line snippets for CRUD operations, indexing, and aggregation pipelines using the Mongo Shell (`mongosh`).

---

## 1. Relational (SQL) vs. MongoDB (NoSQL) Terminologies

If you are coming from a relational database background (MySQL, PostgreSQL), here is how concepts translate to MongoDB:

| Relational (SQL) Term | MongoDB (NoSQL) Equivalent | Description |
| :--- | :--- | :--- |
| **Database** | **Database** | A container for collections. |
| **Table** | **Collection** | A group of documents (analogous to a table). |
| **Row** | **Document** | A single record (stored in BSON format). |
| **Column** | **Field** | A key-value pair inside a document. |
| **Primary Key** | **Primary Key (`_id`)** | Automatically generated unique 12-byte identifier for documents. |
| **Table Join** | **Embedding** or **`$lookup` Reference** | Storing nested data inside a document, or referencing via IDs. |

---

## 2. JSON vs. BSON

MongoDB stores records as **BSON** (Binary JSON) documents. 

*   **JSON (JavaScript Object Notation)**: A text-based, human-readable format that only supports basic types (Strings, Numbers, Booleans, Nulls, Arrays, and Objects).
*   **BSON (Binary JSON)**: A binary serialization of JSON. It is not human-readable but is designed for speed (efficient to parse and traverse by computers) and supports additional data types:
    *   `ObjectId` (12-byte unique identifier)
    *   `Date` (UTC datetime representation)
    *   `Decimal128` (High-precision floats for financial operations)
    *   `Int32` / `Int64` (Explicit integer sizes)
    *   `Binary Data` (For storing small blobs like profile photos)

---

## 3. Getting Started with the Mongo Shell (`mongosh`)

Here are the basic environment commands to navigate databases and collections:

```javascript
// 1. Show all databases in the server
show dbs

// 2. Switch to a database (creates it automatically on the first write if it does not exist)
use shop_db

// 3. Check which database you are currently using
db

// 4. Create a collection explicitly (optional; done automatically on first insert)
db.createCollection("products")

// 5. List all collections in the current database
show collections

// 6. Delete a collection (deletes all data and indexes)
db.products.drop()

// 7. Delete the current active database
db.dropDatabase()
```

---

## 4. CRUD Operations (Create, Read, Update, Delete)

All CRUD operations are performed on the collection object (e.g., `db.collectionName`).

### A. CREATE Operations

#### 1. Insert a Single Document (`insertOne`)
```javascript
db.users.insertOne({
  name: "Alice Smith",
  age: 28,
  email: "alice@example.com",
  skills: ["Java", "Spring Boot"],
  joinedDate: new Date()
})
```

#### 2. Insert Multiple Documents (`insertMany`)
```javascript
db.users.insertMany([
  { name: "Bob Jones", age: 34, email: "bob@example.com", skills: ["Python", "Django"] },
  { name: "Charlie Brown", age: 22, email: "charlie@example.com", skills: ["JavaScript", "React"] },
  { name: "Diana Prince", age: 31, email: "diana@example.com", skills: ["Java", "Docker"] }
])
```

---

### B. READ Operations

#### 1. Read All Documents
```javascript
db.users.find()
// Pretty-print JSON format in older shells (mongosh pretty prints by default)
db.users.find().pretty()
```

#### 2. Read with Equality Filter
```javascript
db.users.find({ name: "Bob Jones" })
```

#### 3. Comparison Operators (`$eq`, `$ne`, `$gt`, `$gte`, `$lt`, `$lte`, `$in`, `$nin`)
```javascript
// Find users where age is greater than or equal to 30
db.users.find({ age: { $gte: 30 } })

// Find users who have either 22 or 28 as their age
db.users.find({ age: { $in: [22, 28] } })

// Find users whose name is NOT "Alice Smith"
db.users.find({ name: { $ne: "Alice Smith" } })
```

#### 4. Logical Operators (`$or`, `$and`, `$not`, `$nor`)
```javascript
// Find users who are either older than 30 OR have name "Charlie Brown"
db.users.find({
  $or: [
    { age: { $gt: 30 } },
    { name: "Charlie Brown" }
  ]
})

// Implicit AND (find users with age > 25 AND age < 35)
db.users.find({ age: { $gt: 25, $lt: 35 } })
```

#### 5. Element and Type Operators (`$exists`, `$type`)
```javascript
// Find documents that contain the "skills" field
db.users.find({ skills: { $exists: true } })

// Find documents where "age" is stored as a number (double/int)
db.users.find({ age: { $type: "number" } })
```

#### 6. Array Queries
```javascript
// Find users whose skills array contains exactly "Java" (matches elements inside array)
db.users.find({ skills: "Java" })

// Find users whose skills array contains BOTH "Java" AND "Docker" (order doesn't matter)
db.users.find({ skills: { $all: ["Java", "Docker"] } })
```

#### 7. Projections (Select which fields to return)
Specify fields to include (1) or exclude (0) as the second argument:
```javascript
// Return only name and email (exclude _id)
db.users.find({}, { name: 1, email: 1, _id: 0 })
```

#### 8. Sorting, Limiting, and Pagination
*   `1` represents Ascending order.
*   `-1` represents Descending order.
```javascript
// Find all users, sort by age descending, limit to 2 results
db.users.find().sort({ age: -1 }).limit(2)

// Pagination: Skip first 2 documents, limit to next 2
db.users.find().sort({ name: 1 }).skip(2).limit(2)
```

---

### C. UPDATE Operations

Update commands require a **filter** (which documents to update) and an **update modifier** (what changes to apply).

#### Common Update Operators:
*   `$set`: Updates or adds a field.
*   `$unset`: Removes a field.
*   `$inc`: Increments/decrements a numeric value.
*   `$push`: Appends an item to an array.
*   `$pull`: Removes items from an array.
*   `$addToSet`: Appends an item to an array *only* if the item does not already exist (prevents duplicates).

#### 1. Update One Document (`updateOne`)
```javascript
// Update Alice's email and set a new field "location"
db.users.updateOne(
  { name: "Alice Smith" },
  { 
    $set: { email: "alice.new@example.com", location: "New York" },
    $inc: { age: 1 } // Increment age by 1
  }
)
```

#### 2. Update Multiple Documents (`updateMany`)
```javascript
// Add a "status" field set to "active" for all users under 30
db.users.updateMany(
  { age: { $lt: 30 } },
  { $set: { status: "active" } }
)
```

#### 3. Array Updates
```javascript
// Add "Kubernetes" to Bob's skills array
db.users.updateOne(
  { name: "Bob Jones" },
  { $addToSet: { skills: "Kubernetes" } }
)
```

#### 4. Upsert (Insert if not found)
Setting `upsert: true` inserts the document if no document matches the filter query.
```javascript
db.users.updateOne(
  { email: "newuser@example.com" },
  { $set: { name: "New User", age: 25 } },
  { upsert: true }
)
```

---

### D. DELETE Operations

#### 1. Delete One Document (`deleteOne`)
```javascript
// Deletes the first document matching the filter
db.users.deleteOne({ name: "Charlie Brown" })
```

#### 2. Delete Multiple Documents (`deleteMany`)
```javascript
// Deletes all users who do not have an active status
db.users.deleteMany({ status: { $ne: "active" } })
```

---

## 5. Basic Indexing

Indexes in MongoDB improve lookup query performance. Without indexes, MongoDB must perform a full collection scan (read every document).

```javascript
// 1. Create a Single-Field Index on "email" in ascending order
db.users.createIndex({ email: 1 })

// 2. Create a Compound Index on "age" (ascending) and "name" (descending)
db.users.createIndex({ age: 1, name: -1 })

// 3. View all indexes on the collection
db.users.getIndexes()

// 4. Drop an index using its name (find name via getIndexes())
db.users.dropIndex("email_1")

// 5. Analyze if a query is using an index
db.users.find({ email: "bob@example.com" }).explain("executionStats")
```

---

## 6. The Aggregation Pipeline (Intermediate)

While `.find()` is great for basic fetching, the **Aggregation Pipeline** is MongoDB’s powerful framework for data transformation, grouping, and computation.

Think of it as an assembly line where documents enter and flow through multiple stages:
```
[Raw Documents] -> [$match] -> [$group] -> [$sort] -> [Final Output]
```

### Core Aggregation Stages:
*   `$match`: Filters documents (like `WHERE`). Place this first to minimize processed documents.
*   `$project`: Filters/renames fields and computes temporary values (like `SELECT`).
*   `$group`: Aggregates documents by a key and calculates totals, averages, counts (like `GROUP BY`).
*   `$unwind`: Splts documents containing arrays into individual documents per array element.
*   `$lookup`: Joins documents from another collection (like `LEFT JOIN`).

### Practical Aggregation Pipeline Example:
Suppose we have a `sales` collection with the following documents:
```javascript
db.sales.insertMany([
  { item: "Laptop", category: "Electronics", price: 1000, quantity: 2 },
  { item: "Phone", category: "Electronics", price: 500, quantity: 5 },
  { item: "Notebook", category: "Stationery", price: 5, quantity: 10 },
  { item: "Pen", category: "Stationery", price: 2, quantity: 50 },
  { item: "Headphones", category: "Electronics", price: 100, quantity: 3 }
])
```

#### Task: Calculate the total revenue (price * quantity) for each category, filtering out categories with less than $50 total revenue, and sort categories by highest revenue.

```javascript
db.sales.aggregate([
  // Stage 1: Reshape documents to calculate total revenue per sale item
  {
    $project: {
      category: 1,
      itemRevenue: { $multiply: ["$price", "$quantity"] }
    }
  },
  
  // Stage 2: Group by category and sum up their revenues
  {
    $group: {
      _id: "$category", // Group key
      totalRevenue: { $sum: "$itemRevenue" },
      itemCount: { $sum: 1 }
    }
  },
  
  // Stage 3: Match/Filter groups where totalRevenue >= 50
  {
    $match: {
      totalRevenue: { $gte: 50 }
    }
  },
  
  // Stage 4: Sort by totalRevenue in descending order (-1)
  {
    $sort: { totalRevenue: -1 }
  }
])
```

#### Output:
```json
[
  { "_id": "Electronics", "totalRevenue": 4800, "itemCount": 3 },
  { "_id": "Stationery", "totalRevenue": 150, "itemCount": 2 }
]
```

---

## 7. Common Interview Q&As for Beginners & Intermediate

### Q1: What is the difference between `update()` and `save()` in MongoDB? (Note: deprecated in modern mongosh, replaced by `updateOne`/`replaceOne`)
**Answer:**
*   Historically, `update()` was used to modify fields in an existing document. It required update operators like `$set`. If no operator was supplied, it would overwrite/replace the entire document.
*   `save()` acted as an upsert wrapper. If the document contained an `_id` field that already existed in the collection, it performed a full document replacement (equivalent to `replaceOne`). If the `_id` did not exist or was absent, it inserted a new document (equivalent to `insertOne`).
*   *Modern Recommendation*: Always use `updateOne()`, `updateMany()`, or `replaceOne()` to avoid accidental document overrides.

### Q2: What is the `_id` field in MongoDB, and what is its structure?
**Answer:**
Every document in MongoDB requires a unique `_id` field that serves as the primary key. If omitted, MongoDB automatically generates a 12-byte `ObjectId`.
The 12-byte layout of an `ObjectId` contains:
*   **4 bytes**: A timestamp representing the ObjectId's creation, measured in seconds since the Unix epoch. (Allows you to extract creation date from the id itself).
*   **5 bytes**: A random value unique to the machine and process.
*   **3 bytes**: An incrementing counter, initialized with a random value.

### Q3: What is the purpose of the `$unwind` stage in Aggregation?
**Answer:**
The `$unwind` stage deconstructs an array field from the input documents to output a document for *each* element of the array. Each output document is a copy of the input document but with the array field replaced by the single array element.
*   *Example*: If a document is `{ name: "Bob", skills: ["Java", "Docker"] }`.
*   Applying `{ $unwind: "$skills" }` yields two documents:
    1.  `{ name: "Bob", skills: "Java" }`
    2.  `{ name: "Bob", skills: "Docker" }`
*   This is essential for performing group operations on specific items inside arrays (e.g., counting the most popular skills across all users).
