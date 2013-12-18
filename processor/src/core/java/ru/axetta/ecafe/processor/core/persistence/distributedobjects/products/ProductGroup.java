/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConfigurationProviderDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.07.12
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class ProductGroup extends ConfigurationProviderDistributedObject {

    @Override
    public void createProjections(Criteria criteria) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("guid"), "guid");
        projectionList.add(Projections.property("globalId"), "globalId");
        projectionList.add(Projections.property("globalVersion"), "globalVersion");
        projectionList.add(Projections.property("deletedState"), "deletedState");
        projectionList.add(Projections.property("orgOwner"), "orgOwner");
        projectionList.add(Projections.property("nameOfGroup"), "nameOfGroup");
        projectionList.add(Projections.property("classificationCode"), "classificationCode");
        criteria.setProjection(projectionList);
    }

    @Override
    protected void beforeProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Name", nameOfGroup);
        XMLUtils.setAttributeIfNotNull(element, "ClassificationCode", classificationCode);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setNameOfGroup(((ProductGroup) distributedObject).getNameOfGroup());
        setClassificationCode(((ProductGroup) distributedObject).getClassificationCode());
        setIdOfConfigurationProvider(((ProductGroup) distributedObject).getIdOfConfigurationProvider());
    }

    @Override
    protected ProductGroup parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        String stringNameOfGroup = XMLUtils.getStringAttributeValue(node, "Name", 512);
        if (stringNameOfGroup != null)
            setNameOfGroup(stringNameOfGroup);
        String stringClassificationCode = XMLUtils.getStringAttributeValue(node, "ClassificationCode", 32);
        if (stringClassificationCode != null)
            setClassificationCode(stringClassificationCode);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    private String nameOfGroup;
    private Set<Product> productInternal;
    private String classificationCode;
    private Set<Prohibition> prohibitionInternal;

    public Set<Prohibition> getProhibitionInternal() {
        return prohibitionInternal;
    }

    public void setProhibitionInternal(Set<Prohibition> prohibitionInternal) {
        this.prohibitionInternal = prohibitionInternal;
    }

    public String getNameOfGroup() {
        return nameOfGroup;
    }

    public String getShortNameOfGroup() {
        if (nameOfGroup.length() > 35) {
            return nameOfGroup.substring(0, 32) + "...";
        }
        return nameOfGroup;
    }

    public void setNameOfGroup(String nameOfGroup) {
        this.nameOfGroup = nameOfGroup;
    }

    public List<Product> getProducts(){
        return Collections.unmodifiableList(new ArrayList<Product>(getProductInternal()));
    }

    public void addProduct(Product product){
        productInternal.add(product);
    }

    public void removeProduct(Product product){
        productInternal.remove(product);
    }

    private Set<Product> getProductInternal() {
        return productInternal;
    }

    private void setProductInternal(Set<Product> productInternal) {
        this.productInternal = productInternal;
    }

    public String getCodeClassificationSystem() {
        StringBuilder stringBuilder = new StringBuilder(classificationCode.substring(0, 4));
        stringBuilder.append(classificationCode.substring(4, 5));
        stringBuilder.append(classificationCode.substring(5, 6));
        stringBuilder.append(classificationCode.substring(6, 7));
        stringBuilder.append(classificationCode.substring(8));
        return stringBuilder.toString();
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

}
