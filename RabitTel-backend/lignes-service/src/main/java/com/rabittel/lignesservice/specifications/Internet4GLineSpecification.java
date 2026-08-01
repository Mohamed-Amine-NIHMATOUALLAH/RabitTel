package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.Internet4GLine;
import org.springframework.data.jpa.domain.Specification;

public class Internet4GLineSpecification {

    public static Specification<Internet4GLine> hasServiceFunction(String serviceFunction) {
        return (root, query, cb) ->
                serviceFunction == null ? null : cb.like(cb.lower(root.get("serviceFunction")), "%" + serviceFunction.toLowerCase() + "%");
    }

    public static Specification<Internet4GLine> hasSimSerialNumber(String simSerialNumber) {
        return (root, query, cb) ->
                simSerialNumber == null ? null : cb.like(cb.lower(root.get("simSerialNumber")), "%" + simSerialNumber.toLowerCase() + "%");
    }

    public static Specification<Internet4GLine> hasPinCode(String pinCode) {
        return (root, query, cb) ->
                pinCode == null ? null : cb.like(cb.lower(root.get("pinCode")), "%" + pinCode.toLowerCase() + "%");
    }

    public static Specification<Internet4GLine> hasPukCode(String pukCode) {
        return (root, query, cb) ->
                pukCode == null ? null : cb.like(cb.lower(root.get("pukCode")), "%" + pukCode.toLowerCase() + "%");
    }

    public static Specification<Internet4GLine> hasEquipment(String equipment) {
        return (root, query, cb) ->
                equipment == null ? null : cb.like(cb.lower(root.get("equipment")), "%" + equipment.toLowerCase() + "%");
    }

    public static Specification<Internet4GLine> hasEquipmentSerialNumber(String equipmentSerialNumber) {
        return (root, query, cb) ->
                equipmentSerialNumber == null ? null : cb.like(cb.lower(root.get("equipmentSerialNumber")), "%" + equipmentSerialNumber.toLowerCase() + "%");
    }

    public static Specification<Internet4GLine> hasBandwidth(Long bandwidth) {
        return (root, query, cb) ->
                bandwidth == null ? null : cb.like(cb.lower(root.get("bandwidth")), "%" + bandwidth + "%");
    }
}
