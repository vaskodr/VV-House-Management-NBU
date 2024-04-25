package org.manager.dto;

import lombok.*;

import java.math.BigDecimal;

public class MonthTaxDTO{
    private long id;
    private BigDecimal totalAmountToPay;
    private boolean paid;

    public MonthTaxDTO(long id, BigDecimal totalAmountToPay, boolean paid) {
        this.id = id;
        this.totalAmountToPay = totalAmountToPay;
        this.paid = paid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmountToPay() {
        return totalAmountToPay;
    }

    public void setTotalAmountToPay(BigDecimal totalAmountToPay) {
        this.totalAmountToPay = totalAmountToPay;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
