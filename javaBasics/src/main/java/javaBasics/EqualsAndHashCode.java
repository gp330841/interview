package javaBasics;
import java.util.HashSet;
import java.util.Set;
/**
 * Question: What is the difference between == and .equals() method? Why override hashCode()?
 * 
 * Critical Points:
 * - '==' compares memory addresses (reference equality) for objects, and values for primitives.
 * - '.equals()' evaluates the meaningful equivalence of two objects based on their properties.
 * - If you override equals(), you MUST override hashCode(). The contract states that if two objects 
 *   are equal according to equals(), they must have the same hashCode. 
 * - If you don't override hashCode, Collections like HashSet or HashMap won't be able to effectively 
 *   find the object (because they put objects into buckets based on hashCode).
 */
public class EqualsAndHashCode {
    public static void main(String[] args) {
        System.out.println("--- == vs .equals() Demo ---");
        Employee e1 = new Employee();
        e1.setName("Alice");
        e1.setDepartment("IT");
        e1.setSalary(10000);
        Employee e2 = new Employee();
        e2.setName("Alice");
        e2.setDepartment("IT");
        e2.setSalary(12000); // Notice salary is different, but our equals() only checks name and department
        // '==' Operator checks reference (memory location)
        System.out.println("e1 == e2 : " + (e1 == e2)); // false
        // '.equals()' Method evaluates business logic
        System.out.println("e1.equals(e2) : " + e1.equals(e2)); // true (because name and department match)
        System.out.println("\n--- hashCode() Demo in HashSet ---");
        Set<Employee> employees = new HashSet<>();
        employees.add(e1);
        employees.add(e2);
        // Even though we added 2 objects, the Set only holds 1 because their equals() is true AND hashCode() is the same
        System.out.println("HashSet size: " + employees.size()); // 1
    }
}


class Employee  {
    private String name;
    private int salary;
    private String department;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getSalary() {
        return salary;
    }
    public void setSalary(int salary) {
        this.salary = salary;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return name.equals(employee.name) && department.equals(employee.department);
    }
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + department.hashCode();
        return result;
    }
}