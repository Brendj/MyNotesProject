/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "cf_registry")
public class ClientRegistry {
    public static final Long THE_ONLY_INSTANCE_ID = 1L;

    @Id
    @Column(name = "idofregistry")
    private Long idOfRegistry;

    @Column(name = "version")
    private Long version;

    @Column(name = "clientregistryversion")
    private Long clientRegistryVersion;

    @Column(name = "smsid")
    private String smsId;

    public Long getIdOfRegistry() {
        return idOfRegistry;
    }

    public void setIdOfRegistry(Long idOfRegistry) {
        this.idOfRegistry = idOfRegistry;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getClientRegistryVersion() {
        return clientRegistryVersion;
    }

    public void setClientRegistryVersion(Long clientRegistryVersion) {
        this.clientRegistryVersion = clientRegistryVersion;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientRegistry that = (ClientRegistry) o;
        return Objects.equals(idOfRegistry, that.idOfRegistry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfRegistry);
    }
}
