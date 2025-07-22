package org.ikigaidigital.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
public class WithdrawalEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "time_deposit_id", nullable = false)
  private Integer timeDepositId;

  @Column(nullable = false)
  private Double amount;

  @Column(nullable = false)
  private LocalDate date;

  public WithdrawalEntity() {
  }

  public WithdrawalEntity(final Integer id, final Integer timeDepositId, final Double amount, final LocalDate date) {
    this.id = id;
    this.timeDepositId = timeDepositId;
    this.amount = amount;
    this.date = date;
  }

  public Integer getId() { return id; }
  public void setId(final Integer id) { this.id = id; }

  public Integer getTimeDepositId() { return timeDepositId; }
  public void setTimeDepositId(final Integer timeDepositId) { this.timeDepositId = timeDepositId; }

  public Double getAmount() { return amount; }
  public void setAmount(final Double amount) { this.amount = amount; }

  public LocalDate getDate() { return date; }
  public void setDate(final LocalDate date) { this.date = date; }
}
