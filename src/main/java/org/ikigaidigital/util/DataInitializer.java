package org.ikigaidigital.util;

import java.time.LocalDate;
import org.ikigaidigital.adapter.persistence.jpa.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.persistence.jpa.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.persistence.jpa.repository.TimeDepositJpaRepository;
import org.ikigaidigital.adapter.persistence.jpa.repository.WithdrawalJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
  private final TimeDepositJpaRepository depositRepo;
  private final WithdrawalJpaRepository withdrawalRepo;

  public DataInitializer(TimeDepositJpaRepository depositRepo, WithdrawalJpaRepository withdrawalRepo) {
    this.depositRepo = depositRepo;
    this.withdrawalRepo = withdrawalRepo;
  }

  @Override
  public void run(String... args) {
    depositRepo.deleteAll();
    withdrawalRepo.deleteAll();

    // Create basic deposit and withdrawal
    var basic = depositRepo.save(new TimeDepositEntity(null, "basic", 45, 1000.0));
    withdrawalRepo.save(new WithdrawalEntity(null, basic.getId(), 100.0, LocalDate.now()));

    // Create student deposit and withdrawal
    var student = depositRepo.save(new TimeDepositEntity(null, "student", 60, 1000.0));
    withdrawalRepo.save(new WithdrawalEntity(null, student.getId(), 50.0, LocalDate.now()));

    // Create premium deposit and withdrawal
    var premium = depositRepo.save(new TimeDepositEntity(null, "premium", 120, 1000.0));
    withdrawalRepo.save(new WithdrawalEntity(null, premium.getId(), 25.0, LocalDate.now()));

  }
}
