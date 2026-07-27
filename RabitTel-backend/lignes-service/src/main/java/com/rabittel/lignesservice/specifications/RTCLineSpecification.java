package com.rabittel.lignesservice.specifications;

import com.rabittel.lignesservice.entities.RTCLine;

import org.springframework.data.jpa.domain.Specification;

public class RTCLineSpecification {

    public static Specification<RTCLine> anything() {
        return (root, query, cb) -> null;
    }
}
