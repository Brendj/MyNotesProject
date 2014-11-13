/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.requestsandorders;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 11.11.14
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class Complex extends HashMap<String, DateElement> {

    @Override
    public DateElement put(String key, DateElement dateElement) {
        if (this.containsKey(key)) {
            DateElement oldDateElement = this.get(key);
            for (Object obj: dateElement.keySet()) {
                String keyString = (String) obj;
                oldDateElement.put(keyString, dateElement.get(keyString));
            }
            return super.put(key, oldDateElement);
        } else {
            return super.put(key, dateElement);
        }
    }
}