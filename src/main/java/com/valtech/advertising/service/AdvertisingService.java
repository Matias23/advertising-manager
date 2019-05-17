package com.valtech.advertising.service;

import com.valtech.advertising.dto.Advertising;
import com.valtech.advertising.dto.CreateAdvertisingRequest;
import com.valtech.advertising.dto.Gender;
import com.valtech.advertising.service.domain.AdvertisingEntity;
import com.valtech.advertising.service.mapper.AdvertisingMapper;
import com.valtech.advertising.service.repository.AdvertisingRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class AdvertisingService {

    @Autowired
    private AdvertisingRepository repository;

    public Advertising postAdvertising(CreateAdvertisingRequest createRequest) {
        AdvertisingEntity advertising = AdvertisingMapper.requestToEntity(createRequest);
        AdvertisingEntity savedAdvertising = repository.save(advertising);
        return AdvertisingMapper.entityToDto(savedAdvertising);
    }


    public List<Advertising> getAdvertiseList(String country, Integer age, Gender gender) {
        Stream<AdvertisingEntity> advertisingList = repository.findAll().stream();
        //apply segmentation filter
        advertisingList = advertisingList
            .filter(country != null ?
                    ad -> ad.getSegmentation() != null && country.equals(ad.getSegmentation().getCountry()) : ad -> true)
            .filter(age != null ?
                    ad -> ad.getSegmentation() != null && age.equals(ad.getSegmentation().getAge()) : ad -> true)
            .filter(gender != null ?
                    ad -> ad.getSegmentation() != null && gender.equals(ad.getSegmentation().getGender()) : ad -> true);
        //apply date filter
        Stream<AdvertisingEntity> oldFilter  = advertisingList
                .filter(ad -> ad.getEndDate().after(new Date()));
        //apply affordability filter
        Stream<AdvertisingEntity> affordabilityFilter  = oldFilter
                .filter(ad -> ad.getMaxCost() > ad.getPrintCost() || (ad.getMaxCost().equals(ad.getPrintCost()) && isToday(ad.getEndDate())));

        //retrieve a list with 3 ads
        return getChosenAds(affordabilityFilter.collect(Collectors.toList()));
    }

    private boolean isToday(Date adDate) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(adDate);
        cal2.setTime(new Date());
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    private AdvertisingEntity weightedChoice(List<AdvertisingEntity> ads) {
        if (ads == null || ads.isEmpty()) {
            return null;
        }
        Integer completeWeight = 0;
        for (AdvertisingEntity ad : ads)
            completeWeight += ad.getPrintCost();
        double pick = Math.random() * completeWeight;
        double countWeight = 0;
        for (AdvertisingEntity ad : ads) {
            countWeight += ad.getPrintCost();
            if (countWeight >= pick)
                return ad;
        }
        return null;
    }

    private List<Advertising> getChosenAds(List<AdvertisingEntity> ads) {
        List<Advertising> chosenAds = new ArrayList<>();
        AdvertisingEntity nextChoice = weightedChoice(ads);
        while (chosenAds.size() < 3 && nextChoice != null) {
            ads.remove(nextChoice);
            nextChoice.setMaxCost(nextChoice.getMaxCost() - nextChoice.getPrintCost());
            repository.save(nextChoice);
            chosenAds.add(AdvertisingMapper.entityToDto(nextChoice));
            nextChoice = weightedChoice(ads);
        }
        if (chosenAds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no suitable advertisings found");
        }
        return chosenAds;
    }

}
