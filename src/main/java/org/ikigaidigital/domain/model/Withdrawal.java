package org.ikigaidigital.domain.model;

import java.time.LocalDate;

public class Withdrawal {

  private int id;

  private Double amount;

  private LocalDate date;

  public Withdrawal(int id, Double amount, LocalDate date) {
    this.id = id;
    this.amount = amount;
    this.date = date;
  }

  public Withdrawal() {
  }

  public int getId() {
    return id;
  }

  public Double getAmount() {
    return amount;
  }

  public LocalDate getDate() {
    return date;
  }
}
