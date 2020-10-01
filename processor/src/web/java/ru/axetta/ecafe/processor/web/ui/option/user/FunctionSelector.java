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
                    Function.FUNC_RESTRICT_TOTAL_SERVICES_REPORT, Function.FUNC_RESTRICT_TRANSACTIONS_REPORT
            );
    private static final List<String> securityAdminFunctions = Arrays
            .asList(Function.FUNC_USER_VIEW, Function.FUNC_USER_EDIT, Function.FUNC_USER_DELETE,
                    Function.FUNC_WORK_OPTION);
    //private static final List<String>

    public List<Item> getOnlineReportItems() {
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
        List allFunctions = allFunctionsCriteria.list();
        Set<Function> supplierReportFunctions = new HashSet<Function>();
        for (Object object : allFunctions) {
            Function function = (Function) object;

            if (function.getFunctionName().equalsIgnoreCase(Function.FUNC_WORK_ONLINE_REPORT)) {
                supplierReportFunctions.add(function);
            }
        }

        for (Item item : onlineReportItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                supplierReportFunctions.add(function);
            }
        }

        return supplierReportFunctions;
    }

    public Set<Function> getCardOperatorFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        Set<Function> cardOperatorFunctions = new HashSet<Function>();
        for (Object object : allFunctions) {
            Function function = (Function) object;
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
        List<Item> optionsItems = new LinkedList<>();

        for (Function function : allFunctions) {
            Item item = new Item(function);
            if (item.getFunctionName().equals("orgEdit") || item.getFunctionName().equals("orgView")) {
                organizationItems.add(item);
            } else if (item.getFunctionName().equals("contraEdit") || item.getFunctionName().equals("contraView")
                    || item.getFunctionName().equals("payProcess") || item.getFunctionName().equals("pmntEdit") || item
                    .getFunctionName().equals("pmntView") || item.getFunctionName().equals("posEdit") || item
                    .getFunctionName().equals("posView")) {
                contragentItems.add(item);
            } else if (item.getFunctionName().equals("clientDel") || item.getFunctionName().equals("clientEdit") || item
                    .getFunctionName().equals("onlineRprtClients") || item.getFunctionName().equals("clientView")) {
                clientItems.add(item);
            } else if (item.getFunctionName().equals("visitorDogmEdit")) {
                visitorItems.add(item);
            } else if (item.getFunctionName().equals("cardEdit") || item.getFunctionName().equals("cardView")) {
                cardItems.add(item);
            } else if (item.getFunctionName().equals("commAcc")) {
                wayBillItems.add(item);
            } else if (item.getFunctionName().equals("servAdm") || item.getFunctionName().equals("servClnt") || item
                    .getFunctionName().equals("servSupp")) {
                serviceItems.add(item);
            } else if (item.getFunctionName().equals("monitor")) {
                monitorItems.add(item);
            } else if (item.getFunctionName().equals("showReportRepository")) {
                repositoryItems.add(item);
            } else if (item.getFunctionName().equals("helpdesk")) {
                helpdeskItems.add(item);
            } else if (item.getFunctionName().equals("workOption") || item.getFunctionName().equals("catEdit") || item
                    .getFunctionName().equals("catView") || item.getFunctionName().equals("ruleEdit") || item
                    .getFunctionName().equals("ruleView") || item.getFunctionName().equals("reportEdit") || item
                    .getFunctionName().equals("reportView") || item.getFunctionName().equals("supplier")) {
                optionsItems.add(item);
            } else if (item.getFunctionName().equals("onlineRprt") || item.getFunctionName().equals("onlineRprtComplex")
                    || item.getFunctionName().equals("onlineRprtBenefit") || item.getFunctionName()
                    .equals("onlineRprtRequest") || item.getFunctionName().equals("electronicReconciliationRprt")
                    || item.getFunctionName().equals("onlineRprtMeals") || item.getFunctionName().equals("paidFood")
                    || item.getFunctionName().equals("subscriptionFeeding") || item.getFunctionName()
                    .equals("onlineRprtRefill") || item.getFunctionName().equals("onlineRprtActivity") || item
                    .getFunctionName().equals("clientRprts") || item.getFunctionName().equals("statisticDifferences")
                    || item.getFunctionName().equals("financialControl") || item.getFunctionName().equals("informRprts")
                    || item.getFunctionName().equals("salesRprt") || item.getFunctionName().equals("enterEventRprt")
                    || item.getFunctionName().equals("totalServicesRprt") || item.getFunctionName()
                    .equals("clientsBenefitsRprt") || item.getFunctionName().equals("transactionsRprt") || item
                    .getFunctionName().equals("cardRprts") || item.getFunctionName().equals("countCP") || item
                    .getFunctionName().equals("feedingSettingsSupplier") || item.getFunctionName()
                    .equals("feedingSettingsAdmin") || item.getFunctionName().equals("manualRprt") || item
                    .getFunctionName().equals("messageARMinOO") || item.getFunctionName()
                    .equals("coverageNutritionRprt")) {
                onlineReportItems.add(item);
            }
        }
        Collections.sort(onlineReportItems);
        Collections.sort(organizationItems);
        Collections.sort(contragentItems);
        Collections.sort(clientItems);
        Collections.sort(cardItems);
        Collections.sort(serviceItems);
        Collections.sort(optionsItems);

        this.onlineReportItems = onlineReportItems;
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
        this.optionsItems = optionsItems;
    }

    public void fill(Session session, Set<Function> selectedFunctions) throws Exception {
        List<Item> onlineReportItems = new LinkedList<>();
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
        List<Item> optionsItems = new LinkedList<>();

        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List<Function> allFunctions = allFunctionsCriteria.list();

        for (Function function : allFunctions) {
            Item item = new Item(function);

            if (item.getFunctionName().equals("orgEdit") || item.getFunctionName().equals("orgView")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                organizationItems.add(item);
            } else if (item.getFunctionName().equals("contraEdit") || item.getFunctionName().equals("contraView")
                    || item.getFunctionName().equals("payProcess") || item.getFunctionName().equals("pmntEdit") || item
                    .getFunctionName().equals("pmntView") || item.getFunctionName().equals("posEdit") || item
                    .getFunctionName().equals("posView")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                contragentItems.add(item);
            } else if (item.getFunctionName().equals("clientDel") || item.getFunctionName().equals("clientEdit") || item
                    .getFunctionName().equals("onlineRprtClients") || item.getFunctionName().equals("clientView")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                clientItems.add(item);
            } else if (item.getFunctionName().equals("visitorDogmEdit")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                visitorItems.add(item);
            } else if (item.getFunctionName().equals("cardEdit") || item.getFunctionName().equals("cardView")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                cardItems.add(item);
            } else if (item.getFunctionName().equals("commAcc")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                wayBillItems.add(item);
            } else if (item.getFunctionName().equals("servAdm") || item.getFunctionName().equals("servClnt") || item
                    .getFunctionName().equals("servSupp")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                serviceItems.add(item);
            } else if (item.getFunctionName().equals("monitor")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                monitorItems.add(item);
            } else if (item.getFunctionName().equals("showReportRepository")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                repositoryItems.add(item);
            } else if (item.getFunctionName().equals("helpdesk")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                helpdeskItems.add(item);
            } else if (item.getFunctionName().equals("workOption") || item.getFunctionName().equals("catEdit") || item
                    .getFunctionName().equals("catView") || item.getFunctionName().equals("ruleEdit") || item
                    .getFunctionName().equals("ruleView") || item.getFunctionName().equals("reportEdit") || item
                    .getFunctionName().equals("reportView") || item.getFunctionName().equals("supplier")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                optionsItems.add(item);
            } else if (item.getFunctionName().equals("onlineRprt") || item.getFunctionName().equals("onlineRprtComplex")
                    || item.getFunctionName().equals("onlineRprtBenefit") || item.getFunctionName()
                    .equals("onlineRprtRequest") || item.getFunctionName().equals("electronicReconciliationRprt")
                    || item.getFunctionName().equals("onlineRprtMeals") || item.getFunctionName().equals("paidFood")
                    || item.getFunctionName().equals("subscriptionFeeding") || item.getFunctionName()
                    .equals("onlineRprtRefill") || item.getFunctionName().equals("onlineRprtActivity") || item
                    .getFunctionName().equals("clientRprts") || item.getFunctionName().equals("statisticDifferences")
                    || item.getFunctionName().equals("financialControl") || item.getFunctionName().equals("informRprts")
                    || item.getFunctionName().equals("salesRprt") || item.getFunctionName().equals("enterEventRprt")
                    || item.getFunctionName().equals("totalServicesRprt") || item.getFunctionName()
                    .equals("clientsBenefitsRprt") || item.getFunctionName().equals("transactionsRprt") || item
                    .getFunctionName().equals("cardRprts") || item.getFunctionName().equals("countCP") || item
                    .getFunctionName().equals("feedingSettingsSupplier") || item.getFunctionName()
                    .equals("feedingSettingsAdmin") || item.getFunctionName().equals("manualRprt") || item
                    .getFunctionName().equals("messageARMinOO") || item.getFunctionName()
                    .equals("coverageNutritionRprt")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                onlineReportItems.add(item);
            }
        }
        Collections.sort(onlineReportItems);
        Collections.sort(organizationItems);
        Collections.sort(contragentItems);
        Collections.sort(clientItems);
        Collections.sort(cardItems);
        Collections.sort(serviceItems);
        Collections.sort(optionsItems);

        this.onlineReportItems = onlineReportItems;
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
        this.optionsItems = optionsItems;
    }

    public Set<Function> getSelected(Session session) throws HibernateException {
        Set<Function> selectedFunctions = new HashSet<Function>();

        for (Item item : onlineReportItems) {
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

        for (Item item : optionsItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        return selectedFunctions;
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
