/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicBasketReport;
import ru.axetta.ecafe.processor.core.report.BasicBasketReportBuilder;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodListItem;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodListItemsPanel;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class BasicBasketReportPage extends OnlineReportPage implements BasicGoodListItemsPanel.CompleteHandler {

    private final static Logger logger = LoggerFactory.getLogger(BasicBasketReportPage.class);
    private BasicBasketReport basicBasketReport;
    private String htmlReport;
    private List<BasicGoodListItem> selectedList = new ArrayList<BasicGoodListItem>();

    @Autowired
    BasicGoodListItemsPanel basicGoodListItemsPanel;

    public Object showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public void exportToHtml() {
        if (validateFormData()) {
            return;
        }
        basicBasketReport =(BasicBasketReport)makeReport();
        htmlReport = basicBasketReport.getHtmlReport();
    }

    public Object showBasicBasketSelectPage() throws Exception{
        basicGoodListItemsPanel.reload(selectedList);
        basicGoodListItemsPanel.pushCompleteHandler(this);
        return null;
    }

    public void completeBasicGoodListSelection(List<BasicGoodListItem> idOfBasicGoodList) throws Exception {
        selectedList = idOfBasicGoodList;
        if (selectedList.size() == 0)
            filter = "Не выбрано";
        else {
            filter = "";
            for(BasicGoodListItem item : selectedList) {
                filter = filter.concat(item.getName() + "; ");
            }
            filter = filter.substring(0, filter.length() - 1);
        }
    }

    private BasicReportJob makeReport() {
        Properties properties = new Properties();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateShortFileName = BasicBasketReport.class.getSimpleName() + ".jasper";
        String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + templateShortFileName;
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        BasicBasketReport.Builder builder = new BasicBasketReportBuilder(templateFilename);
        if (!CollectionUtils.isEmpty(selectedList)) {
            List<Long> bbGoods = new ArrayList<Long>();
            for (BasicGoodListItem item : selectedList) {
                bbGoods.add(item.getIdOfBasicGood());
            }
            String idOfBBGoodsString = StringUtils.join(bbGoods.iterator(), ",");
            properties.setProperty("BBGoods", idOfBBGoodsString);
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        BasicReportJob report = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            startDate = CalendarUtils.truncateToDayOfMonth(startDate);
            endDate = CalendarUtils.endOfDay(endDate); //localCalendar.getTime();
            builder.setReportProperties(properties);
            report = builder.build(persistenceSession, startDate, endDate, localCalendar);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return report;
    }
    public void exportToXLS(ActionEvent actionEvent) {
        if (validateFormData()) {
            return;
        }
        BasicReportJob report = makeReport();
        if (report != null) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

                ServletOutputStream servletOutputStream = response.getOutputStream();

                facesContext.responseComplete();
                response.setContentType("application/xls");
                String filename = buildFileName(new Date(), report);
                response.setHeader("Content-disposition", String.format("inline;filename=%s.xls", filename));

                JRXlsExporter xlsExport = new JRXlsExporter();
                xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, report.getPrint());
                xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
                xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
                xlsExport.exportReport();
                servletOutputStream.flush();
                servletOutputStream.close();
                printMessage("Отчет построен");
            } catch (Exception e) {
                logger.error("Failed export report : ", e);
                printError("Ошибка при подготовке отчета: " + e.getMessage());
            }
        }
    }

    public String getPageFilename() {
        return "report/online/basic_basket_report";
    }

    private String buildFileName(Date generateTime, BasicReportJob basicReportJob) {
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        String reportDistinctText = basicReportJob.getReportDistinctText();
        String format = timeFormat.format(generateTime);
        return String.format("%s-%s-%s", "BasicBasketReport", reportDistinctText, format);
    }

    private boolean validateFormData() {
        if (startDate == null) {
            printError("Не указана дата");
            return true;
        }
        if (CollectionUtils.isEmpty(selectedList)) {
            printError("Выберите один или несколько элементов базовой корзины");
            return true;
        }
        return false;
    }

    public BasicBasketReport getBasicBasketReport() {
        return basicBasketReport;
    }

    public void setBasicBasketReport(BasicBasketReport basicBasketReport) {
        this.basicBasketReport = basicBasketReport;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }
}
