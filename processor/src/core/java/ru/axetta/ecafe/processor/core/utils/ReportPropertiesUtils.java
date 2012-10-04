/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.event.BasicEvent;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.text.DateFormat;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 17:02:12
 * To change this template use File | Settings | File Templates.
 */
public class ReportPropertiesUtils {
    
    public static String P_ID_OF_ORG="idOfOrg";
    public static String P_ORG_TAG="orgTag";
    public static String P_ORG_NUMBER_IN_NAME="orgNumberInName";
    public static String P_SHORT_NAME="shortName";
    public static String P_OFFICIAL_NAME="officialName";
    public static String P_JOB_NAME ="reportJob";
    public static String P_REPORT_PERIOD="reportPeriod";
    public static String P_REPORT_CLASS="class";
    public static String P_DATES_SPECIFIED_BY_USER="datesSpecifiedByUser";

    //private static String DELIMETER = ",";

    private ReportPropertiesUtils() {

    }

    public static void addProperties(Properties properties, Class reportClass, BasicReportJob.AutoReportBuildTask autoReportBuildTask) throws Exception {
        properties.setProperty(P_REPORT_CLASS, reportClass.getCanonicalName());
        properties.setProperty(P_JOB_NAME, autoReportBuildTask==null?"null":autoReportBuildTask.jobName);
        properties.setProperty(P_DATES_SPECIFIED_BY_USER, autoReportBuildTask==null?"false":(autoReportBuildTask.datesSpecifiedByUser?"true":"false"));
    }

    public static void addProperties(Session session, Properties properties, Org org, String prefix) throws Exception {
        String realPrefix = StringUtils.defaultString(prefix);
        properties.setProperty(realPrefix.concat(P_ID_OF_ORG), org.getIdOfOrg().toString());
        properties.setProperty(realPrefix.concat(P_ORG_TAG), org.getTag()==null?"":org.getTag());
        properties.setProperty(realPrefix.concat(P_ORG_NUMBER_IN_NAME), org.getOrgNumberInName());
        properties.setProperty(realPrefix.concat(P_SHORT_NAME), org.getShortName());
        properties.setProperty(realPrefix.concat(P_OFFICIAL_NAME), org.getOfficialName());
        //idOfMenuSourceOrg
        Long idOfMenuSourceOrg = DAOUtils.findMenuExchangeSourceOrg(session, org.getIdOfOrg());
        if (idOfMenuSourceOrg != null)
            properties.setProperty(realPrefix.concat("idOfMenuSourceOrg"), idOfMenuSourceOrg.toString());
        //category
        StringBuilder categories = new StringBuilder();
        for (CategoryOrg categoryOrg : org.getCategories()) {
            categories.append(categoryOrg.getCategoryName()).append(RuleProcessor.DELIMETER);
        }
        properties.setProperty(realPrefix.concat("category"), categories.toString());
    }

    public static void addProperties(Properties properties, ClientGroup clientGroup, String prefix) throws Exception {
        String realPrefix = StringUtils.defaultString(prefix);
        String groupNameKey = realPrefix.concat("groupName");
        if (null == clientGroup) {
            properties.setProperty(groupNameKey, "");
        } else {
            properties.setProperty(groupNameKey, clientGroup.getGroupName());
        }
    }

    public static void addProperties(Properties properties, Person person, String prefix) throws Exception {
        String realPrefix = StringUtils.defaultString(prefix);
        properties.setProperty(realPrefix.concat("surname"), person.getSurname());
        properties.setProperty(realPrefix.concat("firstname"), person.getFirstName());
        properties.setProperty(realPrefix.concat("secondname"), person.getSecondName());
        properties.setProperty(realPrefix.concat("abbreviation"),
                AbbreviationUtils.buildAbbreviation(person.getFirstName(), person.getSurname(),
                        person.getSecondName()));
    }

    public static void addProperties(Properties properties, Client client, String prefix) throws Exception {
        String realPrefix = StringUtils.defaultString(prefix);
        properties.setProperty(realPrefix.concat("idOfClient"), client.getIdOfClient().toString());
        properties.setProperty(realPrefix.concat("email"), client.getEmail());
        addProperties(properties, client.getContractPerson(), realPrefix.concat("contractPerson."));
        addProperties(properties, client.getPerson(), realPrefix.concat("person."));
        properties.setProperty(realPrefix.concat("phone"), client.getPhone());
        properties.setProperty(realPrefix.concat("mobile"), client.getMobile());
        properties.setProperty(realPrefix.concat("address"), client.getAddress());
    }

    public static void addProperties(Properties properties, BasicEvent basicEvent, DateFormat timeFormat)
            throws Exception {
        properties.setProperty("class", basicEvent.getClass().getCanonicalName());
        properties.setProperty("eventTime", timeFormat.format(basicEvent.getEventTime()));
    }

    public static void addProperties(Properties properties, User user, String prefix) throws Exception {
        String realPrefix = StringUtils.defaultString(prefix);
        properties.setProperty(realPrefix.concat("idOfUser"), StringUtils.defaultString(toString(user.getIdOfUser())));
        properties.setProperty(realPrefix.concat("userName"), StringUtils.defaultString(user.getUserName()));
    }

    public static void addProperties(Properties properties, Contragent contragent, String prefix) throws Exception {
        String realPrefix = StringUtils.defaultString(prefix);
        properties.setProperty(realPrefix.concat("idOfContragent"),
                StringUtils.defaultString(toString(contragent.getIdOfContragent())));
        properties.setProperty(realPrefix.concat("contragentName"),
                StringUtils.defaultString(contragent.getContragentName()));
    }

    public static String toString(Long value) throws Exception {
        if (null == value) {
            return null;
        } else {
            return value.toString();
        }
    }
}