package springboot.interview.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 1. Spring Data Method Derivation (no SQL needed)
    Optional<Employee> findByEmail(String email);

    // 2. Custom JPQL Query (using objects, not tables)
    @Query("SELECT e FROM Employee e WHERE e.department.name = :deptName")
    List<Employee> findEmployeesByDepartmentName(@Param("deptName") String deptName);

    // 3. Solving N+1 problem using JOIN FETCH
    @Query("SELECT e FROM Employee e JOIN FETCH e.tasks WHERE e.id = :id")
    Optional<Employee> findByIdWithTasks(@Param("id") Long id);
}
