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
    // Для добавления новой функции пользователя достаточно строго в конце прописать новые константы.
    // Значение имени функции должно начинаться с FUNC_, ограничение с FUNC_RESTRICT, описание функции с FUNCD_.
    // Детали реализации смотри в методе getFuncList().
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
    public static final String FUNC_RESTICT_MESSAGE_IN_ARM_OO = "messageARMinOO";
    public static final String FUNCD_RESTICT_MESSAGE_IN_ARM_OO = "Закрыть 'Сообщения в АРМ администратора ОО'";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_REQUEST = "Закрыть раздел 'Отчет по заявкам'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_MEALS = "onlineRprtMeals";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_MEALS = "Закрыть раздел 'Льготное питание'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_REFILL = "onlineRprtRefill";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_REFILL = "Закрыть раздел 'Отчеты по пополнениям'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_ACTIVITY = "onlineRprtActivity";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_ACTIVITY = "Закрыть раздел 'Отчеты по активности'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_CALENDAR = "onlineRprtCalendar";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_CALENDAR = "Закрыть раздел 'Календарь дней питания'";
    public static final String FUNC_RESTRICT_ONLINE_REPORT_CLIENTS = "onlineRprtClients";
    public static final String FUNCD_RESTRICT_ONLINE_REPORT_CLIENTS = "Закрыть раздел 'Отчеты по клиентам'";
    public static final String FUNC_SHOW_REPORTS_REPOSITORY = "showReportRepository";
    public static final String FUNCD_SHOW_REPORTS_REPOSITORY = "Репозиторий отчетов";
    public static final String FUNC_VISITORDOGM_EDIT = "visitorDogmEdit";
    public static final String FUNCD_VISITORDOGM_EDIT= "Редактирование сотрудников";
    public static final String FUNC_RESTRICT_ELECTRONIC_RECONCILIATION_REPORT = "electronicReconciliationRprt";
    public static final String FUNCD_RESTRICT_ELECTRONIC_RECONCILIATION_REPORT = "Закрыть подраздел 'Электронная сверка'";
    public static final String FUNC_RESTRICT_PAID_FOOD_REPORT = "paidFood";
    public static final String FUNCD_RESTRICT_PAID_FOOD_REPORT = "Закрыть подраздел 'Платное питание'";
    public static final String FUNC_RESTRICT_SUBSCRIPTION_FEEDING = "subscriptionFeeding";
    public static final String FUNCD_RESTRICT_SUBSCRIPTION_FEEDING = "Закрыть подраздел 'Абонементное питание'";
    public static final String FUNC_RESTRICT_CLIENT_REPORTS = "clientRprts";
    public static final String FUNCD_RESTRICT_CLIENT_REPORTS = "Закрыть подраздел 'Отчеты по балансам'";
    public static final String FUNC_RESTRICT_STATISTIC_DIFFERENCES = "statisticDifferences";
    public static final String FUNCD_RESTRICT_STATISTIC_DIFFERENCES = "Закрыть подраздел 'Статистика по расхождениям данных'";
    public static final String FUNC_RESTRICT_FINANCIAL_CONTROL = "financialControl";
    public static final String FUNCD_RESTRICT_FINANCIAL_CONTROL = "Закрыть подраздел 'Отчеты для службы финансового контроля'";
    public static final String FUNC_RESTRICT_INFORM_REPORTS = "informRprts";
    public static final String FUNCD_RESTRICT_INFORM_REPORTS = "Закрыть подраздел 'Отчеты по информированию'";
    public static final String FUNC_RESTRICT_SALES_REPORTS = "salesRprt";
    public static final String FUNCD_RESTRICT_SALES_REPORTS = "Закрыть подраздел 'Отчеты по продажам'";
    public static final String FUNC_RESTRICT_CARD_REPORTS = "cardRprts";
    public static final String FUNCD_RESTRICT_CARD_REPORTS = "Закрыть подраздел 'Отчеты по картам'";
    public static final String FUNC_RESTRICT_ENTER_EVENT_REPORT = "enterEventRprt";
    public static final String FUNCD_RESTRICT_ENTER_EVENT_REPORT = "Закрыть 'Отчет по турникетам'";
    public static final String FUNC_RESTRICT_TOTAL_SERVICES_REPORT = "totalServicesRprt";
    public static final String FUNCD_RESTRICT_TOTAL_SERVICES_REPORT = "Закрыть 'Свод по услугам'";
    public static final String FUNC_RESTRICT_CLIENTS_BENEFITS_REPORT = "clientsBenefitsRprt";
    public static final String FUNCD_RESTRICT_CLIENTS_BENEFITS_REPORT = "Закрыть 'Расчет комплексов по льготным правилам'";
    public static final String FUNC_RESTRICT_TRANSACTIONS_REPORT = "transactionsRprt";
    public static final String FUNCD_RESTRICT_TRANSACTIONS_REPORT = "Закрыть 'Отчеты по транзакциям'";
    public static final String FUNC_RESTRICT_MANUAL_REPORT = "manualRprt";
    public static final String FUNCD_RESTRICT_MANUAL_REPORT = "Закрыть 'Ручной запуск отчетов'";
    public static final String FUNC_RESTRICT_CARD_OPERATOR = "cardOperator";
    public static final String FUNCD_RESTRICT_CARD_OPERATOR = "Опрерации по картам";
    public static final String FUNC_FEEDING_SETTINGS_SUPPLIER = "feedingSettingsSupplier";
    public static final String FUNCD_FEEDING_SETTINGS_SUPPLIER = "Настройки платного питания - поставщик";
    public static final String FUNC_FEEDING_SETTINGS_ADMIN = "feedingSettingsAdmin";
    public static final String FUNCD_FEEDING_SETTINGS_ADMIN = "Настройки платного питания - админ";
    public static final String FUNC_HELPDESK = "helpdesk";
    public static final String FUNCD_HELPDESK = "Заявки в службу помощи";
    public static final String FUNC_COVERAGENUTRITION = "coverageNutritionRprt";
    public static final String FUNCD_COVERAGENUTRITION = "Отчет по охвату питания";
    public static final String FUNC_RESTRICT_CARD_SIGNS = "cardSingsRestrict";
    public static final String FUNCD_RESTRICT_CARD_SIGNS = "Закрыть подраздел 'Цифровые подписи'";
    public static final String FUNC_WORK_ONLINE_REPORT_DOCS = "onlineRprtDocs";
    public static final String FUNCD_WORK_ONLINE_REPORT_DOCS = "Онлайн отчеты - Документы";
    public static final String FUNC_WORK_ONLINE_REPORT_EE_REPORT = "onlineRprtEEReport";
    public static final String FUNCD_WORK_ONLINE_REPORT_EE_REPORT = "Онлайн отчеты - Отчеты по проходам";
    public static final String FUNC_WORK_ONLINE_REPORT_MENU_REPORT = "onlineRprtMenuReport";
    public static final String FUNCD_WORK_ONLINE_REPORT_MENU_REPORT = "Онлайн отчеты - Отчет по меню";

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
