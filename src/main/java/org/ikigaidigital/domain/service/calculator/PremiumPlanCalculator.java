package org.ikigaidigital.domain.service.calculator;

import org.ikigaidigital.domain.model.TimeDeposit;

public class PremiumPlanCalculator implements InterestCalculator {

  public static final String PLAN_TYPE_PREMIUM = "premium";

  @Override
  public boolean isPlanType(final String planType) {
    return PLAN_TYPE_PREMIUM.equalsIgnoreCase(planType);
  }

  @Override
  public double calculate(TimeDeposit deposit) {
    if (deposit.getDays() <= 45) return 0;
    return deposit.getBalance() * 0.05 / 12;
  }

}
