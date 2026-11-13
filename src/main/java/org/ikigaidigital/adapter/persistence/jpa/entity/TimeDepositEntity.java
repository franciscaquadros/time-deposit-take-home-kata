package org.ikigaidigital.adapter.persistence.jpa.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "time_deposits")
public class TimeDepositEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String planType;

  @Column(nullable = false)
  private Integer days;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  public TimeDepositEntity() {
  }

  public TimeDepositEntity(final Integer id, final String planType, final Integer days, final BigDecimal balance) {
    this.id = id;
    this.planType = planType;
    this.days = days;
    this.balance = balance;
  }

  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public String getPlanType() {
    return planType;
  }

  public void setPlanType(final String planType) {
    this.planType = planType;
  }

  public Integer getDays() {
    return days;
  }

  public void setDays(final Integer days) {
    this.days = days;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(final BigDecimal balance) {
    this.balance = balance;
  }
}
