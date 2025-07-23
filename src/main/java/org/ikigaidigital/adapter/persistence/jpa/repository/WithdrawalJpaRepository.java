package org.ikigaidigital.adapter.persistence.jpa.repository;

import java.util.List;
import org.ikigaidigital.adapter.persistence.jpa.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalJpaRepository extends JpaRepository<WithdrawalEntity, Integer> {

  List<WithdrawalEntity> findByTimeDepositId(Integer timeDepositId);
}
