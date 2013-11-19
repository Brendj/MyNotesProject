/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.commodity.accounting;

import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.items.WayBillPositionItem;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBillPosition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.13
 * Time: 17:22
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class WayBillPositionService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Transactional
    @SuppressWarnings("unchecked")
    public List<WayBillPositionItem> findByWayBill(Long idOfWayBill){
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(WayBillPosition.class);
        criteria.createAlias("good", "g", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("wayBill", "w", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("w.globalId", idOfWayBill));
        ProjectionList projectionList = Projections.projectionList();
        //projectionList.add(Projections.property("guid"), "guid");
        //projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");
        projectionList.add(Projections.property("unitsScale"), "unitsScale");
        projectionList.add(Projections.property("totalCount"), "totalCount");
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("grossWeight"), "grossWeight");
        projectionList.add(Projections.property("goodsCreationDate"), "goodsCreationDate");
        projectionList.add(Projections.property("lifeTime"), "lifeTime");
        projectionList.add(Projections.property("price"), "price");
        projectionList.add(Projections.property("nds"), "nds");
        projectionList.add(Projections.property("good"), "good");

        //projectionList.add(Projections.property("g.globalId"), "idOfGood");
        //projectionList.add(Projections.property("g.nameOfGood"), "goodName");

        //projectionList.add(Projections.property("w.guid"), "guidOfWB");
        criteria.setProjection(projectionList);
        criteria.setResultTransformer(Transformers.aliasToBean(WayBillPositionItem.class));
        List<WayBillPositionItem> items = (List<WayBillPositionItem>) criteria.list();
        for (WayBillPositionItem item: items){
            Criteria orgCriteria = session.createCriteria(Org.class);
            orgCriteria.add(Restrictions.eq("idOfOrg", item.getOrgOwner()));
            orgCriteria.setProjection(Projections.property("shortName"));
            item.setOrgOwnerShortName((String) orgCriteria.uniqueResult());
        }
        return items;
    }


    public List<Product> findProduct(ProductGroup productGroup, ConfigurationProvider provider, String filter, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findProduct(session, productGroup, provider, filter, orgOwners, deletedStatusSelected);
    }


    public List<GoodGroup> findGoodGroup(ConfigurationProvider provider, String filter, List<Long> orgOwners, Boolean deletedStatusSelected) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findGoodGroup(session, provider, filter, orgOwners, deletedStatusSelected);
    }

    public Long countProductsByProductGroup(ProductGroup currentProductGroup) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.countProductByProductGroup(session, currentProductGroup);
    }

    public List<Product> findProductByProductGroup(ProductGroup currentProductGroup) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findProductByProductGroup(session, currentProductGroup);
    }

}
