package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.GSMLine;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class GSMLineSpecification {

    public static Specification<GSMLine> hasServiceFunction(String serviceFunction) {
        return (root, query, cb) ->
                serviceFunction == null ? null : cb.like(cb.lower(root.get("serviceFunction")), "%" + serviceFunction.toLowerCase() + "%");
    }

    public static Specification<GSMLine> hasChipSerialNumber(String chipSerialNumber) {
        return (root, query, cb) ->
                chipSerialNumber == null ? null : cb.like(cb.lower(root.get("chipSerialNumber")), "%" + chipSerialNumber.toLowerCase() + "%");
    }

    public static Specification<GSMLine> chipDeliveryDateFrom(LocalDate chipDeliveryDateFrom) {
        return (root, query, cb) ->
                chipDeliveryDateFrom == null ? null : cb.greaterThanOrEqualTo(root.get("chipDeliveryDate"), chipDeliveryDateFrom);
    }

    public static Specification<GSMLine> chipDeliveryDateTo(LocalDate chipDeliveryDateTo) {
        return (root, query, cb) ->
                chipDeliveryDateTo == null ? null : cb.lessThanOrEqualTo(root.get("chipDeliveryDate"), chipDeliveryDateTo);
    }

    public static Specification<GSMLine> hasPinCode(String pinCode) {
        return (root, query, cb) ->
                pinCode == null ? null : cb.like(cb.lower(root.get("pinCode")), "%" + pinCode.toLowerCase() + "%");
    }

    public static Specification<GSMLine> hasPukCode(String pukCode) {
        return (root, query, cb) ->
                pukCode == null ? null : cb.like(cb.lower(root.get("pukCode")), "%" + pukCode.toLowerCase() + "%");
    }
}
