/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by i.semenov on 07.11.2017.
 */
@Component("ImportRegisterEmployeeFileService")
@Scope("singleton")
public class ImportRegisterEmployeeFileService extends ImportRegisterFileService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterEmployeeFileService.class);

    public final String FILENAME_PROPERTY = "ecafe.processor.nsi.registry.employee.filename";
    public final String NODE_PROPERTY = "ecafe.processor.nsi.registry.employee.node";

    protected final String INITIAL_INSERT_STATEMENT = "insert into cf_registry_employee_file(firstname, secondname, "
            + "surname, birthdate, gender, snils, guidoforg) values ";

    protected Integer LINE_SIZE = 10;
    protected String TRUNCATE_STATEMENT = "truncate table cf_registry_employee_file";
    protected String DROP_INDEX = "drop index if exists cf_registry_employee_file_guidoforg_idx";
    protected String CREATE_INDEX = "create index cf_registry_employee_file_guidoforg_idx on cf_registry_employee_file using btree (guidoforg)";

    @Override
    protected String buildOneInsertValue(String[] arr) {
        //0-Фамилия, 1-Имя, 2-Отчество, 3-Дата рождения, 4-Пол, 5-СНИЛС, 6-Гуид ОО, 7-Какая-то дата, 8-Еще какая-то дата, 9-Статус записи

        StringBuilder sb = new StringBuilder();

        sb.append(getQuotedStr(arr[1])).append(", ");   //firstname
        sb.append(getQuotedStr(arr[2])).append(", ");   //secondname
        sb.append(getQuotedStr(arr[0])).append(", ");   //surname
        sb.append(getQuotedStr(getDate(arr[3]))).append(", ");   //birthdate
        sb.append(getQuotedStr(arr[4])).append(", ");   //gender
        sb.append(getQuotedStr(arr[5])).append(", ");   //snils
        sb.append(getQuotedStr(arr[6]));                //guidoforg

        return sb.toString();
    }

    private String getDate(String date) {
        try {
            String arr[] = date.split(" ");
            return arr[2] + "." + getMonth(arr[1]) + "." + arr[5];
        } catch (Exception e) {
            return "";
        }
    }

    private String getMonth(String mon) throws Exception {
        if (mon.equals("Jan")) return "01";
        else if (mon.equals("Feb")) return "02";
        else if (mon.equals("Mar")) return "03";
        else if (mon.equals("Apr")) return "04";
        else if (mon.equals("May")) return "05";
        else if (mon.equals("Jun")) return "06";
        else if (mon.equals("Jul")) return "07";
        else if (mon.equals("Aug")) return "08";
        else if (mon.equals("Sep")) return "09";
        else if (mon.equals("Oct")) return "10";
        else if (mon.equals("Nov")) return "11";
        else if (mon.equals("Dec")) return "12";
        throw new Exception("Can't parse month");
    }

    protected String getInitialInsertStatement() {
        return INITIAL_INSERT_STATEMENT;
    }

    protected String getTruncateStatement() {
        return TRUNCATE_STATEMENT;
    }

    protected String getDropIndexStatement() {
        return DROP_INDEX;
    }

    protected String getCreateIndexStatement() {
        return CREATE_INDEX;
    }

    protected Integer getLineSizeValue() {
        return LINE_SIZE;
    }

    protected String getNodeProperty() {
        return NODE_PROPERTY;
    }

    protected String getFilenameProperty() {
        return FILENAME_PROPERTY;
    }

    protected org.slf4j.Logger getLogger() {
        return logger;
    }
}
