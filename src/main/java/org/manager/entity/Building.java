package org.manager.entity;

import jakarta.validation.constraints.*;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "building")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @NotBlank(message = "Building address cannot be blank!")
    @Size(max = 25, message = "Building address should be with up to 25 characters")
    @Pattern(regexp = "^([A-Z]).*", message = "Building address should start with capital letter!")
    private String address;

    @NotNull
    @DecimalMin(value = "5", message = "Building floors should be more than or equal to 5")
    @DecimalMax(value = "40", message = "Building floors should be less than or equal to 40")
    private int floors;

    @Column(name = "total_area")
    private double totalArea;

    @OneToMany(mappedBy = "building")
    @ToString.Exclude
    private Set<Apartment> apartments;

    @ManyToOne()
    private Employee employee;

    @OneToOne(mappedBy = "building")
    private Contract contract;

    @ManyToOne
    private Company company;

    @OneToMany(mappedBy = "building")
    private Set<MonthTax> monthTaxes;

    public Building(String address) {
        this.address = address;
    }

    public Building(String address, Employee employee, Company company) {
        this.address = address;
        this.employee = employee;
        this.company = company;
    }
    public Set<Apartment> getApartments() {
        if (apartments == null) {
            apartments = new HashSet<>();
        }
        return apartments;
    }

    public Set<MonthTax> getMonthTaxes() {
        if(monthTaxes == null){
            monthTaxes = new HashSet<>();
        }
        return monthTaxes;
    }
}
