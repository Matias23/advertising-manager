package com.valtech.advertising.service.mapper;

import com.valtech.advertising.dto.Advertising;
import com.valtech.advertising.dto.CreateAdvertisingRequest;
import com.valtech.advertising.service.domain.AdvertisingEntity;
import org.modelmapper.ModelMapper;

public interface AdvertisingMapper {

    static Advertising entityToDto(AdvertisingEntity entity) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(entity, Advertising.class);
    }

    static AdvertisingEntity dtoToEntity(CreateAdvertisingRequest request) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(request, AdvertisingEntity.class);
    }

}
