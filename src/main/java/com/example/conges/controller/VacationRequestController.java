package com.example.conges.controller;

import com.example.conges.entity.Employee;
import com.example.conges.entity.Status;
import com.example.conges.entity.VacationRequest;
import com.example.conges.repository.EmployeeRepository;
import com.example.conges.repository.VacationRequestRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
public class VacationRequestController {

    @Autowired
    private VacationRequestRepository vacationRequestRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/vacations")
    public String listVacations(Model model) {
        model.addAttribute("listVacations", vacationRequestRepository.findAll());
        return "vacations";
    }

    @GetMapping("/vacations/new")
    public String showNewVacationForm(Model model) {
        model.addAttribute("vacation", new VacationRequest());
        model.addAttribute("listEmployees", employeeRepository.findAll());
        return "new_vacation";
    }

    @PostMapping("/vacations/saveNew")
    public String saveNewVacation(@ModelAttribute("vacation") @Valid VacationRequest vacationRequest, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("listEmployees", employeeRepository.findAll());
            return "new_vacation";
        }
        // on vérifie que les dates sont postérieures à ajd'hui
        LocalDate beginDate = vacationRequest.getBeginDate();
        LocalDate endDate = vacationRequest.getEndDate();
        LocalDate now = LocalDate.now(); 
        if (!beginDate.isAfter(now)) {
            model.addAttribute("listEmployees", employeeRepository.findAll());
            model.addAttribute("message", "Begin date must be in the future !");
            return "new_vacation";
        }
        if (!endDate.isAfter(now)) {
            model.addAttribute("listEmployees", employeeRepository.findAll());
            model.addAttribute("message", "End date must be in the future !");
            return "new_vacation";
        }
        // endDate > beginDate
        if (!endDate.isAfter(beginDate)) {
            model.addAttribute("listEmployees", employeeRepository.findAll());
            model.addAttribute("message", "End date must be after begin date !");
            return "new_vacation";
        }
        // on calcule le nombre de jours
        long nbDays = ChronoUnit.DAYS.between(beginDate, endDate);
        vacationRequest.setNbDays((int) nbDays);
        
        // Vérification du quota annuel : on récupère d'abord l'employé qui a demandé les vacances 
        Employee employee = vacationRequest.getEmployee();
        //on vérifie s'il a déjà des vacances acceptées
        List<VacationRequest> existingRequests = vacationRequestRepository.findByEmployeeAndStatus(employee, Status.ACCEPTED);
        
        int totalDaysUsed = 0;
        for (VacationRequest vr : existingRequests) {
            totalDaysUsed += vr.getNbDays();
        }
        
        if (totalDaysUsed + nbDays > employee.getNbDaysOff()) {
            vacationRequest.setStatus(Status.REJECTED);
            vacationRequest.setExplanation("Number of days exceeded");
        } else {
            vacationRequest.setStatus(Status.WAITING);
        }
        vacationRequestRepository.save(vacationRequest);
        return "redirect:/vacations";
    }

    @GetMapping("/vacations/update")
    public String showUpdateVacationForm(@RequestParam(name = "id") int id, Model model) {
        Optional<VacationRequest> optional = vacationRequestRepository.findById(id);
        if (optional.isPresent()) {
            VacationRequest vacation = optional.get();
            model.addAttribute("vacation", vacation);
            model.addAttribute("beginDateStr", vacation.getBeginDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("endDateStr", vacation.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            throw new RuntimeException("Vacation request not found for id: " + id);
        }
        model.addAttribute("listEmployees", employeeRepository.findAll());
        return "update_vacation";
    }

    @PostMapping("/vacations/saveUpdate")
    public String saveUpdateVacation(@ModelAttribute("vacation") @Valid VacationRequest vacationRequest, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("listEmployees", employeeRepository.findAll());
            return "update_vacation";
        }
        vacationRequestRepository.save(vacationRequest);
        return "redirect:/vacations";
    }

    @GetMapping("/vacations/delete")
    public String deleteVacation(@RequestParam(name = "id") int id) {
        vacationRequestRepository.deleteById(id);
        return "redirect:/vacations";
    }
}