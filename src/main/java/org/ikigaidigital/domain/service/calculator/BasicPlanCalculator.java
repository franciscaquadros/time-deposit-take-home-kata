package org.ikigaidigital.domain.service.calculator;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.springframework.stereotype.Component;

@Component
public class BasicPlanCalculator implements InterestCalculator {

  public static final String PLAN_TYPE_BASIC = "basic";

  @Override
      public boolean isPlanType(final String planType) {
          return PLAN_TYPE_BASIC.equalsIgnoreCase(planType);
      }

      @Override
      public double calculate(final TimeDeposit deposit) {
          if (deposit.getDays() > 30) {
              return deposit.getBalance() * 0.01 / 12;
          }
          return 0;
      }

}
