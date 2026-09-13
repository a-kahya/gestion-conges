package com.example.conges.repository;

import com.example.conges.entity.Employee;
import com.example.conges.entity.Status;
import com.example.conges.entity.VacationRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRequestRepository extends JpaRepository<VacationRequest, Integer> {
    
    List<VacationRequest> findByEmployeeAndStatus(Employee employee, Status status);
}