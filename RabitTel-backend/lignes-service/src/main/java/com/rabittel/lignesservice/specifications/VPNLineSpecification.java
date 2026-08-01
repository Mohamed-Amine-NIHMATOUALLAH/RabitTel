package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.VPNLine;
import org.springframework.data.jpa.domain.Specification;

public class VPNLineSpecification {

    public static Specification<VPNLine> hasBandwidth(Long bandwidth) {
        return (root, query, cb) ->
                bandwidth == null ? null : cb.like(cb.lower(root.get("bandwidth")), "%" + bandwidth + "%");
    }

    public static Specification<VPNLine> hasIpAddress(String ipAddress) {
        return (root, query, cb) ->
                ipAddress == null ? null : cb.like(cb.lower(root.get("ipAddress")), "%" + ipAddress.toLowerCase() + "%");
    }
}
