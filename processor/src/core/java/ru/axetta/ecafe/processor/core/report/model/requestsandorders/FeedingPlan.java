/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.requestsandorders;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 11.11.14
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
public class FeedingPlan extends HashMap<FeedingPlanType, Complex> {

    @Override
    public Complex put(FeedingPlanType key, Complex complex) {
        if (this.containsKey(key)) {
            Complex oldComplex = this.get(key);
            for (Object obj: complex.keySet()) {
                String keyString = (String) obj;
                oldComplex.put(keyString, complex.get(keyString));
            }
            return super.put(key, oldComplex);
        } else {
            return super.put(key, complex);
        }
    }
}
