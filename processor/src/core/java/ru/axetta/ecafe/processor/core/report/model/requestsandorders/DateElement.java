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

public class DateElement extends HashMap<String, Element> {

    @Override
    public Element put(String key, Element element) {
        if (this.containsKey(key)) {
            Element oldElement = this.get(key);
            for (Object obj: element.keySet()) {
                State state = (State) obj;
                oldElement.put(state, element.get(state));
            }
            return super.put(key, oldElement);
        } else {
            return super.put(key, element);
        }
    }
}