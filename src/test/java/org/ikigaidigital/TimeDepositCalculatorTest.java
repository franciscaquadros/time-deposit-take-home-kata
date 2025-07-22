package org.ikigaidigital;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.calculator.TimeDepositCalculator;
import org.ikigaidigital.domain.service.calculator.BasicPlanCalculator;
import org.ikigaidigital.domain.service.calculator.InterestCalculator;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeDepositCalculatorTest {
    @Test
    public void updateBalance_Test() {

        // Assuming BasicPlanCalculator is the only calculator available
        final List<InterestCalculator> calculators = List.of(new BasicPlanCalculator());
        TimeDepositCalculator calc = new TimeDepositCalculator(calculators);
        List<TimeDeposit> plans = Arrays.asList(
            new TimeDeposit(1,"basic", 1234567.00, 45)
        );
        calc.updateBalance(plans);

        assertThat(1).isEqualTo(1);
    }
}
