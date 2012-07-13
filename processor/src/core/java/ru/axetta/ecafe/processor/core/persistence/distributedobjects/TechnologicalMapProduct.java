/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMapProduct extends DistributedObject {

    //Масса брутто, г
    private double grossWeight;

    //Масса нетто, г
    private double netWeight;

    private TechnologicalMap technologicalMap;

    private Product product;
    //Наименование продукта вытягивается из таблицы продуктов
    //private String nameOfProduct;

    @Override
    public void fill(DistributedObject distributedObject) {
        setGrossWeight(((TechnologicalMapProduct) distributedObject).getGrossWeight());
        setNetWeight(((TechnologicalMapProduct) distributedObject).getNetWeight());
        setProduct(((TechnologicalMapProduct) distributedObject).getProduct());
        setTechnologicalMap(((TechnologicalMapProduct) distributedObject).getTechnologicalMap());
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"GWeight", grossWeight);
        setAttribute(element,"NWeight", netWeight);
        setAttribute(element,"GuidOfP", product.getGuid());
        setAttribute(element,"GuidOfTM", technologicalMap.getGuid());
    }

    @Override
    protected TechnologicalMapProduct parseAttributes(Node node) {

        Float floatGrossMass = getFloatAttributeValue(node,"GWeight");
        if(floatGrossMass!=null) setGrossWeight(floatGrossMass);

        Float floatNetMass = getFloatAttributeValue(node,"NWeight");
        if(floatNetMass!=null) setNetWeight(floatNetMass);

        String stringRefGUIDOfProduct = getStringAttributeValue(node,"GuidOfP",36);
        setProduct(DAOService.getInstance().findDistributedObjectByRefGUID(Product.class,stringRefGUIDOfProduct));

        String stringRefGUIDOfTechnoMap = getStringAttributeValue(node,"GuidOfTM",36);
        setTechnologicalMap(DAOService.getInstance().findDistributedObjectByRefGUID(TechnologicalMap.class,stringRefGUIDOfTechnoMap));

        return this;
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
/*
    public String getNameOfProduct() {
        return nameOfProduct;
    }

    public void setNameOfProduct(String nameOfProduct) {
        this.nameOfProduct = nameOfProduct;
    }*/

    public double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(double netWeight) {
        this.netWeight = netWeight;
    }

    public double getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(double grossWeight) {
        this.grossWeight = grossWeight;
    }

}
