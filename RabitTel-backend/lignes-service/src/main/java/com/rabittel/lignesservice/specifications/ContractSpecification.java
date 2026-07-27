package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.Contract;
import com.rabittel.lignesservice.enums.ContractStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ContractSpecification {

    public static Specification<Contract> hasStatus(ContractStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Contract> startDateFrom(LocalDate startDateFrom) {
        return (root, query, cb) ->
                startDateFrom == null ? null : cb.greaterThanOrEqualTo(root.get("startDate"), startDateFrom);
    }

    public static Specification<Contract> startDateTo(LocalDate startDateTo) {
        return (root, query, cb) ->
                startDateTo == null ? null : cb.lessThanOrEqualTo(root.get("startDate"), startDateTo);
    }

    public static Specification<Contract> endDateFrom(LocalDate endDateFrom) {
        return (root, query, cb) ->
                endDateFrom == null ? null : cb.greaterThanOrEqualTo(root.get("endDate"), endDateFrom);
    }

    public static Specification<Contract> endDateTo(LocalDate endDateTo) {
        return (root, query, cb) ->
                endDateTo == null ? null : cb.lessThanOrEqualTo(root.get("endDate"), endDateTo);
    }

    public static Specification<Contract> expiringBefore(LocalDate thresholdDate) {
        return (root, query, cb) ->
                thresholdDate == null ? null : cb.lessThan(root.get("endDate"), thresholdDate);
    }
}
