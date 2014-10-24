/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 24.10.14
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class ExternalSystemStats {
    protected String name;
    protected String instance;
    protected Date createDate;
    protected Map<Integer, Double> values;

    public ExternalSystemStats(Date createDate, String name, String instance) {
        this.createDate = createDate;
        this.name = name;
        this.instance = instance;
    }

    public String getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Map<Integer, Double> getValues() {
        return values;
    }

    public Double getValue(int typeId) {
        if(values == null) {
            return 0D;
        }
        Double v = values.get(typeId);
        return v == null ? 0D : v;
    }

    public void setValue(int typeId, double value) {
        if(values == null) {
            values = new HashMap<Integer, Double>();
        }
        values.put(typeId, value);
    }
}