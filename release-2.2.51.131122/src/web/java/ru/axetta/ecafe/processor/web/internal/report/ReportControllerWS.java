/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.ClientReportItem;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.internal.report.dataflow.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@WebService()
public class ReportControllerWS extends HttpServlet implements ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportControllerWS.class);

    private static final Long RC_INTERNAL_ERROR = 100L, RC_OK = 0L;
    private static final String RC_OK_DESC = "OK";
    private static final String RC_INTERNAL_ERROR_DESC = "Внутренняя ошибка";

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
            service.setSession(persistenceSession);
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
            List<ClientReportItem> clientReportItems = service.fetchClientReportItem(startDate, endDate);
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
}
