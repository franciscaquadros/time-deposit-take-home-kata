package org.ikigaidigital.application.usecase;

import java.util.List;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.port.TimeDepositRepository;
import org.ikigaidigital.domain.service.calculator.TimeDepositCalculator;
import org.springframework.stereotype.Service;

@Service
public class UpdateBalanceUseCase {

  private final TimeDepositRepository timeDepositRepository;
  private final TimeDepositCalculator calculator;

  public UpdateBalanceUseCase(final TimeDepositRepository timeDepositRepository, final TimeDepositCalculator calculator) {
    this.timeDepositRepository = timeDepositRepository;
    this.calculator = calculator;
  }

  public void execute() {
    List<TimeDeposit> deposits = timeDepositRepository.findAll();
    calculator.updateBalance(deposits);
    timeDepositRepository.saveAll(deposits);
  }
}
