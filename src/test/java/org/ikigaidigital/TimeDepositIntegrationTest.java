package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.ikigaidigital.adapter.persistence.jpa.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.persistence.jpa.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.persistence.jpa.repository.TimeDepositJpaRepository;
import org.ikigaidigital.adapter.persistence.jpa.repository.WithdrawalJpaRepository;
import org.ikigaidigital.application.model.TimeDepositWithWithdrawals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimeDepositIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5")
      .withDatabaseName("testdb")
      .withUsername("user")
      .withPassword("pass");

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private TimeDepositJpaRepository timeDepositJpaRepository;

  @Autowired
  private WithdrawalJpaRepository withdrawalJpaRepository;

  @Autowired
  private TestRestTemplate restTemplate;

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    timeDepositJpaRepository.deleteAll();
    withdrawalJpaRepository.deleteAll();
    var basicDeposit = timeDepositJpaRepository.save(new TimeDepositEntity(
        null, "basic", 45, 1000.0
    ));
    var studentDeposit = timeDepositJpaRepository.save(new TimeDepositEntity(
        null, "student", 60, 1000.0
    ));
    var premiumDeposit = timeDepositJpaRepository.save(new TimeDepositEntity(
        null, "premium", 120, 1000.0
    ));
    // Add withdrawals for each deposit
    withdrawalJpaRepository.save(new WithdrawalEntity(null, basicDeposit.getId(), 100.0, LocalDate.now()));
    withdrawalJpaRepository.save(new WithdrawalEntity(null, studentDeposit.getId(), 50.0, LocalDate.now()));
    withdrawalJpaRepository.save(new WithdrawalEntity(null, premiumDeposit.getId(), 25.0, LocalDate.now()));
  }

  @Test
  void testUpdateBalance() {
    restTemplate.put("http://localhost:" + port + "/api/time-deposits/balances", null);

    var deposits = timeDepositJpaRepository.findAll();
    assertThat(deposits).hasSize(3);

    for(TimeDepositEntity timeDeposit : deposits) {
      switch (timeDeposit.getPlanType()) {
        case "basic" -> assertThat(timeDeposit.getBalance()).isEqualTo(1000.83);
        case "student" -> assertThat(timeDeposit.getBalance()).isEqualTo(1002.50);
        case "premium" -> assertThat(timeDeposit.getBalance()).isEqualTo(1004.17);
      }
    }
  }

  @Test
  void testGetAllDeposits() {
    var response = restTemplate.getForEntity("http://localhost:" + port + "/api/time-deposits", TimeDepositWithWithdrawals[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    var deposits = response.getBody();
    assertThat(deposits).isNotNull().hasSize(3);

    final var basicDeposit = deposits[0];
    assertThat(basicDeposit.getPlanType()).isEqualTo("basic");
    assertThat(basicDeposit.getDays()).isEqualTo(45);
    assertThat(basicDeposit.getBalance()).isEqualTo(1000.0);
    assertThat(basicDeposit.getWithdrawals()).hasSize(1);
    assertThat(basicDeposit.getWithdrawals().get(0).getAmount()).isEqualTo(100.0);

    final var studentDeposit = deposits[1];
    assertThat(studentDeposit.getPlanType()).isEqualTo("student");
    assertThat(studentDeposit.getDays()).isEqualTo(60);
    assertThat(studentDeposit.getBalance()).isEqualTo(1000.0);
    assertThat(studentDeposit.getWithdrawals()).hasSize(1);
    assertThat(studentDeposit.getWithdrawals().get(0).getAmount()).isEqualTo(50.0);

    final var premiumDeposit = deposits[2];
    assertThat(premiumDeposit.getPlanType()).isEqualTo("premium");
    assertThat(premiumDeposit.getDays()).isEqualTo(120);
    assertThat(premiumDeposit.getBalance()).isEqualTo(1000.0);
    assertThat(premiumDeposit.getWithdrawals()).hasSize(1);
    assertThat(premiumDeposit.getWithdrawals().get(0).getAmount()).isEqualTo(25.0);
  }

  @Test
  void testGetAllDepositsAfterUpdate() {
    restTemplate.put("http://localhost:" + port + "/api/time-deposits/balances", null);
    var response = restTemplate.getForEntity("http://localhost:" + port + "/api/time-deposits", TimeDepositWithWithdrawals[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    var deposits = response.getBody();
    assertThat(deposits).isNotNull().hasSize(3);

    final var basicDeposit = deposits[0];
    assertThat(basicDeposit.getPlanType()).isEqualTo("basic");
    assertThat(basicDeposit.getDays()).isEqualTo(45);
    assertThat(basicDeposit.getBalance()).isEqualTo(1000.83);
    assertThat(basicDeposit.getWithdrawals()).hasSize(1);
    assertThat(basicDeposit.getWithdrawals().get(0).getAmount()).isEqualTo(100.0);


    final var studentDeposit = deposits[1];
    assertThat(studentDeposit.getPlanType()).isEqualTo("student");
    assertThat(studentDeposit.getDays()).isEqualTo(60);
    assertThat(studentDeposit.getBalance()).isEqualTo(1002.50);
    assertThat(studentDeposit.getWithdrawals()).hasSize(1);
    assertThat(studentDeposit.getWithdrawals().get(0).getAmount()).isEqualTo(50.0);

    final var premiumDeposit = deposits[2];
    assertThat(premiumDeposit.getPlanType()).isEqualTo("premium");
    assertThat(premiumDeposit.getDays()).isEqualTo(120);
    assertThat(premiumDeposit.getBalance()).isEqualTo(1004.17);
    assertThat(premiumDeposit.getWithdrawals()).hasSize(1);
    assertThat(premiumDeposit.getWithdrawals().get(0).getAmount()).isEqualTo(25.0);
  }
}
