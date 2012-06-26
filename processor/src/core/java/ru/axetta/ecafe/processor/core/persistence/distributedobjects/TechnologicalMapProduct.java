/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMapProduct extends DistributedObject {

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"NameOfProduct", nameOfProduct);
        setAttribute(element,"GrossMass", grossMass);
        setAttribute(element,"NetMass", netMass);
    }

    @Override
    public TechnologicalMapProduct build(Node node) {
        /* Begin required params */
        Long gid = getLongAttributeValue(node,"GID");
        if(gid!=null) setGlobalId(gid);

        Long version = getLongAttributeValue(node,"V");
        if(version!=null) setGlobalVersion(version);

        Long lid = getLongAttributeValue(node,"LID");
        if(lid!=null) setLocalID(lid);

        Integer status = getIntegerAttributeValue(node,"D");
        setDeletedState(status != null);
        tagName = node.getNodeName();
        /* End required params */
        String stringNameOfProduct = getStringAttributeValue(node,"NameOfProduct",512);
        if(stringNameOfProduct!=null) setNameOfProduct(stringNameOfProduct);

        Float floatGrossMass = getFloatAttributeValue(node,"GrossMass");
        if(floatGrossMass!=null) setGrossMass(floatGrossMass);

        Float floatNetMass = getFloatAttributeValue(node,"NetMass");
        if(floatNetMass!=null) setNetMass(floatNetMass);
        return this;
    }

    //Наименование продукта
    private String nameOfProduct;
    //Масса брутто, г
    private Float grossMass;
    //Масса нетто, г
    private Float netMass;

    public String getNameOfProduct() {
        return nameOfProduct;
    }

    public void setNameOfProduct(String nameOfProduct) {
        this.nameOfProduct = nameOfProduct;
    }

    public Float getGrossMass() {
        return grossMass;
    }

    public void setGrossMass(Float grossMass) {
        this.grossMass = grossMass;
    }

    public Float getNetMass() {
        return netMass;
    }

    public void setNetMass(Float netMass) {
        this.netMass = netMass;
    }
}
