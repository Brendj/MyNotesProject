/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.ApplicationForFoodHistoryReportItem;
import ru.axetta.ecafe.processor.core.report.ApplicationForFoodReportItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;
import ru.axetta.ecafe.processor.web.ui.report.online.PeriodTypeMenu;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class ApplicationForFoodReportPage extends OnlineReportPage {

    Logger logger = LoggerFactory.getLogger(ApplicationForFoodReportPage.class);
    private List<ApplicationForFoodReportItem> items = new ArrayList<ApplicationForFoodReportItem>();
    private ApplicationForFoodReportItem currentItem;

    private List<SelectItem> statuses = readAllItems();
    private String status;
    private List<SelectItem> benefits = readAllBenefits();
    private Integer benefit;
    private String number = "";
    private final static Integer ALL_BENEFITS = -1;
    private Boolean showPeriod = false;

    private static List<SelectItem> readAllItems() {
        ApplicationForFoodState[] states = ApplicationForFoodState.values();
        List<SelectItem> items = new ArrayList<SelectItem>(states.length);
        items.add(new SelectItem("Все", "Все"));
        for (ApplicationForFoodState state : states) {
            if (!state.equals(ApplicationForFoodState.DENIED)) {
                items.add(new SelectItem(state.getCode().toString(), state.getCode().toString()));
            } else {
                items.add(new SelectItem("1080.1", "1080.1"));
                items.add(new SelectItem("1080.2", "1080.2"));
                items.add(new SelectItem("1080.3", "1080.3"));
            }
        }
        return items;
    }

    private List<SelectItem> readAllBenefits() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(ALL_BENEFITS, "Все"));
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            List<CategoryDiscountDSZN> list = DAOUtils.getCategoryDiscountDSZNForReportList(session);
            for (CategoryDiscountDSZN category : list) {
                items.add(new SelectItem(category.getCode(), category.getCode() + " - " + category.getDescription()));
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in reload applicationForFoodReport: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return items;
    }

    public ApplicationForFoodReportPage() {
        periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
    }

    public void reload() {
        items.clear();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            ApplicationForFoodStatus statusCondition = null;
            if (!status.equals("Все")) {
                String[] arr = status.split("\\.");
                statusCondition = new ApplicationForFoodStatus(ApplicationForFoodState.fromCode(Integer.parseInt(arr[0])),
                        arr.length == 1 ? null : ApplicationForFoodDeclineReason.fromInteger(Integer.parseInt(arr[1])));
            }
            Long benefitCondition = null;
            if (benefit > ALL_BENEFITS) {
                benefitCondition = benefit.longValue();
            }
            List<Long> idOfClientList = new ArrayList<Long>();
            if (getClientList().size() > 0) {
                for (ClientSelectListPage.Item clientItem : getClientList()) {
                    idOfClientList.add(clientItem.getIdOfClient());
                }
            }
            List<ApplicationForFood> list = DAOUtils.getApplicationForFoodListByOrgs(session, idOfOrgList, statusCondition,
                    benefitCondition, idOfClientList, number, CalendarUtils.startOfDay(startDate), CalendarUtils.endOfDay(endDate), showPeriod);
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

    public void makeArchieved() {
        for (ApplicationForFoodReportItem item : items) {
            if (item.getApplicationForFood().getIdOfApplicationForFood().equals(currentItem.getApplicationForFood().getIdOfApplicationForFood())) {
                item.setMovedToArchieve(true);
                item.setChanged(true);
                break;
            }
        }
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
            Long otherDiscountCode = null;
            for (ApplicationForFoodReportItem item : items) {
                if (item.isChanged()) {
                    wereChanges = true;
                    long pause = - RuntimeContext.getAppContext().getBean(ETPMVService.class).getPauseValue();
                    for (ApplicationForFoodStatus status : item.getStatuses()) {
                        DAOUtils.updateApplicationForFoodWithVersion(session, item.getApplicationForFood(), status, nextVersion, historyVersion);
                        RuntimeContext.getAppContext().getBean(ETPMVService.class).sendStatusAsync(System.currentTimeMillis() + pause, item.getServiceNumber(),
                                status.getApplicationForFoodState(), status.getDeclineReason());
                        pause += RuntimeContext.getAppContext().getBean(ETPMVService.class).getPauseValue();
                        if (status.getApplicationForFoodState().equals(ApplicationForFoodState.OK)) {
                            if (null == otherDiscountCode) {
                                otherDiscountCode = DAOUtils.getOtherDiscountCode(session);
                            }
                            ClientManager.addOtherDiscountForClient(session, item.getApplicationForFood().getClient(), otherDiscountCode);
                        }
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

    public String getStatusString(ApplicationForFoodStatus status) {
        return status.getApplicationForFoodState().getCode().toString()
                + (status.getApplicationForFoodState().equals(ApplicationForFoodState.DENIED) ? "." + status.getDeclineReason().getCode() : "");
    }

    public String getBenefitText() {
        if (currentItem == null) return "";
        try {
            return RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getDSZNBenefitName(currentItem.getBenefit());
        } catch (NoResultException e) {
            return "Льгота не найдена в справочнике по коду";
        }
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

    public List<ApplicationForFoodHistoryReportItem> getHistoryItems() {
        List<ApplicationForFoodHistoryReportItem> result = new ArrayList<ApplicationForFoodHistoryReportItem>();
        if (currentItem == null) return result;
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            List<ApplicationForFoodHistory> list = DAOUtils.getHistoryByApplicationForFood(session, currentItem.getApplicationForFood());

            for (ApplicationForFoodHistory history : list) {
                result.add(new ApplicationForFoodHistoryReportItem(history));
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in get ApplicationForFoodHistory info: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    public List<SelectItem> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<SelectItem> statuses) {
        this.statuses = statuses;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SelectItem> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<SelectItem> benefits) {
        this.benefits = benefits;
    }

    public Integer getBenefit() {
        return benefit;
    }

    public void setBenefit(Integer benefit) {
        this.benefit = benefit;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getShowPeriod() {
        return showPeriod;
    }

    public void setShowPeriod(Boolean showPeriod) {
        this.showPeriod = showPeriod;
    }
}
