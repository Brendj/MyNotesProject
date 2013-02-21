
package ru.axetta.ecafe.processor.web.internal.report;

import ru.axetta.ecafe.processor.web.internal.report.dataflow.ReportDataInfo;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
import java.util.Date;


@WebService()
public interface ReportController {

    @WebMethod(operationName = "generateReportOrders")
    ReportDataInfo generateReportOrders(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);

    @WebMethod(operationName = "generateReportDemandsForDeliveries")
    ReportDataInfo generateReportMenus(@WebParam(name = "idOfContragent") Long idOfContragent, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);
}
