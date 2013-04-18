
package ru.axetta.ecafe.processor.web.internal.report;

import ru.axetta.ecafe.processor.web.internal.report.dataflow.ReportDataInfo;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Date;
import java.util.List;


@WebService()
public interface ReportController {

    @WebMethod(operationName = "generateReportTradeMaterialGood")
    ReportDataInfo generateReportTradeMaterialGood(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate)
            throws Exception;

    //@WebMethod(operationName = "generateReportOrders")
    //ReportDataInfoOld generateReportOrders(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);
    //
    //@WebMethod(operationName = "generateReportDemandsForDeliveries")
    //ReportDataInfoOld generateReportMenus(@WebParam(name = "idOfContragent") Long idOfContragent, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);


}
