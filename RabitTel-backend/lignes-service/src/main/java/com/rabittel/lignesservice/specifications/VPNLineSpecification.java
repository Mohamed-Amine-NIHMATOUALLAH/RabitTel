package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.VPNLine;
import org.springframework.data.jpa.domain.Specification;

public class VPNLineSpecification {

    public static Specification<VPNLine> hasBandwidth(String bandwidth) {
        return (root, query, cb) ->
                bandwidth == null ? null : cb.like(cb.lower(root.get("bandwidth")), "%" + bandwidth.toLowerCase() + "%");
    }

    public static Specification<VPNLine> hasIpAddress(String ipAddress) {
        return (root, query, cb) ->
                ipAddress == null ? null : cb.like(cb.lower(root.get("ipAddress")), "%" + ipAddress.toLowerCase() + "%");
    }
}
