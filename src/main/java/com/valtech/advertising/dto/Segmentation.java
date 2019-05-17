package com.valtech.advertising.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Segmentation {

    private String country;

    @Min(value = 1, message = "age should be greater than 0")
    private Integer age;

    private Gender gender;
}
