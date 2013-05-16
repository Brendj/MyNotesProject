/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import org.hibernate.HibernateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 06.05.13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class OnlineReportWithContragentPage extends OnlineReportPage {
    protected boolean selectIdOfOrgList = true;                         //  Флаг того какой массив сейчас заполнять: оргов (true) или контрагетов (false)
    protected String contragentFileter = "Не выбрано";
    protected List<Long> idOfContragentOrgList = new ArrayList<Long>();

    OnlineReportWithContragentPage (){
        super();
    }

    public boolean isSelectIdOfOrgList() {
        return selectIdOfOrgList;
    }

    public void setSelectIdOfOrgList(boolean selectIdOfOrgList) {
        this.selectIdOfOrgList = selectIdOfOrgList;
    }

    public List<Long> getIdOfOrgList() {
        if (selectIdOfOrgList) {
            return idOfOrgList;
        } else {
            return idOfContragentOrgList;
        }
    }

    public String getContragentFileter() {
        return contragentFileter;
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        //  Если заполняем орги (true), то вызываем родительский метод
        if (selectIdOfOrgList){
            super.completeOrgListSelection(orgMap);
        } else {
            if (orgMap != null) {
                idOfContragentOrgList = new ArrayList<Long>();
                if (orgMap.isEmpty())
                    contragentFileter = "Не выбрано";
                else {
                    contragentFileter = "";
                    for(Long idOfOrg : orgMap.keySet()) {
                        idOfContragentOrgList.add(idOfOrg);
                        contragentFileter = contragentFileter.concat(orgMap.get(idOfOrg) + "; ");
                    }
                    contragentFileter = contragentFileter.substring(0, contragentFileter.length() - 1);
                }
            }
        }
    }
}