package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Core Concept: Lock-Free Programming & Atomic Classes.
 * 
 * Atomics rely on hardware-level CPU support for CAS (Compare-And-Swap) instructions.
 * CAS performs updates atomically in a single clock cycle without using OS locks or blocking threads.
 * 
 * This class covers:
 * 1. AtomicInteger basics: incrementAndGet(), getAndSet(), compareAndSet().
 * 2. AtomicReference: Coordinating thread-safe transitions of custom State objects.
 * 3. CAS Loop: Building custom CAS loops for non-trivial state updates.
 * 4. AtomicIntegerFieldUpdater: Highly memory-efficient atomic updates of a volatile field directly,
 *    avoiding the memory allocation overhead of wrapping primitive variables inside an AtomicInteger object.
 */
public class AtomicClassesDemo {

    // Custom state class for AtomicReference
    static class ConnectionState {
        final String status;
        final long timestamp;

        ConnectionState(String status, long timestamp) {
            this.status = status;
            this.timestamp = timestamp;
        }
    }

    // Class for AtomicIntegerFieldUpdater demo
    static class UserSession {
        private final String username;
        // MUST be volatile and non-private/accessible to the updater for field reflection to work
        volatile int loginCount = 0; 

        // Static factory creation of the field updater to avoid per-instance reflection overhead
        private static final AtomicIntegerFieldUpdater<UserSession> countUpdater = 
            AtomicIntegerFieldUpdater.newUpdater(UserSession.class, "loginCount");

        UserSession(String username) {
            this.username = username;
        }

        void recordLogin() {
            countUpdater.incrementAndGet(this); // Atomic increment directly on the volatile int field!
        }

        int getLoginCount() {
            return loginCount;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. AtomicInteger Demo ===");
        demonstrateAtomicInteger();

        System.out.println("\n=== 2. AtomicReference & CAS Loop Demo ===");
        demonstrateAtomicReferenceCAS();

        System.out.println("\n=== 3. AtomicIntegerFieldUpdater Demo ===");
        demonstrateFieldUpdater();
    }

    private static void demonstrateAtomicInteger() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        Runnable r = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.incrementAndGet(); // Atomically adds 1 and gets the new value
            }
        };

        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final Counter value (expected 2000): " + counter.get());

        // Simple CAS check
        boolean success = counter.compareAndSet(2000, 3000); // expect 2000, if match update to 3000
        System.out.println("CAS update status: " + success + ", current value: " + counter.get());
    }

    private static void demonstrateAtomicReferenceCAS() {
        AtomicReference<ConnectionState> stateRef = new AtomicReference<>(new ConnectionState("DISCONNECTED", System.currentTimeMillis()));

        // Perform custom state transition via a CAS loop (Spin-lock pattern)
        ConnectionState oldState;
        ConnectionState newState;
        do {
            oldState = stateRef.get();
            if ("CONNECTED".equals(oldState.status)) {
                break; // already in desired state
            }
            newState = new ConnectionState("CONNECTED", System.currentTimeMillis());
            
            // CAS update: only set to newState if the current value is exactly oldState (reference equality)
        } while (!stateRef.compareAndSet(oldState, newState));

        System.out.println("Connection status successfully updated to: " + stateRef.get().status);
    }

    private static void demonstrateFieldUpdater() throws InterruptedException {
        UserSession session = new UserSession("SeniorBackendDev");

        Runnable r = () -> {
            for (int i = 0; i < 1000; i++) {
                session.recordLogin();
            }
        };

        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("User session logins (expected 2000): " + session.getLoginCount());
        System.out.println("Note: Using FieldUpdater saved us 24 bytes of object overhead per Session instance!");
    }
}
