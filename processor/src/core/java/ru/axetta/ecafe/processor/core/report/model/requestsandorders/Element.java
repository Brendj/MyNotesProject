/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.requestsandorders;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 11.11.14
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class Element extends HashMap<State, Long> {

    @Override
    public Long put(State key, Long value) {
        if (value != null) {
            Long newValue = value + (this.containsKey(key) ? this.get(key) : 0L);
            return super.put(key, newValue);
        } else {
            return super.put(key, this.containsKey(key) ? this.get(key) : 0L);
        }
    }
}