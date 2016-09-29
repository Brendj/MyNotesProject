/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.contragent.ContragentReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ReportDAOService;
import ru.axetta.ecafe.processor.core.report.TotalSalesReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Онлайн отчеты / Сводный отчет по продажам
 * User: shamil
 * Date: 27.01.15
 * Time: 15:52
 */
@Component
@Scope("session")
public class TotalSalesPage extends OnlineReportPage implements ContragentSelectPage.CompleteHandler{

    private final static Logger logger = LoggerFactory.getLogger(TotalSalesPage.class);

    @Autowired
    private ReportDAOService daoService;
    private String htmlReport = null;
    private Boolean includeActDiscrepancies = true;
    private PeriodTypeMenu periodTypeMenu = new PeriodTypeMenu(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
    private Long contragentId = -1L;
    private List<SelectItem> contragentsSelectItems;
    private List<String> titlesComplex;
    private List<String>  titleAndSumList;
    private HashMap<String, String> titleAndSumMap;

    private Integer[] preferentialTitleComplexes;

    public Integer[] getPreferentialTitleComplexes() {
        return preferentialTitleComplexes;
    }

    public void setPreferentialTitleComplexes(Integer[] preferentialTitleComplexes) {
        this.preferentialTitleComplexes = preferentialTitleComplexes;
    }

    private boolean showDetail;

    public boolean isShowDetail() {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    public List<SelectItem> getAvailableTitleComplexes() {

        List<SelectItem> list = new ArrayList<SelectItem>();
        String[] productionNamesTypes = OrderDetail.PRODUCTION_NAMES_TYPES;
        int i = 0;
        for (String productionNameType: productionNamesTypes) {
            SelectItem selectItem = new SelectItem(i, "Буфет ".concat(productionNameType));
            list.add(selectItem);
            i++;
        }

        if (contragent != null) {
            List<String> complexesWithPriceTitles;

            Long idOfContragent = contragent.getIdOfContragent();
            complexesWithPriceTitles = getTitlesComplexesWithPriceByContragent(startDate, endDate, idOfContragent);

            if (!complexesWithPriceTitles.isEmpty() && complexesWithPriceTitles != null) {

                for (String title: complexesWithPriceTitles) {
                    SelectItem selectItem = new SelectItem(i, title);
                    list.add(selectItem);
                    i++;
                }
            }
        }

        contragentsSelectItems = list;

        return list;
    }

    public PeriodTypeMenu getPeriodTypeMenu() {
        return periodTypeMenu;
    }

    public void onReportPeriodChanged(ActionEvent event) {
        htmlReport = null;
        switch (periodTypeMenu.getPeriodType()){
            case ONE_DAY: {
                setEndDate(startDate);
            } break;
            case ONE_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 6));
            } break;
            case TWO_WEEK: {
                setEndDate(CalendarUtils.addDays(startDate, 13));
            } break;
            case ONE_MONTH: {
                setEndDate(CalendarUtils.addDays(CalendarUtils.addMonth(startDate, 1), -1));
            } break;
        }
    }

    public void onEndDateSpecified(ActionEvent event) {
        htmlReport = null;
        Date end = CalendarUtils.truncateToDayOfMonth(endDate);
        if(CalendarUtils.addMonth(CalendarUtils.addOneDay(end), -1).equals(startDate)){
            periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        } else {
            long diff=end.getTime()-startDate.getTime();
            int noOfDays=(int)(diff/(24*60*60*1000));
            switch (noOfDays){
                case 0: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_DAY); break;
                case 6: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_WEEK); break;
                case 13: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.TWO_WEEK); break;
                default: periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.FIXED_DAY); break;
            }
        }
        if(startDate.after(endDate)){
            printError("Дата выборки от меньше дата выборки до");
        }
    }

    @Override
    public void onShow() throws Exception {
        contragentsSelectItems = new ArrayList<SelectItem>();
        ContragentReadOnlyRepository contragentReadOnlyRepository = ContragentReadOnlyRepository.getInstance();
        for(Contragent contragent :contragentReadOnlyRepository.findAllByType(Contragent.TSP)){
            contragentsSelectItems.add(new SelectItem(contragent.getIdOfContragent(), contragent.getContragentName()));
        }
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public List<String> getTitlesComplexes() {
        List<String> titlesComplexList = new ArrayList<String>();

        if (preferentialTitleComplexes != null) {
            if (preferentialTitleComplexes.length > 0) {
                for (Integer prefer : preferentialTitleComplexes) {
                    titlesComplexList.add(contragentsSelectItems.get(prefer).getLabel());
                }
            }
        }
        return titlesComplexList;
    }

    public String getStringTitleComplexes(String titleComplexesString) {
        titlesComplex = getTitlesComplexes();

        for (String titleComplexItem: titlesComplex) {
            titleComplexesString = titleComplexesString.concat(titleComplexItem).concat(",");
        }
        return titleComplexesString;
    }

    private List<String> getTitleAndSums() {
        List<String> titleAndSumList = new ArrayList<String>();

        if (preferentialTitleComplexes != null) {
            if (preferentialTitleComplexes.length > 0) {
                for (Integer prefer : preferentialTitleComplexes) {
                    if (titleAndSumMap != null && titleAndSumMap.get(contragentsSelectItems.get(prefer).getLabel()) != null) {
                        titleAndSumList.add(contragentsSelectItems.get(prefer).getLabel() + "," + titleAndSumMap.get(contragentsSelectItems.get(prefer).getLabel()));
                    }
                }
            }
        }
        return titleAndSumList;
    }

    public String getTitleAndSumByString(String titleAndSumListString) {
        titleAndSumList = getTitleAndSums();

        for (String titleAndSumItem: titleAndSumList) {
            titleAndSumListString = titleAndSumListString.concat(titleAndSumItem).concat(";");
        }
        return titleAndSumListString;
    }

    public Object buildReportHTML() {

        String titleComplexesString = getStringTitleComplexes("");

        String titleAndSumString = getTitleAndSumByString("");

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortFileName = TotalSalesReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return null;
        }
        TotalSalesReport.Builder builder = new TotalSalesReport.Builder(templateFilename);
        if(contragent!= null){
            builder.setContragent(contragent);
        }
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            builder.getReportProperties().setProperty("titleComplexes", titleComplexesString);
            builder.getReportProperties().setProperty("titleAndSumList", titleAndSumString);
            builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());

            BasicReportJob report =  builder.build(session,startDate, endDate, localCalendar);
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
            printMessage("Отчет построен");
        } catch (Exception e) {
            printError("Ошибка при построении отчета: "+e.getMessage());
            logger.error("Failed build report: " + e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return null;
    }

    public Object clear(){

        filter=null;
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
        includeActDiscrepancies = true;
        htmlReport = null;
        contragent = null;
        periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        preferentialTitleComplexes = null;
        return null;
    }

    public void showCSVList(ActionEvent actionEvent){
        String titleComplexesString = getStringTitleComplexes("");

        String titleAndSumString = getTitleAndSumByString("");

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortFileName = TotalSalesReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if(!(new File(templateFilename)).exists()){
            printError(String.format("Не найден файл шаблона '%s'", templateShortFileName));
            return;
        }
        Date generateTime = new Date();
        TotalSalesReport.Builder builder = new TotalSalesReport.Builder(templateFilename);
        if(contragent!= null){
            builder.setContragent(contragent);
        }
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            builder.getReportProperties().setProperty("titleComplexes", titleComplexesString);
            builder.getReportProperties().setProperty("titleAndSumList", titleAndSumString);
            builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());

            TotalSalesReport totalSalesReport = (TotalSalesReport) builder.build(session,startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/xls");
            String filename = buildFileName(generateTime, totalSalesReport);
            response.setHeader("Content-disposition", String.format("inline;filename=%s", filename));

            JRXlsExporter xlsExport = new JRXlsExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, totalSalesReport.getPrint());
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();

            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (JRException fnfe) {
            logger.error("Failed export report: ", fnfe);
            printError("Не найден шаблон отчета: " + fnfe.getMessage());
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private String buildFileName(Date generateTime, TotalSalesReport totalSalesReport) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = totalSalesReport.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s.xls", "TotalSalesReport", reportDistinctText, format);
    }

    public Boolean getIncludeActDiscrepancies() {
        return includeActDiscrepancies;
    }

    public void setIncludeActDiscrepancies(Boolean includeActDiscrepancies) {
        htmlReport = null;
        this.includeActDiscrepancies = includeActDiscrepancies;
    }

    @Override
    public String getPageFilename() {
        return "report/online/total_sales_report";
    }

    public Object showContragentListSelectPage () {
        //setSelectIdOfOrgList(false);
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public void setContragentId(Long contragentId) {
        this.contragentId = contragentId;
    }

    public Long getContragentId() {
        return contragentId;
    }

    public void setContragentsSelectItems(List<SelectItem> contragentsSelectItems) {
        this.contragentsSelectItems = contragentsSelectItems;
    }

    public List<SelectItem> getContragentsSelectItems() {
        return contragentsSelectItems;
    }


    private Contragent contragent;

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        if (null != idOfContragent) {
            this.contragent = (Contragent) session.get(Contragent.class, idOfContragent);
        }
    }

    public List<String> getTitlesComplexesWithPriceByContragent(Date startDate, Date endDate, Long idOfContragent) {
        List<String> titles = new ArrayList<String>();

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            List<Long> idOfOrgs = new ArrayList<Long>();

            if (!idOfOrgList.isEmpty()) {
                for (Long orgId: idOfOrgList) {
                    idOfOrgs.add(orgId);
                }
            } else {
                Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
                Set<Org> contragentOrgs = contragent.getOrgs();

                for (Org org : contragentOrgs) {
                    idOfOrgs.add(org.getIdOfOrg());
                }
            }

            if (!idOfOrgs.isEmpty()) {

                session.doWork(new Work() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        connection.prepareStatement("SET enable_seqscan TO OFF").execute();
                    }
                });

                Query query = session.createSQLQuery(
                        "SELECT od.socdiscount FROM CF_Orders o INNER JOIN CF_OrderDetails od ON o.idOfOrder = od.idOfOrder AND o.idOfOrg = od.idOfOrg "
                                + "WHERE o.idoforg IN (:idOfOrgs) AND o.createdDate >= :startDate AND o.createdDate <= :endDate AND od.socdiscount > 0 AND"
                                + "      (od.menuType = 0 OR (od.menuType >= 50 AND od.menuType <= 99)) AND o.state = 0 AND od.state = 0"
                                + "GROUP BY od.socdiscount ORDER BY od.socdiscount");

                query.setParameter("startDate", startDate.getTime());
                query.setParameter("endDate", endDate.getTime());
                query.setParameterList("idOfOrgs", idOfOrgs);

                List resultList = query.list();

                String str;
                titleAndSumMap = new HashMap<String, String>();
                for (Object o : resultList) {
                    str = "Льготный комплекс " + ((BigInteger) o).longValue() / 100 + "."
                            + ((BigInteger) o).longValue() % 100 + " руб.";
                    titleAndSumMap.put(str, o.toString());
                    titles.add(str);
                }
            }

            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    connection.prepareStatement("SET enable_seqscan TO ON").execute();
                }
            });

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return titles;
    }

    public Object showOrgListSelectPage() {
        if (contragent != null) {
            MainPage.getSessionInstance().setIdOfContragentList(Arrays.asList(contragent.getIdOfContragent()));
        }
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }
}
