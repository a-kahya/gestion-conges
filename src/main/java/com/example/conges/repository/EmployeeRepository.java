package com.example.conges.repository;

import com.example.conges.entity.Employee;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    
    List<Employee> findByLastNameContaining(String motCle);
    
    List<Employee> findByBirthDateIs(LocalDate birthDate);
    
    List<Employee> findByEmail(String email);
}