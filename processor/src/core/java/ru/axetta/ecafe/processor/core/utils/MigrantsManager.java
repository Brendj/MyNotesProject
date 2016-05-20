/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfVisitReqResolutionHist;
import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.persistence.VisitReqResolutionHist;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 18.05.16
 * Time: 12:18
 */

@Component
@Scope("prototype")
public class MigrantsManager {
    private static final Logger logger = LoggerFactory.getLogger(MigrantsManager.class);

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.migrantsmanager.node", "1");
        return !(StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim()));
    }

    /**
     * Обертка для запуска по расписанию
     */
    public void checkOverdueMigrants() throws Exception {
        if(isOn()){
            closeOverdueMigrants();
        }
    }

    private void closeOverdueMigrants() throws Exception{
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            List<Migrant> migrants = DAOUtils.getOverdueMigrants(persistenceSession);

            Long nextId = DAOUtils.nextIdOfProcessorMigrantResolutions(persistenceSession);

            for(Migrant migrant : migrants){
                migrant.setSyncState(Migrant.CLOSED);
                persistenceSession.save(migrant);
                VisitReqResolutionHist hist1 =
                        new VisitReqResolutionHist(new CompositeIdOfVisitReqResolutionHist(nextId,
                                migrant.getCompositeIdOfMigrant().getIdOfRequest(), migrant.getOrgRegistry().getIdOfOrg()), migrant.getOrgRegistry(),
                                VisitReqResolutionHist.RES_OVERDUE_SERVER, new Date(), "Закрыта на сервере по истечению срока.", null,
                                null, VisitReqResolutionHist.NOT_SYNCHRONIZED);
                nextId = nextId + 1L;
                VisitReqResolutionHist hist2 =
                        new VisitReqResolutionHist(new CompositeIdOfVisitReqResolutionHist(nextId,
                                migrant.getCompositeIdOfMigrant().getIdOfRequest(), migrant.getOrgVisit().getIdOfOrg()), migrant.getOrgRegistry(),
                                VisitReqResolutionHist.RES_OVERDUE_SERVER, new Date(), "Закрыта на сервере по истечению срока.", null,
                                null, VisitReqResolutionHist.NOT_SYNCHRONIZED);
                nextId = nextId + 1L;
                persistenceSession.save(hist1);
                persistenceSession.save(hist2);
            }

            logger.info(migrants.size() + " migrant requests closed due to the expiration of time.");

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
}
