package com.valtech.advertising.service.repository;

import com.valtech.advertising.service.domain.AdvertisingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisingRepository extends MongoRepository<AdvertisingEntity, String> {
}
