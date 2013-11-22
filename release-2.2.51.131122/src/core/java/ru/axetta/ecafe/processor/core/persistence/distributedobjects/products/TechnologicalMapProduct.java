/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConfigurationProviderDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMapProduct extends ConfigurationProviderDistributedObject {

    //Масса брутто, г
    private Integer grossWeight;

    //Масса нетто, г
    private Integer netWeight;

    private TechnologicalMap technologicalMap;

    private Product product;

    private String guidOfP;
    private String guidOfTM;
    private Integer numberGroupReplace;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("product", "p", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("technologicalMap", "tm", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");
        projectionList.add(Projections.property("grossWeight"), "grossWeight");
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("numberGroupReplace"), "numberGroupReplace");

        projectionList.add(Projections.property("p.guid"), "guidOfP");
        projectionList.add(Projections.property("tm.guid"), "guidOfTM");

        criteria.setProjection(projectionList);

    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((TechnologicalMapProduct) distributedObject).getOrgOwner());
        setGrossWeight(((TechnologicalMapProduct) distributedObject).getGrossWeight());
        setNetWeight(((TechnologicalMapProduct) distributedObject).getNetWeight());
        setProduct(((TechnologicalMapProduct) distributedObject).getProduct());
        setTechnologicalMap(((TechnologicalMapProduct) distributedObject).getTechnologicalMap());
        setNumberGroupReplace(((TechnologicalMapProduct) distributedObject).getNumberGroupReplace());
        setIdOfConfigurationProvider(((TechnologicalMapProduct) distributedObject).getIdOfConfigurationProvider());
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "GWeight", grossWeight);
        XMLUtils.setAttributeIfNotNull(element, "NWeight", netWeight);
        XMLUtils.setAttributeIfNotNull(element, "NumberGroupReplace", numberGroupReplace);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfP", guidOfP);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfTM", guidOfTM);
    }

    @Override
    protected TechnologicalMapProduct parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        Integer integerGrossMass = XMLUtils.getIntegerAttributeValue(node, "GWeight");
        if (integerGrossMass != null) {
            setGrossWeight(integerGrossMass);
        }
        Integer integerNetMass = XMLUtils.getIntegerAttributeValue(node, "NWeight");
        if (integerNetMass != null) {
            setNetWeight(integerNetMass);
        }
        Integer integerNumberGroupReplace = XMLUtils.getIntegerAttributeValue(node, "NumberGroupReplace");
        if (integerNumberGroupReplace != null) {
            setNumberGroupReplace(integerNumberGroupReplace);
        }
        guidOfP = XMLUtils.getStringAttributeValue(node, "GuidOfP", 36);
        guidOfTM = XMLUtils.getStringAttributeValue(node, "GuidOfTM", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    protected void beforeProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        Product p = DAOUtils.findDistributedObjectByRefGUID(Product.class, session, guidOfP);
        TechnologicalMap tm = DAOUtils.findDistributedObjectByRefGUID(TechnologicalMap.class, session, guidOfTM);
        if (tm == null || p == null) {
            throw new DistributedObjectException("NOT_FOUND_VALUE");
        }
        setProduct(p);
        setTechnologicalMap(tm);
    }

    public Integer getNumberGroupReplace() {
        return numberGroupReplace;
    }

    public void setNumberGroupReplace(Integer numberGroupReplace) {
        this.numberGroupReplace = numberGroupReplace;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public void setTechnologicalMap(TechnologicalMap technologicalMap) {
        this.technologicalMap = technologicalMap;
    }

    public Integer getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Integer netWeight) {
        this.netWeight = netWeight;
    }

    public Integer getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Integer grossWeight) {
        this.grossWeight = grossWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DistributedObject)) {
            return false;
        }

        DistributedObject that = (DistributedObject) o;

        if (guid != null ? !guid.equals(that.getGuid()) : that.getGuid() != null) {
            return false;
        }

        // Когда объектам не назначены guid-ы, однако указаны ссылки на базовые продукты и тех.карты
        if (((guid == null) && (that.getGuid() == null)) && (that instanceof TechnologicalMapProduct) && !(
                ((TechnologicalMapProduct) that).getProduct().equals(getProduct()) && ((TechnologicalMapProduct) that).getTechnologicalMap()
                        .equals(getTechnologicalMap()))) {
            return false;
        }

        return true;
    }

}
