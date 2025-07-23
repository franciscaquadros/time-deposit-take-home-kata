package org.ikigaidigital.domain.port;

import java.util.List;
import org.ikigaidigital.application.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.TimeDeposit;

public interface TimeDepositRepository {
  List<TimeDepositWithWithdrawals> findAllWithWithdrawals();

  List<TimeDeposit> findAll();

  void saveAll(List<TimeDeposit> timeDeposits);
}
