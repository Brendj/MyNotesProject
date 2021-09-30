/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 16.12.13
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ReferReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(ReferReportPage.class);

    private Date start;
    private Date end;
    private String htmlReport;
    private ReferReport monthlyReport;
    private DailyReferReport dailyReport;
    private String category;
    private String region;
    private List<String> categories;
    private Boolean showDailySales = false;

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    protected static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


    public Date getStart() {
        if(start == null) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            start = cal.getTime();
        }
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        if(end == null) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            end = cal.getTime();
        }
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public List<SelectItem> getRegions() {
        List<String> regions = DAOReadonlyService.getInstance().getRegions();
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(""));
        for(String reg : regions) {
            items.add(new SelectItem(reg));
        }
        return items;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<SelectItem> getCategories() {
        /*if(categories == null || categories.size() < 1) {
            RuntimeContext.getAppContext().getBean(ReferReportPage.class).loadCategories();
        }
        for (String cat : categories) {
            items.add(new SelectItem(cat));
        }*/

        List<SelectItem> items = new ArrayList<SelectItem>();
        if(idOfOrg != null) {
            items.add(new SelectItem(DailyReferReport.SUBCATEGORY_ALL.getName()));

            boolean useSchoolTypes = false;
            boolean useKindergartenTypes = false;
            Org org = DAOReadonlyService.getInstance().findOrg(idOfOrg);
            if(org.getType().ordinal() == OrganizationType.SCHOOL.ordinal()) {
                useSchoolTypes = true;
            } else if(org.getType().ordinal() == OrganizationType.KINDERGARTEN.ordinal()) {
                useKindergartenTypes = true;
            }

            if(useSchoolTypes) {
                for(DailyReferReport.CategoryFilter filter : DailyReferReport.SUBCATEGORY_SHOOL) {
                    items.add(new SelectItem(filter.getName()));
                }
            }
            if(useKindergartenTypes) {
                for(DailyReferReport.CategoryFilter filter : DailyReferReport.SUBCATEGORY_KINDERGARTEN) {
                    items.add(new SelectItem(filter.getName()));
                }
            }
        }
        return items;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getShowMissReportMessage() {
        boolean res = (dailyReport!=null || monthlyReport!=null) && StringUtils.isBlank(htmlReport);
        return res;
    }



    @Transactional
    public void loadCategories() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            categories = DAOUtils.getDiscountRuleSubcategories(session);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public ReferReport getMonthlyReport() {
        return monthlyReport;
    }

    public DailyReferReport getDailyReport() {
        return dailyReport;
    }

    @Override
    public void onShow() throws Exception {
        category = "Все";
    }

    public void clear(){
    }
    
    private static final int MONTHLY_REPORT = 1;
    private static final int DAILY_REPORT   = 2;

    public Boolean getShowDailySales() {
        return showDailySales;
    }

    public void setShowDailySales(Boolean showDailySales) {
        this.showDailySales = showDailySales;
    }

    public void doGenerateMonthly() {
        RuntimeContext.getAppContext().getBean(ReferReportPage.class).generateHTML(MONTHLY_REPORT);
    }

    public void doGenerateDaily() {
        RuntimeContext.getAppContext().getBean(ReferReportPage.class).generateHTML(DAILY_REPORT);
    }

    public void doGenerateMonthlyExcel(ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(ReferReportPage.class).generateXLS(MONTHLY_REPORT);
    }

    public void doGenerateDailyExcel(ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(ReferReportPage.class).generateXLS(DAILY_REPORT);
    }

    public Calendar preprocessReport() {
        monthlyReport = null;
        dailyReport = null;


        if(start == null && end == null) {
            throw new IllegalStateException("Необходимо выбрать хотя бы одну дату (начальную или конечную)");
        }
        if(start == null && end != null) {
            start = updateStartDate(end);
        }
        if(end != null && end.after(start)) {
            end = updateEndDate(end);
        } else {
            end = updateEndDate(start);
        }


        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    @Transactional
    public void generateHTML(int reportType) {
        Session session = null;
        try {
            Calendar cal = preprocessReport();

            session = (Session) entityManager.getDelegate();
            BasicReportJob.OrgShortItem orgItem = getOrgItem();
            switch (reportType) {
                case MONTHLY_REPORT:
                    generateMonthlyReport(session, orgItem, cal, null);
                    if(monthlyReport.getItems().size() > 0) {
                        htmlReport = monthlyReport.getHtmlReport();
                    } else {
                        htmlReport = "";
                    }
                    break;
                case DAILY_REPORT:
                    generateDailyReport(session, orgItem, cal, null);
                    if(dailyReport.getItems().size() > 0) {
                        htmlReport = dailyReport.getHtmlReport();
                    } else {
                        htmlReport = "";
                    }
                    break;
            }
        } catch (Exception e) {
            logAndPrintMessage("Не удалось построить отчет",e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    @Transactional
    public void generateXLS(int reportType) {
        Session session = null;
        try {
            Calendar cal = preprocessReport();

            session = (Session) entityManager.getDelegate();
            BasicReportJob.OrgShortItem orgItem = getOrgItem();
            switch (reportType) {
                case MONTHLY_REPORT:
                    generateMonthlyReport(session, orgItem, cal, getTemplateFileName(ReferReport.class));
                    exportToExcel(monthlyReport);
                    break;
                case DAILY_REPORT:
                    generateDailyReport(session, orgItem, cal, getTemplateFileName(DailyReferReport.class));
                    exportToExcel(dailyReport);
                    break;
            }
        } catch (Exception e) {
            logAndPrintMessage("Не удалось построить отчет",e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    protected String getTemplateFileName(Class clazz) {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + clazz.getSimpleName() + ".jasper";
        return templateFilename;
    }

    public void generateMonthlyReport(Session session, BasicReportJob.OrgShortItem orgItem, Calendar cal, String templateName) throws Exception {
        ReferReport.Builder reportBuilder = null;
        if(templateName != null) {
            reportBuilder = new ReferReport.Builder(templateName);
        } else {
            reportBuilder = new ReferReport.Builder();
        }
        Properties props = addRegionProperty(null, region);
        reportBuilder.setReportProperties(props);
        reportBuilder.setOrg(orgItem);
        try {
            monthlyReport = reportBuilder.build(session, start, end, cal);
        } catch (Exception e) {
            throw e;
            //logAndPrintMessage(String.format("Не удалось построить отчет: %s", e.getMessage()),e);
        }
    }

    public void generateDailyReport(Session session, BasicReportJob.OrgShortItem orgItem, Calendar cal, String templateName) throws Exception {
        DailyReferReport.Builder reportBuilder = null;
        if(templateName != null) {
            reportBuilder = new DailyReferReport.Builder(templateName);
        } else {
            reportBuilder = new DailyReferReport.Builder();
        }
        Properties props = new Properties();
        props.setProperty(DailyReferReport.SUBCATEGORY_PARAMETER, category);
        props.setProperty(DailyReferReport.SHOW_DAILY_SALES_PARAMETER, "" + showDailySales);
        addRegionProperty(props, region);
        reportBuilder.setReportProperties(props);
        reportBuilder.setOrg(orgItem);
        try {
            dailyReport = reportBuilder.build(session, start, end, cal);
        } catch (Exception e) {
            throw e;
            //logAndPrintMessage(String.format("Не удалось построить отчет: %s", e.getMessage()),e);
        }
    }

    public void exportToExcel(BasicReportForAllOrgJob report) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            String reportName = null;
            if(report instanceof ReferReport) {
                reportName =
                        String.format("Справка о расходе средств бюджета города Москвы на питание обучающихся за период с %s по %s",
                                DATE_FORMAT.format(start), DATE_FORMAT.format(end));

            } else if(report instanceof DailyReferReport) {
                if(StringUtils.isBlank(category) || category.equals(DailyReferReport.SUBCATEGORY_ALL.getName())) {
                    reportName =
                            String.format("Сводная справка об услугах питания за счет средств бюджета города Москвы период с %s по %s",
                                    DATE_FORMAT.format(start), DATE_FORMAT.format(end));
                } else {
                    reportName =
                            String.format("Справка по предоставлению бесплатного питания для %s за период с %s по %s",
                                    category, DATE_FORMAT.format(start), DATE_FORMAT.format(end));
                }
            } else {
                reportName =
                    String.format("Справка о расходе средств бюджета города Москвы на питание обучающихся за период %s по %s",
                        DATE_FORMAT.format(start), DATE_FORMAT.format(end));
            }
            reportName = URLEncoder.encode(reportName, "windows-1251").replace("+", "%20") + ".xls";
            response.setHeader("Content-disposition", "attachment; filename*=windows-1251'ru-RU'" + reportName);

            JRXlsExporter xlsExport = new JRXlsExporter();
            //JRCsvExporter csvExporter = new JRCsvExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            //xlsExport.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (JRException fnfe) {
            String message = (fnfe.getCause()==null?fnfe.getMessage():fnfe.getCause().getMessage());
            logAndPrintMessage(String.format("Ошибка при подготовке отчета не найден файл шаблона: %s", message),fnfe);
        } catch (Exception e) {
            getLogger().error("Failed to build sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", e.getMessage()));
        }
    }

    public Date updateStartDate(Date end) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(end.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public Date updateEndDate(Date start) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(start.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public BasicReportJob.OrgShortItem getOrgItem() {
        if (idOfOrg != null) {
            Org org = null;
            if (idOfOrg != null && idOfOrg > -1) {
                org = DAOReadonlyService.getInstance().findOrById(idOfOrg);
            }
            return new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName());
        } else {
            return null;
        }

    }

    @Override
    public String getPageFilename() {
        return "report/online/refer_report";
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public Properties addRegionProperty(Properties props, String region) {
        if(props == null) {
            props = new Properties();
        }
        if(region != null && region.trim().length() > 0) {
            props.put("region", region);
        }
        return props;
    }
}
