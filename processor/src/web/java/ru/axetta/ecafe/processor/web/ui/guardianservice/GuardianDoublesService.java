package ru.axetta.ecafe.processor.web.ui.guardianservice;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import javax.persistence.Query;
import java.util.*;

@Component
public class GuardianDoublesService {
    Logger logger = LoggerFactory.getLogger(GuardianDoublesService.class);
    private Set<Long> processedCG = new HashSet<>();

    public void processDeleteDoubleGuardiansForOrg(long idOfOrg) {
        logger.info("Start process one org. Id=" + idOfOrg);
        processedCG.clear();
        List<CGItem> items = getCGItems(idOfOrg);
        Map<Long, List<CGItem>> map = getCGItemsMap(items);
        for (Map.Entry me : map.entrySet()) {
            Long idOfClient = (Long)me.getKey();
            List<CGItem> guardians = (List)me.getValue();
            processOneClient(idOfClient, guardians);


            //logger.info(me.getKey().toString() + ": " + ((List)me.getValue()).size());
        }
        logger.info("End process one org. Id=" + idOfOrg);
    }

    private List<CGItem> getCGItems(long idOfOrg) {
        Session session = null;
        Transaction transaction = null;
        List<CGItem> result = new LinkedList<>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            String query_str = "select c.idofclient, cg.idofguardian, pg.surname, pg.firstname, " +
                    "pg.secondname, g.mobile, ca.cardno, ca.state, c.idoforg as clientOrg, g.idoforg as guardianOrg, " +
                    "cg.idofclientguardian " +
                    "from cf_clients c join cf_client_guardian cg on c.idofclient = cg.idofchildren " +
                    "join cf_clients g on g.idofclient = cg.idofguardian " +
                    "join cf_persons pg on pg.idofperson = g.idofperson " +
                    "left join cf_cards ca on ca.idofclient = g.idofclient and ca.state in (0, 4) and ca.lifestate = 4 and ca.validdate > :card_date " +
                    "where c.idoforg = :idOfOrg and NOT cg.deletedState " +
                    "and g.idofclientgroup >= :group_employees " +
                    "and g.idofclientgroup not in (:group_leaving, :group_deleted) " +
                    "order by c.idofclient";
            Query query = session.createNativeQuery(query_str);
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("group_leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            query.setParameter("group_deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameter("card_date", (new Date()).getTime());
            List list = query.getResultList();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                CGItem item = new CGItem(HibernateUtils.getDbLong(row[0]),
                        HibernateUtils.getDbLong(row[1]),
                        HibernateUtils.getDbString(row[2]).trim()
                                .concat(HibernateUtils.getDbString(row[3]))
                                .concat(HibernateUtils.getDbString(row[4])),
                        HibernateUtils.getDbString(row[5]),
                        HibernateUtils.getDbLong(row[6]),
                        HibernateUtils.getDbInt(row[7]),
                        HibernateUtils.getDbLong(row[8]).equals(HibernateUtils.getDbLong(row[9])),
                        HibernateUtils.getDbLong(row[10]));
                result.add(item);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error(String.format("Error in getCGItems. IdOrg = %s: ", idOfOrg), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<CGItem>> getCGItemsMap(List<CGItem> items) {
        Map<Long, List<CGItem>> result = new HashMap<>();
        for (CGItem item : items) {
            List<CGItem> list = result.get(item.getIdOfClient());
            if (list == null) {
                list = new LinkedList<>();
            }

            list.add(item);
            result.put(item.getIdOfClient(), list);
        }
        return result;
    }

    private void processOneClient(Long idOfClient, List<CGItem> guardians) {
        for (CGItem item : guardians) {
            if (processedCG.contains(item.getIdOfClientGuardian())) continue;
            List<CGItem> processList = getNotProcessedDoubles(item, guardians);
            if (processList.size() == 1) continue;
            //Есть дубли, дальше обрабатываем
            logger.info("Doubles: " + processList.size());
        }
    }

    private List<CGItem> getNotProcessedDoubles(CGItem item, List<CGItem> guardians) {
        List<CGItem> result = new LinkedList<>();
        result.add(item);
        for (CGItem guardian : guardians) {
            if (!processedCG.contains(guardian.getIdOfClientGuardian())
                    && item.getFioPlusMobile().equals(guardian.getFioPlusMobile())) {
                result.add(guardian);
            }
        }
        return result;
    }


}
