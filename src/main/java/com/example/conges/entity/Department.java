/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.conges.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    @NotEmpty(message = "Must not be empty")
    private String name;

    @Column(name = "address")
    @NotEmpty(message = "Must not be empty")
    private String address;

    @Column(name = "floor")
    @NotNull(message = "Must not be empty")
    @Digits(integer = 2, fraction = 0)
    private Integer floor;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "department", cascade = CascadeType.ALL)
    List<Employee> employees = new ArrayList<>();

    public Department() {
        super();
    }

    public Department(String name, String address, Integer floor) {
        super();
        this.name = name;
        this.address = address;
        this.floor = floor;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public List<Employee> getEmployees(){ 
        return employees; 
    }
    public void setEmployees(List<Employee> employees){ 
        this.employees = employees; 
    }
}