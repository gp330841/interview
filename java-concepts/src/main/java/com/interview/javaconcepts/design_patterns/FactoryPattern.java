package com.interview.javaconcepts.design_patterns;

/**
 * Demonstrates both Simple Factory and Abstract Factory patterns.
 * 
 * Factory Pattern instantiates subclasses depending on input logic.
 * Abstract Factory acts as a factory of factories.
 */
public class FactoryPattern {

    // Common Interface
    public interface Computer {
        String getRAM();
        String getHDD();
        String getCPU();
    }

    // Subclass 1: PC
    public static class PC implements Computer {
        private final String ram;
        private final String hdd;
        private final String cpu;

        public PC(String ram, String hdd, String cpu) {
            this.ram = ram;
            this.hdd = hdd;
            this.cpu = cpu;
        }

        @Override public String getRAM() { return this.ram; }
        @Override public String getHDD() { return this.hdd; }
        @Override public String getCPU() { return this.cpu; }

        @Override
        public String toString() {
            return "PC Config:: RAM=" + getRAM() + ", HDD=" + getHDD() + ", CPU=" + getCPU();
        }
    }

    // Subclass 2: Server
    public static class Server implements Computer {
        private final String ram;
        private final String hdd;
        private final String cpu;

        public Server(String ram, String hdd, String cpu) {
            this.ram = ram;
            this.hdd = hdd;
            this.cpu = cpu;
        }

        @Override public String getRAM() { return this.ram; }
        @Override public String getHDD() { return this.hdd; }
        @Override public String getCPU() { return this.cpu; }

        @Override
        public String toString() {
            return "Server Config:: RAM=" + getRAM() + ", HDD=" + getHDD() + ", CPU=" + getCPU();
        }
    }

    // ==========================================
    // Approach 1: Simple Factory Pattern
    // ==========================================
    public static class ComputerFactory {
        public static Computer getComputer(String type, String ram, String hdd, String cpu) {
            if ("PC".equalsIgnoreCase(type)) {
                return new PC(ram, hdd, cpu);
            } else if ("Server".equalsIgnoreCase(type)) {
                return new Server(ram, hdd, cpu);
            }
            throw new IllegalArgumentException("Unknown computer type!");
        }
    }

    // ==========================================
    // Approach 2: Abstract Factory Pattern
    // ==========================================
    public interface ComputerAbstractFactory {
        Computer createComputer();
    }

    public static class PCFactory implements ComputerAbstractFactory {
        private final String ram;
        private final String hdd;
        private final String cpu;

        public PCFactory(String ram, String hdd, String cpu) {
            this.ram = ram;
            this.hdd = hdd;
            this.cpu = cpu;
        }

        @Override
        public Computer createComputer() {
            return new PC(ram, hdd, cpu);
        }
    }

    public static class ServerFactory implements ComputerAbstractFactory {
        private final String ram;
        private final String hdd;
        private final String cpu;

        public ServerFactory(String ram, String hdd, String cpu) {
            this.ram = ram;
            this.hdd = hdd;
            this.cpu = cpu;
        }

        @Override
        public Computer createComputer() {
            return new Server(ram, hdd, cpu);
        }
    }

    public static class ComputerConsumer {
        public static Computer getComputer(ComputerAbstractFactory factory) {
            return factory.createComputer();
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Simple Factory Pattern ---");
        Computer pc = ComputerFactory.getComputer("pc", "16 GB", "500 GB", "Intel Core i7");
        Computer server = ComputerFactory.getComputer("server", "64 GB", "4 TB", "AMD EPYC");
        System.out.println("Simple Factory PC: " + pc);
        System.out.println("Simple Factory Server: " + server);

        System.out.println("\n--- Abstract Factory Pattern ---");
        Computer absPC = ComputerConsumer.getComputer(new PCFactory("32 GB", "1 TB", "M2 Ultra"));
        Computer absServer = ComputerConsumer.getComputer(new ServerFactory("128 GB", "10 TB", "Intel Xeon"));
        System.out.println("Abstract Factory PC: " + absPC);
        System.out.println("Abstract Factory Server: " + absServer);
    }
}
