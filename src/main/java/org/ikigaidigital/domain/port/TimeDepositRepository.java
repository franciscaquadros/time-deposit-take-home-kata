package org.ikigaidigital.domain.port;

import java.util.List;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.TimeDeposit;

public interface TimeDepositRepository {
  List<TimeDepositWithWithdrawals> findAllWithWithdrawals();

  List<TimeDeposit> findAllForUpdate();

  void saveAll(List<TimeDeposit> timeDeposits);
}
