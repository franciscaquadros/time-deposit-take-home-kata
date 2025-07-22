package org.ikigaidigital.domain;

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
}
