package com.shop.common.utils.all.mapping;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import java.util.IdentityHashMap;
import java.util.Map;

public class CommonCyclingAvoidingContext {

    private Map<Object, Object> knownInstances = new IdentityHashMap<>();


    @BeforeMapping
    public <T> T getMappedInstance(Object from, @TargetType Class<T> toType) {
        return (T) knownInstances.get(from);
    }

    @BeforeMapping
    public void storeMappedInstance(Object from, @MappingTarget Object to) {
        knownInstances.put(from, to);
    }
}
