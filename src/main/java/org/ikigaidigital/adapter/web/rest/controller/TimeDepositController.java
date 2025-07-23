package org.ikigaidigital.adapter.web.rest.controller;

import java.util.List;
import org.ikigaidigital.application.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.application.usecase.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.usecase.UpdateBalanceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/time-deposits")
public class TimeDepositController {

  private final GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;

  private final UpdateBalanceUseCase updateBalancesUseCase;

  public TimeDepositController(final GetAllTimeDepositsUseCase getAllTimeDepositsUseCase, final UpdateBalanceUseCase updateBalancesUseCase) {
    this.getAllTimeDepositsUseCase = getAllTimeDepositsUseCase;
    this.updateBalancesUseCase = updateBalancesUseCase;
  }

  @GetMapping(value = "", produces = {"application/json"})
  public ResponseEntity<List<TimeDepositWithWithdrawals>> getAllTimeDeposits() {
    List<TimeDepositWithWithdrawals> timeDeposits = getAllTimeDepositsUseCase.execute();
    return ResponseEntity.ok(timeDeposits);
  }

  @PutMapping(value = "/balances", produces = {"application/json"})
  public ResponseEntity<Void> updateBalances() {
    updateBalancesUseCase.execute();
    return ResponseEntity.ok().build();
  }
}
