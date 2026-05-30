# Object Serialization in Java

This module covers Java Object Serialization, JVM deserialization mechanics, custom serialization controls, security, and the Externalizable interface.

---

## 🌟 Core Concepts

Serialization is the process of converting an object's state into a byte stream, which can then be written to a file, database, or sent over a network. Deserialization is the reverse process.

To make a Java object serializable, the class must implement the **`java.io.Serializable`** marker interface (which has no methods).

```
  Object State  ====== (Serialization via ObjectOutputStream) =====>  Byte Stream
  Byte Stream   ===== (Deserialization via ObjectInputStream) ====>  Object Instance
```

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the purpose of `serialVersionUID` and what happens if you omit it?
`serialVersionUID` is a unique version identifier for each serializable class, used to verify that the sender and receiver of a serialized object have loaded classes that are compatible.
- **If omitted**: The JVM calculates a default `serialVersionUID` value at runtime based on the class structure (fields, methods, modifiers).
- **The Danger**: If you add or modify a field in the class, the recalculated hash will change. Any attempt to deserialize older byte streams will fail with an **`InvalidClassException`**, making version control and backward compatibility impossible.

---

### Q2: What is the effect of the `transient` keyword?
The `transient` keyword is applied to member variables to prevent them from being serialized.
- When an object is serialized, any variable marked `transient` is ignored, and its value is not written to the byte stream.
- Upon deserialization, `transient` variables are initialized to their default values (e.g., `null` for objects, `0` for numbers).
- **Use Cases**: Storing sensitive data (passwords, SSNs), holding references to non-serializable objects (threads, database connections), or caching derived values.

---

### Q3: How do custom `writeObject` and `readObject` methods work?
If a class needs custom logic during serialization (e.g., encrypting passwords, executing validation), it can define two private methods with exact signatures:
- `private void writeObject(ObjectOutputStream oos) throws IOException`
- `private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException`
The JVM will detect these private methods via reflection and call them *instead* of executing standard serialization. You can call `oos.defaultWriteObject()` within them to serialize normal fields automatically.

---

### Q4: Serializable vs Externalizable?
| Feature | Serializable | Externalizable |
| :--- | :--- | :--- |
| **Interface Type** | Marker Interface (no methods). | Standard Interface (`writeExternal`, `readExternal`). |
| **Control** | Handled automatically by the JVM. | Full manual control by the developer. |
| **Performance** | Slower (uses reflection under the hood). | Much faster (direct custom read/write logic). |
| **Constructor** | No constructor is forced. | Requires a public default constructor. |

---

## 🛠️ Code Examples
- **[SerializationDemo.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/serialization/SerializationDemo.java)**: Comprehensive demo showing standard serialization, transient variable suppression, custom encryption using `writeObject`/`readObject`, and manual performance control with `Externalizable`.
