/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 10:03
 */

public class CompositeIdOfMigrant implements Serializable{
    private Long idOfRequest; // Идентификатор запроса на посещение
    private Long idOfOrgRegistry; // Идентификатор ОО регистрации

    public CompositeIdOfMigrant() {
    }

    public CompositeIdOfMigrant(Long idOfRequest, Long idOfOrgRegistry) {
        this.idOfRequest = idOfRequest;
        this.idOfOrgRegistry = idOfOrgRegistry;
    }

    public Long getIdOfRequest() {
        return idOfRequest;
    }

    public void setIdOfRequest(Long idOfRequest) {
        this.idOfRequest = idOfRequest;
    }

    public Long getIdOfOrgRegistry() {
        return idOfOrgRegistry;
    }

    public void setIdOfOrgRegistry(Long idOfOrgRegistry) {
        this.idOfOrgRegistry = idOfOrgRegistry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfMigrant)) {
            return false;
        }
        final CompositeIdOfMigrant that = (CompositeIdOfMigrant) o;
        return getIdOfRequest().equals(that.getIdOfRequest()) && idOfOrgRegistry.equals(that.getIdOfOrgRegistry());
    }

    @Override
    public int hashCode() {
        int result = idOfRequest.hashCode();
        result = 31 * result + idOfOrgRegistry.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfMigrant{" +
                "idOfRequest=" + idOfRequest +
                ", idOfOrgRegistry=" + idOfOrgRegistry +
                '}';
    }
}
