package org.manager.entity;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "month_tax")
@NoArgsConstructor
@AllArgsConstructor
public class MonthTax {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Positive
    @Column(name = "total_amount_to_pay", nullable = false)
    private BigDecimal totalAmountToPay;

    @Positive
    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    private boolean paid;

    @PastOrPresent
    @Column(name = "date_tax_issue", nullable = false)
    private LocalDate dateOfTaxIssue;

    @FutureOrPresent
    @Column(name = "paid_date_time")
    private LocalDateTime paidDateAndTime;


    @ManyToOne
    private Building building;


}
