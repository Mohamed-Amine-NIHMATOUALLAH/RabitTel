package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.VPN4GLine;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class VPN4GLineSpecification {

    public static Specification<VPN4GLine> hasEquipment(String equipment) {
        return (root, query, cb) ->
                equipment == null ? null : cb.like(cb.lower(root.get("equipment")), "%" + equipment.toLowerCase() + "%");
    }

    public static Specification<VPN4GLine> hasIpAddress(String ipAddress) {
        return (root, query, cb) ->
                ipAddress == null ? null : cb.like(cb.lower(root.get("ipAddress")), "%" + ipAddress.toLowerCase() + "%");
    }

    public static Specification<VPN4GLine> hasSerialNumber(String serialNumber) {
        return (root, query, cb) ->
                serialNumber == null ? null : cb.like(cb.lower(root.get("serialNumber")), "%" + serialNumber.toLowerCase() + "%");
    }

    public static Specification<VPN4GLine> deliveryDateFrom(LocalDate deliveryDateFrom) {
        return (root, query, cb) ->
                deliveryDateFrom == null ? null : cb.greaterThanOrEqualTo(root.get("deliveryDate"), deliveryDateFrom);
    }

    public static Specification<VPN4GLine> deliveryDateTo(LocalDate deliveryDateTo) {
        return (root, query, cb) ->
                deliveryDateTo == null ? null : cb.lessThanOrEqualTo(root.get("deliveryDate"), deliveryDateTo);
    }
}
