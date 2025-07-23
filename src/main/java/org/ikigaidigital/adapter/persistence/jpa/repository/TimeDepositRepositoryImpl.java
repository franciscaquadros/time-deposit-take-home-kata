package org.ikigaidigital.adapter.persistence.jpa.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.ikigaidigital.adapter.persistence.jpa.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.persistence.jpa.entity.WithdrawalEntity;
import org.ikigaidigital.application.model.TimeDepositWithWithdrawals;
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
    return timeDepositJpaRepository.findAll().stream()
        .map(entity -> {
          var deposit = toDomain(entity);
          var withdrawals = withdrawalJpaRepository.findByTimeDepositId(deposit.getId()).stream()
              .map(this::mapWithdrawalToDomain)
              .collect(Collectors.toList());
          return new TimeDepositWithWithdrawals(deposit, withdrawals);
        })
        .collect(Collectors.toList());
  }

  @Override
  public List<TimeDeposit> findAll() {
    List<TimeDepositEntity> entities = timeDepositJpaRepository.findAll();
    return entities.stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void saveAll(final List<TimeDeposit> deposits) {
    List<TimeDepositEntity> entities = deposits.stream()
        .map(this::toEntity)
        .collect(Collectors.toList());
    timeDepositJpaRepository.saveAll(entities);
  }

  private TimeDepositEntity toEntity(final TimeDeposit deposit) {
    TimeDepositEntity entity = new TimeDepositEntity();
    entity.setId(deposit.getId());
    entity.setPlanType(deposit.getPlanType());
    entity.setBalance(deposit.getBalance());
    entity.setDays(deposit.getDays());
    return entity;
  }

  private TimeDeposit toDomain(final TimeDepositEntity entity) {
    return new TimeDeposit(
        entity.getId(),
        entity.getPlanType(),
        entity.getBalance(),
        entity.getDays()
    );
  }

  private Withdrawal mapWithdrawalToDomain(final WithdrawalEntity entity) {
    return new Withdrawal(
        entity.getId(),
        entity.getAmount(),
        entity.getDate()
    );
  }
}
