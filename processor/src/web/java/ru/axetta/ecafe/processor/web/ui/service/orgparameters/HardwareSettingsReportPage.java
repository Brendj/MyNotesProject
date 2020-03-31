/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.orgparameters;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.orghardware.HardwareSettingsReport;
import ru.axetta.ecafe.processor.core.report.orghardware.HardwareSettingsReportItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class HardwareSettingsReportPage extends OnlineReportPage implements OrgListSelectPage.CompleteHandlerList {

    private Integer status = 0;
    private List<SelectItem> statuses;
    private List<HardwareSettingsReportItem> items = Collections.emptyList();
    private List<SelectItem> listOfOrgDistricts;
    private String selectedDistricts = "";

    private Boolean allFriendlyOrgs = true;

    private final Logger logger = LoggerFactory.getLogger(HardwareSettingsReportPage.class);

    private List<SelectItem> buildStatuses() {
        List<SelectItem> items = new ArrayList<SelectItem>(3);
        items.add(new SelectItem(0, "Все"));
        items.add(new SelectItem(1, "Обслуживается"));
        items.add(new SelectItem(2, "Не обслуживается"));
        return items;
    }

    private List<SelectItem> buildListOfOrgDistricts(Session session) {
        List<String> allDistricts = null;
        List<SelectItem> selectItemList = new LinkedList<SelectItem>();
        selectItemList.add(new SelectItem("", "Все"));
        try {
            allDistricts = DAOUtils.getAllDistinctDepartmentsFromOrgs(session);

            for (String district : allDistricts) {
                selectItemList.add(new SelectItem(district, district));
            }
        } catch (Exception e) {
            logger.error("Cant build Districts items", e);
        }
        return selectItemList;
    }

    @Override
    public void onShow() throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            statuses = buildStatuses();
            listOfOrgDistricts = buildListOfOrgDistricts(session);
            items.clear();
        } catch (Exception e) {
            logger.error("Exception when prepared the OrgSettingsPage: ", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void buildHTML() {
        Session persistenceSession = null;
        Transaction transaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = persistenceSession.beginTransaction();

            List<String> idOfOrgListString = new ArrayList<>(idOfOrgList.size());
            for (String item : idOfOrgListString) {
                idOfOrgList.add(Long.parseLong(item));
            }

            items = HardwareSettingsReport.Builder
                    .buildOrgHardwareCollection(idOfOrgList, status, persistenceSession, selectedDistricts,
                            allFriendlyOrgs);
            //Collections.sort(items);
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Can't build HTML report: ", e);
            printError("Не удалось построить отчет: " + e.getMessage());
        } finally {
            HibernateUtils.close(persistenceSession, logger);
            HibernateUtils.rollback(transaction, logger);
        }
    }

    //public List<HardwareSettingsReportItem> getItems() {
    //    if(items == null){
    //        return Collections.emptyList();
    //    }
    //    return items;
    //}

    @Override
    public String getPageFilename() {
        return "service/hardware_settings_report";
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SelectItem> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<SelectItem> statuses) {
        this.statuses = statuses;
    }

    public List<HardwareSettingsReportItem> getItems() {
        return items;
    }

    public void setItems(List<HardwareSettingsReportItem> items) {
        this.items = items;
    }

    public List<SelectItem> getListOfOrgDistricts() {
        return listOfOrgDistricts;
    }

    public void setListOfOrgDistricts(List<SelectItem> listOfOrgDistricts) {
        this.listOfOrgDistricts = listOfOrgDistricts;
    }

    public String getSelectedDistricts() {
        return selectedDistricts;
    }

    public void setSelectedDistricts(String selectedDistricts) {
        this.selectedDistricts = selectedDistricts;
    }

    public Boolean getAllFriendlyOrgs() {
        return allFriendlyOrgs;
    }

    public void setAllFriendlyOrgs(Boolean allFriendlyOrgs) {
        this.allFriendlyOrgs = allFriendlyOrgs;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
