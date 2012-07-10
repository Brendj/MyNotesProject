/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.07.12
 * Time: 12:19
 * To change this template use File | Settings | File Templates.
 */
public class ErrorObject {

    private Class<? extends DistributedObject> clazz;
    private String message;
    private String guid;
    private Integer type;

    public ErrorObject() {}

    public ErrorObject(Class<? extends DistributedObject> clazz, String guid) {
        this.clazz = clazz;
        this.guid = guid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Erorr element ");
        sb.append("{class=").append(clazz.getSimpleName());
        sb.append(", guid='").append(guid).append('\'');
        sb.append("}:");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ErrorObject that = (ErrorObject) o;

        if (!clazz.equals(that.clazz)) {
            return false;
        }
        if (!guid.equals(that.guid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = clazz.hashCode();
        result = 31 * result + guid.hashCode();
        return result;
    }

    public Class<? extends DistributedObject> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends DistributedObject> clazz) {
        this.clazz = clazz;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
