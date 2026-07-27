package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.Agency;
import org.springframework.data.jpa.domain.Specification;

public class AgencySpecification {

    public static Specification<Agency> hasActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<Agency> hasRegion(String region) {
        return (root, query, cb) ->
                region == null ? null : cb.equal(root.get("region"), region);
    }

    public static Specification<Agency> hasDirectorateCode(String directorateCode) {
        return (root, query, cb) ->
                directorateCode == null ? null : cb.equal(root.get("directorateCode"), directorateCode);
    }

    public static Specification<Agency> nameContains(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}