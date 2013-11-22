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
public class RegistrySMS {

    public static final Long THE_ONLY_INSTANCE_ID = 1L;

    private long version;
    private Long idOfRegistrySMS;
    private String smsId;

    protected RegistrySMS() {
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

    public Long getIdOfRegistrySMS() {
        return idOfRegistrySMS;
    }

    private void setIdOfRegistrySMS(Long idOfRegistry) {
        // For Hibernate only
        this.idOfRegistrySMS = idOfRegistry;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    @Override
    public String toString() {
        return "SMSRegistry{" + "idOfRegistry=" + idOfRegistrySMS + '}';
    }
}