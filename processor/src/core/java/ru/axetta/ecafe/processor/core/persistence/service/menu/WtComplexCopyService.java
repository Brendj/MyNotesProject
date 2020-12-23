/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.menu;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("singleton")
public class WtComplexCopyService {
    private static final Logger logger = LoggerFactory.getLogger(WtComplexCopyService.class);

    public void run() {
        if (!isOn()) return;
        runTask();
    }

    public void runTask() {
        String lastProcessedFromOptions = DAOService.getInstance().getLastProcessedWtComplex();
        if (StringUtils.isEmpty(lastProcessedFromOptions)) lastProcessedFromOptions = "0";
        Long idOfComplex = new Long(lastProcessedFromOptions);
        logger.info("Start process WT menu complexes copies");
        processCopyComplexesByLastId(idOfComplex);
        logger.info("End process WT menu complexes copies");
    }

    private boolean isOn() {
        return RuntimeContext.getInstance().actionIsOnByNode("ecafe.processor.wtcomplex.service.node");
    }

    private void processCopyComplexesByLastId(Long idOfComplex) {
        Long newValue = idOfComplex;
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Query query = session.createQuery("select c from WtComplex c where c.idOfParentComplex is not null and idOfComplex > :idOfComplex and c.deleteState = 0 order by c.idOfComplex");
            query.setParameter("idOfComplex", idOfComplex);
            List<WtComplex> list = query.list();
            for (WtComplex wtComplex : list) {
                newValue = wtComplex.getIdOfComplex();
                WtComplex oldComplex = (WtComplex)session.load(WtComplex.class, wtComplex.getIdOfParentComplex());
                if (!oldComplex.getWtAgeGroupItem().getIdOfAgeGroupItem().equals(wtComplex.getWtAgeGroupItem().getIdOfAgeGroupItem())
                        || !oldComplex.getWtComplexGroupItem().getIdOfComplexGroupItem().equals(wtComplex.getWtComplexGroupItem().getIdOfComplexGroupItem())
                        || !oldComplex.getWtDietType().getIdOfDietType().equals(wtComplex.getWtDietType().getIdOfDietType())) {
                    continue;
                }
                Query q = session.createSQLQuery("insert into cf_wt_discountrules_complexes (idofrule, idofcomplex) "
                        + "select idofrule, :wtComplex from cf_wt_discountrules_complexes ttt where idofcomplex = :oldComplex "
                        + "and not exists (select * from cf_wt_discountrules_complexes qqq where ttt.idofrule = qqq.idofrule and qqq.idofcomplex = :wtComplex)");
                q.setParameter("wtComplex", wtComplex.getIdOfComplex());
                q.setParameter("oldComplex", oldComplex.getIdOfComplex());
                int count = q.executeUpdate();
                logger.info(String.format("Комплекс ид=%s включен в %s правил соц скидок", wtComplex.getIdOfComplex(), count));
            }
            if (newValue > idOfComplex) {
                DAOService.getInstance().setOnlineOptionValue(newValue.toString(), Option.OPTION_LAST_PROCESSED_WT_COMPLEX);
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in processCopyComplexesByLastId: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

}
