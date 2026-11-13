package org.ikigaidigital.application.usecase;

import java.util.List;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.port.TimeDepositRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllTimeDepositsUseCase {
  private final TimeDepositRepository timeDepositRepository;

  public GetAllTimeDepositsUseCase(TimeDepositRepository timeDepositRepository) {
    this.timeDepositRepository = timeDepositRepository;
  }

  @Transactional(readOnly = true)
  public List<TimeDepositWithWithdrawals> execute() {
    return timeDepositRepository.findAllWithWithdrawals();
  }

}
