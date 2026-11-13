package org.ikigaidigital.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.ikigaidigital.adapter.persistence.jpa.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.persistence.jpa.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.persistence.jpa.repository.TimeDepositJpaRepository;
import org.ikigaidigital.adapter.persistence.jpa.repository.WithdrawalJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Optional sample data for trying the two endpoints locally. */
@Component
@Profile("demo")
public class DataInitializer implements CommandLineRunner {
  private final TimeDepositJpaRepository depositRepo;
  private final WithdrawalJpaRepository withdrawalRepo;

  public DataInitializer(TimeDepositJpaRepository depositRepo, WithdrawalJpaRepository withdrawalRepo) {
    this.depositRepo = depositRepo;
    this.withdrawalRepo = withdrawalRepo;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (depositRepo.count() > 0) {
      return;
    }
    addDeposit("basic", 45, "100.00");
    addDeposit("student", 60, "50.00");
    addDeposit("premium", 120, "25.00");
  }

  private void addDeposit(String planType, int days, String withdrawalAmount) {
    var deposit = depositRepo.save(
        new TimeDepositEntity(null, planType, days, new BigDecimal("1000.00")));
    withdrawalRepo.save(new WithdrawalEntity(null, deposit.getId(),
        new BigDecimal(withdrawalAmount), LocalDate.of(2025, 7, 23)));
  }
}
