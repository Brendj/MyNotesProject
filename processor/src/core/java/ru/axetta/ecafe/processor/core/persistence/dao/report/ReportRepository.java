/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.ReportInfo;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class ReportRepository extends BaseJpaDao {
    private final String REPORT_DELIVERED_SERVICES = "DeliveredServicesReport";
    private final String REPORT_DELIVERED_SERVICES_SUBJECT = "Сводный отчет по услугам (предварительный)";
    private final String REPORT_DELIVERED_SERVICES_APPROVAL = "DeliveredServicesReportApproval";
    private final String REPORT_DELIVERED_SERVICES_APPROVAL_SUBJECT = "Сводный отчет по услугам (электронная сверка)";
    private final String REPORT_REGISTER_STAMP = "RegisterStampReport";
    private final String REPORT_REGISTER_STAMP_SUBJECT = "Реестр талонов по льготному питанию";
    private final String REPORT_REGISTER_STAMP_PAID = "RegisterStampPaidReport";
    private final String REPORT_REGISTER_STAMP_PAID_SUBJECT = "Реестр талонов по платному питанию";
    private final String REPORT_REGISTER_STAMP_SUBSCRIPTION_FEEDING = "RegisterStampSubscriptionFeedingReport";
    private final String REPORT_REGISTER_STAMP_SUBSCRIPTION_FEEDING_SUBJECT = "Реестр талонов по абонементному питанию";
    private final String REPORT_DAILY_SALES_BY_GROUPS_REPORT = "DailySalesByGroupsReport";
    private final String REPORT_DAILY_SALES_BY_GROUPS_REPORT_SUBJECT = "Дневные продажи по категориям";
    private final String REPORT_AUTO_ENTER_EVENTS= "AutoEnterEventByDaysReport";
    private final String REPORT_AUTO_ENTER_EVENTS_BY_CLIENT = "AutoEnterEventByDaysForClientReport";
    private final String REPORT_AUTO_ENTER_EVENTS_SUBJECT = "Сводный отчет по посещению";
    private final String DETAILED_ENTER_EVENT_REPORT = "AutoEnterEventV2Report"; //DetailedEnterEventReport
    private final String DETAILED_ENTER_EVENT_REPORT_SUBJECT = "Детализированный отчет по посещению";
    private final String REPORT_CLIENT_TRANSACTIONS = "ClientTransactionsReport";
    private final String REPORT_CLIENT_TRANSACTIONS_SUBJECT = "Транзакции клиента";
    private final String REPORT_SPENDING_FUNDS_INQUIRY = "SpendingFundsInquiryReport";
    private final String REPORT_SPENDING_FUNDS_INQUIRY_SUBJECT = "Справка расходования средств";
    private final String REPORT_CONSOLIDATE_DISCOUNTS_FOOD_SERVICES = "ConsolidateDiscontsFoodServicesReport";
    private final String REPORT_CONSOLIDATE_DISCOUNTS_FOOD_SERVICES_SUBJECT = "Сводная справка об услугах питания за счет бюджета города";
    private final String REPORT_DISCOUNT_COMPLEXES_IN_ALL_SUPER_CATEGORIES = "DiscountComplexesInAllSuperCategoriesReport";
    private final String REPORT_DISCOUNT_COMPLEXES_IN_ALL_SUPER_CATEGORIES_SUBJECT = "Справка по предоставлению бесплатного питания обучающимся";
    private final String REPORT_ENTER_EVENT_JOURNAL="EnterEventJournalReport";
    private final String REPORT_ENTER_EVENT_JOURNAL_SUBJECT="Журнал посещений";
    private final String REPORT_PREORDER_JOURNAL_REPORT = "VariableFeedingJournalReport";
    private final String REPORT_PREORDER_JOURNAL_REPORT_SUBJECT = "Журнал операций ВП";
    private final String REPORT_ENTER_EVENT_JOURNAL_CALENDAR_FOOD = "FoodDaysCalendarReport";
    private final String REPORT_ENTER_EVENT_JOURNAL_CALENDAR_FOOD_SUBJECT = "Журнал ведения календаря дней питания";


    private static final Logger logger = LoggerFactory.getLogger(ReportRepository.class);

    private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override protected DateFormat initialValue() { return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); }
    };
    private ThreadLocal<DateFormat> dateFormatLetter = new ThreadLocal<DateFormat>() {
        @Override protected DateFormat initialValue() { return new SimpleDateFormat("dd.MM.yyyy"); }
    };

    public static ReportRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(ReportRepository.class);
    }

    public byte[] buildReportAndReturnRawDataByType(String reportType, List<ReportParameter> parameters) throws Exception {
        if (reportType.equals(REPORT_DELIVERED_SERVICES)) {
            return getDeliveredServicesElectronicCollationReport(parameters, REPORT_DELIVERED_SERVICES_SUBJECT);
        } else if (reportType.equals(REPORT_DELIVERED_SERVICES_APPROVAL)) {
            return getDeliveredServicesElectronicCollationApprovalReport(parameters,REPORT_DELIVERED_SERVICES_APPROVAL_SUBJECT);
        } else if (reportType.equals(REPORT_DAILY_SALES_BY_GROUPS_REPORT)) {
            return getDailySalesByGroupsReport(parameters, REPORT_DAILY_SALES_BY_GROUPS_REPORT_SUBJECT);
        } else if (reportType.equals(REPORT_REGISTER_STAMP)) {
            return getRegisterStampReport(parameters, REPORT_REGISTER_STAMP_SUBJECT);
        } else if (reportType.equals(REPORT_REGISTER_STAMP_PAID)) {
            return getRegisterStampPaidReport(parameters, REPORT_REGISTER_STAMP_PAID_SUBJECT);
        } else if (reportType.equals(REPORT_REGISTER_STAMP_SUBSCRIPTION_FEEDING)) {
            return getRegisterStampSubscriptionFeedingReport(parameters,
                    REPORT_REGISTER_STAMP_SUBSCRIPTION_FEEDING_SUBJECT);
        } else if (reportType.equals(REPORT_AUTO_ENTER_EVENTS) ||  (reportType.equals(REPORT_AUTO_ENTER_EVENTS_BY_CLIENT))) {
            return getAutoEnterEventByDaysReport(parameters, REPORT_AUTO_ENTER_EVENTS_SUBJECT);
        } else if (reportType.equals(DETAILED_ENTER_EVENT_REPORT)) {
            return getDetailedEnterEventReport (parameters, DETAILED_ENTER_EVENT_REPORT_SUBJECT);
        } else if (reportType.equals(REPORT_CLIENT_TRANSACTIONS)) {
            return getClientTransactionsReport(parameters, REPORT_CLIENT_TRANSACTIONS_SUBJECT);
        } else if (reportType.equals(REPORT_SPENDING_FUNDS_INQUIRY)) {
            return getReferReport(parameters, REPORT_SPENDING_FUNDS_INQUIRY_SUBJECT);
        } else if (reportType.equals(REPORT_CONSOLIDATE_DISCOUNTS_FOOD_SERVICES)) {
            return getDailyReferReportConsolidated(parameters, REPORT_CONSOLIDATE_DISCOUNTS_FOOD_SERVICES_SUBJECT);
        } else if (reportType.equals(REPORT_DISCOUNT_COMPLEXES_IN_ALL_SUPER_CATEGORIES)) {
            return getDailyReferReportDiscount(parameters, REPORT_DISCOUNT_COMPLEXES_IN_ALL_SUPER_CATEGORIES_SUBJECT);
        } else if (reportType.equals(REPORT_ENTER_EVENT_JOURNAL)) {
            return getEnterEventJournal(parameters, REPORT_ENTER_EVENT_JOURNAL_SUBJECT);
        } else if (reportType.equals(REPORT_PREORDER_JOURNAL_REPORT)) {
            return getPreorderJournal(parameters, REPORT_PREORDER_JOURNAL_REPORT_SUBJECT);
        } else if (reportType.equals(REPORT_ENTER_EVENT_JOURNAL_CALENDAR_FOOD)) {
            return getFoodDayJournal(parameters, REPORT_ENTER_EVENT_JOURNAL_CALENDAR_FOOD_SUBJECT);
        }
        return null;
    }

    private byte[] getDeliveredServicesElectronicCollationReport(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildDeliveredServicesReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] arr = stream.toByteArray();
        postReportToEmails(subject, reportParameters, arr);
        return arr;
    }

    private byte[] getDeliveredServicesElectronicCollationApprovalReport(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildDeliveredServicesElectronicCollationReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] arr = stream.toByteArray();
        postReportToEmails(subject, reportParameters, arr);
        return arr;
    }



    private byte[] getRegisterStampReport(List<ReportParameter> parameters,String subject) throws Exception {
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null;
        }
        BasicJasperReport jasperReport = buildRegisterStampReport(reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getRegisterStampPaidReport(List<ReportParameter> parameters, String subject) throws Exception {
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null;
        }
        BasicJasperReport jasperReport = buildRegisterStampPaidReport(reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getRegisterStampSubscriptionFeedingReport(List<ReportParameter> parameters, String subject) throws Exception {
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null;
        }
        BasicJasperReport jasperReport = buildRegisterStampSubscriptionFeedingReport(reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getDailySalesByGroupsReport(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildDailySalesByGroupsReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getAutoEnterEventByDaysReport(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildAutoEnterEventByDaysReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }


    private byte[] getDetailedEnterEventReport (List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildDetailedEnterEventReport (session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPagesOrZero(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getClientTransactionsReport(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildClientTransactionsReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPagesOrZero(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getReferReport(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildReferReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getDailyReferReportConsolidated(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildDailyReferReportConsolidated(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getDailyReferReportDiscount(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildDailyReferReportDiscount(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getEnterEventJournal(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildEnterEventJournalReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPagesOrZero(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getPreorderJournal(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildPreorderJournalReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPagesOrZero(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private byte[] getFoodDayJournal(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        ReportParameters reportParameters = new ReportParameters(parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildFoodDayJournalReport(session, reportParameters);
        if (jasperReport == null || isEmptyReportPrintPagesOrZero(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] rawDataReport = stream.toByteArray();
        postReportToEmails(subject, reportParameters, rawDataReport);
        return rawDataReport;
    }

    private boolean isEmptyReportPrintPages(BasicJasperReport deliveredServicesReport) {
        return deliveredServicesReport.getPrint().getPages() != null
                && deliveredServicesReport.getPrint().getPages().get(0).getElements().size() == 0;
    }

    private boolean isEmptyReportPrintPagesOrZero(BasicJasperReport deliveredServicesReport) {
        return deliveredServicesReport.getPrint().getPages() != null
                && deliveredServicesReport.getPrint().getPages().size() == 0;
    }

    private void postReportToEmails(String subject, ReportParameters reportParameters, byte[] arr) {
        String email = reportParameters.getEmail();
        if (email != null && !email.isEmpty()) {
            String[] emails = email.split(";");
            DateFormat df = dateFormatLetter.get();
            for (String em : emails) {
                postReport(em, subject + String.format(" (%s - %s)", df.format(reportParameters.getStartDate()), df.format(reportParameters.getEndDate())), arr);
            }
        }
    }

    private BasicJasperReport buildPreorderJournalReport(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + PreorderJournalReport.class.getSimpleName() + ".jasper";
        PreorderJournalReport.Builder builder = new PreorderJournalReport.Builder(templateFilename);
        try {
            Long idOrg;
            Properties properties = new Properties();
            if (reportParameters.getIdOfOrg() == null) {
                idOrg = reportParameters.getSourceOrg();


                List<Long> idOfOrgList = new ArrayList<>();
                //Добавляем главный корпус
                idOfOrgList.add(idOrg);

                //Добавляем все дружественные корпуса
                List<Org> friendlyOrgs = DAOUtils.findAllFriendlyOrgs(session, idOrg);
                if (!friendlyOrgs.isEmpty())
                {
                    for (Org frOrg: friendlyOrgs)
                    {
                        idOfOrgList.add(frOrg.getIdOfOrg());
                    }
                }

                properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG,
                        StringUtils.join(idOfOrgList.iterator(), ","));
            }
            else {
                idOrg = reportParameters.getIdOfOrg();
                properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, idOrg.toString());
            }

            if(reportParameters.getIdOfContract() != null){
                Client client = DAOUtils.findClientByContractId(session, reportParameters.getIdOfContract());
                properties.setProperty(PreorderJournalReport.P_ID_OF_CLIENTS, client.getIdOfClient().toString());
            }
            properties.setProperty(PreorderJournalReport.P_LINE_SEPARATOR, "\n");
            builder.setReportProperties(properties);

            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(), new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        } catch (Exception e){
            logger.error("Failure to build a report", e);
            return null;
        }
    }

    private BasicJasperReport buildFoodDayJournalReport(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + FoodDaysCalendarReport.class.getSimpleName() + ".jasper";
        FoodDaysCalendarReportBuilder builder = new FoodDaysCalendarReportBuilder(templateFilename);
        try {
            Properties properties = new Properties();
            Long idOrg = reportParameters.getIdOfOrg();
            if(idOrg == null)
                throw new EntityNotFoundException();
            List<Long> idOfOrgList = new ArrayList<>();
            idOfOrgList.add(idOrg);
            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, StringUtils.join(idOfOrgList.iterator(), ","));
            if (reportParameters.getGroupName() != null )
                properties.setProperty("selectGroupName", reportParameters.getGroupName());
            properties.setProperty("allOrg", reportParameters.getIsAllFriendlyOrgs());
            builder.setReportProperties(properties);
            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(), new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        } catch (Exception e){
            logger.error("Failure to build a report", e);
            return null;
        }
    }

    private BasicJasperReport buildDeliveredServicesElectronicCollationReport(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + DeliveredServicesElectronicCollationReport.class.getSimpleName() + ".jasper";
        DeliveredServicesElectronicCollationReportBuilder builder = new DeliveredServicesElectronicCollationReportBuilder(
                templateFilename);
        builder.setOrg(reportParameters.getIdOfOrg());
        try {
            BasicJasperReport deliveredServicesReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(),
                            new GregorianCalendar(), reportParameters.getIdOfOrg(), reportParameters.getIdOfContragent(), reportParameters.getIdOfContract(),
                            reportParameters.getRegion(), false, false);
            return deliveredServicesReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;  //не найдена организация
        }
    }

    private BasicJasperReport buildDeliveredServicesReport(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + DeliveredServicesReport.class.getSimpleName() + ".jasper";
        DeliveredServicesReportBuilder builder = new DeliveredServicesReportBuilder(
                templateFilename);
        builder.setOrg(reportParameters.getIdOfOrg());
        try {
            BasicJasperReport deliveredServicesReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(),
                            new GregorianCalendar(), reportParameters.getIdOfOrg(), reportParameters.getIdOfContragent(), reportParameters.getIdOfContract(),
                            reportParameters.getRegion(), false, false);
            return deliveredServicesReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;  //не найдена организация
        }
    }

    private BasicJasperReport buildRegisterStampReport(ReportParameters reportParameters) throws Exception {
        String templateFilename = getAutoReportGenerator().getReportsTemplateFilePath() + RegisterStampReport.class.getSimpleName() + ".jasper";
        RegisterStampNewReport.Builder builder = new RegisterStampNewReport.Builder(templateFilename);
        return buildCommonRegisterStampReport(builder,reportParameters);
    }

    private BasicJasperReport buildRegisterStampPaidReport(ReportParameters reportParameters) throws Exception {
        String templateFilename = getAutoReportGenerator().getReportsTemplateFilePath() + RegisterStampPaidReport.class.getSimpleName() + ".jasper";
        RegisterStampPaidReport.Builder builder = new RegisterStampPaidReport.Builder(templateFilename);
        return buildCommonRegisterStampReport(builder,reportParameters);
    }

    private BasicJasperReport buildRegisterStampSubscriptionFeedingReport(ReportParameters reportParameters) throws Exception {
        String templateFilename =
                getAutoReportGenerator().getReportsTemplateFilePath() + RegisterStampSubscriptionFeedingReport.class.getSimpleName() + ".jasper";
        RegisterStampSubscriptionFeedingReport.Builder builder = new RegisterStampSubscriptionFeedingReport.Builder(
                templateFilename);
        return buildCommonRegisterStampReport(builder, reportParameters);
    }

    private BasicJasperReport buildCommonRegisterStampReport(BasicReportJob.Builder builder, ReportParameters reportParameters) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        Org org = (Org) session.load(Org.class, reportParameters.getIdOfOrg());
        builder.setOrg(new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName(),
                org.getAddress()));
        Properties properties = new Properties();
        addContractProperties(properties, org);
        builder.setReportProperties(properties);
        BasicJasperReport jasperReport = builder
                .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(), new GregorianCalendar());
        return jasperReport;
    }

    private BasicJasperReport buildDailySalesByGroupsReport(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + DailySalesByGroupsReport.class.getSimpleName() + ".jasper";
        DailySalesByGroupsReport.Builder builder = new DailySalesByGroupsReport.Builder(templateFilename);
        try {
            List<BasicReportJob.OrgShortItem> orgShortItemList = new ArrayList<>();
            Org org;

            if (reportParameters.getIdOfOrg() == null) {

                org = (Org) session.load(Org.class, reportParameters.getSourceOrg());

                //Добавляем главный корпус
                orgShortItemList.add(new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                        org.getShortName(), org.getOfficialName(), org.getAddress()));

                //Добавляем все дружественные корпуса
                List<Org> friendlyOrgs = DAOUtils.findAllFriendlyOrgs(session, org.getIdOfOrg());
                for (Org orgFriend: friendlyOrgs)
                {
                    orgShortItemList.add(new BasicReportJob.OrgShortItem(orgFriend.getIdOfOrg(),
                            orgFriend.getShortName(), orgFriend.getOfficialName(), orgFriend.getAddress()));
                }
                builder.setOrgShortItemList(orgShortItemList);
            }
            else {
                org = (Org) session.load(Org.class, reportParameters.getIdOfOrg());

                BasicReportJob.OrgShortItem orgShortItem = new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                        org.getShortName(), org.getOfficialName(), org.getAddress());
                builder.setOrgShortItemList(Arrays.asList(orgShortItem));
            }

            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(), new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        }
    }

    private BasicJasperReport buildAutoEnterEventByDaysReport(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFilename;

        if(reportParameters.getIdOfContract() == null){
            templateFilename =
                    autoReportGenerator.getReportsTemplateFilePath() + AutoEnterEventByDaysReport.class.getSimpleName() + ".jasper";
        }
        else
            templateFilename = autoReportGenerator.getReportsTemplateFilePath() + AutoEnterEventByDaysReport.TEMPLATE_FILE_NAMES_FOR_CLIENT;

        AutoEnterEventByDaysReport.Builder builder = new AutoEnterEventByDaysReport.Builder(templateFilename);
        try {
            Org org = (Org) session.load(Org.class, reportParameters.getIdOfOrg());
            BasicReportJob.OrgShortItem orgShortItem = new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                    org.getShortName(), org.getOfficialName(), org.getAddress());
            builder.setOrg(orgShortItem);
            builder.setOrgShortItemList(Arrays.asList(orgShortItem));
            if(reportParameters.getEnterEventType() != null) {
                builder.getReportProperties().setProperty("enterEventType", RuleCondition.ENTEREVENT_TYPE_TEXT[Integer.parseInt(reportParameters.getEnterEventType())]);
            }

            Properties properties = new Properties();

            if (reportParameters.getGroupName() != null) {
                properties.setProperty("groupName", reportParameters.getGroupName());
            }

            if(reportParameters.getIdOfContract() != null){
                Client client = DAOService.getInstance().getClientByContractId(reportParameters.getIdOfContract());
                properties.setProperty(AutoEnterEventByDaysReport.P_ID_CLIENT, client.getIdOfClient().toString());
            }

            //Изменено 27.08.19

            //if (reportParameters.getIsAllFriendlyOrgs() != null) {
            //    properties.setProperty("isAllFriendlyOrgs", reportParameters.getIsAllFriendlyOrgs());
            //} else {
            //    properties.setProperty("isAllFriendlyOrgs", "true");
            //}
            properties.setProperty("isAllFriendlyOrgs", "true");

            builder.setReportProperties(properties);

            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(), new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        }
    }

    private BasicJasperReport buildDetailedEnterEventReport (Session session, ReportParameters reportParameters)
            throws Exception {

        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFilename = "DetailedEnterEventReport.jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFilename;

        DetailedEnterEventReport.Builder builder = new DetailedEnterEventReport.Builder(templateFilename);
        try {
            Properties properties = new Properties();


            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, reportParameters.getIdOfOrg().toString());

            if (reportParameters.getGroupName() != null) {
                properties.setProperty("groupName", reportParameters.getGroupName());
            }

            if (reportParameters.getIdOfContract() != null) {
                Client client =  DAOService.getInstance().getClientByContractId(reportParameters.getIdOfContract());
                properties.setProperty(DetailedEnterEventReport.P_ID_OF_CLIENTS, client.getIdOfClient().toString());
            }

            if (reportParameters.getIsAllFriendlyOrgs() != null) {
                properties.setProperty(DetailedEnterEventReport.P_ALL_FRIENDLY_ORGS, reportParameters.getIsAllFriendlyOrgs());
            } else {
                properties.setProperty(DetailedEnterEventReport.P_ALL_FRIENDLY_ORGS, "true");
            }

            builder.setReportProperties(properties);

            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(), new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        }
    }

    private BasicJasperReport buildEnterEventJournalReport(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + EnterEventJournalReport.class.getSimpleName() + ".jasper";
        EnterEventJournalReport.Builder builder = new EnterEventJournalReport.Builder(templateFilename);
        try {
            Org org = (Org) session.load(Org.class, reportParameters.getIdOfOrg());
            if(org == null){
                throw new EntityNotFoundException("Not found org by ID: " + reportParameters.getIdOfOrg());
            }

            BasicReportJob.OrgShortItem orgShortItem = new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                    org.getShortName(), org.getOfficialName(), org.getAddress());
            builder.setOrg(orgShortItem);
            builder.setOrgShortItemList(Arrays.asList(orgShortItem));
            builder.setIdOfOrg(reportParameters.getIdOfOrg());

            Properties properties = new Properties();
            if (reportParameters.getGroupName() != null) {
                properties.setProperty("groupName", reportParameters.getGroupName());
            }
            if(reportParameters.getOutputMigrants() != null){
                properties.setProperty("outputMigrants", reportParameters.getOutputMigrants());
            }
            if(reportParameters.getSortedBySections() != null){
                properties.setProperty("sortedBySections", reportParameters.getSortedBySections());
            }
            if(reportParameters.getIdOfContract() != null){
                Client client = DAOService.getInstance().getClientByContractId(reportParameters.getIdOfContract());
                properties.setProperty(EnterEventJournalReport.P_ID_CLIENT, client.getIdOfClient().toString());
            }
            builder.setReportProperties(properties);

            boolean isAllFriendlyOrgs;
            if (reportParameters.getIsAllFriendlyOrgs() == null) {
                isAllFriendlyOrgs = true;
            } else {
                isAllFriendlyOrgs = Boolean.parseBoolean(reportParameters.getIsAllFriendlyOrgs());
            }
            builder.setAllFriendlyOrgs(isAllFriendlyOrgs);

            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(), new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        } catch (Exception e){
            logger.error("Failure to build a report", e);
            return null;
        }
    }

    private BasicJasperReport buildClientTransactionsReport(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFileName =
                autoReportGenerator.getReportsTemplateFilePath() + ClientTransactionsReport.class.getSimpleName()
                        + ".jasper";
        ClientTransactionsReport.Builder builder = new ClientTransactionsReport.Builder(templateFileName);
        try {
            Client client = DAOUtils.findClientByContractId(session, reportParameters.getIdOfContract());
            Org org = client.getOrg();
            Properties properties = new Properties();
            properties.setProperty("idOfOrgList", String.valueOf(org.getIdOfOrg()));
            properties.setProperty("clientList", String.valueOf(client.getIdOfClient()));
            properties.setProperty("operationType", "0");
            properties.setProperty("showAllBuildings", "true");
            builder.setReportProperties(properties);
            builder.setFilterType(ClientTransactionsReport.FilterType.Client);
            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(),
                            new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        }
    }

    private BasicJasperReport buildReferReport(Session session, ReportParameters reportParameters) throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFileName =
                autoReportGenerator.getReportsTemplateFilePath() + ReferReport.class.getSimpleName() + ".jasper";
        ReferReport.Builder builder = new ReferReport.Builder(templateFileName);
        try {

            Long idOfOrg = reportParameters.getIdOfOrg();
            Org org = (Org) session.load(Org.class, idOfOrg);
            BasicReportJob.OrgShortItem orgShortItem = new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                    org.getShortName(), org.getOfficialName(), org.getAddress());

            builder.setOrg(orgShortItem);
            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(),
                            new GregorianCalendar());

            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        }
    }

    private BasicJasperReport buildDailyReferReportConsolidated(Session session, ReportParameters reportParameters) throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFileName =
                autoReportGenerator.getReportsTemplateFilePath() + DailyReferReport.class.getSimpleName() + ".jasper";
        DailyReferReport.Builder builder = new DailyReferReport.Builder(templateFileName);
        try {

            Long idOfOrg = reportParameters.getIdOfOrg();
            Org org = (Org) session.load(Org.class, idOfOrg);
            BasicReportJob.OrgShortItem orgShortItem = new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                    org.getShortName(), org.getOfficialName(), org.getAddress());
            builder.setOrg(orgShortItem);
            Properties props = new Properties();
            props.setProperty(DailyReferReport.SUBCATEGORY_PARAMETER, DailyReferReport.SUBCATEGORY_ALL.getName());
            builder.setReportProperties(props);
            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(),
                            new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        }
    }

    private BasicJasperReport buildDailyReferReportDiscount(Session session, ReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = getAutoReportGenerator();
        String templateFileName =
                autoReportGenerator.getReportsTemplateFilePath() + DailyReferReport.class.getSimpleName() + ".jasper";
        DailyReferReport.Builder builder = new DailyReferReport.Builder(templateFileName);
        try {

            Long idOfOrg = reportParameters.getIdOfOrg();
            Org org = (Org) session.load(Org.class, idOfOrg);
            BasicReportJob.OrgShortItem orgShortItem = new BasicReportJob.OrgShortItem(org.getIdOfOrg(),
                    org.getShortName(), org.getOfficialName(), org.getAddress());

            builder.setOrg(orgShortItem);
            Properties props = new Properties();
            props.setProperty(DailyReferReport.SUBCATEGORY_PARAMETER,
                    DailyReferReport.SUBCATEGORY_SHOOL[reportParameters.getCategory()].getName());

            builder.setReportProperties(props);
            BasicJasperReport jasperReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(),
                            new GregorianCalendar());
            return jasperReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;
        }
    }

    private AutoReportGenerator getAutoReportGenerator() {
        return RuntimeContext.getInstance().getAutoReportGenerator();
    }

    private void addContractProperties(Properties properties, Org org) {
        Contract orgContract = org.getContract();
        properties.setProperty("contractNumber", orgContract != null ? orgContract.getContractNumber() : "           ");
        DateFormat formatter = new SimpleDateFormat("\"dd\" MMMMM yyyyг.", new Locale("ru"));
        properties.setProperty("contractDate", orgContract != null ? CalendarUtils.replaceMonthNameByGenitive(
                formatter.format(CalendarUtils.addOneDay(org.getContract().getDateOfConclusion()))) : "           г.");
    }



    private ByteArrayOutputStream exportReportToJRXls(BasicJasperReport deliveredServicesReport) throws JRException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JRXlsExporter xlsExport = new JRXlsExporter();
        xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, deliveredServicesReport.getPrint());
        xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, stream);
        xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
        xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
        xlsExport.exportReport();
        xlsExport.reset();
        return stream;
    }


    public boolean areParametersBad(List<ReportParameter> parameters) {
        Date startDate = null;
        Date endDate = null;
        Long idOfOrg = null;
        Long sourceOrg = null;
        DateFormat safeDateFormat = dateFormat.get();
        try {
            for (ReportParameter parameter : parameters) {
                if (parameter.getParameterName().equals("startDate")) {
                    startDate = safeDateFormat.parse(parameter.getParameterValue());
                }
                else if (parameter.getParameterName().equals("endDate")) {
                    endDate = safeDateFormat.parse(parameter.getParameterValue());
                }
                else if (parameter.getParameterName().equals("idOfOrg")) {
                    idOfOrg = Long.parseLong(parameter.getParameterValue());
                }
                else if (parameter.getParameterName().equals("sourceOrg")) {
                    sourceOrg = Long.parseLong(parameter.getParameterValue());
                }
            }
        } catch (Exception e) {
            return true;
        }
        if ((idOfOrg == null && sourceOrg == null) || startDate == null || endDate == null || startDate.after(endDate)) {
            return true; //не переданы или заполнены с ошибкой обязательные параметры
        } else {
            return false;
        }
    }

    private void postReport(String address, String subject, byte[] report) {
        ReportPoster poster = new ReportPoster(RuntimeContext.getInstance().getPostman(), address, subject, report);
        new Thread(poster).start();
    }

    public List<ReportInfoItem> getReportInfos(Long idOfOrg, Date startDate, Date endDate) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        HashMap<Long, Org> orgMap = new HashMap<Long, Org>();
        Org org = (Org)session.load(Org.class, idOfOrg);
        orgMap.put(idOfOrg, org);
        List org_ids = new ArrayList<Long>();
        for (Org fOrg : org.getFriendlyOrg()) {
            org_ids.add(fOrg.getIdOfOrg());
            orgMap.put(fOrg.getIdOfOrg(), (Org)session.load(Org.class, fOrg.getIdOfOrg()));
        }

        startDate = CalendarUtils.truncateToDayOfMonth(startDate);
        endDate = CalendarUtils.endOfDay(endDate);

        Criteria criteria = session.createCriteria(ReportInfo.class);
        criteria.add(Restrictions.in("idOfOrg", org_ids));
        criteria.add(Restrictions.between("createdDate", startDate, endDate));
        criteria.add(Restrictions.ilike("reportFile", DeliveredServicesReport.class.getSimpleName(), MatchMode.ANYWHERE));
        List<ReportInfo> reportInfo = criteria.list();
        List<ReportInfoItem> result = new ArrayList<ReportInfoItem>();
        String basePath = (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.processor.autoreport.path");
        for(ReportInfo info : reportInfo) {
            File file = new File(basePath + info.getReportFile());
            if (file.exists() && file.isFile()) {
                Org o = orgMap.get(info.getIdOfOrg());
                ReportInfoItem item = new ReportInfoItem(info.getReportName(), o.getShortName(),
                        o.getAddress(), info.getCreatedDate(), info.getStartDate(), info.getEndDate(), info.getIdOfReportInfo());
                result.add(item);
            }
        }
        return result;
    }

    public byte[] getRepositoryReportById(Long idOfReport) throws Exception {
        byte[] result = null;
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ReportInfo.class);
        criteria.add(Restrictions.eq("idOfReportInfo", idOfReport));
        ReportInfo report = (ReportInfo)criteria.uniqueResult();
        String basePath = (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.processor.autoreport.path");
        if (report != null && basePath != null && report.getReportFile() != null) {
            File file = new File(basePath + report.getReportFile());
            if (file.exists() && file.isFile()) {
                InputStream stream = new FileInputStream(file);
                result = IOUtils.toByteArray(stream);
            }
        }
        return result;
    }




    private class ReportParameters {
        private List<ReportParameter> parameters;
        private Date startDate;
        private Date endDate;
        private Long idOfOrg;
        private Long sourceOrg;
        private Long idOfContragent;
        private Long idOfContract;
        private String region;
        private String email;
        private String enterEventType;
        private Integer category;
        private String groupName;
        private String isAllFriendlyOrgs;
        private String outputMigrants;   // receive from ARM 1 or 0
        private String sortedBySections; // receive from ARM 1 or 0

        public ReportParameters(List<ReportParameter> parameters) {
            this.parameters = parameters;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public Long getIdOfContract() {
            return idOfContract;
        }

        public String getRegion() {
            return region;
        }

        public String getEmail() {
            return email;
        }

        public String getEnterEventType() {
            return enterEventType;
        }

        public Integer getCategory() {
            return category;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getIsAllFriendlyOrgs() {
            return isAllFriendlyOrgs;
        }

        public ReportParameters parse() throws ParseException {
            startDate = null;
            endDate = null;
            idOfOrg = null;
            idOfContragent = null;
            idOfContract = null;
            region = null;
            email = null;
            enterEventType = null;
            category = null;
            groupName = null;
            isAllFriendlyOrgs = null;
            outputMigrants = null;
            sortedBySections = null;

            DateFormat safeDateFormat = dateFormat.get();
            for (ReportParameter parameter : parameters) {
                if (parameter.getParameterName().equals("startDate")) {
                    startDate = safeDateFormat.parse(parameter.getParameterValue());
                    startDate = CalendarUtils.truncateToDayOfMonth(startDate);
                }
                else if (parameter.getParameterName().equals("endDate")) {
                    endDate = safeDateFormat.parse(parameter.getParameterValue());
                    endDate = CalendarUtils.endOfDay(endDate);
                }
                else if (parameter.getParameterName().equals("idOfOrg")) {
                    idOfOrg = Long.parseLong(parameter.getParameterValue());
                }
                else if (parameter.getParameterName().equals("sourceOrg")) {
                    sourceOrg = Long.parseLong(parameter.getParameterValue());
                }
                else if (parameter.getParameterName().equals("idOfContragent")) {
                    idOfContragent = Long.parseLong(parameter.getParameterValue());
                }
                else if (parameter.getParameterName().equals("idOfContract")) {
                    idOfContract = Long.parseLong(parameter.getParameterValue());
                }
                else if (parameter.getParameterName().equals("region")) {
                    region = parameter.getParameterValue();
                }
                else if (parameter.getParameterName().equals("email")) {
                    email = parameter.getParameterValue();
                }
                else if (parameter.getParameterName().equals("enterEventType")) {
                    enterEventType = parameter.getParameterValue();
                }
                else if (parameter.getParameterName().equals("categories")) {
                    category = Integer.valueOf(parameter.getParameterValue());
                }
                else if (parameter.getParameterName().equals("clientGroupName")) {
                    groupName = parameter.getParameterValue();
                }
                else if (parameter.getParameterName().equals("isAllFriendlyOrgs")) {
                    isAllFriendlyOrgs = parameter.getParameterValue();
                }
                else if(parameter.getParameterName().equals("outputMigrants")){
                    outputMigrants = Boolean.toString(parameter.getParameterValue().equals("1"));
                }
                else if(parameter.getParameterName().equals("sortedBySections")){
                    sortedBySections = Boolean.toString(parameter.getParameterValue().equals("1"));
                }
            }
            return this;
        }

        public boolean checkRequiredParameters() {
            //Либо указана целеваю организация, либо источник запроса
            return   (idOfOrg != null || sourceOrg != null) && startDate != null
                    && endDate != null;
        }


        public String getOutputMigrants() {
            return outputMigrants;
        }

        public void setOutputMigrants(String outputMigrants) {
            this.outputMigrants = outputMigrants;
        }

        public String getSortedBySections() {
            return sortedBySections;
        }

        public void setSortedBySections(String sortedBySections) {
            this.sortedBySections = sortedBySections;
        }

        public Long getSourceOrg() {
            return sourceOrg;
        }

        public void setSourceOrg(Long sourceOrg) {
            this.sourceOrg = sourceOrg;
        }
    }
}
