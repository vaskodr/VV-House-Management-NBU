package org.manager.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @NotBlank(message = "Company name cannot be blank!")
    @Size(max = 20, message = "Company name should be with up to 20 characters!")
    @Pattern(regexp = "^([A-Z]).*", message = "Company name has to start with Capital letter!")
    @Column(name = "name", nullable = false)
    private String name;

    @Positive
    @Column(name = "income_taxes")
    private BigDecimal incomeFromTaxes;

    public Company(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "company")
    @ToString.Exclude
    private Set<Building> buildings;

    @OneToMany(mappedBy = "company")
    @ToString.Exclude
    private Set<Employee> employees;

//    @OneToMany(mappedBy = "company")
//    @ToString.Exclude
//    private Set<Contract> contracts;

    public Set<Employee> getEmployees() {
        if(employees == null){
           employees = new HashSet<>();
        }
        return employees;
    }
}
