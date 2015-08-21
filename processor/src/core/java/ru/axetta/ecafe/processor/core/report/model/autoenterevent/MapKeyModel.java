/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.autoenterevent;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 19.08.15
 * Time: 13:00
 */

public class MapKeyModel {

    public String date;
    public String clientID;

    public MapKeyModel() {
    }

    public MapKeyModel(String date, String clientID) {
        this.date = date;
        this.clientID = clientID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapKeyModel)) {
            return false;
        }
        MapKeyModel mapKeyModel = (MapKeyModel) o;
        return date == mapKeyModel.date && clientID == mapKeyModel.clientID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(date).
                append(clientID).
                toHashCode();
    }
}
