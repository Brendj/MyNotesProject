/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.items.TradeMaterialGoodItem;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TradeMaterialGood;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.13
 * Time: 17:22
 * To change this template use File | Settings | File Templates.
 */
public class TradeMaterialGoodService extends AbstractDAOService {

    private static final Logger logger = LoggerFactory.getLogger(TradeMaterialGoodService.class);

    @SuppressWarnings("unchecked")
    public List<TradeMaterialGoodItem> findReportDataInfo(Long idOfOrg, Date startDate, Date endDate){
        Criteria criteria = getSession().createCriteria(TradeMaterialGood.class);
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        criteria.add(Restrictions.between("goodsCreationDate", startDate, endDate));
        criteria.createAlias("good", "gd", JoinType.LEFT_OUTER_JOIN);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("gd.goodsCode"), "goodsCode")
                .add(Projections.property("gd.nameOfGood"), "nameOfGood")
                .add(Projections.property("gd.fullName"), "fullName")
                .add(Projections.property("unitsScale"), "unitsScale")
                .add(Projections.property("totalCount"), "totalCount")
                .add(Projections.property("netWeight"), "netWeight")
                .add(Projections.property("selfPrice"), "selfPrice")
                .add(Projections.property("nds"), "nds")
        );
        criteria.setResultTransformer(Transformers.aliasToBean(TradeMaterialGoodItem.class));
        return (List<TradeMaterialGoodItem>) criteria.list();
    }

}
