package org.manager.entity;

import jakarta.validation.constraints.*;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString

@NoArgsConstructor

@Entity
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @PastOrPresent
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Future
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Positive
    @Column(name = "base_tax", nullable = false)
    private BigDecimal baseTax;

    @Positive
    @Column(name = "tax_per_resident", nullable = false)
    private BigDecimal taxPerResident;

    @Positive
    @Column(name = "tax_per_pets", nullable = false)
    private BigDecimal taxPerPets;

    @Positive
    @DecimalMin(value = "0.5", message = "Company percentage from tax should be more than or equal to 50%")
    @DecimalMax(value = "0.9", message = "Company percentage from tax should be less than or equal to 90%")
    private BigDecimal percentageForCompany;

    @Positive
    @DecimalMin(value = "0.1", message = "Employee percentage from tax should be more than or equal to 10%")
    @DecimalMax(value = "0.5", message = "Employee percentage from tax should be less than or equal to 50%")
    private BigDecimal percentageForEmployee;


//    @ManyToOne
//    private Company company;

    @OneToOne
    private Building building;

//    @ManyToOne
//    private Employee employee;


}
