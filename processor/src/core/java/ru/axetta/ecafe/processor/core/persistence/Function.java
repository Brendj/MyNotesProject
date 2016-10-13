/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.06.2009
 * Time: 15:56:56
 * To change this template use File | Settings | File Templates.
 */
public class Function {

    public static final String FUNC_USER_VIEW= "viewUser";
    public static final String FUNCD_USER_VIEW = "Просмотр пользователей";
    public static final String FUNC_USER_EDIT= "editUser";
    public static final String FUNCD_USER_EDIT= "Редактирование пользователей";
    public static final String FUNC_USER_DELETE = "deleteUser";
    public static final String FUNCD_USER_DELETE= "Удаление пользователей";
    public static final String FUNC_PAY_PROCESS = "payProcess";
    public static final String FUNCD_PAY_PROCESS = "Проводка платежа";
    public static final String FUNC_ORG_VIEW = "orgView";
    public static final String FUNCD_ORG_VIEW = "Просмотр организаций";
    public static final String FUNC_ORG_EDIT = "orgEdit";
    public static final String FUNCD_ORG_EDIT = "Редактирование организаций";
    public static final String FUNC_CONTRAGENT_VIEW = "contraView";
    public static final String FUNCD_CONTRAGENT_VIEW = "Просмотр контрагентов";
    public static final String FUNC_CONTRAGENT_EDIT = "contraEdit";
    public static final String FUNCD_CONTRAGENT_EDIT = "Редактирование контрагентов";
    public static final String FUNC_CLIENT_VIEW = "clientView";
    public static final String FUNCD_CLIENT_VIEW = "Просмотр клиентов";
    public static final String FUNC_CLIENT_EDIT = "clientEdit";
    public static final String FUNCD_CLIENT_EDIT = "Редактирование клиентов";
    public static final String FUNC_CLIENT_REMOVE = "clientDel";
    public static final String FUNCD_CLIENT_REMOVE = "Удаление клиентов";
    public static final String FUNC_CARD_VIEW = "cardView";
    public static final String FUNCD_CARD_VIEW = "Просмотр карт";
    public static final String FUNC_CARD_EDIT = "cardEdit";
    public static final String FUNCD_CARD_EDIT = "Редактирование карт";
    public static final String FUNC_SERVICE_ADMIN = "servAdm";
    public static final String FUNCD_SERVICE_ADMIN = "Сервис - администратор";
    public static final String FUNC_SERVICE_SUPPORT = "servSupp";
    public static final String FUNCD_SERVICE_SUPPORT = "Сервис - поддержка";
    public static final String FUNC_SERVICE_CLIENTS = "servClnt";
    public static final String FUNCD_SERVICE_CLIENTS = "Сервис клиенты";
    public static final String FUNC_REPORT_VIEW = "reportView";
    public static final String FUNCD_REPORT_VIEW = "Отчеты просмотр";
    public static final String FUNC_REPORT_EDIT = "reportEdit";
    public static final String FUNCD_REPORT_EDIT = "Отчеты редактирование";
    // baybikov
    public static final String FUNC_WORK_OPTION = "workOption";
    public static final String FUNCD_WORK_OPTION = "Редактирование настроек";
    public static final String FUNC_WORK_ONLINE_REPORT = "onlineRprt";
    public static final String FUNCD_WORK_ONLINE_REPORT = "Онлайн отчеты";
    public static final String FUNC_COUNT_CURRENT_POSITIONS = "countCP";
    public static final String FUNCD_COUNT_CURRENT_POSITIONS = "Рассчитать текущие позиции";
    public static final String FUNC_POS_VIEW = "posView";
    public static final String FUNCD_POS_VIEW = "Просмотр точек продаж";
    public static final String FUNC_POS_EDIT = "posEdit";
    public static final String FUNCD_POS_EDIT = "Редактирование точек продаж";
    public static final String FUNC_PAYMENT_VIEW = "pmntView";
    public static final String FUNCD_PAYMENT_VIEW = "Просмотр платежей";
    public static final String FUNC_PAYMENT_EDIT = "pmntEdit";
    public static final String FUNCD_PAYMENT_EDIT = "Редактирование платежей";
    public static final String FUNC_CATEGORY_VIEW = "catView";
    public static final String FUNCD_CATEGORY_VIEW = "Просмотр категорий";
    public static final String FUNC_CATEGORY_EDIT = "catEdit";
    public static final String FUNCD_CATEGORY_EDIT = "Редактирование категорий";
    public static final String FUNC_RULE_VIEW = "ruleView";
    public static final String FUNCD_RULE_VIEW = "Просмотр правил";
    public static final String FUNC_RULE_EDIT = "ruleEdit";
    public static final String FUNCD_RULE_EDIT = "Редактирование правил";
    public static final String FUNC_MONITORING = "monitor";
    public static final String FUNCD_MONITORING = "Мониторинг";
    public static final String FUNC_SUPPLIER = "supplier";
    public static final String FUNCD_SUPPLIER = "Поставщик";
    public static final String FUNC_COMMODITY_ACCOUNTING = "commAcc";
    public static final String FUNCD_COMMODITY_ACCOUNTING = "Товарный учет";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_COMPLEX = "onlineRprtComplex";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_COMPLEX = "Закрыть раздел 'Отчет по комплексам'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_BENEFIT = "onlineRprtBenefit";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_BENEFIT = "Закрыть раздел 'Отчет по льготам'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_REQUEST = "onlineRprtRequest";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_REQUEST = "Закрыть раздел 'Отчет по заявкам'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_MEALS = "onlineRprtMeals";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_MEALS = "Закрыть раздел 'Льготное питание'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_REFILL = "onlineRprtRefill";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_REFILL = "Закрыть раздел 'Отчеты по пополнениям'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_ACTIVITY = "onlineRprtActivity";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_ACTIVITY = "Закрыть раздел 'Отчеты по активности'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_CLIENTS = "onlineRprtClients";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_CLIENTS = "Закрыть раздел 'Отчеты по клиентам'";
    public static final String FUNC_SHOW_REPORTS_REPOSITORY = "showReportRepository";
    public static final String FUNCD_SHOW_REPORTS_REPOSITORY = "Репозиторий отчетов";
    public static final String FUNC_VISITORDOGM_EDIT = "visitorDogmEdit";
    public static final String FUNCD_VISITORDOGM_EDIT= "Редактирование сотрудников ДОгМ";


    private Long idOfFunction;
    private String functionName;
    private boolean restrict;
    private Set<User> users = new HashSet<User>();

    protected Function() {
        // For Hibernate
    }

    public Function(String functionName) {
        this.functionName = functionName;
    }

    public Long getIdOfFunction() {
        return idOfFunction;
    }

    private void setIdOfFunction(Long idOfFunction) {
        // For Hibernate
        this.idOfFunction = idOfFunction;
    }

    public String getFunctionName() {
        return functionName;
    }

    private void setFunctionName(String functionName) {
        // For Hibernate
        this.functionName = functionName;
    }

    protected Set<User> getUsersInternal() {
        // For Hibernate
        return users;
    }

    protected void setUsersInternal(Set<User> users) {
        // For Hibernate
        this.users = users;
    }

    public Set<User> getUsers() {
        return Collections.unmodifiableSet(getUsersInternal());
    }

    public boolean isRestrict() {
        return restrict;
    }

    public void setRestrict(boolean restrict) {
        this.restrict = restrict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Function)) {
            return false;
        }
        final Function function = (Function) o;
        return idOfFunction.equals(function.getIdOfFunction());
    }

    @Override
    public int hashCode() {
        return idOfFunction.hashCode();
    }

    @Override
    public String toString() {
        return "Function{" + "idOfFunction=" + idOfFunction + ", functionName='" + functionName + '\'' + '}';
    }

    private static HashMap<String, String> functionDesc = new HashMap<String, String>();

    public static String getFunctionDesc(String funcName) {
        return functionDesc.get(funcName);
    }

    public static List<Function> getFuncList() throws Exception {
        LinkedList<Function> list = new LinkedList<Function>();
        long nFunc = 1;
        for (Field f : Function.class.getFields()) {
            if (f.getName().startsWith("FUNC_")) {
                Function func = new Function();
                func.setFunctionName((String) f.get(func));
                func.setIdOfFunction(nFunc);
                func.setRestrict(f.getName().startsWith("FUNC_RESTRICT"));
                list.addLast(func);
                String funcDesc = (String) Function.class.getField(f.getName().replace("FUNC_", "FUNCD_")).get(func);
                functionDesc.put(func.getFunctionName(), funcDesc);
                nFunc++;
            }
        }
        return list;
    }
}
