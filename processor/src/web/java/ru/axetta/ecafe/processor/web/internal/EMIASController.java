/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.emias.LiberateClientsList;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.voinov on 28.10.2019.
 */
@WebService(targetNamespace = "http://ru.axetta.ecafe")
public class EMIASController extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(EMIASController.class);

    @WebMethod(operationName = "getLiberateClientsList")
    public List<OrgSummaryResult> getLiberateClientsList(
            @WebParam(name = "LiberateClientsList") List<LiberateClientsList> liberateClientsLists) {
        List<OrgSummaryResult> orgSummaryResults = new ArrayList<>();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            for (LiberateClientsList liberateClientsList : liberateClientsLists) {

                if (liberateClientsList.getGuid() == null || liberateClientsList.getIdEventEMIAS() == null
                        || liberateClientsList.getTypeEventEMIAS() == null
                        || liberateClientsList.getDateLiberate() == null) {
                    if (liberateClientsList.getIdEventEMIAS() == null) {
                        orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_ARGUMENT_NOT_FOUND, ResponseItem.ERROR_ARGUMENT_NOT_FOUND_MESSAGE));
                    }
                    else
                    {
                        orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_ARGUMENT_NOT_FOUND, ResponseItem.ERROR_ARGUMENT_NOT_FOUND_MESSAGE,
                                liberateClientsList.getIdEventEMIAS() == null ? 0L : liberateClientsList.getIdEventEMIAS()));
                    }
                    continue;
                }

                if (DAOUtils.findClientByGuid(persistenceSession, liberateClientsList.getGuid()) == null) {
                    orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_CLIENT_NOT_FOUND_EMIAS,
                            ResponseItem.ERROR_CLIENT_NOT_FOUND_MESSAGE_EMIAS, liberateClientsList.getIdEventEMIAS()));
                    continue;
                }

                switch (liberateClientsList.getTypeEventEMIAS().intValue()) {
                    case 1://создание освобождения (в том числе продление освобождения)
                        if (liberateClientsList.getStartDateLiberate() == null
                                || liberateClientsList.getEndDateLiberate() == null) {
                            orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_ARGUMENT_NOT_FOUND,
                                    ResponseItem.ERROR_ARGUMENT_NOT_FOUND_MESSAGE, liberateClientsList.getIdEventEMIAS()));
                            continue;
                        }
                        DAOUtils.saveEMIAS(persistenceSession, liberateClientsList);
                        break;
                    case 2:
                        DAOUtils.updateEMIAS(persistenceSession, liberateClientsList);
                        break;
                    case 3:
                        DAOUtils.saveEMIAS(persistenceSession, liberateClientsList);
                        break;
                    case 4:
                        DAOUtils.updateEMIAS(persistenceSession, liberateClientsList);
                        break;
                    default: {
                        orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_EVENT_NOT_FOUND,
                                ResponseItem.ERROR_EVENT_NOT_FOUND_MESSAGE, liberateClientsList.getIdEventEMIAS()));
                        continue;
                    }
                }
                orgSummaryResults.add(new OrgSummaryResult(ResponseItem.OK,
                        ResponseItem.OK_MESSAGE_2, liberateClientsList.getIdEventEMIAS()));
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            logger.error("Ошибка при сохранении данных для ЕМИАС", e);
            orgSummaryResults.clear();
            orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_INTERNAL_EMIAS,
                    ResponseItem.ERROR_INTERNAL_MESSAGE_EMIAS));
            return orgSummaryResults;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return orgSummaryResults;
    }
}
