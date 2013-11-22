/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class Registry {

    public static final Long THE_ONLY_INSTANCE_ID = 1L;

    private long version;
    private Long idOfRegistry;
    private Long clientRegistryVersion;
    private String smsId;

    protected Registry() {
        // For Hibernate only
    }

    private long getVersion() {
        // For Hibernate only
        return version;
    }

    private void setVersion(long version) {
        // For Hibernate only
        this.version = version;
    }

    public Long getIdOfRegistry() {
        return idOfRegistry;
    }

    private void setIdOfRegistry(Long idOfRegistry) {
        // For Hibernate only
        this.idOfRegistry = idOfRegistry;
    }

    public Long getClientRegistryVersion() {
        return clientRegistryVersion;
    }

    public void setClientRegistryVersion(Long clientRegistryVersion) {
        this.clientRegistryVersion = clientRegistryVersion;
    }

    private String getSmsId() {
        return smsId;
    }

    private void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    @Override
    public String toString() {
        return "ClientRegistry{" + "idOfRegistry=" + idOfRegistry + ", clientRegistryVersion=" + clientRegistryVersion
                + '}';
    }
}