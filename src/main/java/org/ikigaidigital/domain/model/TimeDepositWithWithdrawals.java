package org.ikigaidigital.domain.model;

import java.util.List;

public class TimeDepositWithWithdrawals {

  private int id;

  private String planType;

  private Double balance;

  private int days;

  private List<Withdrawal> withdrawals;

  public TimeDepositWithWithdrawals(final TimeDeposit timeDeposit, final List<Withdrawal> withdrawals) {
    this.id = timeDeposit.getId();
    this.planType = timeDeposit.getPlanType();
    this.balance = timeDeposit.getBalance();
    this.days = timeDeposit.getDays();
    this.withdrawals = withdrawals;
  }

  public TimeDepositWithWithdrawals() {
  }

  public int getId() {
    return id;
  }

  public String getPlanType() {
    return planType;
  }

  public Double getBalance() {
    return balance;
  }

  public int getDays() {
    return days;
  }

  public List<Withdrawal> getWithdrawals() {
    return withdrawals;
  }
}
