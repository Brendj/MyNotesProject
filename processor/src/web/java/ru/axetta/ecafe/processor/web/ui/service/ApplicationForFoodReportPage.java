/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientDiscountHistoryService;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.ApplicationForFoodHistoryReportItem;
import ru.axetta.ecafe.processor.core.report.ApplicationForFoodReportItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;
import ru.axetta.ecafe.processor.web.ui.report.online.PeriodTypeMenu;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import java.util.*;

@Component
@Scope("session")
public class ApplicationForFoodReportPage extends OnlineReportPage {

    Logger logger = LoggerFactory.getLogger(ApplicationForFoodReportPage.class);
    private List<ApplicationForFoodReportItem> items = new ArrayList<ApplicationForFoodReportItem>();
    private ApplicationForFoodReportItem currentItem;
    private List<ApplicationForFoodReportItem> deletedItems = new ArrayList<>();
    private List<ApplicationForFoodReportItem> changeDatesItems = new ArrayList<>();

    @Autowired
    private ClientDiscountHistoryService clientDiscountHistoryService;

    private List<SelectItem> statuses = readAllItems();
    private String status;
    private List<SelectItem> benefits = readAllBenefits();
    private Integer benefit;
    private String number = "";
    private final static Integer ALL_BENEFITS = -1;
    private Boolean showPeriod = false;
    private static final String ARCHIEVE_COMMENT = "ЗЛП заархивировано";
    private Boolean needAction = false;
    private Date benefitStartDate;
    private Date benefitEndDate;
    private Boolean noErrorsOnValidate;
    private String errorMessage;

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
        needAction = false;
        items.clear();
        deletedItems.clear();
        changeDatesItems.clear();
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
                ClientDtisznDiscountInfo info = DAOUtils.getActualDTISZNDiscountsInfoInoeByClient(session, applicationForFood.getClient().getIdOfClient());
                if (info != null) {
                    item.setStartDate(info.getDateStart());
                    item.setEndDate(info.getDateEnd());
                }
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
        needAction = true;
    }

    public void makeOK() {
        setStatus(new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING, null));
        setStatus(new ApplicationForFoodStatus(ApplicationForFoodState.OK, null));
        needAction = true;
    }

    public void makeDenied() {
        setStatus(new ApplicationForFoodStatus(ApplicationForFoodState.DENIED, ApplicationForFoodDeclineReason.NO_DOCS));
        needAction = true;
    }

    public void makeArchieved() {
        for (ApplicationForFoodReportItem item : items) {
            if (item.getApplicationForFood().getIdOfApplicationForFood().equals(currentItem.getApplicationForFood().getIdOfApplicationForFood())) {
                deletedItems.add(item);
                item.setArchieved(true);
                break;
            }
        }
        needAction = true;
    }

    public void changeDates() {
        for (ApplicationForFoodReportItem item : items) {
            if (item.getApplicationForFood().getIdOfApplicationForFood().equals(currentItem.getApplicationForFood().getIdOfApplicationForFood())) {
                if (!validateDates()) return;
                item.setStartDate(benefitStartDate);
                item.setEndDate(benefitEndDate);
                item.setExpand(false);
                changeDatesItems.add(item);
                break;
            }
        }
        needAction = true;
    }

    private Date convertDate(Date date) {
        return CalendarUtils.startOfDay(date);
    }

    public boolean validateDates() {
        if (benefitStartDate == null || benefitEndDate == null) return false;
        if (benefitStartDate.after(benefitEndDate)) {
            errorMessage = "Начальная дата не может быть больше конечной";
            return false;
        }
        if (convertDate(benefitEndDate).before(convertDate(new Date()))) {
            errorMessage = "Конечная дата не может быть меньше текущей даты";
            return false;
        }
        for (ApplicationForFoodReportItem item : items) {
            if (item.getApplicationForFood().getIdOfApplicationForFood().equals(currentItem.getApplicationForFood().getIdOfApplicationForFood())) {
                if (item.getStartDate() == null) continue;
                if (!convertDate(item.getStartDate()).equals(convertDate(benefitStartDate))) {
                    if (convertDate(benefitStartDate).before(convertDate(new Date()))) {
                        errorMessage = "Начальная дата не может быть меньше текущей даты";
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Boolean needAction() {
        return needAction;
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
                        if (status.getApplicationForFoodState().equals(ApplicationForFoodState.OK)) {
                            DiscountManager.addOtherDiscountForClient(session, item.getApplicationForFood().getClient());
                        }
                    }
                }
            }
            CategoryDiscountDSZN discountInoe = getDiscountInoe(session);
            Long isppCodeInoe = discountInoe.getCategoryDiscount().getIdOfCategoryDiscount();
            Long clientDTISZNDiscountVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);
            for (ApplicationForFoodReportItem item : deletedItems) {
                wereChanges = true;
                ApplicationForFood applicationForFood = (ApplicationForFood)session.load(ApplicationForFood.class, item.getApplicationForFood().getIdOfApplicationForFood());
                applicationForFood.setArchived(true);
                applicationForFood.setVersion(nextVersion);
                applicationForFood.setLastUpdate(new Date());
                session.update(applicationForFood);
                JournalApplicationForFood journalApplicationForFood =
                        new JournalApplicationForFood();
                journalApplicationForFood.saveJournalApplicationForFood(session, MainPage.getSessionInstance().getCurrentUser(),
                        applicationForFood.getIdOfApplicationForFood(), OperationtypeForJournalApplication.ARCHIVED);

                Client client = DAOUtils.findClient(session, applicationForFood.getClient().getIdOfClient());
                if (applicationForFoodInoeExists(session, client, applicationForFood.getCreatedDate())) continue;
                Set<CategoryDiscount> discounts = client.getCategories();
                if (discounts.contains(discountInoe.getCategoryDiscount())) {
                    Set<CategoryDiscount> oldDiscounts = client.getCategories();
                    Set<CategoryDiscount> newDiscounts = new HashSet<>();
                    for (CategoryDiscount categoryDiscount : oldDiscounts) {
                        if (categoryDiscount.getIdOfCategoryDiscount() != isppCodeInoe)
                            newDiscounts.add(categoryDiscount);
                    }
                    DiscountManager
                            .renewDiscounts(session, client, newDiscounts, oldDiscounts, ARCHIEVE_COMMENT);
                }
                Criteria criteria = session.createCriteria(ClientDtisznDiscountInfo.class);
                criteria.add(Restrictions.eq("client", client));
                criteria.add(Restrictions.eq("archived", false));
                criteria.add(Restrictions.eq("dtisznCode", new Long(discountInoe.getCode())));
                List<ClientDtisznDiscountInfo> list = criteria.list();
                for (ClientDtisznDiscountInfo info : list) {
                    DiscountManager.ClientDtisznDiscountInfoBuilder builder = new DiscountManager.ClientDtisznDiscountInfoBuilder(info);
                    builder.withArchived(true);
                    builder.save(session, clientDTISZNDiscountVersion);
                }
            }

            for (ApplicationForFoodReportItem item : changeDatesItems) {
                wereChanges = true;
                ClientDtisznDiscountInfo info = DAOUtils.getActualDTISZNDiscountsInfoInoeByClient(session, item.getApplicationForFood().getClient().getIdOfClient());
                DiscountManager.ClientDtisznDiscountInfoBuilder builder = new DiscountManager.ClientDtisznDiscountInfoBuilder(info);
                DAOUtils.updateApplicationForFoodWithVersion(session, item.getApplicationForFood(), item.getApplicationForFood().getStatus(),
                            nextVersion, historyVersion);
                builder.withDateStart(item.getStartDate());
                builder.withDateEnd(item.getEndDate());
                item.getApplicationForFood().setDiscountDateStart(item.getStartDate());
                item.getApplicationForFood().setDiscountDateEnd(item.getEndDate());
                session.update(item.getApplicationForFood());
                builder.save(session, clientDTISZNDiscountVersion);
                JournalApplicationForFood journalApplicationForFood =
                        new JournalApplicationForFood();
                journalApplicationForFood.saveJournalApplicationForFood(session, MainPage.getSessionInstance().getCurrentUser(),
                        item.getApplicationForFood().getIdOfApplicationForFood(), OperationtypeForJournalApplication.CHANGED_DATE);

                clientDiscountHistoryService.saveChangeHistoryByDiscountInfo(session, info,
                        DiscountChangeHistory.MODIFY_IN_WEBAPP + MainPage.getSessionInstance().getCurrentUser().getUserName());
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

    private boolean applicationForFoodInoeExists(Session session, Client client, Date date) {
        Criteria criteria = session.createCriteria(ApplicationForFood.class);
        criteria.add(Restrictions.eq("client", client));
        criteria.add(Restrictions.gt("createdDate", date));
        ApplicationForFoodStatus status = new ApplicationForFoodStatus(ApplicationForFoodState.OK, null);
        criteria.add(Restrictions.eq("status", status));
        return criteria.list().size() > 0;
    }

    private CategoryDiscountDSZN getDiscountInoe(Session session) {
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        criteria.add(Restrictions.isNull("ETPCode"));
        criteria.add(Restrictions.eq("deleted", false));
        return (CategoryDiscountDSZN)criteria.uniqueResult();
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
        return currentItem == null ? new ApplicationForFoodReportItem() : currentItem;
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

    public Date getBenefitStartDate() {
        return benefitStartDate;
    }

    public void setBenefitStartDate(Date benefitStartDate) {
        this.benefitStartDate = benefitStartDate;
    }

    public Date getBenefitEndDate() {
        return benefitEndDate;
    }

    public void setBenefitEndDate(Date benefitEndDate) {
        this.benefitEndDate = benefitEndDate;
    }

    public Boolean getNoErrorsOnValidate() {
        return noErrorsOnValidate;
    }

    public void setNoErrorsOnValidate(Boolean noErrorsOnValidate) {
        this.noErrorsOnValidate = noErrorsOnValidate;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
