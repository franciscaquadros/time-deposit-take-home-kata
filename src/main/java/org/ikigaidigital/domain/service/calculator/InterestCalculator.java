package org.ikigaidigital.domain.service.calculator;

import org.ikigaidigital.domain.model.TimeDeposit;

public interface InterestCalculator {

  boolean isPlanType(String planType);

  double calculate(TimeDeposit deposit);
}
