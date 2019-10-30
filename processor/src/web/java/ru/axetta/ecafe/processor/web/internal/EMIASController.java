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
import java.util.List;

/**
 * Created by a.voinov on 28.10.2019.
 */
@WebService(targetNamespace = "http://ru.axetta.ecafe")
public class EMIASController extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(EMIASController.class);

    @WebMethod(operationName = "getLiberateClientsList")
    public  ResponseItem getLiberateClientsList(@WebParam(name = "LiberateClientsList") List<LiberateClientsList> liberateClientsLists) {


        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            for (LiberateClientsList liberateClientsList: liberateClientsLists)
            {
                DAOUtils.saveEMIAS(persistenceSession, liberateClientsList);
            }


            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            logger.error("Ошибка при сохранении данных для ЕМИАС", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return new ResponseItem(ResponseItem.ERROR_INTERNAL, ResponseItem.ERROR_INTERNAL_MESSAGE);
    }
}
