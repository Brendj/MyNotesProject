/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 27.08.15
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public enum RNIPVersion {

    RNIP_V115("1.15"),
    RNIP_V116("1.16");

    private final String description;

    private RNIPVersion(String description) {
        this.description = description;
    }

    static public RNIPVersion getType(String pType) {
        for (RNIPVersion type: RNIPVersion.values()) {
            if (type.toString().equals(pType)) {
                return type;
            }
        }
        throw new RuntimeException("unknown RNIP Version in options");
    }

    @Override
    public String toString() {
        return description;
    }
}
