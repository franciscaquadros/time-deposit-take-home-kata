package org.ikigaidigital.domain.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.springframework.stereotype.Component;

@Component
public class TimeDepositCalculator {

    private final List<InterestCalculator> calculators;

    public TimeDepositCalculator(final List<InterestCalculator> calculators) {
        this.calculators = calculators;
    }

    //keep the method signature, this is why the keyword final is not used here
    public void updateBalance(List<TimeDeposit> xs) {

        for (final TimeDeposit deposit : xs) {
            double interest = calculateInterest(deposit);
            double newBalance = deposit.getBalance() + round(interest);
            deposit.setBalance(newBalance);
        }
    }

    private double calculateInterest(final TimeDeposit deposit) {
        return calculators.stream()
            .filter(c -> c.isPlanType(deposit.getPlanType()))
            .findFirst()
            .map(c -> c.calculate(deposit))
            .orElse(0.0);
    }

    private double round(final double value) {
        return BigDecimal.valueOf(value)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
