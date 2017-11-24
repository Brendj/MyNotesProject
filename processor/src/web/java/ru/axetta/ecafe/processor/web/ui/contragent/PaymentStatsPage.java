/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by i.semenov on 23.11.2017.
 */
@Component
@Scope("session")
public class PaymentStatsPage extends BasicWorkspacePage implements ContragentListSelectPage.CompleteHandler {
    private final List<ContragentPaymentStatItem> items = new ArrayList<ContragentPaymentStatItem>();
    private Long selectedIdOfItem;
    private Long amountTotalMeasure;
    private Long amountNotSuccessfulMeasure;

    @Override
    public String getPageFilename() {
        return "contragent/payment_stats";
    }

    public void completeContragentListSelection(Session session, List<Long> idOfContragentList, int multiContrFlag, String classTypes) throws Exception {
        for (Long idOfContragent : idOfContragentList) {
            Contragent currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            ContragentPaymentStatItem item = new ContragentPaymentStatItem(currentContragent);
            items.add(item);
        }
        Collections.sort(items);
    }

    public void deteteItem() {
        if (selectedIdOfItem == null) {
            return;
        }
        for (Iterator<ContragentPaymentStatItem> iterator = items.iterator(); iterator.hasNext();) {
            ContragentPaymentStatItem item = iterator.next();
            if (item.getIdOfItem().equals(selectedIdOfItem)) {
                iterator.remove();
                return;
            }
        }
    }

    public void generateXLS(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
            String templateFilename = autoReportGenerator.getReportsTemplateFilePath() + "PaymentStatsReport.jasper";
            setValues();
            parameterMap.put("startDate", CalendarUtils.dateToString(new Date()));
            parameterMap.put("amountTotalMeasure", amountTotalMeasure.toString());
            parameterMap.put("amountNotSuccessfulMeasure", amountNotSuccessfulMeasure.toString());
            JasperPrint jasperPrint = JasperFillManager.fillReport
                    (templateFilename, parameterMap, new JRBeanCollectionDataSource(items));

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            facesContext.responseComplete();
            response.setContentType("application/xls");
            response.setHeader("Content-disposition", "inline;filename=PaymentStats.xls");
            JRXlsExporter xlsExport = new JRXlsExporter();
            xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, jasperPrint);
            xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, servletOutputStream);
            xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            xlsExport.exportReport();
            servletOutputStream.close();

        } catch (Exception e) {
            logAndPrintMessage("Ошибка при выгрузке отчета:", e);
        }
    }

    private void setValues() {
        Long num = 1L;
        amountTotalMeasure = 0L;
        amountNotSuccessfulMeasure = 0L;
        for (ContragentPaymentStatItem item : items) {
            item.setRownum(num);
            num++;
            item.setPercent(item.getPercentStr());
            try {
                amountTotalMeasure += Long.parseLong(item.getAmountTotal());
            } catch (Exception ignore) {}
            try {
                amountNotSuccessfulMeasure += Long.parseLong(item.getAmountNotSuccessful());
            } catch (Exception ignore) {}
        }
    }

    public List<ContragentPaymentStatItem> getItems() {
        return items;
    }

    public Long getSelectedIdOfItem() {
        return selectedIdOfItem;
    }

    public void setSelectedIdOfItem(Long selectedIdOfItem) {
        this.selectedIdOfItem = selectedIdOfItem;
    }
}
