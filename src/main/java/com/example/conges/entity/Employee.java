package com.example.conges.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    @NotEmpty(message = "Must not be empty")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotEmpty(message = "Must not be empty")
    private String lastName;

    @Column(name = "birth_date")
    @NotNull(message = "Must not be empty")
    private LocalDate birthDate;

    @Column(name = "email", unique = true, nullable = false)
    @NotEmpty(message = "Must not be empty")
    @Email(message = "Must be a valid email")
    private String email;

    @Column(name = "nb_days_off")
    @NotNull(message = "Must not be empty")
    private Integer nbDaysOff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL)
    List<VacationRequest> vacationRequests = new ArrayList<>();

    public Employee() {
        super();
    }

    public Employee(String firstName, String lastName, LocalDate birthDate, String email, Integer nbDaysOff, Department department) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.nbDaysOff = nbDaysOff;
        this.department = department;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getNbDaysOff() { return nbDaysOff; }
    public void setNbDaysOff(Integer nbDaysOff) { this.nbDaysOff = nbDaysOff; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public List<VacationRequest> getVacationRequests() { return vacationRequests; }
    public void setVacationRequests(List<VacationRequest> vacationRequests) { this.vacationRequests = vacationRequests; }
}