package ru.axetta.ecafe.processor.core.daoservices.contract;

import ru.axetta.ecafe.processor.core.daoservices.client.items.ClientMigrationHistoryReportItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.08.13
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class ContractDAOService {

    @SuppressWarnings("unchecked")
    public List<OrgContractReportItem> generateOrgContractReport(Boolean selectBindContract, Date startDate){
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(Org.class);
        criteria.createAlias("contract","contr", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("contr.contragent","agent", JoinType.LEFT_OUTER_JOIN);
        Criterion mainRestriction = Restrictions.disjunction();

        Criterion dateRestriction =  Restrictions.conjunction();
        dateRestriction = ((Conjunction)dateRestriction)
            .add(Restrictions.le("contr.dateOfConclusion", startDate))
            .add(Restrictions.ge("contr.dateOfClosing", startDate));

        if(selectBindContract){
            dateRestriction = ((Conjunction)dateRestriction).add(Restrictions.isNull("contract"));
        }
        mainRestriction = ((Disjunction)mainRestriction).add(dateRestriction);

        if(selectBindContract){
            mainRestriction = ((Disjunction)mainRestriction).add(Restrictions.isNull("contract"));
        }
        criteria.add(mainRestriction);

        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("idOfOrg"), "idOfOrg")
                .add(Projections.property("shortName"), "shortName")
                .add(Projections.property("contr.idOfContract"), "idOfContract")
                .add(Projections.property("contr.contractNumber"), "contractNumber")
                .add(Projections.property("agent.idOfContragent"), "idOfContragent")
                .add(Projections.property("agent.contragentName"), "contragentName")
        );
        criteria.addOrder(Order.asc("idOfOrg"));
        criteria.setResultTransformer(Transformers.aliasToBean(OrgContractReportItem.class));
        return (List<OrgContractReportItem>) criteria.list();
    }

    @PersistenceContext
    private EntityManager entityManager;

}
