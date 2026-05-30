package com.interview.javaconcepts.design_patterns;

/**
 * Demonstrates the classic inner-class Builder pattern in Java.
 * 
 * The Builder pattern is used to construct complex objects step-by-step.
 * It is particularly useful when an object has many attributes, some of which are optional.
 */
public class BuilderPattern {

    public static class Computer {
        // Required parameters
        private final String HDD;
        private final String RAM;

        // Optional parameters
        private final boolean isGraphicsCardEnabled;
        private final boolean isBluetoothEnabled;

        public String getHDD() { return HDD; }
        public String getRAM() { return RAM; }
        public boolean isGraphicsCardEnabled() { return isGraphicsCardEnabled; }
        public boolean isBluetoothEnabled() { return isBluetoothEnabled; }

        // Private constructor to enforce construction via Builder only
        private Computer(Builder builder) {
            this.HDD = builder.HDD;
            this.RAM = builder.RAM;
            this.isGraphicsCardEnabled = builder.isGraphicsCardEnabled;
            this.isBluetoothEnabled = builder.isBluetoothEnabled;
        }

        @Override
        public String toString() {
            return "Computer [HDD=" + HDD + ", RAM=" + RAM + 
                   ", GraphicsCard=" + isGraphicsCardEnabled + 
                   ", Bluetooth=" + isBluetoothEnabled + "]";
        }

        // Static nested Builder class
        public static class Builder {
            // Required parameters
            private final String HDD;
            private final String RAM;

            // Optional parameters - initialized to default values
            private boolean isGraphicsCardEnabled = false;
            private boolean isBluetoothEnabled = false;

            public Builder(String hdd, String ram) {
                this.HDD = hdd;
                this.RAM = ram;
            }

            public Builder setGraphicsCardEnabled(boolean isGraphicsCardEnabled) {
                this.isGraphicsCardEnabled = isGraphicsCardEnabled;
                return this; // Return builder instance for chaining
            }

            public Builder setBluetoothEnabled(boolean isBluetoothEnabled) {
                this.isBluetoothEnabled = isBluetoothEnabled;
                return this;
            }

            public Computer build() {
                return new Computer(this);
            }
        }
    }

    public static void main(String[] args) {
        // Constructing a computer using fluid builder chain
        Computer comp = new Computer.Builder("1 TB NVMe SSD", "32 GB DDR5")
                .setGraphicsCardEnabled(true)
                .setBluetoothEnabled(true)
                .build();

        System.out.println("Built Computer: " + comp);
    }
}
