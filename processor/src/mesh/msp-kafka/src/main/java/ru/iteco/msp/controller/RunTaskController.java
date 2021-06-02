/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.controller;

import ru.iteco.msp.kafka.SupplyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Validated
@RestController
@RequestMapping("/run-task")
public class RunTaskController {
    private final Logger log = LoggerFactory.getLogger(RunTaskController.class);

    private final SupplyService supplyService;

    public RunTaskController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @GetMapping("/supply")
    public ResponseEntity<String> runSupply(
            @NotNull @Positive @RequestParam("beginPeriod") Long beginParam,
            @NotNull @Positive @RequestParam("endPeriod") Long endParam,
            @Max(500000) @RequestParam(value = "sampleSize", defaultValue = "50000") Integer sampleSize){
        Date begin = new Date(beginParam);
        Date end = new Date(endParam);
        supplyService.runFromController(begin, end, sampleSize);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
