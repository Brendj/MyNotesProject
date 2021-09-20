/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Function;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 10:20:05
 * To change this template use File | Settings | File Templates.
 */
public class FunctionSelector {

    private List<Item> onlineReportItems = Collections.emptyList();
    private List<Item> onlineReportItemsSupplierReport = Collections.emptyList();
    private List<Item> organizationItems = Collections.emptyList();
    private List<Item> contragentItems = Collections.emptyList();
    private List<Item> clientItems = Collections.emptyList();
    private List<Item> visitorItems = Collections.emptyList();
    private List<Item> cardItems = Collections.emptyList();
    private List<Item> wayBillItems = Collections.emptyList();
    private List<Item> serviceItems = Collections.emptyList();
    private List<Item> monitorItems = Collections.emptyList();
    private List<Item> repositoryItems = Collections.emptyList();
    private List<Item> helpdeskItems = Collections.emptyList();
    private List<Item> espItems = Collections.emptyList();
    private List<Item> optionsItems = Collections.emptyList();

    private static final List<String> userFunctions = Arrays
            .asList(Function.FUNC_USER_VIEW, Function.FUNC_USER_EDIT, Function.FUNC_USER_DELETE);
    private static final List<String> notAdminFunctions = Arrays
            .asList(Function.FUNC_RESTRICT_CARD_REPORTS, Function.FUNC_RESTRICT_CLIENT_REPORTS,
                    Function.FUNC_RESTRICT_CLIENTS_BENEFITS_REPORT,
                    Function.FUNC_RESTRICT_ELECTRONIC_RECONCILIATION_REPORT, Function.FUNC_RESTRICT_ENTER_EVENT_REPORT,
                    Function.FUNC_RESTRICT_FINANCIAL_CONTROL, Function.FUNC_RESTRICT_INFORM_REPORTS,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_ACTIVITY, Function.FUNC_RESTRICT_ONLINE_REPORT_BENEFIT,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_COMPLEX, Function.FUNC_RESTRICT_ONLINE_REPORT_MEALS,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_REFILL, Function.FUNC_RESTRICT_ONLINE_REPORT_REQUEST,
                    Function.FUNC_RESTRICT_PAID_FOOD_REPORT, Function.FUNC_RESTRICT_SALES_REPORTS,
                    Function.FUNC_RESTRICT_STATISTIC_DIFFERENCES, Function.FUNC_RESTRICT_SUBSCRIPTION_FEEDING,
                    Function.FUNC_RESTRICT_TOTAL_SERVICES_REPORT, Function.FUNC_RESTRICT_TRANSACTIONS_REPORT,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_CALENDAR);
    private static final List<String> securityAdminFunctions = Arrays
            .asList(Function.FUNC_USER_VIEW, Function.FUNC_USER_EDIT, Function.FUNC_USER_DELETE,
                    Function.FUNC_WORK_OPTION);
    private static final List<String> organizationFuncs = Arrays.asList(Function.FUNC_ORG_EDIT, Function.FUNC_ORG_VIEW);
    private static final List<String> contragentFuncs = Arrays
            .asList(Function.FUNC_CONTRAGENT_EDIT, Function.FUNC_CONTRAGENT_VIEW, Function.FUNC_PAY_PROCESS,
                    Function.FUNC_PAYMENT_EDIT, Function.FUNC_PAYMENT_VIEW, Function.FUNC_POS_EDIT,
                    Function.FUNC_POS_VIEW);
    private static final List<String> clientFuncs = Arrays
            .asList(Function.FUNC_CLIENT_REMOVE, Function.FUNC_CLIENT_EDIT, Function.FUNC_CLIENT_VIEW,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_CLIENTS);
    private static final List<String> visitorFuncs = Collections.singletonList(Function.FUNC_VISITORDOGM_EDIT);
    private static final List<String> cardFuncs = Arrays
            .asList(Function.FUNC_CARD_EDIT, Function.FUNC_CARD_VIEW, Function.FUNC_RESTRICT_CARD_SIGNS);
    private static final List<String> wayBillFuncs = Collections.singletonList(Function.FUNC_COMMODITY_ACCOUNTING);
    private static final List<String> serviceFuncs = Arrays
            .asList(Function.FUNC_SERVICE_ADMIN, Function.FUNC_SERVICE_CLIENTS, Function.FUNC_SERVICE_SUPPORT);
    private static final List<String> monitorFuncs = Collections.singletonList(Function.FUNC_MONITORING);
    private static final List<String> repositoryFuncs = Collections
            .singletonList(Function.FUNC_SHOW_REPORTS_REPOSITORY);
    private static final List<String> helpdeskFuncs = Collections.singletonList(Function.FUNC_HELPDESK);
    private static final List<String> espFuncs = Collections.singletonList(Function.FUNC_ESP);
    private static final List<String> optionsFuncs = Arrays
            .asList(Function.FUNC_WORK_OPTION, Function.FUNC_CATEGORY_EDIT, Function.FUNC_CATEGORY_VIEW,
                    Function.FUNC_RULE_VIEW, Function.FUNC_RULE_EDIT, Function.FUNC_REPORT_EDIT,
                    Function.FUNC_REPORT_VIEW, Function.FUNC_SUPPLIER);
    private static final List<String> onlineReportFuncs = Arrays
            .asList(Function.FUNC_WORK_ONLINE_REPORT, Function.FUNC_WORK_ONLINE_REPORT_DOCS,
                    Function.FUNC_WORK_ONLINE_REPORT_EE_REPORT, Function.FUNC_WORK_ONLINE_REPORT_MENU_REPORT,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_COMPLEX, Function.FUNC_RESTRICT_ONLINE_REPORT_BENEFIT,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_REQUEST,
                    Function.FUNC_RESTRICT_ELECTRONIC_RECONCILIATION_REPORT, Function.FUNC_RESTRICT_ONLINE_REPORT_MEALS,
                    Function.FUNC_RESTRICT_PAID_FOOD_REPORT, Function.FUNC_RESTRICT_SUBSCRIPTION_FEEDING,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_REFILL, Function.FUNC_RESTRICT_ONLINE_REPORT_ACTIVITY,
                    Function.FUNC_RESTRICT_CLIENT_REPORTS, Function.FUNC_RESTRICT_STATISTIC_DIFFERENCES,
                    Function.FUNC_RESTRICT_FINANCIAL_CONTROL, Function.FUNC_RESTRICT_INFORM_REPORTS,
                    Function.FUNC_RESTRICT_SALES_REPORTS, Function.FUNC_RESTRICT_ENTER_EVENT_REPORT,
                    Function.FUNC_RESTRICT_TOTAL_SERVICES_REPORT, Function.FUNC_COVERAGENUTRITION,
                    Function.FUNC_RESTRICT_CLIENTS_BENEFITS_REPORT, Function.FUNC_RESTRICT_TRANSACTIONS_REPORT,
                    Function.FUNC_RESTRICT_CARD_REPORTS, Function.FUNC_COUNT_CURRENT_POSITIONS,
                    Function.FUNC_FEEDING_SETTINGS_SUPPLIER, Function.FUNC_FEEDING_SETTINGS_ADMIN,
                    Function.FUNC_RESTICT_MESSAGE_IN_ARM_OO, Function.FUNC_RESTRICT_MANUAL_REPORT,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_CALENDAR);
    private static final List<String> onlineReportFuncsForSupplierReport = Arrays
            .asList(Function.FUNC_WORK_ONLINE_REPORT, Function.FUNC_WORK_ONLINE_REPORT_DOCS,
                    Function.FUNC_WORK_ONLINE_REPORT_EE_REPORT, Function.FUNC_WORK_ONLINE_REPORT_MENU_REPORT,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_COMPLEX, Function.FUNC_RESTRICT_ONLINE_REPORT_REQUEST,
                    Function.FUNC_RESTRICT_ELECTRONIC_RECONCILIATION_REPORT, Function.FUNC_RESTRICT_ONLINE_REPORT_MEALS,
                    Function.FUNC_RESTRICT_PAID_FOOD_REPORT, Function.FUNC_RESTRICT_SUBSCRIPTION_FEEDING,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_REFILL, Function.FUNC_RESTRICT_CLIENT_REPORTS,
                    Function.FUNC_RESTRICT_STATISTIC_DIFFERENCES, Function.FUNC_RESTRICT_SALES_REPORTS,
                    Function.FUNC_RESTRICT_TOTAL_SERVICES_REPORT, Function.FUNC_COVERAGENUTRITION,
                    Function.FUNC_RESTRICT_TRANSACTIONS_REPORT, Function.FUNC_COUNT_CURRENT_POSITIONS,
                    Function.FUNC_FEEDING_SETTINGS_SUPPLIER, Function.FUNC_FEEDING_SETTINGS_ADMIN);
    private static final List<String> blockedForSupplierReport = Arrays
            .asList(Function.FUNC_RESTRICT_ENTER_EVENT_REPORT, Function.FUNC_RESTRICT_FINANCIAL_CONTROL,
                    Function.FUNC_RESTRICT_INFORM_REPORTS, Function.FUNC_RESTRICT_CARD_REPORTS,
                    Function.FUNC_RESTRICT_ONLINE_REPORT_BENEFIT, Function.FUNC_RESTRICT_ONLINE_REPORT_ACTIVITY,
                    Function.FUNC_RESTRICT_CLIENTS_BENEFITS_REPORT, Function.FUNC_RESTRICT_MANUAL_REPORT,
                    Function.FUNC_RESTICT_MESSAGE_IN_ARM_OO, Function.FUNC_RESTRICT_ONLINE_REPORT_CALENDAR);

    public List<Item> getOnlineReportItems() {
        return onlineReportItems;
    }

    public List<Item> onlineReportItemsAll(Boolean supplierReport) {
        if (supplierReport) {
            return onlineReportItemsSupplierReport;
        }
        return onlineReportItems;
    }


    public List<Item> getOrganizationItems() {
        return organizationItems;
    }

    public List<Item> getContragentItems() {
        return contragentItems;
    }

    public List<Item> getClientItems() {
        return clientItems;
    }

    public List<Item> getVisitorItems() {
        return visitorItems;
    }

    public List<Item> getCardItems() {
        return cardItems;
    }

    public List<Item> getWayBillItems() {
        return wayBillItems;
    }

    public List<Item> getServiceItems() {
        return serviceItems;
    }

    public List<Item> getMonitorItems() {
        return monitorItems;
    }

    public List<Item> getRepositoryItems() {
        return repositoryItems;
    }

    public List<Item> getHelpdeskItems() {
        return helpdeskItems;
    }

    public List<Item> getEspItems() {
        return espItems;
    }

    public List<Item> getOptionsItems() {
        return optionsItems;
    }

    public Set<Function> getSecurityAdminFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        allFunctionsCriteria.add(Restrictions.in("functionName", securityAdminFunctions));
        return new HashSet<Function>((List<Function>) allFunctionsCriteria.list());
    }

    public Set<Function> getAdminFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        allFunctionsCriteria.add(Restrictions.not(Restrictions.in("functionName", userFunctions)));
        allFunctionsCriteria.add(Restrictions.not(Restrictions.in("functionName", notAdminFunctions)));
        return new HashSet<Function>((List<Function>) allFunctionsCriteria.list());
    }

    public Set<Function> getMonitoringFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List<Function> allFunctions = allFunctionsCriteria.list();
        Set<Function> monitoringFunctions = new HashSet<Function>();
        for (Function function : allFunctions) {
            if (function.getFunctionName().equalsIgnoreCase(Function.FUNC_MONITORING)) {
                monitoringFunctions.add(function);
            }
        }
        return monitoringFunctions;
    }

    public Set<Function> getSupplierFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List<Function> allFunctions = allFunctionsCriteria.list();
        Set<Function> supplierFunctions = new HashSet<Function>();
        for (Function function : allFunctions) {
            if (function.getFunctionName().equalsIgnoreCase(Function.FUNC_ORG_VIEW) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_CONTRAGENT_VIEW) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_CLIENT_VIEW) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_REPORT_VIEW) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_WORK_ONLINE_REPORT) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_WORK_ONLINE_REPORT_DOCS) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_WORK_ONLINE_REPORT_EE_REPORT) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_WORK_ONLINE_REPORT_MENU_REPORT) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_PAYMENT_VIEW) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_RULE_VIEW) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_REPORT_EDIT) || function.getFunctionName()
                    .equalsIgnoreCase(Function.FUNC_COMMODITY_ACCOUNTING)
                /*|| function.getFunctionName().equalsIgnoreCase(Function.FUNC_RESTRICT_MANUAL_REPORT)*/) {
                supplierFunctions.add(function);
            }
        }

        for (Item item : onlineReportItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                supplierFunctions.add(function);
            }
        }

        for (Item item : organizationItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                supplierFunctions.add(function);
            }
        }

        for (Item item : contragentItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                supplierFunctions.add(function);
            }
        }

        for (Item item : clientItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                supplierFunctions.add(function);
            }
        }

        for (Item item : wayBillItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                supplierFunctions.add(function);
            }
        }

        return supplierFunctions;
    }

    public Set<Function> getSupplierReportFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List<Function> allFunctions = allFunctionsCriteria.list();
        Set<Function> supplierReportFunctions = new HashSet<Function>();
        for (Function function : allFunctions) {
            if (function.getFunctionName().equalsIgnoreCase(Function.FUNC_WORK_ONLINE_REPORT)) {
                supplierReportFunctions.add(function);
            }
            //Добавляем функции, которые должны быть у Отчетность поставщика питания (911)
            if (blockedForSupplierReport.contains(function.getFunctionName()))
            {
                supplierReportFunctions.add(function);
            }
        }

        for (Item item : onlineReportItemsSupplierReport) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                supplierReportFunctions.add(function);
            }
        }

        return supplierReportFunctions;
    }

    public Set<Function> getCardOperatorFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List<Function> allFunctions = allFunctionsCriteria.list();
        Set<Function> cardOperatorFunctions = new HashSet<Function>();
        for (Function function : allFunctions) {
            if (function.getFunctionName().equalsIgnoreCase(Function.FUNC_RESTRICT_CARD_OPERATOR)) {
                cardOperatorFunctions.add(function);
            }
        }
        return cardOperatorFunctions;
    }

    public void fill(Session session) throws Exception {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        allFunctionsCriteria.add(Restrictions
                .not(Restrictions.in("functionName", userFunctions))); //исключаем права на операции с пользователями
        List<Function> allFunctions = (List<Function>) allFunctionsCriteria.list();

        List<Item> onlineReportItems = new LinkedList<>();
        List<Item> onlineReportItemsSupplierReport = new LinkedList<>();
        List<Item> organizationItems = new LinkedList<>();
        List<Item> contragentItems = new LinkedList<>();
        List<Item> clientItems = new LinkedList<>();
        List<Item> visitorItems = new LinkedList<>();
        List<Item> cardItems = new LinkedList<>();
        List<Item> wayBillItems = new LinkedList<>();
        List<Item> serviceItems = new LinkedList<>();
        List<Item> monitorItems = new LinkedList<>();
        List<Item> repositoryItems = new LinkedList<>();
        List<Item> helpdeskItems = new LinkedList<>();
        List<Item> espItems = new LinkedList<>();
        List<Item> optionsItems = new LinkedList<>();

        for (Function function : allFunctions) {
            Item item = new Item(function);
            if (organizationFuncs.contains(item.getFunctionName())) {
                organizationItems.add(item);
            } else if (contragentFuncs.contains(item.getFunctionName())) {
                contragentItems.add(item);
            } else if (clientFuncs.contains(item.getFunctionName())) {
                clientItems.add(item);
            } else if (visitorFuncs.contains(item.getFunctionName())) {
                visitorItems.add(item);
            } else if (cardFuncs.contains(item.getFunctionName())) {
                cardItems.add(item);
            } else if (wayBillFuncs.contains(item.getFunctionName())) {
                wayBillItems.add(item);
            } else if (serviceFuncs.contains(item.getFunctionName())) {
                serviceItems.add(item);
            } else if (monitorFuncs.contains(item.getFunctionName())) {
                monitorItems.add(item);
            } else if (repositoryFuncs.contains(item.getFunctionName())) {
                repositoryItems.add(item);
            } else if (helpdeskFuncs.contains(item.getFunctionName())) {
                helpdeskItems.add(item);
            } else if (espFuncs.contains(item.getFunctionName())) {
                espItems.add(item);
            } else if (optionsFuncs.contains(item.getFunctionName())) {
                optionsItems.add(item);
            } else {
                if (onlineReportFuncs.contains(item.getFunctionName())) {
                    onlineReportItems.add(item);
                }
                if (onlineReportFuncsForSupplierReport.contains(item.getFunctionName())) {
                    onlineReportItemsSupplierReport.add(item);
                }
            }

        }

        Collections.sort(onlineReportItems);
        Collections.sort(onlineReportItemsSupplierReport);
        Collections.sort(organizationItems);
        Collections.sort(contragentItems);
        Collections.sort(clientItems);
        Collections.sort(cardItems);
        Collections.sort(serviceItems);
        Collections.sort(optionsItems);

        this.onlineReportItems = onlineReportItems;
        this.onlineReportItemsSupplierReport = onlineReportItemsSupplierReport;
        this.organizationItems = organizationItems;
        this.contragentItems = contragentItems;
        this.clientItems = clientItems;
        this.visitorItems = visitorItems;
        this.cardItems = cardItems;
        this.wayBillItems = wayBillItems;
        this.serviceItems = serviceItems;
        this.monitorItems = monitorItems;
        this.repositoryItems = repositoryItems;
        this.helpdeskItems = helpdeskItems;
        this.espItems = espItems;
        this.optionsItems = optionsItems;
    }

    public void fill(Session session, Set<Function> selectedFunctions) throws Exception {
        List<Item> onlineReportItems = new LinkedList<>();
        List<Item> onlineReportItemsSupplierReport = new LinkedList<>();
        List<Item> organizationItems = new LinkedList<>();
        List<Item> contragentItems = new LinkedList<>();
        List<Item> clientItems = new LinkedList<>();
        List<Item> visitorItems = new LinkedList<>();
        List<Item> cardItems = new LinkedList<>();
        List<Item> wayBillItems = new LinkedList<>();
        List<Item> serviceItems = new LinkedList<>();
        List<Item> monitorItems = new LinkedList<>();
        List<Item> repositoryItems = new LinkedList<>();
        List<Item> helpdeskItems = new LinkedList<>();
        List<Item> espItems = new LinkedList<>();
        List<Item> optionsItems = new LinkedList<>();

        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List<Function> allFunctions = allFunctionsCriteria.list();

        for (Function function : allFunctions) {
            Item item = new Item(function);

            if (organizationFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                organizationItems.add(item);
            } else if (contragentFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                contragentItems.add(item);
            } else if (clientFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                clientItems.add(item);
            } else if (visitorFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                visitorItems.add(item);
            } else if (cardFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                cardItems.add(item);
            } else if (wayBillFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                wayBillItems.add(item);
            } else if (serviceFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                serviceItems.add(item);
            } else if (monitorFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                monitorItems.add(item);
            } else if (repositoryFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                repositoryItems.add(item);
            } else if (helpdeskFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                helpdeskItems.add(item);
            } else if (espFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                espItems.add(item);
            } else if (optionsFuncs.contains(item.getFunctionName())) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                optionsItems.add(item);
            } else {
                if (onlineReportFuncs.contains(item.getFunctionName())) {
                    if (selectedFunctions != null && selectedFunctions.contains(function)) {
                        item.setSelected(true);
                    }
                    onlineReportItems.add(item);
                }
                if (onlineReportFuncsForSupplierReport.contains(item.getFunctionName())) {
                    if (selectedFunctions != null && selectedFunctions.contains(function)) {
                        item.setSelected(true);
                    }
                    onlineReportItemsSupplierReport.add(item);
                }
            }

        } Collections.sort(onlineReportItems);
        Collections.sort(organizationItems);
        Collections.sort(contragentItems);
        Collections.sort(clientItems);
        Collections.sort(cardItems);
        Collections.sort(serviceItems);
        Collections.sort(optionsItems);

        this.onlineReportItems = onlineReportItems;
        this.onlineReportItemsSupplierReport = onlineReportItemsSupplierReport;
        this.organizationItems = organizationItems;
        this.contragentItems = contragentItems;
        this.clientItems = clientItems;
        this.visitorItems = visitorItems;
        this.cardItems = cardItems;
        this.wayBillItems = wayBillItems;
        this.serviceItems = serviceItems;
        this.monitorItems = monitorItems;
        this.repositoryItems = repositoryItems;
        this.helpdeskItems = helpdeskItems;
        this.espItems=espItems;
        this.optionsItems = optionsItems;
    }

    public Set<Function> getSelectedForForSupplierReportFunction(Session session)
    {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List<Function> allFunctions = allFunctionsCriteria.list();
        Set<Function> supplierReportFunctions = new HashSet<Function>();
        for (Function function : allFunctions) {
            //Добавляем функции, которые должны быть у Отчетность поставщика питания (911)
            if (blockedForSupplierReport.contains(function.getFunctionName()))
            {
                supplierReportFunctions.add(function);
            }
        }
        supplierReportFunctions.addAll(getSelected(session));
        return supplierReportFunctions;
    }

    public Set<Function> getSelected(Session session) throws HibernateException {
        Set<Function> selectedFunctions = new HashSet<Function>();

        for (Item item : onlineReportItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : onlineReportItemsSupplierReport) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : organizationItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : contragentItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : clientItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : visitorItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : cardItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : wayBillItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : serviceItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : monitorItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : repositoryItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : helpdeskItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : espItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        for (Item item : optionsItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        return selectedFunctions;
    }

    public List<Item> getOnlineReportItemsSupplierReport() {
        return onlineReportItemsSupplierReport;
    }

    public void setOnlineReportItemsSupplierReport(List<Item> onlineReportItemsSupplierReport) {
        this.onlineReportItemsSupplierReport = onlineReportItemsSupplierReport;
    }

    public static class Item implements Comparable<Item> {

        private boolean selected;
        private final Long idOfFunction;
        private final String functionName;
        private final String functionDesc;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public Long getIdOfFunction() {
            return idOfFunction;
        }

        public String getFunctionName() {
            return functionName;
        }

        public String getFunctionDesc() {
            return functionDesc;
        }

        public Item(Function function) {
            this.selected = false;
            this.idOfFunction = function.getIdOfFunction();
            this.functionName = function.getFunctionName();
            this.functionDesc = Function.getFunctionDesc(functionName);
        }

        @Override
        public int compareTo(Item o) {
            int res = this.functionName.compareTo(o.functionName);
            if (res == 0) {
                res = this.idOfFunction.compareTo(o.idOfFunction);
            }
            return res;
        }
    }
}
