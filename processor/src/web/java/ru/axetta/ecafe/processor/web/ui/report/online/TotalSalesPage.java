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
    private Boolean showAgeGroups = false;

    private List<SelectItem> contragentsSelectItemsPrefer;
    private List<SelectItem> contragentsSelectItemsBenefit;
    private List<SelectItem> contragentsSelectItemsPaid;

    private List<String> titlesComplex;

    private List<String> benefitTitleAndSumList;
    private List<String> paidTitleAndSumList;

    private HashMap<String, String> titleAndSumBenefitMap;
    private HashMap<String, String> titleAndSumPaidMap;

    // буф прод
    private Integer[] preferentialTitleComplexes;

    public Integer[] getPreferentialTitleComplexes() {
        return preferentialTitleComplexes;
    }

    public void setPreferentialTitleComplexes(Integer[] preferentialTitleComplexes) {
        this.preferentialTitleComplexes = preferentialTitleComplexes;
    }

    // льгот компл
    private Integer[] benefitTitleComplexes;

    public Integer[] getBenefitTitleComplexes() {
        return benefitTitleComplexes;
    }

    public void setBenefitTitleComplexes(Integer[] benefitTitleComplexes) {
        this.benefitTitleComplexes = benefitTitleComplexes;
    }

    // платн компл
    private Integer[] paidTitleComplexes;

    public Integer[] getPaidTitleComplexes() {
        return paidTitleComplexes;
    }

    public void setPaidTitleComplexes(Integer[] paidTitleComplexes) {
        this.paidTitleComplexes = paidTitleComplexes;
    }

    private boolean showDetail;

    public boolean getShowDetail() {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    private boolean showBenefitDetail;

    public boolean getShowBenefitDetail() {
        return showBenefitDetail;
    }

    public void setShowBenefitDetail(boolean showBenefitDetail) {
        this.showBenefitDetail = showBenefitDetail;
    }

    private boolean showPaidDetail;

    public boolean getShowPaidDetail() {
        return showPaidDetail;
    }

    public void setShowPaidDetail(boolean showPaidDetail) {
        this.showPaidDetail = showPaidDetail;
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

        contragentsSelectItemsPrefer = list;

        preferentialTitleComplexes = new Integer[contragentsSelectItemsPrefer.size()];

        return list;
    }

    public List<SelectItem> getAvailableComplexesWithPriceTitlesBenefit() {

        List<SelectItem> list = new ArrayList<SelectItem>();
        int i = 0;
        if (contragent != null) {
            List<String> complexesWithPriceTitles;

            Long idOfContragent = contragent.getIdOfContragent();
            complexesWithPriceTitles = getTitlesComplexesWithPriceBenefitByContragent(startDate, endDate, idOfContragent);

            if (!complexesWithPriceTitles.isEmpty() && complexesWithPriceTitles != null) {

                for (String title: complexesWithPriceTitles) {
                    SelectItem selectItem = new SelectItem(i, title);
                    list.add(selectItem);
                    i++;
                }
            }
        }

        contragentsSelectItemsBenefit = list;

        benefitTitleComplexes = new Integer[contragentsSelectItemsBenefit.size()];

        return list;
    }

    public List<SelectItem> getAvailableComplexesWithPriceTitlesPaid() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        int i = 0;
        if (contragent != null) {
            List<String> complexesWithPriceTitles;

            Long idOfContragent = contragent.getIdOfContragent();
            complexesWithPriceTitles = getTitlesComplexesWithPricePaidByContragent(startDate, endDate, idOfContragent);

            if (!complexesWithPriceTitles.isEmpty() && complexesWithPriceTitles != null) {

                for (String title: complexesWithPriceTitles) {
                    SelectItem selectItem = new SelectItem(i, title);
                    list.add(selectItem);
                    i++;
                }
            }
        }

        contragentsSelectItemsPaid = list;

        paidTitleComplexes = new Integer[contragentsSelectItemsPaid.size()];

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
        contragentsSelectItemsPrefer = new ArrayList<SelectItem>();
        ContragentReadOnlyRepository contragentReadOnlyRepository = ContragentReadOnlyRepository.getInstance();
        for(Contragent contragent :contragentReadOnlyRepository.findAllByType(Contragent.TSP)){
            contragentsSelectItemsPrefer.add(new SelectItem(contragent.getIdOfContragent(), contragent.getContragentName()));
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
                    if (prefer != null) {
                        titlesComplexList.add(contragentsSelectItemsPrefer.get(prefer).getLabel());
                    }
                }
            }
        }
        return titlesComplexList;
    }

    public String getStringPreferentialTitleComplexes(String preferentialTitleComplexesString) {
        titlesComplex = getTitlesComplexes();

        for (String titleComplexItem: titlesComplex) {
            preferentialTitleComplexesString = preferentialTitleComplexesString.concat(titleComplexItem).concat(",");
        }
        return preferentialTitleComplexesString;
    }

    private List<String> getBenefitTitleAndSums() {
        List<String> titleAndSumList = new ArrayList<String>();

        if (benefitTitleComplexes != null) {
            if (benefitTitleComplexes.length > 0) {
                //Льгот
                for (Integer prefer : benefitTitleComplexes) {
                    if (prefer != null) {
                        if (titleAndSumBenefitMap != null
                                && titleAndSumBenefitMap.get(contragentsSelectItemsBenefit.get(prefer).getLabel())
                                != null) {
                            titleAndSumList.add(contragentsSelectItemsBenefit.get(prefer).getLabel() + ","
                                    + titleAndSumBenefitMap.get(contragentsSelectItemsBenefit.get(prefer).getLabel()));
                        }
                    }
                }
            }
        }
        return titleAndSumList;
    }

    private List<String> getPaidTitleAndSums() {
        List<String> titleAndSumList = new ArrayList<String>();

        if (paidTitleComplexes != null) {
            if (paidTitleComplexes.length > 0) {
                //Платные
                for (Integer prefer : paidTitleComplexes) {
                    if (prefer != null) {
                        if (titleAndSumPaidMap != null
                                && titleAndSumPaidMap.get(contragentsSelectItemsPaid.get(prefer).getLabel()) != null) {
                            titleAndSumList
                                    .add(contragentsSelectItemsPaid.get(prefer).getLabel() + "," + titleAndSumPaidMap
                                            .get(contragentsSelectItemsPaid.get(prefer).getLabel()));
                        }
                    }
                }
            }
        }
        return titleAndSumList;
    }

    public String getBenefitTitleAndSumByString(String benefitTitleAndSumListString) {
        benefitTitleAndSumList = getBenefitTitleAndSums();

        for (String titleAndSumItem: benefitTitleAndSumList) {
            benefitTitleAndSumListString = benefitTitleAndSumListString.concat(titleAndSumItem).concat(";");
        }
        return benefitTitleAndSumListString;
    }

    public String getPaidTitleAndSumByString(String paidTitleAndSumListString) {
        paidTitleAndSumList = getPaidTitleAndSums();

        for (String titleAndSumItem: paidTitleAndSumList) {
            paidTitleAndSumListString = paidTitleAndSumListString.concat(titleAndSumItem).concat(";");
        }
        return paidTitleAndSumListString;
    }

    public Object buildReportHTML() {

        String preferentialTitleComplexesString = getStringPreferentialTitleComplexes("");

        String benefitTitleAndSumListString = getBenefitTitleAndSumByString("");

        String paidTitleAndSumListString = getPaidTitleAndSumByString("");

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortName = showAgeGroups ? TotalSalesReport.class.getSimpleName() + "WithAgeGroups" : TotalSalesReport.class.getSimpleName();
        String templateShortFileName = templateShortName + ".jasper";
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

            builder.getReportProperties().setProperty("preferentialTitleComplexes", showDetail ? preferentialTitleComplexesString : "");
            builder.getReportProperties().setProperty("benefitTitleAndSumList", showBenefitDetail ? benefitTitleAndSumListString : "");
            builder.getReportProperties().setProperty("paidTitleAndSumList", showPaidDetail ? paidTitleAndSumListString : "");
            builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty("showAgeGroups", Boolean.toString(showAgeGroups));

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

        filter = "Не выбрано";
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
        showAgeGroups = false;
        htmlReport = null;
        contragent = null;
        periodTypeMenu.setPeriodType(PeriodTypeMenu.PeriodTypeEnum.ONE_MONTH);
        preferentialTitleComplexes = null;
        benefitTitleComplexes = null;
        paidTitleComplexes = null;
        showDetail = false;
        showBenefitDetail = false;
        showPaidDetail = false;
        idOfOrgList.clear();
        return null;
    }

    public void showCSVList(ActionEvent actionEvent){
        String preferentialTitleComplexesString = getStringPreferentialTitleComplexes("");

        String benefitTitleAndSumString = getBenefitTitleAndSumByString("");

        String paidTitleAndSumListString = getPaidTitleAndSumByString("");

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = runtimeContext.getAutoReportGenerator();
        String templateShortName = showAgeGroups ? TotalSalesReport.class.getSimpleName() + "WithAgeGroups" : TotalSalesReport.class.getSimpleName();
        String templateShortFileName = templateShortName + ".jasper";
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

            builder.getReportProperties().setProperty("preferentialTitleComplexes", preferentialTitleComplexesString);
            builder.getReportProperties().setProperty("benefitTitleAndSumList", benefitTitleAndSumString);
            builder.getReportProperties().setProperty("paidTitleAndSumList", paidTitleAndSumListString);
            builder.getReportProperties().setProperty("idOfOrgList", getGetStringIdOfOrgList());
            builder.getReportProperties().setProperty("showAgeGroups", Boolean.toString(showAgeGroups));

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

    public List<SelectItem> getContragentsSelectItemsPrefer() {
        return contragentsSelectItemsPrefer;
    }

    public void setContragentsSelectItemsPrefer(List<SelectItem> contragentsSelectItemsPrefer) {
        this.contragentsSelectItemsPrefer = contragentsSelectItemsPrefer;
    }

    public List<SelectItem> getContragentsSelectItemsBenefit() {
        return contragentsSelectItemsBenefit;
    }

    public void setContragentsSelectItemsBenefit(List<SelectItem> contragentsSelectItemsBenefit) {
        this.contragentsSelectItemsBenefit = contragentsSelectItemsBenefit;
    }

    public List<SelectItem> getContragentsSelectItemsPaid() {
        return contragentsSelectItemsPaid;
    }

    public void setContragentsSelectItemsPaid(List<SelectItem> contragentsSelectItemsPaid) {
        this.contragentsSelectItemsPaid = contragentsSelectItemsPaid;
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
            this.idOfOrgList.clear();
            this.filter = "Не выбрано";
        }
    }

    public List<String> getTitlesComplexesWithPriceBenefitByContragent(Date startDate, Date endDate, Long idOfContragent) {
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
                        "SELECT od.socdiscount, od.rprice, od.discount FROM CF_Orders o INNER JOIN CF_OrderDetails od ON o.idOfOrder = od.idOfOrder AND o.idOfOrg = od.idOfOrg "
                                + "WHERE o.idoforg IN (:idOfOrgs) AND o.createdDate >= :startDate AND o.createdDate <= :endDate AND od.rprice = 0 AND"
                                + "      (od.menuType = 0 OR (od.menuType >= 50 AND od.menuType <= 99)) AND o.state = 0 AND od.state = 0"
                                + "GROUP BY od.socdiscount, od.rprice, od.discount ORDER BY od.socdiscount");

                query.setParameter("startDate", startDate.getTime());
                query.setParameter("endDate", endDate.getTime());
                query.setParameterList("idOfOrgs", idOfOrgs);

                List resultList = query.list();

                String str;
                titleAndSumBenefitMap = new HashMap<String, String>();
                for (Object o : resultList) {
                    Object[] row = (Object[]) o;
                    Long socdiscount = ((BigInteger) row[0]).longValue();
                    Long price = ((BigInteger) row[1]).longValue() + ((BigInteger) row[2]).longValue();
                    if (socdiscount > 0) {
                        str = "Льготный комплекс " + socdiscount / 100 + "." + socdiscount % 100 + " руб.";
                    } else {
                        str = "Льготный комплекс " + price / 100 + "." + price % 100 + " руб.";
                    }
                    titleAndSumBenefitMap.put(str, socdiscount > 0 ? socdiscount.toString() : price.toString());
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

    public List<String> getTitlesComplexesWithPricePaidByContragent(Date startDate, Date endDate, Long idOfContragent) {
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
                        "SELECT od.rprice, od.socdiscount, od.discount FROM CF_Orders o INNER JOIN CF_OrderDetails od ON o.idOfOrder = od.idOfOrder AND o.idOfOrg = od.idOfOrg "
                                + "WHERE o.idoforg IN (:idOfOrgs) AND o.createdDate >= :startDate AND o.createdDate <= :endDate  AND od.rprice > 0 "
                                + "AND(od.menuType >= 50 AND od.menuType <= 99) AND o.state = 0 AND od.state = 0"
                                + "GROUP BY od.rprice, od.socdiscount, od.discount ORDER BY od.rprice");

                query.setParameter("startDate", startDate.getTime());
                query.setParameter("endDate", endDate.getTime());
                query.setParameterList("idOfOrgs", idOfOrgs);

                List resultList = query.list();

                String str;
                titleAndSumPaidMap = new HashMap<String, String>();
                for (Object o : resultList) {
                    Object[] row = (Object[]) o;
                    Long rprice = ((BigInteger) row[0]).longValue();
                    Long discount = ((BigInteger) row[1]).longValue() + ((BigInteger) row[2]).longValue();
                    str = "Платный комплекс " + (rprice + discount) / 100 + "."
                            + (rprice + discount) % 100 + " руб.";

                    if (discount > 0) {
                        str += String.format(" (скидка %s.%s)", discount / 100, discount % 100);
                    }
                    titleAndSumPaidMap.put(str, row[0].toString());
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

    public Boolean getShowAgeGroups() {
        return showAgeGroups;
    }

    public void setShowAgeGroups(Boolean showAgeGroups) {
        this.showAgeGroups = showAgeGroups;
    }
}
