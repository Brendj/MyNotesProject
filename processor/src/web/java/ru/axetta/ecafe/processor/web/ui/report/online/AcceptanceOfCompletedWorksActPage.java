/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.AcceptanceOfCompletedWorksAct;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;


/**
 * Created by anvarov on 19.02.2018.
 */
@Component
@Scope(value = "session")
public class AcceptanceOfCompletedWorksActPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(AcceptanceOfCompletedWorksActPage.class);

    private String type;

    private Boolean showAllOrgs = false;

    private String htmlReport = null;

    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);

    @Override
    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public void onReportPeriodChanged() {
        htmlReport = null;
        switch (periodTypeMenu.getPeriodType()) {
            case ONE_DAY: {
                setEndDate(startDate);
            }
            break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
            }
            break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
            }
            break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
            }
            break;
        }
    }

    public void onEndDateSpecified() {
        htmlReport = null;
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if (CalendarUtils.addMonth(CalendarUtils.addOneDay(end), -1).equals(startDate)) {
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff = end.getTime() - startDate.getTime();
            int noOfDays = (int) (diff / (24 * 60 * 60 * 1000));
            switch (noOfDays) {
                case 0:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY);
                    break;
                case 6:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK);
                    break;
                case 13:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK);
                    break;
                default:
                    periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY);
                    break;
            }
        }
        if (startDate.after(endDate)) {
            printError("Дата выборки от меньше дата выборки до");
        }
    }

    public List<SelectItem> getTypes() {
        List<String> types = new ArrayList<String>();
        types.add("Льготное питание");
        types.add("Платное горячее питание");
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (String reg : types) {
            items.add(new SelectItem(reg));
        }
        return items;
    }

    @Override
    public void onShow() throws Exception {
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        htmlReport = null;
        super.completeOrgSelection(session, idOfOrg);
    }

    public Object buildReportHTML() {
        if (this.idOfOrg == null) {
            printError("Не выбрана организация");
            return null;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortFileName = AcceptanceOfCompletedWorksAct.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return null;
        }
        AcceptanceOfCompletedWorksAct.Builder builder = new AcceptanceOfCompletedWorksAct.Builder(templateFilename);
        Properties properties = new Properties();
        properties.put("showAllOrgs", Boolean.toString(showAllOrgs));
        properties.put("type", type);
        builder.setReportProperties(properties);
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            Org org = (Org) session.load(Org.class, idOfOrg);
            builder.setOrg(
                    new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
            BasicReportJob report = builder.build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            if (report != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, report.getPrint());
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                htmlReport = os.toString("UTF-8");
                os.close();
            }
            printMessage("АКТ сдачи-приёмки оказанных услуг");
        } catch (Exception e) {
            printError("Ошибка при построении акта: " + e.getMessage());
            logger.error("Failed build report ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return null;
    }

    public Object clear() {
        showAllOrgs = false;
        idOfOrg = null;
        filter = null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.MONTH).getTime();

        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
        htmlReport = null;
        return null;
    }

    public void showWordList(ActionEvent actionEvent) {
        if (this.idOfOrg == null) {
            printError("Не выбрана организация");
            return;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortFileName = AcceptanceOfCompletedWorksAct.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (!(new File(templateFilename)).exists()) {
            printError(String.format("Не найден файл шаблона '%s'", templateFilename));
            return;
        }

        AcceptanceOfCompletedWorksAct.Builder builder = new AcceptanceOfCompletedWorksAct.Builder(templateFilename);
        Properties properties = new Properties();
        properties.put("showAllOrgs", Boolean.toString(showAllOrgs));
        properties.put("type", type);

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            Org org = (Org) session.load(Org.class, idOfOrg);

            builder.setReportProperties(properties);
            builder.setOrg(
                    new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getShortNameInfoService(),
                            org.getAddress()));
            AcceptanceOfCompletedWorksAct acceptanceOfCompletedWorksAct = (AcceptanceOfCompletedWorksAct) builder
                    .build(session, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            FacesContext facesContext = FacesContext.getCurrentInstance();

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/x-msdownload");
            String filename = buildFileName(acceptanceOfCompletedWorksAct);
            response.setHeader("Content-disposition", String.format("inline;filename=%s", filename));

            JRDocxExporter docxExporter = new JRDocxExporter();
            docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, acceptanceOfCompletedWorksAct.getPrint());
            docxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
            docxExporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "windows-1251");
            docxExporter.setParameter(JRDocxExporterParameter.FLEXIBLE_ROW_HEIGHT, true);
            docxExporter.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (JRException fnfe) {
            logger.error("Failed export act: ", fnfe);
            printError("Не найден шаблон отчета: " + fnfe.getMessage());
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public Boolean getShowAllOrgs() {
        return showAllOrgs;
    }

    public void setShowAllOrgs(Boolean showAllOrgs) {
        this.showAllOrgs = showAllOrgs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String buildFileName(AcceptanceOfCompletedWorksAct acceptanceOfCompletedWorksAct) {
        return String.format("%s.doc", "AcceptanceOfCompletedWorksAct");
    }

    @Override
    public String getPageFilename() {
        return "report/online/acceptance_completed_act";
    }
}
