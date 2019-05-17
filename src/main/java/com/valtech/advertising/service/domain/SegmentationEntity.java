package com.valtech.advertising.service.domain;

import com.valtech.advertising.dto.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SegmentationEntity {

    @Field(value = "country")
    private String country;

    @Field(value = "age")
    private Integer age;

    @Field(value = "gender")
    private Gender gender;
}