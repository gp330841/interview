package com.interview.javaconcepts.serialization;

import java.io.*;

/**
 * Demonstrates Java Serialization, Custom Serialization, transient fields,
 * and the Externalizable interface frequently tested in core Java interviews.
 */
public class SerializationDemo {

    // A Standard Serializable Class
    public static class User implements Serializable {
        // serialVersionUID ensures class versions match during serialization and deserialization
        @Serial
        private static final long serialVersionUID = 1L;

        private final String username;
        
        // 'transient' fields are skipped during serialization
        private transient final String password; 
        
        private final int age;

        public User(String username, String password, int age) {
            this.username = username;
            this.password = password;
            this.age = age;
        }

        @Override
        public String toString() {
            return "User [username=" + username + ", password=" + password + " (should be null after deserialization), age=" + age + "]";
        }
    }

    // A Serializable Class with Custom readObject and writeObject (e.g. for Encryption)
    public static class EncryptedUser implements Serializable {
        @Serial
        private static final long serialVersionUID = 2L;

        private String name;
        private transient String ssn; // Sensitive field to be custom encrypted

        public EncryptedUser(String name, String ssn) {
            this.name = name;
            this.ssn = ssn;
        }

        // Custom writing mechanism
        @Serial
        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject(); // Serialize non-transient fields (name)
            // Encrypt and write the transient sensitive field
            String encryptedSSN = encrypt(ssn);
            oos.writeObject(encryptedSSN);
        }

        // Custom reading mechanism
        @Serial
        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject(); // Deserialize non-transient fields (name)
            // Read and decrypt the sensitive field
            String encryptedSSN = (String) ois.readObject();
            this.ssn = decrypt(encryptedSSN);
        }

        private String encrypt(String raw) {
            return new StringBuilder(raw).reverse().toString(); // Mock encryption: reverse string
        }

        private String decrypt(String enc) {
            return new StringBuilder(enc).reverse().toString(); // Mock decryption: reverse back
        }

        @Override
        public String toString() {
            return "EncryptedUser [name=" + name + ", ssn=" + ssn + "]";
        }
    }

    // Externalizable Class (Full manual control over serialization layout - faster performance)
    public static class Company implements Externalizable {
        private String name;
        private int employeeCount;

        // Mandated public default constructor for Externalizable
        public Company() {}

        public Company(String name, int employeeCount) {
            this.name = name;
            this.employeeCount = employeeCount;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // Write only what's required in specific order
            out.writeUTF(name);
            out.writeInt(employeeCount);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            // Read exactly in the same order
            this.name = in.readUTF();
            this.employeeCount = in.readInt();
        }

        @Override
        public String toString() {
            return "Company [name=" + name + ", employeeCount=" + employeeCount + "]";
        }
    }

    public static void main(String[] args) {
        String filepath = "user.ser";

        try {
            System.out.println("--- 1. Basic Serialization (with transient password) ---");
            User originalUser = new User("java_dev", "secret_pass123", 28);
            System.out.println("Before Serialization: " + originalUser);

            // Serialize to file
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
                oos.writeObject(originalUser);
            }

            // Deserialize from file
            User deserializedUser;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
                deserializedUser = (User) ois.readObject();
            }
            System.out.println("After Deserialization:  " + deserializedUser);

            System.out.println("\n--- 2. Custom Serialization (ssn Encryption) ---");
            EncryptedUser encOriginal = new EncryptedUser("John Doe", "123-456-789");
            System.out.println("Before custom Serialization: " + encOriginal);

            // Serialize
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
                oos.writeObject(encOriginal);
            }

            // Deserialize
            EncryptedUser encDeserialized;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
                encDeserialized = (EncryptedUser) ois.readObject();
            }
            System.out.println("After custom Deserialization:  " + encDeserialized);

            System.out.println("\n--- 3. Externalizable Interface ---");
            Company compOriginal = new Company("Google", 150000);
            System.out.println("Before Externalizable serialization: " + compOriginal);

            // Serialize
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
                oos.writeObject(compOriginal);
            }

            // Deserialize
            Company compDeserialized;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
                compDeserialized = (Company) ois.readObject();
            }
            System.out.println("After Externalizable deserialization:  " + compDeserialized);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Clean up serialized file
            new File(filepath).delete();
        }
    }
}
