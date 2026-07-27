package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.FTTHLine;
import org.springframework.data.jpa.domain.Specification;

public class FTTHLineSpecification {

    public static Specification<FTTHLine> hasFixedLineNumber(String fixedLineNumber) {
        return (root, query, cb) ->
                fixedLineNumber == null ? null : cb.like(cb.lower(root.get("fixedLineNumber")), "%" + fixedLineNumber.toLowerCase() + "%");
    }

    public static Specification<FTTHLine> hasRouterBrand(String routerBrand) {
        return (root, query, cb) ->
                routerBrand == null ? null : cb.like(cb.lower(root.get("routerBrand")), "%" + routerBrand.toLowerCase() + "%");
    }

    public static Specification<FTTHLine> hasBandwidth(String bandwidth) {
        return (root, query, cb) ->
                bandwidth == null ? null : cb.like(cb.lower(root.get("bandwidth")), "%" + bandwidth.toLowerCase() + "%");
    }
}
