package com.valtech.advertising.controller;


import com.valtech.advertising.dto.Advertising;
import com.valtech.advertising.dto.CreateAdvertisingRequest;
import com.valtech.advertising.dto.Gender;
import com.valtech.advertising.service.AdvertisingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "v1/advertisings")
@RestController
@RequestMapping("v1/advertisings")
@RequiredArgsConstructor
public class AdvertisingController {

    private final AdvertisingService service;

    @ApiOperation("Stores an advertising")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 409, message = "Advertising for given request already exists"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Advertising postAdvertising(
            @RequestBody @Validated CreateAdvertisingRequest createRequest) {
        return service.postAdvertising(createRequest);
    }

    @ApiOperation("Gets a advertising")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 404, message = "Advertising for given id not exists"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Advertising> getAdvertisingList(
            @RequestParam(required = false, name = "country") String country,
            @RequestParam(required = false, name = "age") Integer age,
            @RequestParam(required = false, name = "gender") Gender gender) {
        return service.getAdvertiseList(country, age, gender);
    }

}
