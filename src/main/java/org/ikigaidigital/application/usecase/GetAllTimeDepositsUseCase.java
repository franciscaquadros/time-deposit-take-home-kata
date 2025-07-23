package org.ikigaidigital.application.usecase;

import java.util.List;
import org.ikigaidigital.application.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.port.TimeDepositRepository;
import org.springframework.stereotype.Service;

@Service
public class GetAllTimeDepositsUseCase {
  private final TimeDepositRepository timeDepositRepository;

  public GetAllTimeDepositsUseCase(TimeDepositRepository timeDepositRepository) {
    this.timeDepositRepository = timeDepositRepository;
  }

  public List<TimeDepositWithWithdrawals> execute() {
    return timeDepositRepository.findAllWithWithdrawals();
  }

}
