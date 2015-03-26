/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Данные модифицируемые при синхронизации вынесены
 * User: Shamil
 * Date: 26.03.2015
 * Time: 10:39:31
 */
public class ContragentSync implements Serializable {

    private Long idOfContragent;
    private long version;

    private Contragent contragent;

    private String lastRNIPUpdate;

    public ContragentSync() {
    }

    public ContragentSync(Contragent contragent) {
        this.contragent = contragent;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    public void setIdOfContragent(Long idOfContragent) {
        this.idOfContragent = idOfContragent;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public String getLastRNIPUpdate() {
        return lastRNIPUpdate;
    }

    public void setLastRNIPUpdate(String lastRNIPUpdate) {
        this.lastRNIPUpdate = lastRNIPUpdate;
    }
}
