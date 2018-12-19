/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodDeclineReason;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodState;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodStatus;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.ApplicationForFoodReportItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class ApplicationForFoodReportPage extends OnlineReportPage {

    Logger logger = LoggerFactory.getLogger(ApplicationForFoodReportPage.class);
    private List<ApplicationForFoodReportItem> items = new ArrayList<ApplicationForFoodReportItem>();
    private ApplicationForFoodReportItem currentItem;

    public ApplicationForFoodReportPage() {

    }

    public void reload() {
        if (idOfOrgList.size() == 0) {
            printError("Выберите одну или несколько организаций");
            return;
        }
        items.clear();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            List<ApplicationForFood> list = DAOUtils.getApplicationForFoodListByOrgs(session, idOfOrgList);
            for (ApplicationForFood applicationForFood : list) {
                ApplicationForFoodReportItem item = new ApplicationForFoodReportItem(applicationForFood);
                items.add(item);
            }
            transaction.commit();
            transaction = null;
        }  catch (Exception e) {
            logger.error("Error in reload applicationForFoodReport: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void makeResume() {
        setStatus(new ApplicationForFoodStatus(ApplicationForFoodState.RESUME, null));
    }

    public void makeOK() {
        setStatus(new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING, null));
        setStatus(new ApplicationForFoodStatus(ApplicationForFoodState.OK, null));
    }

    public void makeDenied() {
        setStatus(new ApplicationForFoodStatus(ApplicationForFoodState.DENIED, ApplicationForFoodDeclineReason.NO_DOCS));
    }

    private void setStatus(ApplicationForFoodStatus status) {
        for (ApplicationForFoodReportItem item : items) {
            if (item.getApplicationForFood().getIdOfApplicationForFood().equals(currentItem.getApplicationForFood().getIdOfApplicationForFood())) {
                item.setApplicationForFoodStatus(status);
                item.setChanged(true);
                item.getStatuses().add(status);
                break;
            }
        }
    }

    public void apply() {
        Session session = null;
        Transaction transaction = null;
        boolean wereChanges = false;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Long nextVersion = DAOUtils.nextVersionByApplicationForFood(session);
            Long historyVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);
            for (ApplicationForFoodReportItem item : items) {
                if (item.isChanged()) {
                    wereChanges = true;
                    long pause = - RuntimeContext.getAppContext().getBean(ETPMVService.class).getPauseValue();
                    for (ApplicationForFoodStatus status : item.getStatuses()) {
                        DAOUtils.updateApplicationForFoodWithVersion(session, item.getApplicationForFood(), status, nextVersion, historyVersion);
                        RuntimeContext.getAppContext().getBean(ETPMVService.class).sendStatusAsync(System.currentTimeMillis() + pause, item.getServiceNumber(),
                                status.getApplicationForFoodState(), status.getDeclineReason());
                        pause += RuntimeContext.getAppContext().getBean(ETPMVService.class).getPauseValue();
                    }
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in apply ApplicationForFood save: ", e);
            printError("Во время выполнения операции произошла ошибка с текстом " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        reload();
        printMessage(wereChanges ? "Операция выполнена" : "Нет измененных записей");
    }

    public List<ApplicationForFoodReportItem> getItems() {
        return items;
    }

    public void setItems(List<ApplicationForFoodReportItem> items) {
        this.items = items;
    }

    @Override
    public void onShow() throws Exception {

    }

    @Override
    public String getPageFilename() {
        return "service/application_for_food_report";
    }

    public ApplicationForFoodReportItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(ApplicationForFoodReportItem currentItem) {
        this.currentItem = currentItem;
    }
}
