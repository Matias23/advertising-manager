package com.valtech.advertising.service;

import com.valtech.advertising.dto.Advertising;
import com.valtech.advertising.dto.CreateAdvertisingRequest;
import com.valtech.advertising.service.domain.AdvertisingEntity;
import com.valtech.advertising.service.mapper.AdvertisingMapper;
import com.valtech.advertising.service.repository.AdvertisingRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdvertisingService {

    @Autowired
    private AdvertisingRepository repository;

    public Advertising postAdvertising(CreateAdvertisingRequest createRequest) {
        AdvertisingEntity advertising = AdvertisingMapper.dtoToEntity(createRequest);
        AdvertisingEntity savedAdvertising = repository.save(advertising);
        return AdvertisingMapper.entityToDto(savedAdvertising);
    }

    public Advertising getAdvertising(String advertisingId) {
        Optional<AdvertisingEntity> advertising =  repository.findById(advertisingId);
        if (!advertising.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "advertising not found");
        }
        return AdvertisingMapper.entityToDto(advertising.get());
    }

}
