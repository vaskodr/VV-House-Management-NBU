package org.manager.entity;

import jakarta.validation.constraints.Positive;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee extends Person {

    @Positive
    @Column(name = "salary")
    private BigDecimal salary;

    public Employee(String name, LocalDate dateOfBirth) {
        super(name, dateOfBirth);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
    private Set<Building> buildings;

//    @OneToMany(mappedBy = "employee")
//    private Set<Contract> contracts;
}
