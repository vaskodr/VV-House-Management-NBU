package org.manager.entity;

import jakarta.validation.constraints.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

//@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @NotBlank(message = "Person name cannot be blank!")
    @Size(max = 30, message = "Person name should be with up to 30 characters!")
    @Pattern(regexp = "^([A-Z]).*", message = "Person name should start with capital letter!")
    @Column(name = "name", nullable = false)
    private String name;

    @PastOrPresent(message = "Date of birth cannot be in the future!")
    @Column(name = "birth_date", nullable = false)
    private LocalDate dateOfBirth;

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, LocalDate dateOfBirth) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }
}
