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

    public static class CardReportItem implements Comparable<CardReportItem> {

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

        public CardReportItem(Function function) {
            this.selected = false;
            this.idOfFunction = function.getIdOfFunction();
            this.functionName = function.getFunctionName();
            this.functionDesc = Function.getFunctionDesc(functionName);
        }

        @Override
        public int compareTo(CardReportItem o) {
            int res = this.functionName.compareTo(o.functionName);
            if (res == 0) {
                res = this.idOfFunction.compareTo(o.idOfFunction);
            }
            return res;
        }
    }

    private List<Item> items = Collections.emptyList();
    private List<CardReportItem> cardReportItems = Collections.emptyList();
    private static final String[] userFunctions = new String[] {"viewUser", "editUser", "deleteUser"};
    private static final String[] securityAdminFunctions = new String[] {"viewUser", "editUser", "deleteUser", "workOption"};

    public List<Item> getItems() {
        return items;
    }

    public List<CardReportItem> getCardReportItems() {
        return cardReportItems;
    }

    public Set<Function> getSecurityAdminFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        allFunctionsCriteria.add(Restrictions.in("functionName", securityAdminFunctions));
        return new HashSet<Function>((List<Function>)allFunctionsCriteria.list());
    }

    public Set<Function> getAdminFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        allFunctionsCriteria.add(Restrictions.not(Restrictions.in("functionName", userFunctions)));
        return new HashSet<Function>((List<Function>)allFunctionsCriteria.list());
    }

    public Set<Function> getMonitoringFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        Set<Function> monitoringFunctions = new HashSet<Function>();
        for (Object object : allFunctions) {
            Function function = (Function) object;
            if(function.getFunctionName().equalsIgnoreCase(Function.FUNC_MONITORING)){
                monitoringFunctions.add(function);
            }
        }
        return monitoringFunctions;
    }

    public Set<Function> getSupplierFunctions(Session session) {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        Set<Function> supplierFunctions = new HashSet<Function>();
        for (Object object : allFunctions) {
            Function function = (Function) object;

            if(        function.getFunctionName().equalsIgnoreCase(Function.FUNC_ORG_VIEW)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_CONTRAGENT_VIEW)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_CLIENT_VIEW)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_REPORT_VIEW)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_REPORT_EDIT)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_WORK_ONLINE_REPORT)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_PAYMENT_VIEW)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_RULE_VIEW)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_REPORT_EDIT)
                    || function.getFunctionName().equalsIgnoreCase(Function.FUNC_COMMODITY_ACCOUNTING)
                    ){
                supplierFunctions.add(function);
            }
        }
        return supplierFunctions;
    }

    public void fill(Session session) throws Exception {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        allFunctionsCriteria.add(Restrictions.not(Restrictions.in("functionName", userFunctions))); //исключаем права на операции с пользователями
        List allFunctions = allFunctionsCriteria.list();
        List<Item> items = new ArrayList<Item>(allFunctions.size());
        List<CardReportItem> cardReportItems = new ArrayList<CardReportItem>();
        for (Object object : allFunctions) {
            Function function = (Function) object;
            Item item = new Item(function);
            if (item.getFunctionName().equals("typeOfCardRprt") || item.getFunctionName()
                    .equals("interactiveCardDataRprt")) {
                CardReportItem cardReportItem = new CardReportItem(function);
                cardReportItems.add(cardReportItem);
            } else {
                items.add(item);
            }
        }
        this.items = items;
        Collections.sort(items);
        this.cardReportItems = cardReportItems;
        Collections.sort(cardReportItems);
    }

    public void fill(Session session, Set<Function> selectedFunctions) throws Exception {
        List<Item> items = new ArrayList<Item>();
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        for (Object object : allFunctions) {
            Function function = (Function) object;
            Item item = new Item(function);
            if (selectedFunctions!=null && selectedFunctions.contains(function)) {
                item.setSelected(true);
            }
            items.add(item);
        }
        this.items = items;
        Collections.sort(items);
    }

    public Set<Function> getSelected(Session session) throws HibernateException {
        Set<Function> selectedFunctions = new HashSet<Function>();
        for (Item item : items) {
            if (item.isSelected()) {
                Function function = (Function) session.load(Function.class, item.getIdOfFunction());
                selectedFunctions.add(function);
            }
        }
        return selectedFunctions;
    }
}
