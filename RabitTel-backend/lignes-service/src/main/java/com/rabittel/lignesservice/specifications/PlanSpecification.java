package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.Plan;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PlanSpecification {

    public static Specification<Plan> hasActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<Plan> nameContains(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Plan> priceFrom(BigDecimal priceFrom) {
        return (root, query, cb) ->
                priceFrom == null ? null : cb.greaterThanOrEqualTo(root.get("price"), priceFrom);
    }

    public static Specification<Plan> priceTo(BigDecimal priceTo) {
        return (root, query, cb) ->
                priceTo == null ? null : cb.lessThanOrEqualTo(root.get("price"), priceTo);
    }
}
