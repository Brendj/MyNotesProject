/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NSIResponseItem {
    private Long version;
    private String id;
    @JsonProperty("entity-id")
    private String entityId;
    @JsonProperty("created-at")
    private String createdAt;
    @JsonProperty("updated-at")
    private String updatedAt;

    public NSIResponseItem(Long version, String id, String entityId, String createdAt, String updatedAt) {
        this.version = version;
        this.id = id;
        this.entityId = entityId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public NSIResponseItem() {

    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAtAsDate() {
        try {
            return javax.xml.bind.DatatypeConverter.parseDateTime(createdAt).getTime();
        } catch (Exception e) {
            return new Date();
        }
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
