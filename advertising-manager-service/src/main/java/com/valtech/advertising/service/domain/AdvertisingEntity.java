package com.valtech.advertising.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "advertising")
public class AdvertisingEntity {

    @Id
    @Field(value = "advertising_id")
    private String advertisingId;

    @Field(value = "print_cost")
    private Integer printCost;

    @Field(value = "max_cost")
    private Integer maxCost;

    @Field(value = "end_date")
    private Date endDate;

    @Field(value = "title")
    private String title;

    @Field(value = "description")
    private String description;

    @Field(value = "segmentation")
    private SegmentationEntity segmentation;

}