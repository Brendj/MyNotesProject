/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.PartGroupItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampItem;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.04.13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class OrderDetailsDAOService extends AbstractDAOService {

    @SuppressWarnings("unchecked")
    public List<RegisterStampItem> findNotNullGoodsFullNameByOrg(Long idOfOrg, Date start, Date end){
        String sql;
        sql = "select g.pathPart3 as level1, g.pathPart4 as level2, sum(details.qty) as qty, ord.createTime as date" +
                " from OrderDetail details left join details.good g left join details.order ord " +
                " where g is not null and details.org.idOfOrg=:idOfOrg and ord.createTime between :begin and :end" +
                " group by ord.createTime, g.fullName, details.qty";
        Query query = getSession().createQuery(sql);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("begin",start);
        query.setParameter("end",end);
        query.setResultTransformer(Transformers.aliasToBean(RegisterStampItem.class));
        return (List<RegisterStampItem>) query.list();
    }

    @SuppressWarnings("unchecked")
    public List<PartGroupItem> getCountGroup(Long idOfOrg, Date start, Date end){
        String sql;
        sql = "select count(g.pathPart3) as numCount, g.pathPart3 as name " +
                " from OrderDetail details left join details.good g left join details.order ord " +
                " where g is not null and details.org.idOfOrg=:idOfOrg and ord.createTime between :begin and :end" +
                " group by g.pathPart3 ";
        Query query = getSession().createQuery(sql);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("begin",start);
        query.setParameter("end", end);
        query.setResultTransformer(Transformers.aliasToBean(PartGroupItem.class));
        return (List<PartGroupItem>) query.list();
    }

    @SuppressWarnings("unchecked")
    public List<PartGroupItem> getCountSubGroup(Long idOfOrg, Date start, Date end, String partName){
        String sql;
        sql = "select count(g.pathPart4) as numCount, g.pathPart4 as name " +
                " from OrderDetail details left join details.good g left join details.order ord " +
                " where g.pathPart3=:partName and g is not null and details.org.idOfOrg=:idOfOrg and ord.createTime between :begin and :end" +
                " group by g.pathPart4 ";
        Query query = getSession().createQuery(sql);
        query.setParameter("partName",partName);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("begin",start);
        query.setParameter("end", end);
        query.setResultTransformer(Transformers.aliasToBean(PartGroupItem.class));
        return (List<PartGroupItem>) query.list();
    }

}
