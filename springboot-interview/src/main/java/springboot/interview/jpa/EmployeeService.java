package springboot.interview.jpa;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor Injection (Best Practice)
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Wraps the method in a database transaction
    @Transactional
    public Employee createEmployeeAndAssignTask(Employee employee, Task task) {
        // 1. Save the employee
        Employee savedEmployee = employeeRepository.save(employee);
        
        // 2. Add task to employee's list 
        // (CascadeType.ALL ensures the task gets saved to DB automatically)
        task.setAssignee(savedEmployee);
        savedEmployee.getTasks().add(task);
        
        // If an exception is thrown here, the whole transaction rolls back,
        // and the employee won't be saved in the database.
        
        return savedEmployee;
    }
    
    @Transactional(readOnly = true) // Optimization for read-only operations
    public Employee getEmployeeDetails(Long id) {
        // Uses the custom JOIN FETCH query to prevent N+1 problem
        return employeeRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
