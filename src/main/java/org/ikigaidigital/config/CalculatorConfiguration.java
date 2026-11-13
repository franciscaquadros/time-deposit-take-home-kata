package org.ikigaidigital.config;

import java.util.List;
import org.ikigaidigital.domain.service.calculator.BasicPlanCalculator;
import org.ikigaidigital.domain.service.calculator.InterestCalculator;
import org.ikigaidigital.domain.service.calculator.PremiumPlanCalculator;
import org.ikigaidigital.domain.service.calculator.StudentPlanCalculator;
import org.ikigaidigital.domain.service.calculator.TimeDepositCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalculatorConfiguration {

  @Bean
  BasicPlanCalculator basicPlanCalculator() {
    return new BasicPlanCalculator();
  }

  @Bean
  StudentPlanCalculator studentPlanCalculator() {
    return new StudentPlanCalculator();
  }

  @Bean
  PremiumPlanCalculator premiumPlanCalculator() {
    return new PremiumPlanCalculator();
  }

  @Bean
  TimeDepositCalculator timeDepositCalculator(List<InterestCalculator> calculators) {
    return new TimeDepositCalculator(calculators);
  }
}
