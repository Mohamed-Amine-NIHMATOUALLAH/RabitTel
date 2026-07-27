package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.Line;
import com.rabittel.lignesservice.enums.LineStatus;
import com.rabittel.lignesservice.enums.LineType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class LineSpecification {

    public static <T extends Line> Specification<T> hasLineNumber(String lineNumber) {
        return (root, query, cb) ->
                lineNumber == null ? null : cb.like(cb.lower(root.get("lineNumber")), "%" + lineNumber.toLowerCase() + "%");
    }

    public static <T extends Line> Specification<T> hasLineStatus(LineStatus lineStatus) {
        return (root, query, cb) ->
                lineStatus == null ? null : cb.equal(root.get("lineStatus"), lineStatus);
    }

    public static <T extends Line> Specification<T> hasLineType(LineType lineType) {
        return (root, query, cb) ->
                lineType == null ? null : cb.equal(root.get("lineType"), lineType);
    }

    public static <T extends Line> Specification<T> hasAgencyId(UUID agencyId) {
        return (root, query, cb) ->
                agencyId == null ? null : cb.equal(root.get("agency").get("id"), agencyId);
    }

    public static <T extends Line> Specification<T> hasPlanId(UUID planId) {
        return (root, query, cb) ->
                planId == null ? null : cb.equal(root.get("plan").get("id"), planId);
    }

    public static <T extends Line> Specification<T> hasContractId(UUID contractId) {
        return (root, query, cb) ->
                contractId == null ? null : cb.equal(root.get("contract").get("id"), contractId);
    }
}
