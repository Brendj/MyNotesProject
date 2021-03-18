/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.web.ui.MainPage;

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
public abstract class OnlineReportWithContragentPage extends OnlineReportPage {
    protected boolean selectIdOfOrgList = true; //  Флаг того какой массив сейчас заполнять: оргов (true) или контрагетов (false)
    protected String contragentFilter = "Не выбрано";
    protected List<Long> idOfContragentOrgList = new ArrayList<Long>();

    protected OnlineReportWithContragentPage(){
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

    public String getContragentFilter() {
        return contragentFilter;
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        //  Если заполняем орги (true), то вызываем родительский метод
        if (selectIdOfOrgList){
            super.completeOrgListSelection(orgMap);
        } else {
            if (orgMap != null) {
                idOfContragentOrgList = new ArrayList<Long>();
                if (orgMap.isEmpty()) {
                    contragentFilter = "Не выбрано";
                    idOfContragentOrgList.clear();
                } else {
                    contragentFilter = "";
                    for(Long idOfOrg : orgMap.keySet()) {
                        idOfContragentOrgList.add(idOfOrg);
                        contragentFilter = contragentFilter.concat(orgMap.get(idOfOrg) + "; ");
                    }
                    contragentFilter = contragentFilter.substring(0, contragentFilter.length() - 1);
                }
            }
        }
    }

    public String getContragentStringIdOfOrgList() {
        return idOfContragentOrgList.toString().replaceAll("[^0-9,]","");
    }

    public Object showOrgListSelectPage () {
        setSelectIdOfOrgList(true);
        final List<Long> oldIdOfContragentOrgList1 = MainPage.getSessionInstance().getIdOfContragentOrgList();
        if(oldIdOfContragentOrgList1 !=null && !oldIdOfContragentOrgList1.containsAll(idOfContragentOrgList)){
            MainPage.getSessionInstance().updateOrgListSelectPageWithItemDeselection();
            filter = "Не выбрано";
        }
        MainPage.getSessionInstance().setIdOfContragentOrgList(idOfContragentOrgList);
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    @Override
    public String getFilter() {
        final List<Long> oldIdOfContragentOrgList1 = MainPage.getSessionInstance().getIdOfContragentOrgList();
        if(oldIdOfContragentOrgList1 !=null && !oldIdOfContragentOrgList1.containsAll(idOfContragentOrgList)){
            idOfOrgList = new ArrayList<Long>();
            filter = "Не выбрано";
        }
        return super.getFilter();
    }

    public Object showContragentListSelectPage () {
        setSelectIdOfOrgList(false);
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }
}