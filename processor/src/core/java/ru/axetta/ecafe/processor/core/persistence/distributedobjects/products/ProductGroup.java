/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.IConfigProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.StateChange;

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
public class ProductGroup extends DistributedObject implements IConfigProvider {

    private String nameOfGroup;
    private Set<Product> productInternal;
    private Long idOfConfigurationProvider;
    private String classificationCode;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Name", nameOfGroup);
        setAttribute(element,"ClassificationCode", classificationCode);
    }

    @Override
    protected ProductGroup parseAttributes(Node node) throws Exception{
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);

        String stringNameOfGroup = getStringAttributeValue(node,"Name",512);
        if(stringNameOfGroup!=null) setNameOfGroup(stringNameOfGroup);

        String stringClassificationCode = getStringAttributeValue(node,"ClassificationCode",32);
        if(stringClassificationCode!=null) setClassificationCode(stringClassificationCode);

        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ProductGroup) distributedObject).getOrgOwner());
        setNameOfGroup(((ProductGroup) distributedObject).getNameOfGroup());
        setClassificationCode(((ProductGroup) distributedObject).getClassificationCode());
    }

    public String getNameOfGroup() {
        return nameOfGroup;
    }

    public String getShortNameOfGroup() {
        if(nameOfGroup.length()>35){
            return nameOfGroup.substring(0,32)+"...";
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

    public String getCodeClassificationSystem(){
        StringBuilder stringBuilder = new StringBuilder(classificationCode.substring(0,4));
        stringBuilder.append(classificationCode.substring(4,5));
        stringBuilder.append(classificationCode.substring(5,6));
        stringBuilder.append(classificationCode.substring(6,7));
        stringBuilder.append(classificationCode.substring(8));
        return stringBuilder.toString();
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

}
