package org.ikigaidigital.adapter.persistence.jpa.repository;

import org.ikigaidigital.adapter.persistence.jpa.entity.TimeDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {

}
