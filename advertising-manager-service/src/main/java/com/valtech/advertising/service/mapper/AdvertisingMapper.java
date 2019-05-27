package com.valtech.advertising.service.mapper;

import com.valtech.advertising.dto.Advertising;
import com.valtech.advertising.dto.CreateAdvertisingRequest;
import com.valtech.advertising.dto.Segmentation;
import com.valtech.advertising.service.domain.AdvertisingEntity;
import com.valtech.advertising.service.domain.SegmentationEntity;
import org.modelmapper.ModelMapper;

public interface AdvertisingMapper {

    static Advertising entityToDto(AdvertisingEntity entity) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(entity, Advertising.class);
    }

    static SegmentationEntity segmentationToEntity(Segmentation segmentation) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(segmentation, SegmentationEntity.class);
    }

    static AdvertisingEntity requestToEntity(CreateAdvertisingRequest request) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(request, AdvertisingEntity.class);
    }

}
