package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ikigaidigital.adapter.persistence.jpa.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.persistence.jpa.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.persistence.jpa.repository.TimeDepositJpaRepository;
import org.ikigaidigital.adapter.persistence.jpa.repository.WithdrawalJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/** Runs the same observable API and database contract on H2 and PostgreSQL. */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TimeDepositApiContract {

  private static final LocalDate WITHDRAWAL_DATE = LocalDate.of(2025, 7, 23);

  @Autowired
  TimeDepositJpaRepository deposits;
  @Autowired
  WithdrawalJpaRepository withdrawals;
  @Autowired
  TestRestTemplate http;
  @Autowired
  JdbcTemplate jdbc;

  @BeforeEach
  void seedDeposits() {
    withdrawals.deleteAll();
    deposits.deleteAll();
    var basic = addDeposit("basic", 45);
    var student = addDeposit("student", 60);
    addDeposit("premium", 120);
    withdrawals.save(new WithdrawalEntity(null, basic.getId(), new BigDecimal("100.00"), WITHDRAWAL_DATE));
    withdrawals.save(new WithdrawalEntity(null, basic.getId(), new BigDecimal("20.00"), WITHDRAWAL_DATE));
    withdrawals.save(new WithdrawalEntity(null, student.getId(), new BigDecimal("50.00"), WITHDRAWAL_DATE));
  }

  @Test
  void returnsDepositsWithTheirOwnWithdrawalsRegardlessOfDatabaseOrdering() {
    var response = http.getForEntity("/api/time-deposits", JsonNode.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    Map<String, JsonNode> byPlan = StreamSupport.stream(response.getBody().spliterator(), false)
        .collect(Collectors.toMap(n -> n.path("planType").asText(), Function.identity()));
    assertThat(byPlan).containsOnlyKeys("basic", "student", "premium");
    var basic = byPlan.get("basic");
    assertThat(basic.path("id").asInt()).isPositive();
    assertThat(basic.path("days").asInt()).isEqualTo(45);
    assertThat(basic.path("balance").decimalValue()).isEqualByComparingTo("1000.00");
    assertThat(basic.path("withdrawals").size()).isEqualTo(2);
    assertThat(StreamSupport.stream(basic.path("withdrawals").spliterator(), false)
        .map(n -> n.path("amount").decimalValue()).toList())
        .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
        .containsExactlyInAnyOrder(new BigDecimal("100.00"), new BigDecimal("20.00"));
    for (JsonNode withdrawal : basic.path("withdrawals")) {
      assertThat(withdrawal.path("id").asInt()).isPositive();
      assertThat(withdrawal.path("date").asText()).isEqualTo(WITHDRAWAL_DATE.toString());
    }
    assertThat(byPlan.get("student").path("withdrawals").size()).isEqualTo(1);
    assertThat(byPlan.get("student").path("withdrawals").get(0).path("amount").decimalValue())
        .isEqualByComparingTo("50.00");
    assertThat(byPlan.get("premium").path("withdrawals").size()).isZero();
  }

  @Test
  void persistsInterestAndKeepsWithdrawalsUnchanged() {
    var response = updateBalances();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNullOrEmpty();
    assertBalances("1000.83", "1002.50", "1004.17");
    var body = http.getForEntity("/api/time-deposits", JsonNode.class).getBody();
    assertThat(body).isNotNull();
    var byPlan = StreamSupport.stream(body.spliterator(), false)
        .collect(Collectors.toMap(n -> n.path("planType").asText(), Function.identity()));
    assertThat(byPlan.get("premium").path("balance").decimalValue()).isEqualByComparingTo("1004.17");
    assertThat(withdrawals.findAll()).extracting(WithdrawalEntity::getAmount)
        .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
        .containsExactlyInAnyOrder(new BigDecimal("100.00"), new BigDecimal("20.00"), new BigDecimal("50.00"));
  }

  @Test
  void handlesAnEmptyDatabase() {
    withdrawals.deleteAll();
    deposits.deleteAll();

    assertThat(updateBalances().getStatusCode()).isEqualTo(HttpStatus.OK);
    var response = http.getForEntity("/api/time-deposits", JsonNode.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().isArray()).isTrue();
    assertThat(response.getBody().size()).isZero();
  }

  @Test
  void rejectsWithdrawalsWithoutAnExistingDeposit() {
    assertThatThrownBy(() -> jdbc.update(
        "insert into withdrawals (time_deposit_id, amount, date) values (?, ?, ?)",
        -1, new BigDecimal("10.00"), WITHDRAWAL_DATE))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void rollsBackEveryBalanceWhenOneWriteFails() {
    // Force the premium write to fail after earlier balances have been updated.
    jdbc.execute("alter table time_deposits add constraint test_balance_limit check (balance <= 1003)");
    try {
      assertThat(updateBalances().getStatusCode().is5xxServerError()).isTrue();
      assertBalances("1000.00", "1000.00", "1000.00");
    } finally {
      jdbc.execute("alter table time_deposits drop constraint test_balance_limit");
    }
  }

  @Test
  void serializesConcurrentUpdatesWithoutLosingAnInterestCredit() throws Exception {
    var executor = Executors.newFixedThreadPool(2);
    var start = new CountDownLatch(1);
    try {
      var first = executor.submit(() -> {
        start.await();
        return updateBalances().getStatusCode();
      });
      var second = executor.submit(() -> {
        start.await();
        return updateBalances().getStatusCode();
      });
      start.countDown();
      assertThat(first.get(30, TimeUnit.SECONDS)).isEqualTo(HttpStatus.OK);
      assertThat(second.get(30, TimeUnit.SECONDS)).isEqualTo(HttpStatus.OK);
      assertBalances("1001.66", "1005.01", "1008.35");
    } finally {
      executor.shutdownNow();
    }
  }

  private TimeDepositEntity addDeposit(String plan, int days) {
    return deposits.save(new TimeDepositEntity(null, plan, days, new BigDecimal("1000.00")));
  }

  private org.springframework.http.ResponseEntity<String> updateBalances() {
    return http.exchange("/api/time-deposits/balances", HttpMethod.PUT, HttpEntity.EMPTY, String.class);
  }

  private void assertBalances(String basic, String student, String premium) {
    var byPlan = deposits.findAll().stream()
        .collect(Collectors.toMap(TimeDepositEntity::getPlanType, TimeDepositEntity::getBalance));
    assertThat(byPlan).containsOnlyKeys("basic", "student", "premium");
    assertThat(byPlan.get("basic")).isEqualByComparingTo(basic);
    assertThat(byPlan.get("student")).isEqualByComparingTo(student);
    assertThat(byPlan.get("premium")).isEqualByComparingTo(premium);
  }
}
