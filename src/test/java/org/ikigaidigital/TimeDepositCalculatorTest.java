package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;
import java.util.List;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.calculator.BasicPlanCalculator;
import org.ikigaidigital.domain.service.calculator.PremiumPlanCalculator;
import org.ikigaidigital.domain.service.calculator.StudentPlanCalculator;
import org.ikigaidigital.domain.service.calculator.TimeDepositCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimeDepositCalculatorTest {

  private final TimeDepositCalculator calculator = new TimeDepositCalculator(List.of(
      new BasicPlanCalculator(), new StudentPlanCalculator(), new PremiumPlanCalculator()));

  @ParameterizedTest(name = "{0} at day {1}: balance becomes {2}")
  @CsvSource({
      "basic, 0, 1000.00", "basic, 29, 1000.00", "basic, 30, 1000.00",
      "basic, 31, 1000.83", "basic, 365, 1000.83", "basic, 366, 1000.83",
      "student, 0, 1000.00", "student, 29, 1000.00", "student, 30, 1000.00",
      "student, 31, 1002.50", "student, 364, 1002.50", "student, 365, 1002.50",
      "student, 366, 1000.00", "student, 367, 1000.00",
      "premium, 0, 1000.00", "premium, 30, 1000.00", "premium, 31, 1000.00",
      "premium, 44, 1000.00", "premium, 45, 1000.00", "premium, 46, 1004.17",
      "premium, 366, 1004.17", "unknown, 60, 1000.00",
      "BASIC, 31, 1000.83", "Student, 31, 1002.50", "PREMIUM, 46, 1004.17"
  })
  void appliesPlanRulesAtTheirBoundaries(String plan, int days, double expectedBalance) {
    var deposit = new TimeDeposit(1, plan, 1000.0, days);

    calculator.updateBalance(List.of(deposit));

    assertThat(deposit.getBalance()).isCloseTo(expectedBalance, within(0.000000001));
    assertThat(deposit.getId()).isEqualTo(1);
    assertThat(deposit.getPlanType()).isEqualTo(plan);
    assertThat(deposit.getDays()).isEqualTo(days);
  }

  @ParameterizedTest
  @CsvSource({
      "basic, 6.00, 6.01", "basic, 18.00, 18.02", "basic, 5.99, 5.99",
      "student, 2.00, 2.01", "premium, 1.20, 1.21",
      "basic, 0.00, 0.00", "student, 0.00, 0.00", "premium, 0.00, 0.00"
  })
  void roundsEachMonthlyCreditHalfUp(String plan, double balance, double expectedBalance) {
    var deposit = new TimeDeposit(1, plan, balance, 60);

    calculator.updateBalance(List.of(deposit));

    assertThat(deposit.getBalance()).isCloseTo(expectedBalance, within(0.000000001));
  }

  @Test
  void updatesMixedPlansInPlaceAndCompoundsEachInvocation() {
    var basic = new TimeDeposit(1, "basic", 1000.0, 60);
    var student = new TimeDeposit(2, "student", 1000.0, 60);
    var premium = new TimeDeposit(3, "premium", 1000.0, 60);
    var deposits = new ArrayList<>(List.of(basic, student, premium));

    calculator.updateBalance(deposits);
    calculator.updateBalance(deposits);

    assertThat(deposits).containsExactly(basic, student, premium);
    assertThat(basic.getBalance()).isCloseTo(1001.66, within(0.000000001));
    assertThat(student.getBalance()).isCloseTo(1005.01, within(0.000000001));
    assertThat(premium.getBalance()).isCloseTo(1008.35, within(0.000000001));
  }

  @Test
  void acceptsAnEmptyList() {
    var deposits = new ArrayList<TimeDeposit>();

    calculator.updateBalance(deposits);

    assertThat(deposits).isEmpty();
  }
}
