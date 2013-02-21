/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ReportInfo;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.internal.report.dataflow.ReportDataInfo;
import ru.axetta.ecafe.processor.web.ui.report.repository.ReportRepositoryItem;
import ru.axetta.ecafe.processor.web.ui.report.repository.ReportRepositoryListPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.xml.bind.annotation.XmlMimeType;
import java.util.Date;

//@MTOM
@WebService()
public class ReportControllerWS extends HttpServlet implements ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportControllerWS.class);

    @Override
    public ReportDataInfo generateReportOrders(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        DataHandler dataHandler = null;
        ReportDataInfo reportDataInfo = new ReportDataInfo(dataHandler, ResultEnum.FILE_NOT_FOUND);
        try {
            if (startDate!=null && endDate==null) {
                endDate = CalendarUtils.addDays(startDate, 1);
            }
            ReportInfo reportInfo = DAOService.getInstance().getReportInfo(idOfOrg, startDate, endDate,"DailySalesByGroupsReport");
            ResultEnum resultEnum = ResultEnum.FILE_NOT_FOUND;
            if(reportInfo!=null){
                String path = RuntimeContext.getInstance().getAutoReportGenerator().getReportPath() + reportInfo.getReportFile();
                FileDataSource fileDataSource = new FileDataSource(path);
                dataHandler = new DataHandler(fileDataSource);
                resultEnum = ResultEnum.OK;
            }
            reportDataInfo = new ReportDataInfo(dataHandler,resultEnum);
        } catch (Exception e) {
            logger.error("Initialize error: ",e);
        }
        return reportDataInfo;
    }

    @Override
    public ReportDataInfo generateReportMenus(@WebParam(name = "idOfContragent") Long idOfContragent, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        DataHandler dataHandler = null;
        ReportDataInfo reportDataInfo = new ReportDataInfo(dataHandler, ResultEnum.FILE_NOT_FOUND);
        try {
            if (startDate!=null && endDate==null) {
                endDate = CalendarUtils.addDays(startDate, 1);
            }
            ReportInfo reportInfo = DAOService.getInstance().getReportInfo(idOfContragent, startDate, endDate,"MenuDetailsGroupByMenuOriginReport");
            ResultEnum resultEnum = ResultEnum.FILE_NOT_FOUND;
            if(reportInfo!=null){
                String path = RuntimeContext.getInstance().getAutoReportGenerator().getReportPath() + reportInfo.getReportFile();
                FileDataSource fileDataSource = new FileDataSource(path);
                dataHandler = new DataHandler(fileDataSource);
                resultEnum = ResultEnum.OK;
            }
            reportDataInfo = new ReportDataInfo(dataHandler,resultEnum);
        } catch (Exception e) {
            logger.error("Initialize error: ",e);
        }
        return reportDataInfo;
    }

}
