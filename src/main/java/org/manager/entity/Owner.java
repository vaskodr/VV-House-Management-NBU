package org.manager.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "apartment_owner")
public class Owner extends Person {
    public Owner(String name, LocalDate dateOfBirth) {
        super(name, dateOfBirth);
    }

    @ManyToMany(mappedBy = "apartmentOwners")
    private Set<Apartment> ownedApartments;

    public Set<Apartment> getOwnedApartments() {
        if(ownedApartments == null){
            ownedApartments = new HashSet<>();
        }
        return ownedApartments;
    }
}