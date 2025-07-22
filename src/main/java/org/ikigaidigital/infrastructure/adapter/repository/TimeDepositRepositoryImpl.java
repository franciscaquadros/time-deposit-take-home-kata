package org.ikigaidigital.infrastructure.adapter.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.port.TimeDepositRepository;
import org.ikigaidigital.infrastructure.persistence.jpa.entity.TimeDepositEntity;
import org.ikigaidigital.infrastructure.persistence.jpa.repository.TimeDepositJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class TimeDepositRepositoryImpl implements TimeDepositRepository {

  private final TimeDepositJpaRepository timeDepositJpaRepository;

  public TimeDepositRepositoryImpl(final TimeDepositJpaRepository timeDepositJpaRepository) {
    this.timeDepositJpaRepository = timeDepositJpaRepository;
  }

  @Override
  public List<TimeDeposit> findAll() {
    var entities = timeDepositJpaRepository.findAll();
    return entities.stream()
        .map(this::toDomain).collect(Collectors.toList());
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
}
