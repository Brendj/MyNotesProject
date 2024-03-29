/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.05.13
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
public enum UnitScale {

    GRAMS("граммы"),
    MILLIMETERS("миллиметры"),
    PORTIONS("порции"),
    UNITS("единицы");

    private String description;
    static HashMap<Integer, UnitScale> unitScaleHashMap = new HashMap<Integer, UnitScale>();
    static HashMap<String, UnitScale> descriptionMap = new HashMap<String, UnitScale>();
    static {
         for (UnitScale unitScale: UnitScale.values()){
             unitScaleHashMap.put(unitScale.ordinal(), unitScale);
         }
    }
    static {
        for (UnitScale unitScale: UnitScale.values()){
            descriptionMap.put(unitScale.toString(), unitScale);
        }
    }

    private UnitScale(String description) {
        this.description = description;
    }

    public static UnitScale fromInteger(int value){
         return unitScaleHashMap.get(value);
    }

    public static UnitScale fromString(String description){
        return descriptionMap.get(description);
    }

    @Override
    public String toString() {
        return description;
    }
}
