package com.example.conges.controller;

import com.example.conges.entity.Department;
import com.example.conges.entity.Employee;
import com.example.conges.repository.DepartmentRepository;
import com.example.conges.repository.EmployeeRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("listEmployees", employeeRepository.findAll());
        model.addAttribute("listDepartments", departmentRepository.findAll());
        return "employees";
    }
    
    //premier filtre = celui avec le nom
    @GetMapping("/employees/filter1")
    public String filterByName(Model model, @RequestParam(name = "motCle", defaultValue = "") String mc) {
        model.addAttribute("listEmployees", employeeRepository.findByLastNameContaining(mc));
        model.addAttribute("filter1", mc);
        model.addAttribute("listDepartments", departmentRepository.findAll());
        return "employees";
    }
    
    //deuxième filtre = celui avec la date de naissance, on convertie d'abord le String qu'on récup d'HTML
    //avec le requestParam qu'on met dans dn, en LocalDate
    @GetMapping("/employees/filter2")
    public String filterByBirthDate(Model model, @RequestParam(name = "dateNaiss", defaultValue = "") String dn) {
        if (!dn.equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dn, formatter);
            model.addAttribute("listEmployees", employeeRepository.findByBirthDateIs(date));
        } else {
            model.addAttribute("listEmployees", employeeRepository.findAll());
        }
        model.addAttribute("filter2", dn);
        model.addAttribute("listDepartments", departmentRepository.findAll());
        return "employees";
    }
    
    //troisième par dpt
    @GetMapping("/employees/filter3")
    public String filterByDepartment(Model model, @RequestParam(name = "depart", defaultValue = "0") String d) {
        int num = Integer.parseInt(d);
        if (num == 0)
            model.addAttribute("listEmployees", employeeRepository.findAll());
        else {
            Optional<Department> optional = departmentRepository.findById(num);
            if (optional.isPresent()){
                model.addAttribute("listEmployees", optional.get().getEmployees());
            } else {
                throw new RuntimeException("Department not found for id: " + d);
            }
        }
        model.addAttribute("filter3", d);
        model.addAttribute("listDepartments", departmentRepository.findAll());
        return "employees";
    }

    @GetMapping("/employees/new")
    public String showNewEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("listDepartments", departmentRepository.findAll());
        return "new_employee";
    }

    @PostMapping("/employees/saveNew")
    public String saveNewEmployee(@ModelAttribute("employee") @Valid Employee employee, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("listDepartments", departmentRepository.findAll());
            return "new_employee";
        }
        if (employee.getDepartment() == null) {
            model.addAttribute("listDepartments", departmentRepository.findAll());
            model.addAttribute("message", "Please select a department !");
            return "new_employee";
        }
        LocalDate employeeBD = employee.getBirthDate();
        LocalDate today = LocalDate.now();
        if (employeeBD.isAfter(today)) {
            model.addAttribute("listDepartments", departmentRepository.findAll());
            model.addAttribute("message", "Birth date must be in the past !");
            return "new_employee";
        }
        List<Employee> empEmailList = employeeRepository.findByEmail(employee.getEmail());
        if (!empEmailList.isEmpty()) {
            model.addAttribute("listDepartments", departmentRepository.findAll());
            model.addAttribute("message", "Employee with same email address already exists !");
            return "new_employee";
        }
        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/update")
    public String showUpdateEmployeeForm(@RequestParam(name = "id") int id, Model model) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            Employee emp = optional.get();
            model.addAttribute("employee", emp);
            model.addAttribute("birthDateStr", emp.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            throw new RuntimeException("Employee not found for id: " + id);
        }
        model.addAttribute("listDepartments", departmentRepository.findAll());
        return "update_employee";
    }

    @PostMapping("/employees/saveUpdate")
    public String saveUpdateEmployee(@ModelAttribute("employee") @Valid Employee employee, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("listDepartments", departmentRepository.findAll());
            return "update_employee";
        }
        if (employee.getBirthDate().isAfter(LocalDate.now())) {
            model.addAttribute("listDepartments", departmentRepository.findAll());
            model.addAttribute("message", "Birth date must be in the past !");
            return "update_employee";
        }
        List<Employee> empEmailList = employeeRepository.findByEmail(employee.getEmail());
        if (!empEmailList.isEmpty()) {
            Employee premierEmp = empEmailList.get(0);
            //si l'id du premier emp de la liste = l'id de l'employé saisi dans l'input
            if (!premierEmp.getId().equals(employee.getId())) {
                model.addAttribute("listDepartments", departmentRepository.findAll());
                model.addAttribute("message", "Employee with same email address already exists !");
                return "update_employee";
            }
        }
        
        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/delete")
    public String deleteEmployee(@RequestParam(name = "id") int id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}