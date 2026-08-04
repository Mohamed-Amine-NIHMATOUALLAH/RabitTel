package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.DataLine;
import org.springframework.data.jpa.domain.Specification;

public class DataLineSpecification {

    public static Specification<DataLine> hasBandwidth(String bandwidth) {
        return (root, query, cb) ->
                bandwidth == null ? null : cb.equal(root.get("bandwidth"), bandwidth);
    }

    public static Specification<DataLine> hasIpAddress(String ipAddress) {
        return (root, query, cb) ->
                ipAddress == null ? null : cb.like(cb.lower(root.get("ipAddress")), "%" + ipAddress.toLowerCase() + "%");
    }
}
