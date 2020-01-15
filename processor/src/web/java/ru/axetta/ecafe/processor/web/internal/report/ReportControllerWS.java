/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.ClientReportItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.report.ReportInfoItem;
import ru.axetta.ecafe.processor.core.persistence.dao.report.ReportParameter;
import ru.axetta.ecafe.processor.core.persistence.dao.report.ReportRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.report.mailing.MailingListReportsTypes;
import ru.axetta.ecafe.processor.core.persistence.service.org.OrgService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.internal.report.dataflow.*;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.soap.MTOM;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MTOM
@WebService()
public class ReportControllerWS extends HttpServlet implements ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportControllerWS.class);

    private static final Long RC_OK = 0L;
    private static final Long RC_INTERNAL_ERROR = 100L;
    private static final Long RC_PARAMETERS_ERROR = 101L;
    private static final Long RC_UNKNOWN_REPORT_ERROR = 102L;
    private static final Long RC_FILE_NOT_FOUND_ERROR = 103L;
    private static final Long RC_NO_DATA_ERROR = 104L;
    private static final Long RC_ORG_NOT_FOUND_ERROR = 105L;
    private static final String RC_OK_DESC = "OK";
    private static final String RC_INTERNAL_ERROR_DESC = "Внутренняя ошибка";
    private static final String RC_PARAMETERS_ERROR_DESC = "Ошибочные входные данные";
    private static final String RC_UNKNOWN_REPORT_ERROR_DESC = "Генерация этого отчета не поддерживается";
    private static final String RC_FILE_NOT_FOUND_ERROR_DESC = "Запрашиваемый файл не найден в репозитории";
    private static final String RC_NO_DATA_ERROR_DESC = "Нет данных по запрошенным параметрам";
    private static final String RC_ORG_NOT_FOUND_ERROR_DESC = "Не удалось найти главную организацию";

    @Override
    public ReportTradeMaterialGoodDataInfo generateReportTradeMaterialGood(@WebParam(name = "idOfOrg") Long idOfOrg,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        ReportService service = new ReportService();
        ReportTradeMaterialGoodDataInfo result = new ReportTradeMaterialGoodDataInfo();
        result.setCode(RC_OK);
        result.setResult(RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            //service.setSession(persistenceSession);
            List<TradeMaterialGoodItem> tradeMaterialGoodItemList = service.findReportDataInfo(idOfOrg, startDate, endDate);
            TradeMaterialGoodList tradeMaterialGoodList = new TradeMaterialGoodList();
            tradeMaterialGoodList.setT(tradeMaterialGoodItemList);
            result.setTradeMaterialGoodList(tradeMaterialGoodList);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process report controller request ReportTradeMaterialGood ", e);
            result.setCode(RC_INTERNAL_ERROR);
            result.setResult(RC_INTERNAL_ERROR_DESC);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public ReportClientOrderDetailsByAllOrgDataInfo generateClientOrderDetailsByAllOrgReport(@WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) throws Exception {
        OrderDetailsDAOService service = new OrderDetailsDAOService();
        ReportClientOrderDetailsByAllOrgDataInfo result = new ReportClientOrderDetailsByAllOrgDataInfo();
        result.setCode(RC_OK);
        result.setResult(RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            service.setSession(persistenceSession);
            List<ClientReportItem> clientReportItems = service.fetchClientReportItem(startDate, endDate, null);
            ClientOrderDetailsByAllOrgList clientOrderDetailsByAllOrgList = new ClientOrderDetailsByAllOrgList();
            clientOrderDetailsByAllOrgList.setC(clientReportItems);
            result.setClientOrderDetailsByAllOrgList(clientOrderDetailsByAllOrgList);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process report controller request ClientOrderDetailsByAllOrgReport", e);
            result.setCode(RC_INTERNAL_ERROR);
            result.setResult(RC_INTERNAL_ERROR_DESC);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public @XmlMimeType("application/octet-stream") GenerateReportResult generateReport(@WebParam(name = "reportType") String reportType,
            @WebParam(name = "parameters") List<ReportParameter> parameters) throws Exception {
        /*Параметры метода в структуре parameters
        Обязательные:
          startDate
          endDate
          idOfOrg
        Необязательные:
          idOfContragent
          idOfContract
          region
        */
        GenerateReportResult result = new GenerateReportResult();
        result.setReport(null);
        if (RuntimeContext.getAppContext().getBean(ReportRepository.class).areParametersBad(parameters)) {
            result.setCode(RC_PARAMETERS_ERROR);
            result.setResult(RC_PARAMETERS_ERROR_DESC);
            return result;
        }
        try {
            ReportRepository reportRepository = RuntimeContext.getAppContext().getBean(ReportRepository.class);
            byte[] jasper_content = reportRepository.buildReportAndReturnRawDataByType(reportType, parameters);
            if (jasper_content == null) {
                result.setCode(RC_NO_DATA_ERROR);
                result.setResult(RC_NO_DATA_ERROR_DESC);
            } else {
                result.setReport(jasper_content);
                result.setCode(RC_OK);
                result.setResult(RC_OK_DESC);
            }
        } catch (Exception e) {
            result.setCode(RC_INTERNAL_ERROR);
            result.setResult(RC_INTERNAL_ERROR_DESC);
            logger.error("Error in generateReport", e);
        }
        return result;
    }

    @Override
    public RepositoryReportListResult getRepositoryReportsList(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "startDate") Date startDate,
            @WebParam(name = "endDate") Date endDate) throws Exception {
        RepositoryReportListResult result = new RepositoryReportListResult();
        try {
            List<ReportInfoItem> reportInfos = RuntimeContext.getAppContext().getBean(ReportRepository.class).getReportInfos(idOfOrg, startDate, endDate);
            RepositoryReportItems resultItems = new RepositoryReportItems();
            List<RepositoryReportItem> list = new ArrayList<RepositoryReportItem>();
            for(ReportInfoItem info : reportInfos) {
                RepositoryReportItem item = new RepositoryReportItem();
                item.setReportName(info.getReportName());
                item.setCreatedDate(info.getCreatedDate());
                item.setStartDate(info.getStartDate());
                item.setEndDate(info.getEndDate());
                item.setOrgAddress(info.getOrgAddress());
                item.setOrgShortName(info.getOrgShortName());
                item.setReportFile(info.getReportFile());
                list.add(item);
            }
            resultItems.setRepositoryReportItem(list);
            result.setRepositoryReportItems(resultItems);
            result.setCode(RC_OK);
            result.setResult(RC_OK_DESC);
        } catch (Exception e) {
            result.setRepositoryReportItems(null);
            result.setCode(RC_INTERNAL_ERROR);
            result.setResult(RC_INTERNAL_ERROR_DESC);
            logger.error("Error in getRepositoryReportsList", e);
        }

        return result;
    }

    @Override
    public GenerateReportResult getRepositoryReport(@WebParam(name = "idOfReport") Long idOfReport) throws Exception {
        GenerateReportResult result = new GenerateReportResult();
        result.setReport(null);
        result.setCode(RC_UNKNOWN_REPORT_ERROR);
        result.setResult(RC_UNKNOWN_REPORT_ERROR_DESC);
        try {
            byte[] report_content = RuntimeContext.getAppContext().getBean(ReportRepository.class).getRepositoryReportById(idOfReport);
            if (report_content == null) {
                result.setCode(RC_FILE_NOT_FOUND_ERROR);
                result.setResult(RC_FILE_NOT_FOUND_ERROR_DESC);
            } else {
                result.setReport(report_content);
                result.setCode(RC_OK);
                result.setResult(RC_OK_DESC);
            }
        } catch (Exception e) {
            result.setCode(RC_INTERNAL_ERROR);
            result.setResult(RC_INTERNAL_ERROR_DESC);
            logger.error("Error in generateReport", e);
        }
        return result;
    }

    @Override
    public OrgMailingListResult getMailingListReports(@WebParam(name="idOfOrg")Long idOfOrg,
            @WebParam(name = "mailingListType")Integer mailingListType) throws Exception{
        OrgMailingListResult result = new OrgMailingListResult();
        if (isBadParametersForMailingList(mailingListType)){
            result.setCode(RC_PARAMETERS_ERROR);
            result.setResult(RC_PARAMETERS_ERROR_DESC);
            return result;
        }
        try {
            OrgService orgService = OrgService.getInstance();
            MailingListReportsTypes type = MailingListReportsTypes.getByCode(mailingListType.intValue());
            Org org = orgService.findOrg(idOfOrg);
            if (org != null) {
                result.setMailingList(orgService.getMailingList(org, type));
                result.setCode(RC_OK);
                result.setResult(RC_OK_DESC);
            } else {
                result.setCode(RC_ORG_NOT_FOUND_ERROR);
                result.setResult(RC_ORG_NOT_FOUND_ERROR_DESC);
            }
        } catch (Exception e) {
            result.setCode(RC_INTERNAL_ERROR);
            result.setResult(RC_INTERNAL_ERROR_DESC);
            logger.error("Error in getMailingListReports", e);
        }
        return result;
    }

    private boolean isBadParametersForMailingList(Integer code) {
        return code == null
                || MailingListReportsTypes.getByCode(code.intValue()).equals(MailingListReportsTypes.UNKNOWN);
    }

    @Override
    public OrgMailingListResult updateMailingListReports(@WebParam(name="idOfOrg")Long idOfOrg,@WebParam(name="mailingList")String mailingList,
            @WebParam(name = "mailingListType")Integer mailingListType) throws Exception {
        OrgMailingListResult result = new OrgMailingListResult();
        if (isBadParametersForMailingList(mailingListType)) {
            result.setCode(RC_PARAMETERS_ERROR);
            result.setResult(RC_PARAMETERS_ERROR_DESC);
            return result;
        }
        try {
            OrgService orgService = OrgService.getInstance();
            Org org = orgService.findOrg(idOfOrg);
            if (org != null) {
                MailingListReportsTypes type = MailingListReportsTypes.getByCode(mailingListType.intValue());
                orgService.setMailingList(org,mailingList,type);
                result.setMailingList(orgService.getMailingList(org,type));
                result.setCode(RC_OK);
                result.setResult(RC_OK_DESC);
            }
            else {
                result.setCode(RC_ORG_NOT_FOUND_ERROR);
                result.setResult(RC_ORG_NOT_FOUND_ERROR_DESC);
            }
        } catch (Exception e) {
            result.setCode(RC_INTERNAL_ERROR);
            result.setResult(RC_INTERNAL_ERROR_DESC);
            logger.error("Error in updateMailingListReports", e);
        }
        return result;
    }

}
