package com.valtech.advertising.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAdvertisingRequest {

    @NotNull(message = "print cost is required")
    @Min(value = 1, message = "print cost should be greater than 0")
    private Integer printCost;

    @NotNull(message = "max cost is required")
    @Min(value = 1, message = "max cost should be greater than 0")
    private Integer maxCost;

    @NotNull(message = "endDate is required")
    private Date endDate;

    @AssertTrue(message = "endDate should be later than today")
    public boolean isValidEndDate() {
        return endDate == null || endDate.after(new Date());
    }

    @NotNull(message = "title is required")
    private String title;

    @NotNull(message = "description is required")
    private String description;

    @Valid
    private Segmentation segmentation;

}