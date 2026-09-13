package com.example.conges.controller;

import com.example.conges.entity.Department;
import com.example.conges.repository.DepartmentRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("listDepartments", departmentRepository.findAll());
        return "departments";
    }

    @GetMapping("/departments/new")
    public String showNewDepartmentForm(Model model) {
        Department department = new Department();
        model.addAttribute("department", department);
        return "new_department";
    }

    @PostMapping("/departments/saveNew")
    public String saveNewDepartment(@ModelAttribute("department") @Valid Department department, Errors errors, Model model) {
        if (errors.hasErrors()) {
            return "new_department";
        }
        List<Department> deptNameList = departmentRepository.findByName(department.getName());
        if (!deptNameList.isEmpty()) {
            model.addAttribute("message", "A department with this name already exists !");
            return "new_department";
        }
        departmentRepository.save(department);
        return "redirect:/departments";
    }

    @GetMapping("/departments/update")
    public String showUpdateDepartmentForm(@RequestParam(name = "id") int id, Model model) {
        Optional<Department> optional = departmentRepository.findById(id);
        Department department = null;
        if (optional.isPresent())
            department = optional.get();
        else
            throw new RuntimeException("Department not found for id: " + id);
        model.addAttribute("department", department);
        return "update_department";
    }

    @PostMapping("/departments/saveUpdate")
    public String saveUpdateDepartment(@ModelAttribute("department") @Valid Department department, Errors errors) {
        if (errors.hasErrors()) {
            return "update_department";
        }
        departmentRepository.save(department);
        return "redirect:/departments";
    }

    @GetMapping("/departments/delete")
    public String deleteDepartment(@RequestParam(name = "id") int id, Model model) {
        Optional<Department> optional = departmentRepository.findById(id);
        if (optional.isPresent()){
            departmentRepository.deleteById(id);
        }
        else {
            throw new RuntimeException("Department not found for id: " + id);
        }   
        return "redirect:/departments";
    }
}