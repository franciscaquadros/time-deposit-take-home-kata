package org.ikigaidigital.domain.port;

import java.util.List;
import org.ikigaidigital.domain.model.TimeDeposit;

public interface TimeDepositRepository {
  List<TimeDeposit> findAll();
  void saveAll(List<TimeDeposit> timeDeposits);
}
