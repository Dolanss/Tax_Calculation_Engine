package com.taxengine.domain.repository;

import com.taxengine.domain.entity.ServiceTaxRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceTaxRuleRepository extends JpaRepository<ServiceTaxRule, Long> {

    Optional<ServiceTaxRule> findByMunicipalityCodeAndServiceCodeAndActiveTrue(
            String municipalityCode, String serviceCode);
}
