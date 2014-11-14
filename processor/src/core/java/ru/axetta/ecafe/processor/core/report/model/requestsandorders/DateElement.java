/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.requestsandorders;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 11.11.14
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */

public class DateElement extends HashMap<Date, Element> {

    @Override
    public Element put(Date key, Element element) {
        Date truncatedKey = CalendarUtils.truncateToDayOfMonth(key);
        if (this.containsKey(truncatedKey)) {
            Element oldElement = this.get(truncatedKey);
            for (Object obj: element.keySet()) {
                State state = (State) obj;
                oldElement.put(state, element.get(state));
            }
            return super.put(truncatedKey, oldElement);
        } else {
            return super.put(truncatedKey, element);
        }
    }
}