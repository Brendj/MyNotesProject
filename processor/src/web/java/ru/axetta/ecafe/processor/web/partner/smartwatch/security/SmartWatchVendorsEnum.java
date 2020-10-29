/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.security;

public enum SmartWatchVendorsEnum {
    Geoplaner(".geoplaner"),
    TwoBears(".twobears");

    String configParamName;

    SmartWatchVendorsEnum(String configParamName){
        this.configParamName = configParamName;
    }
}
