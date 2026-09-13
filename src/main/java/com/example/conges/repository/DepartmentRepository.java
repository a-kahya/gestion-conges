package com.example.conges.repository;

import com.example.conges.entity.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByName(String name);
}
