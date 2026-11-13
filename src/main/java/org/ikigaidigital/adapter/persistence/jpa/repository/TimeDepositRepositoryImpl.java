package org.ikigaidigital.adapter.persistence.jpa.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.ikigaidigital.adapter.persistence.jpa.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.persistence.jpa.entity.WithdrawalEntity;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.domain.port.TimeDepositRepository;
import org.springframework.stereotype.Component;

@Component
public class TimeDepositRepositoryImpl implements TimeDepositRepository {

  private final TimeDepositJpaRepository timeDepositJpaRepository;

  private final WithdrawalJpaRepository withdrawalJpaRepository;

  public TimeDepositRepositoryImpl(final TimeDepositJpaRepository timeDepositJpaRepository, final WithdrawalJpaRepository withdrawalJpaRepository) {
    this.timeDepositJpaRepository = timeDepositJpaRepository;
    this.withdrawalJpaRepository = withdrawalJpaRepository;
  }

  @Override
  public List<TimeDepositWithWithdrawals> findAllWithWithdrawals() {
    Map<Integer, List<Withdrawal>> withdrawalsByDeposit = withdrawalJpaRepository.findAll().stream()
        .collect(Collectors.groupingBy(WithdrawalEntity::getTimeDepositId,
            Collectors.mapping(this::mapWithdrawalToDomain, Collectors.toList())));
    return timeDepositJpaRepository.findAll().stream()
        .map(entity -> {
          var deposit = toDomain(entity);
          var withdrawals = withdrawalsByDeposit.getOrDefault(deposit.getId(), List.of());
          return new TimeDepositWithWithdrawals(deposit, withdrawals);
        })
        .toList();
  }

  @Override
  public List<TimeDeposit> findAllForUpdate() {
    List<TimeDepositEntity> entities = timeDepositJpaRepository.findAllForUpdate();
    return entities.stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public void saveAll(final List<TimeDeposit> deposits) {
    List<TimeDepositEntity> entities = deposits.stream()
        .map(this::toEntity)
        .toList();
    timeDepositJpaRepository.saveAll(entities);
  }

  private TimeDepositEntity toEntity(final TimeDeposit deposit) {
    TimeDepositEntity entity = new TimeDepositEntity();
    entity.setId(deposit.getId());
    entity.setPlanType(deposit.getPlanType());
    entity.setBalance(BigDecimal.valueOf(deposit.getBalance()));
    entity.setDays(deposit.getDays());
    return entity;
  }

  private TimeDeposit toDomain(final TimeDepositEntity entity) {
    return new TimeDeposit(
        entity.getId(),
        entity.getPlanType(),
        entity.getBalance().doubleValue(),
        entity.getDays()
    );
  }

  private Withdrawal mapWithdrawalToDomain(final WithdrawalEntity entity) {
    return new Withdrawal(
        entity.getId(),
        entity.getAmount().doubleValue(),
        entity.getDate()
    );
  }
}
