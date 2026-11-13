package org.ikigaidigital.adapter.persistence.jpa.repository;

import org.ikigaidigital.adapter.persistence.jpa.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalJpaRepository extends JpaRepository<WithdrawalEntity, Integer> {
}
