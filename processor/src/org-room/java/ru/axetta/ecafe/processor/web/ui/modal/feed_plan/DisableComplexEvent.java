/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal.feed_plan;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 02.09.13
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class DisableComplexEvent {
    private Map<Integer, Boolean> complexes;
    
    public DisableComplexEvent () {
        complexes = new HashMap<Integer, Boolean>();
    }

    public void addComplex(int complex, boolean disbled) {
        complexes.put(complex, disbled);
    }

    public Map<Integer, Boolean> getComplexes() {
        return complexes;
    }
}
