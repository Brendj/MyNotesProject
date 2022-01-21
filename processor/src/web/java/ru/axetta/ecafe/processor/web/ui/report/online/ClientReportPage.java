/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import org.apache.poi.ss.usermodel.Workbook;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.ClientReport;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;
import ru.axetta.ecafe.processor.web.ui.report.excel.ClientReportService;
import ru.axetta.ecafe.processor.web.ui.report.excel.WriteExcelHelper;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 20.10.11
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class ClientReportPage extends OnlineReportPage implements ContragentListSelectPage.CompleteHandler {
    private ClientReport clientReport;

    private final ClientFilter clientFilter = new ClientFilter();
    private final ClientReportService clientReportService = new ClientReportService();

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    public String getPageFilename() {
        return "report/online/client_report";
    }


    private String contragentFilter = "Не выбрано";
    private String contragentIds;

    private final List<ContragentItem> contragentItems = new ArrayList<ContragentItem>();


    public ClientReport getClientReport() {
        return clientReport;
    }

    public String getContragentFilter() {
        return contragentFilter;
    }

    public void setContragentFilter(String contragentFilter) {
        this.contragentFilter = contragentFilter;
    }

    public String getContragentIds() {
        return contragentIds;
    }

    public void setContragentIds(String contragentIds) {
        this.contragentIds = contragentIds;
    }

    public void buildReport(Session session) throws Exception {
        this.clientReport = new ClientReport();
        ClientReport.Builder reportBuilder = new ClientReport.Builder();
        this.clientReport = reportBuilder.build(contragentIds, clientFilter.getClientGroupId(), session);
    }

    @Override
    public void completeContragentListSelection(Session session, List<Long> idOfContragentList, int multiContrFlag,
                                                String classTypes) throws Exception {
        contragentItems.clear();
        for (Long idOfContragent : idOfContragentList) {
            Contragent currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            ContragentItem contragentItem = new ContragentItem(currentContragent);
            contragentItems.add(contragentItem);
        }
        setContragentFilterInfo(contragentItems);
    }

    private void setContragentFilterInfo(List<ContragentItem> contragentItems) {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        if (contragentItems.isEmpty()) {
            contragentFilter = "Не выбрано";
        } else {
            for (ContragentItem it : contragentItems) {
                if (str.length() > 0) {
                    str.append("; ");
                    ids.append(",");
                }
                str.append(it.getContragentName());
                ids.append(it.getIdOfContragent());
            }
            contragentFilter = str.toString();
        }
        contragentIds = ids.toString();
    }

    public void buildReportExcel(FacesContext facesContext) throws Exception {
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext()
                .getResponse();
        try {
            Workbook wb = clientReportService.buildReport(clientReport.getClientItems());
            WriteExcelHelper.saveExcelReport(wb, response);
        } catch (NullPointerException e) {
            printError("Нет данных для выгрузки отчета");
        }
    }

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }
}
