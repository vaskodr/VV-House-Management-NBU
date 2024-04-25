package org.manager.entity;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "resident")
public class Resident extends Person {

    @Column(name = "use_elevator", nullable = false)
    private boolean useElevator;
    public Resident(String name) {
        super(name);
    }
    public Resident(String name, LocalDate dateOfBirth) {
        super(name, dateOfBirth);
    }
    public Resident(String name, LocalDate dateOfBirth, boolean useElevator) {
        super(name, dateOfBirth);
        this.useElevator = useElevator;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Apartment apartment;
}
