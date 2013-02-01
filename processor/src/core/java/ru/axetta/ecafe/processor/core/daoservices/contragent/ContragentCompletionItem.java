/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

import ru.axetta.ecafe.processor.core.persistence.Contragent;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.01.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ContragentCompletionItem {

    /* короткое наименование образовательного учереждения */
    private String educationalInstitutionName;

    /* имена контрагентов по приему платежей */
    private HashMap<Long,Long> contragentPayItems = new HashMap<Long, Long>();

    private HashMap<Long, Integer> map = new HashMap<Long,Integer>();
    private Long totalSumByOrg;

    private List<Long> values;

    public ContragentCompletionItem(List<Contragent> contragentList) {
        Integer i=0;
        for (Contragent contragent: contragentList){
            contragentPayItems.put(contragent.getIdOfContragent(), 0L);
            map.put(contragent.getIdOfContragent(),i);
            i++;
        }
        values = new ArrayList<Long>(i);
        totalSumByOrg = 0L;
    }

    public void setContragentPayItems(List list){
        Integer i=0;
        for (Object ob: list){
            Object[] objects = (Object[]) ob;
            Long id = (Long) objects[1];
            Long value = (Long) objects[0];
            contragentPayItems.put(id, value);
            values.add(map.get(id),value);
            totalSumByOrg+=value;
        }
    }

    public List<Long> getValues() {
        return values;
    }

    public Long getContragentPayValue(Long idOfContragent){
        return contragentPayItems.get(idOfContragent);
    }

    public HashMap<Long, Long> getContragentPayItems() {
        return contragentPayItems;
    }

    public void addContragentPayItems(HashMap<Long, Long> contragentPayItems) {
        totalSumByOrg = 0L;
        for (Long key: contragentPayItems.keySet()){
            this.contragentPayItems.put(key, this.contragentPayItems.get(key) + contragentPayItems.get(key));
            totalSumByOrg += this.contragentPayItems.get(key) + contragentPayItems.get(key);
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
}
