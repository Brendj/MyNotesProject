/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Function;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 10:20:05
 * To change this template use File | Settings | File Templates.
 */
public class FunctionSelector {

    public static Set<Function> SUPPLIER_FUNCTIONS = null;
    public static Set<Function> MONITORING_FUNCTIONS = null;
    public static Set<Function> ADMIN_FUNCTIONS = null;

    public static class Item {

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
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public void fill(Session session) throws Exception {
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        List<Item> items = new ArrayList<Item>(allFunctions.size());
        SUPPLIER_FUNCTIONS = new HashSet<Function>();
        MONITORING_FUNCTIONS = new HashSet<Function>();
        ADMIN_FUNCTIONS = new HashSet<Function>();
        for (Object object : allFunctions) {
            Function function = (Function) object;
            Item item = new Item(function);
            items.add(item);
            if(function.getFunctionName().equalsIgnoreCase(Function.FUNC_MONITORING)){
                MONITORING_FUNCTIONS.add(function);
            }
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
                SUPPLIER_FUNCTIONS.add(function);
            }
            ADMIN_FUNCTIONS.add(function);
        }
        this.items = items;
    }

    public void fill(Session session, Set<Function> selectedFunctions) throws Exception {
        List<Item> items = new ArrayList<Item>();
        Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        List allFunctions = allFunctionsCriteria.list();
        for (Object object : allFunctions) {
            Function function = (Function) object;
            Item item = new Item(function);
            if (selectedFunctions.contains(function)) {
                item.setSelected(true);
            }
            items.add(item);
        }
        this.items = items;
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
