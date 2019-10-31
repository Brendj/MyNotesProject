/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.report;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoverageNutritionDynamicBean implements DynamicBean {

    private HashMap<String, DynamicProperty> dynamicProperties = new HashMap<>();

    public static final Pattern classesPattern = Pattern.compile("(\\d{1,2}-\\d{1,2})");

    public static final String TOTAL_STUDENTS = "TOTAL_STUDENTS";
    public static final String TOTAL_ALL = "TOTAL_ALL";
    public static final String TOTAL_EMPLOYEE = "TOTAL_EMPLOYEE";

    public static final String CLIENTS_COUNT = "кол-во покупателей";
    public static final String ORDERS_COUNT = "кол-во комплексов";
    public static final String PERCENTAGE_OF_UNIQUE_CLIENTS = "% уникальных покупателей";

    public static final String PAID_AND_FREE = "Платное питание + Бесплатное";
    public static final String PAID_NUTRITION = "Платное питание";
    public static final String FREE_NUTRITION = "Бесплатное питание";
    public static final String MENU_TYPE_BUFFET = "Буфет";

    public static final String BUFFET_CLIENTS_COUNT = "кол-во уникальных покупателей буфета %s%s классы";
    public static final String BUFFET_CLIENTS_COUNT_WITHOUT_CLASSES = "кол-во уникальных покупателей буфета";
    public static final String BUFFET_ORDERS_COUNT = "кол-во проданной продукции";
    public static final String BUFFET_HOT = "горячее";
    public static final String BUFFET_PAID = "покупная";
    public static final String BUFFET_ALL = "общее";
    public static final String BUFFET_HOT_FULL = MENU_TYPE_BUFFET + ' ' + BUFFET_HOT;
    public static final String BUFFET_PAID_FULL = MENU_TYPE_BUFFET + ' ' + BUFFET_PAID;
    public static final String BUFFET_ALL_FULL = MENU_TYPE_BUFFET + ' ' + BUFFET_ALL;

    public static final String CLIENTS_COUNT_TOTAL = "Количество уникальных покупателей всего";
    public static final String CLIENTS_COUNT_TOTAL_SUBTITLE = "комплексы + буфет";
    public static final String PERCENTAGE_OF_ACTIVE_CLIENTS = "% активных клиентов";
    public static final String PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE = "количество уникальных покупателей буфет + комплексы из групп учащихся делим на общее количество учащихся";

    public static final String ORG_CARD_COMPLEXES = "Комплексы проданные по карте ОО";
    public static final String ORG_CARD_ORDERS_COUNT = "количество проданных комплексов по карте зав. пр-ва";
    public static final String ORG_CARD_BUFFET_COUNT = "количество проданных буфет %s по карте зав. пр-ва";

    public static final String EMPLOYEES_TITLE = "Сотрудники";
    public static final String EMPLOYEES_COMPLEXES = "Комплексы";
    public static final String EMPLOYEES_COMPLEXES_SUBTITLE = "количество уникальных покупателей по всем видам комлексов";
    public static final String EMPLOYEES_BUIFFET_SUBTITLE = "количество уникальных покупателей по всем видам буфета";
    public static final String EMPLOYEES_COMPLEXES_AND_BUFFET = "Комплексы + буфет";
    public static final String EMPLOYEES_COMPLEXES_AND_BUFFET_SUBTITLE = "итого";
    public static final String EMPLOYEES_PERCCENTAGE_OF_ACTIVE = "% активных сотрудников";

    public static final String TOTALS_TITLE = "Итоговые значения";
    public static final String TOTALS_UNIQUE_BUYERS = "уникальные покупатели";
    public static final String TOTALS_SOLD_COMPLEXES = "количество проданных комплексов";
    public static final String TOTALS_SOLD_COMPLEXES_BY_ORG_CARD = "количество проданных комплексов по карте зав. пр-ва";
    public static final String TOTALS_SOLD_BUFFET = "количество проданной продукции буфета %s";
    public static final String TOTALS_SOLD_BUFFET_BY_ORG_CARD = "количество проданных позиций буфета %s по карте зав. пр-ва";

    public Object getValue(String propertyName, Class clazz) throws Exception {
        try {
            return dynamicProperties.get(propertyName).getValue();
        } catch (Exception e) {
            return DynamicReportUtils.convertFieldValue(0, clazz);
        }
    }

    @PostConstruct
    private void init() {
        dynamicProperties = new HashMap<>();
    }

    public HashMap<String, DynamicProperty> getDynamicProperties() {
        return dynamicProperties;
    }

    public void setDynamicProperties(HashMap<String, DynamicProperty> dynamicProperties) {
        this.dynamicProperties = dynamicProperties;
    }

    public static String findClassInString(String rawString) {
        String classString = "";
        Matcher matcher = classesPattern.matcher(rawString);
        if (matcher.find()) {
            classString = matcher.group(1);
        }
        return classString;
    }
}
