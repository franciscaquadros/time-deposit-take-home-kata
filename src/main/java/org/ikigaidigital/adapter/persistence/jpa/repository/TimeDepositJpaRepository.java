package org.ikigaidigital.adapter.persistence.jpa.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import org.ikigaidigital.adapter.persistence.jpa.entity.TimeDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select deposit from TimeDepositEntity deposit order by deposit.id")
  List<TimeDepositEntity> findAllForUpdate();
}
