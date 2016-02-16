
package ru.axetta.ecafe.processor.web.internal.report;

import ru.axetta.ecafe.processor.core.persistence.dao.report.ReportParameter;
import ru.axetta.ecafe.processor.web.internal.report.dataflow.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Date;
import java.util.List;


@WebService()
public interface ReportController {

    @WebMethod(operationName = "generateReportTradeMaterialGood")
    ReportTradeMaterialGoodDataInfo generateReportTradeMaterialGood(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate)
            throws Exception;

    @WebMethod(operationName = "generateClientOrderDetailsByAllOrgReport")
    ReportClientOrderDetailsByAllOrgDataInfo generateClientOrderDetailsByAllOrgReport(@WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) throws Exception;

    @WebMethod(operationName = "generateReport")
    GenerateReportResult generateReport(@WebParam(name = "reportType") String reportType, @WebParam(name = "parameters") List<ReportParameter> parameters) throws Exception;

    @WebMethod(operationName = "getRepositoryReportsList")
    RepositoryReportListResult getRepositoryReportsList(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "startDate") Date startDate,
            @WebParam(name = "endDate") Date endDate) throws Exception;

    @WebMethod(operationName = "getRepositoryReport")
    GenerateReportResult getRepositoryReport(@WebParam(name = "idOfReport") Long idOfReport) throws Exception;

    @WebMethod(operationName = "getMailingListReports")
    OrgMailingListResult getMailingListReports(@WebParam(name = "idOfOrg") Long idOfOrg,
            @WebParam(name = "mailingListType") Integer mailingListType) throws Exception;

    @WebMethod(operationName = "updateMailingListReports")
    OrgMailingListResult updateMailingListReports(@WebParam(name = "idOfOrg") Long idOfOrg,
            @WebParam(name = "mailingList") String mailingList,
            @WebParam(name = "mailingListType") Integer mailingListType) throws Exception;

    //@WebMethod(operationName = "generateReportOrders")
    //ReportDataInfoOld generateReportOrders(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);
    //
    //@WebMethod(operationName = "generateReportDemandsForDeliveries")
    //ReportDataInfoOld generateReportMenus(@WebParam(name = "idOfContragent") Long idOfContragent, @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate);


}
