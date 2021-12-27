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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class GuardianDoublesService {
    Logger logger = LoggerFactory.getLogger(GuardianDoublesService.class);

    public void processDeleteDoubleGuardiansForOrg(long idOfOrg) {
        List<CGItem> items = getCGItems(idOfOrg);
        Map<Long, List<CGItem>> map = getCGItemsMap(items);
        for (Map.Entry me : map.entrySet()) {

        }
    }

    private List<CGItem> getCGItems(long idOfOrg) {
        Session session = null;
        Transaction transaction = null;
        List<CGItem> result = new LinkedList<>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            String query_str = "select c.idofclient, cg.idofguardian, pg.surname, pg.firstname, " +
                    "pg.secondname, g.mobile, ca.cardno, ca.state, c.idoforg, g.idoforg " +
                    "from cf_clients c join cf_client_guardian cg on c.idofclient = cg.idofchildren " +
                    "join cf_clients g on g.idofclient = cg.idofguardian " +
                    "join cf_persons pg on pg.idofperson = g.idofperson " +
                    "left join cf_cards ca on ca.idofclient = g.idofclient " +
                    "where c.idoforg = :idOfOrg and NOT cg.deletedState " +
                    "and g.idofclientgroup >= :group_employees " +
                    "and g.idofclientgroup not in (:group_leaving, :group_deleted)" +
                    "order by c.idofclient";
            Query query = session.createNativeQuery(query_str);
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("group_leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            query.setParameter("group_deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
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
                        HibernateUtils.getDbLong(row[8]).equals(HibernateUtils.getDbLong(row[9])));
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
            if (item)
            list.add(item);
            result.put(item.getIdOfClient(), list);
        }
        return result;
    }

}
