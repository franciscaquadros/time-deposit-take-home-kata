package org.ikigaidigital.infrastructure.persistence.jpa.repository;

import org.ikigaidigital.infrastructure.persistence.jpa.entity.TimeDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {

}
