package com.shop.common.utils.all.mapping;

import org.mapstruct.MappingTarget;

public interface CommonCrudMapper<M, D> {

    M mapToModel(D dto);
    D mapToDto(M model);
    void updateDto(M from, @MappingTarget D to);
    void updateModel(D from, @MappingTarget M to);

}
