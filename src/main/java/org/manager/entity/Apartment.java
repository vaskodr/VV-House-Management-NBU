package org.manager.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apartment")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @NotNull
    @Positive
    private int floor;

    @NotNull
    @Positive
    private int number;

    @NotNull
    @Positive
    private double area;

    @Positive
    private int pets;

    @OneToMany(mappedBy = "apartment")
    @ToString.Exclude
    private Set<Resident> residents;

    @ManyToOne
    private Building building;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "heritage",
            joinColumns = @JoinColumn(name = "apartment_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id")
    )
    @ToString.Exclude
    private Set<Owner> apartmentOwners;

    public Apartment(int floor, int number, double area) {
        this.floor = floor;
        this.number = number;
        this.area = area;
    }

    public Set<Resident> getResidents() {
        if (residents == null) {
            residents = new HashSet<>();
        }
        return residents;
    }

    public Set<Owner> getApartmentOwners() {
        if(apartmentOwners == null){
            apartmentOwners = new HashSet<>();
        }
        return apartmentOwners;
    }

}
