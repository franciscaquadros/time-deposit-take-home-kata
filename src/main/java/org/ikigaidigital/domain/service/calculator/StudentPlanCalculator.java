package org.ikigaidigital.domain.service.calculator;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.springframework.stereotype.Component;

@Component
public class StudentPlanCalculator implements InterestCalculator {

  public static final String PLAN_TYPE_STUDENT = "student";

  @Override
  public boolean isPlanType(final String planType) {
    return PLAN_TYPE_STUDENT.equalsIgnoreCase(planType);
  }

  @Override
  public double calculate(TimeDeposit deposit) {
    if (deposit.getDays() <= 30 || deposit.getDays() >= 366) return 0;
    return deposit.getBalance() * 0.03 / 12;
  }

}
