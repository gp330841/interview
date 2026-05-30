package com.interview.javaconcepts.design_patterns;

/**
 * Demonstrates thread-safe Singleton patterns frequently asked in Java interviews.
 * 
 * Singletons ensure that a class has only one instance and provides a global point of access to it.
 */
public class SingletonPattern {

    // ==========================================
    // Approach 1: Double-Checked Locking (Lazy Initialization)
    // ==========================================
    public static class DoubleCheckedLockingSingleton {
        // volatile guarantees visibility of changes across threads and prevents instruction reordering
        private static volatile DoubleCheckedLockingSingleton instance;

        private DoubleCheckedLockingSingleton() {
            // Protect against reflection-based instantiations
            if (instance != null) {
                throw new IllegalStateException("Instance already initialized!");
            }
        }

        public static DoubleCheckedLockingSingleton getInstance() {
            if (instance == null) { // First check (no locking)
                synchronized (DoubleCheckedLockingSingleton.class) {
                    if (instance == null) { // Second check (with locking)
                        instance = new DoubleCheckedLockingSingleton();
                    }
                }
            }
            return instance;
        }
    }

    // ==========================================
    // Approach 2: Bill Pugh Singleton (Lazy & Thread-Safe without Synchronization)
    // ==========================================
    public static class BillPughSingleton {
        private BillPughSingleton() {}

        // The inner static helper class is loaded into memory only when BillPughSingleton.getInstance() is called.
        // JVM guarantees thread-safe class loading.
        private static class SingletonHelper {
            private static final BillPughSingleton INSTANCE = new BillPughSingleton();
        }

        public static BillPughSingleton getInstance() {
            return SingletonHelper.INSTANCE;
        }
    }

    // ==========================================
    // Approach 3: Enum Singleton (Joshua Bloch's Recommended - 100% safe from Serialization/Reflection attacks)
    // ==========================================
    public enum EnumSingleton {
        INSTANCE;

        public void performAction() {
            System.out.println("Enum Singleton action performed successfully!");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Double-Checked Locking Singleton ---");
        var instance1 = DoubleCheckedLockingSingleton.getInstance();
        var instance2 = DoubleCheckedLockingSingleton.getInstance();
        System.out.println("Are both instances equal? " + (instance1 == instance2));

        System.out.println("\n--- Bill Pugh Singleton ---");
        var bp1 = BillPughSingleton.getInstance();
        var bp2 = BillPughSingleton.getInstance();
        System.out.println("Are both Bill Pugh instances equal? " + (bp1 == bp2));

        System.out.println("\n--- Enum Singleton ---");
        EnumSingleton.INSTANCE.performAction();
    }
}
