package com.valtech.advertising.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advertising {

    private String advertisingId;

    private Integer printCost;

    private Integer maxCost;

    private Date endDate;

    private String title;

    private String description;

}
