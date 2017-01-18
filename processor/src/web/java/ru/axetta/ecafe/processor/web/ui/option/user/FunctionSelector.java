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
    private List<Item> optionsItems = Collections.emptyList();
    private static final String[] userFunctions = new String[]{"viewUser", "editUser", "deleteUser"};
    private static final String[] securityAdminFunctions = new String[]{
            "viewUser", "editUser", "deleteUser", "workOption"};

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
        return new HashSet<Function>((List<Function>) allFunctionsCriteria.list());
    }

    public Set<Function> getMonitoringFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        Set<Function> monitoringFunctions = new HashSet<Function>();
        for (Object object : allFunctions) {
            Function function = (Function) object;
            if (function.getFunctionName().equalsIgnoreCase(Function.FUNC_MONITORING)) {
                monitoringFunctions.add(function);
            }
        }
        return monitoringFunctions;
    }

    public Set<Function> getSupplierFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        Set<Function> supplierFunctions = new HashSet<Function>();
        Set<Item> selectedItems = new HashSet<Item>();

        for (Item item: organizationItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        for (Item item: contragentItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        for (Item item: clientItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        for (Item item: wayBillItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        for (Item item: onlineReportItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        for (Object object : allFunctions) {
            Function function = (Function) object;

            for (Item item: selectedItems) {
                if (function.getFunctionName().equals(item.getFunctionName())) {
                    supplierFunctions.add(function);
                }
            }
        }
        return supplierFunctions;
    }

    public Set<Function> getSupplierReportFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        Set<Function> supplierReportFunctions = new HashSet<Function>();

        Set<Item> selectedItems = new HashSet<Item>();

        for (Item item: onlineReportItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        for (Object object : allFunctions) {
            Function function = (Function) object;

            for (Item item: selectedItems) {
                if (function.getFunctionName().equals(item.getFunctionName())) {
                    supplierReportFunctions.add(function);
                }
            }
        }
        return supplierReportFunctions;
    }

    public void fill(Session session) throws Exception {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        allFunctionsCriteria.add(Restrictions
                .not(Restrictions.in("functionName", userFunctions))); //исключаем права на операции с пользователями
        List allFunctions = allFunctionsCriteria.list();
        List<Item> onlineReportItems = new ArrayList<Item>();
        List<Item> organizationItems = new ArrayList<Item>();
        List<Item> contragentItems = new ArrayList<Item>();
        List<Item> clientItems = new ArrayList<Item>();
        List<Item> visitorItems = new ArrayList<Item>();
        List<Item> cardItems = new ArrayList<Item>();
        List<Item> wayBillItems = new ArrayList<Item>();
        List<Item> serviceItems = new ArrayList<Item>();
        List<Item> monitorItems = new ArrayList<Item>();
        List<Item> repositoryItems = new ArrayList<Item>();
        List<Item> optionsItems = new ArrayList<Item>();
        for (Object object : allFunctions) {
            Function function = (Function) object;
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
            } else if (item.getFunctionName().equals("servAdm") || item.getFunctionName().equals("servClnt") || item.getFunctionName().equals("servSupp")) {
                serviceItems.add(item);
            } else if (item.getFunctionName().equals("monitor")) {
                monitorItems.add(item);
            } else if (item.getFunctionName().equals("showReportRepository")) {
                repositoryItems.add(item);
            } else if (item.getFunctionName().equals("workOption") || item.getFunctionName().equals("catEdit") ||
                    item.getFunctionName().equals("catView") || item.getFunctionName().equals("ruleEdit") || item
                    .getFunctionName().equals("ruleView") || item.getFunctionName().equals("reportEdit") || item
                    .getFunctionName().equals("reportView")) {
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
                    .getFunctionName().equals("cardRprts") || item.getFunctionName().equals("countCP") || item.getFunctionName().equals("supplier")) {
                onlineReportItems.add(item);
            }
        }
        this.onlineReportItems = onlineReportItems;
        Collections.sort(onlineReportItems);
        this.organizationItems = organizationItems;
        Collections.sort(organizationItems);
        this.contragentItems = contragentItems;
        Collections.sort(contragentItems);
        this.clientItems = clientItems;
        Collections.sort(clientItems);
        this.visitorItems = visitorItems;
        this.cardItems = cardItems;
        Collections.sort(cardItems);
        this.wayBillItems = wayBillItems;
        this.serviceItems = serviceItems;
        Collections.sort(serviceItems);
        this.monitorItems = monitorItems;
        this.repositoryItems = repositoryItems;
        this.optionsItems =optionsItems;
        Collections.sort(optionsItems);
    }

    public void fill(Session session, Set<Function> selectedFunctions) throws Exception {
        List<Item> onlineReportItems = new ArrayList<Item>();
        List<Item> organizationItems = new ArrayList<Item>();
        List<Item> contragentItems = new ArrayList<Item>();
        List<Item> clientItems = new ArrayList<Item>();
        List<Item> visitorItems = new ArrayList<Item>();
        List<Item> cardItems = new ArrayList<Item>();
        List<Item> wayBillItems = new ArrayList<Item>();
        List<Item> serviceItems = new ArrayList<Item>();
        List<Item> monitorItems = new ArrayList<Item>();
        List<Item> repositoryItems = new ArrayList<Item>();
        List<Item> optionsItems = new ArrayList<Item>();
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        for (Object object : allFunctions) {
            Function function = (Function) object;
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
            } else if (item.getFunctionName().equals("workOption") || item.getFunctionName().equals("catEdit") ||
                    item.getFunctionName().equals("catView") || item.getFunctionName().equals("ruleEdit") || item
                    .getFunctionName().equals("ruleView") || item.getFunctionName().equals("reportEdit") || item
                    .getFunctionName().equals("reportView")) {
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
                    .getFunctionName().equals("cardRprts") || item.getFunctionName().equals("countCP") || item.getFunctionName().equals("supplier")) {
                if (selectedFunctions != null && selectedFunctions.contains(function)) {
                    item.setSelected(true);
                }
                onlineReportItems.add(item);
            }
        }
        this.onlineReportItems = onlineReportItems;
        Collections.sort(onlineReportItems);
        this.organizationItems = organizationItems;
        Collections.sort(organizationItems);
        this.contragentItems = contragentItems;
        Collections.sort(contragentItems);
        this.clientItems = clientItems;
        Collections.sort(clientItems);
        this.visitorItems = visitorItems;
        this.cardItems = cardItems;
        Collections.sort(cardItems);
        this.wayBillItems = wayBillItems;
        this.serviceItems = serviceItems;
        Collections.sort(serviceItems);
        this.monitorItems = monitorItems;
        this.repositoryItems = repositoryItems;
        this.optionsItems =optionsItems;
        Collections.sort(optionsItems);
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

        for (Item item : optionsItems) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }

        return selectedFunctions;
    }
}
