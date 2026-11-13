package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.ikigaidigital.adapter.persistence.jpa.repository.TimeDepositJpaRepository;
import org.ikigaidigital.adapter.persistence.jpa.repository.WithdrawalJpaRepository;
import org.ikigaidigital.util.DataInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("demo")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = "spring.datasource.url=jdbc:h2:mem:demo-test;DB_CLOSE_DELAY=-1")
class DemoDataTest {

  @Autowired
  DataInitializer initializer;
  @Autowired
  TimeDepositJpaRepository deposits;
  @Autowired
  WithdrawalJpaRepository withdrawals;

  @Test
  void seedsAnEmptyDatabaseAndPreservesExistingDataOnSubsequentRuns() {
    assertThat(deposits.count()).isEqualTo(3);
    assertThat(withdrawals.count()).isEqualTo(3);
    var deposit = deposits.findAll().get(0);
    deposit.setBalance(new BigDecimal("777.00"));
    deposits.saveAndFlush(deposit);

    initializer.run();

    assertThat(deposits.count()).isEqualTo(3);
    assertThat(withdrawals.count()).isEqualTo(3);
    assertThat(deposits.findById(deposit.getId()).orElseThrow().getBalance())
        .isEqualByComparingTo("777.00");
  }
}
