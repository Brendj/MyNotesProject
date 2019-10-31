/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 09.12.14
 * Time: 13:38
 */
@Deprecated
public class DailyFormationRegistries {

    /**
     * Номер реестра по показателям
     */
    private Long idOfRegistries;
    /**
     * Дата начала генерации
     */
    private Date generatedDate;
    /**
     * Контрагент ТСП
     */
    private Contragent idOfContragent;
    /**
     * Имя контрагента ТСП
     */
    private String contragentName;

    private Set<DailyOrgRegistries> dailyOrgRegistriesSet;

    public DailyFormationRegistries() {
    }

    public DailyFormationRegistries(Long idOfRegistries, Date generatedDate, Contragent idOfContragent,
            String contragentName) {
        this.idOfRegistries = idOfRegistries;
        this.generatedDate = generatedDate;
        this.idOfContragent = idOfContragent;
        this.contragentName = contragentName;
    }

    public Long getIdOfRegistries() {
        return idOfRegistries;
    }

    public void setIdOfRegistries(Long idOFDailyFormationRegistries) {
        this.idOfRegistries = idOFDailyFormationRegistries;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    public Contragent getIdOfContragent() {
        return idOfContragent;
    }

    public void setIdOfContragent(Contragent idOfContragent) {
        this.idOfContragent = idOfContragent;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public Set<DailyOrgRegistries> getDailyOrgRegistriesSet() {
        return dailyOrgRegistriesSet;
    }

    public void setDailyOrgRegistriesSet(Set<DailyOrgRegistries> dailyOrgRegistriesSet) {
        this.dailyOrgRegistriesSet = dailyOrgRegistriesSet;
    }
}
