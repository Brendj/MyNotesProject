/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

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
    private String сlassificationCode;
    private Long idOfConfigurationProvider;

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"Name", nameOfGroup);
        setAttribute(element,"ClassificationCode", сlassificationCode);
    }

    @Override
    protected ProductGroup parseAttributes(Node node) {

        String stringNameOfGroup = getStringAttributeValue(node,"Name",512);
        if(stringNameOfGroup!=null) setNameOfGroup(stringNameOfGroup);

        String stringClassificationCode = getStringAttributeValue(node,"ClassificationCode",32);
        if(stringClassificationCode!=null) setСlassificationCode(stringClassificationCode);

        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setNameOfGroup(((ProductGroup) distributedObject).getNameOfGroup());
        setСlassificationCode(((ProductGroup) distributedObject).getСlassificationCode());
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

    public String getСlassificationCode() {
        return сlassificationCode;
    }

    public void setСlassificationCode(String сlassificationCode) {
        this.сlassificationCode = сlassificationCode;
    }

}
