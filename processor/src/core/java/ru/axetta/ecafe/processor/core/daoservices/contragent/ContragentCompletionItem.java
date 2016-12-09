/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.01.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ContragentCompletionItem {

    /* ИД образовательного учереждения */
    private Long educationalId;
    /* короткое наименование образовательного учереждения */
    private String educationalInstitutionName;
    /* короткое наименование образовательного учереждения */
    private String educationalCity;
    /* короткое наименование образовательного учереждения */
    private String educationalLocation;
    /* короткое наименование образовательного учереждения */
    private String educationalTags;

    /* имена контрагентов по приему платежей */
    private HashMap<Long,Long> contragentPayItems = new HashMap<Long, Long>();
    private Long totalSumByOrg;
    private int paymentsCount;

    public ContragentCompletionItem(List<Contragent> contragentList) {
        Integer i=0;
        for (Contragent contragent: contragentList){
            contragentPayItems.put(contragent.getIdOfContragent(), 0L);
            i++;
        }
        totalSumByOrg = 0L;
        paymentsCount = 0;
    }

    public void setContragentPayItems(Object[] objects) {
        Long id = (Long) objects[1];
        Long value = (Long) objects[0];
        paymentsCount += ((Long) objects[2]).intValue();
        contragentPayItems.put(id, value);
        totalSumByOrg += value;
        Org org = (Org) objects[3];
        educationalId = org.getIdOfOrg();
        educationalInstitutionName = org.getShortName();
        educationalCity = org.getCity();
        educationalLocation = org.getLocation();
        educationalTags = org.getTag();
    }

    public Long getEducationalId() {
        return educationalId;
    }

    public void setEducationalId(Long educationalId) {
        this.educationalId = educationalId;
    }

    public Long getContragentPayValue(Long idOfContragent){
        return contragentPayItems.get(idOfContragent);
    }

    public HashMap<Long, Long> getContragentPayItems() {
        return contragentPayItems;
    }

    public void addContragentPayItems(HashMap<Long, Long> contragentPayItems) {
        for (Long key: contragentPayItems.keySet()){
            this.contragentPayItems.put(key, this.contragentPayItems.get(key) + contragentPayItems.get(key));
            totalSumByOrg += contragentPayItems.get(key);
        }
    }

    public String getEducationalInstitutionName() {
        return educationalInstitutionName;
    }

    public void setEducationalInstitutionName(String educationalInstitutionName) {
        this.educationalInstitutionName = educationalInstitutionName;
    }

    public Long getTotalSumByOrg() {
        return totalSumByOrg;
    }

    public String getEducationalCity() {
        return educationalCity;
    }

    public void setEducationalCity(String educationalCity) {
        this.educationalCity = educationalCity;
    }

    public String getEducationalLocation() {
        return educationalLocation;
    }

    public void setEducationalLocation(String educationalLocation) {
        this.educationalLocation = educationalLocation;
    }

    public String getEducationalTags() {
        return educationalTags;
    }

    public void setEducationalTags(String educationalTags) {
        this.educationalTags = educationalTags;
    }

    public int getPaymentsCount() {
        return paymentsCount;
    }

    public void appendToPaymentsCount(int surplus) {
        this.paymentsCount += surplus;
    }
}
