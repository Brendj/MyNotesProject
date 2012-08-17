/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 30.05.12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMapProduct extends DistributedObject implements IConfigProvider {

    @Override
    public void fill(DistributedObject distributedObject) {
        //setOrgOwner(((TechnologicalMapProduct) distributedObject).getOrgOwner());
        setGrossWeight(((TechnologicalMapProduct) distributedObject).getGrossWeight());
        setNetWeight(((TechnologicalMapProduct) distributedObject).getNetWeight());
        setProduct(((TechnologicalMapProduct) distributedObject).getProduct());
        setTechnologicalMap(((TechnologicalMapProduct) distributedObject).getTechnologicalMap());
        setNumberGroupReplace(((TechnologicalMapProduct) distributedObject).getNumberGroupReplace());
    }


    @Override
    protected void appendAttributes(Element element) {
        //setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GWeight", grossWeight);
        setAttribute(element, "NWeight", netWeight);
        setAttribute(element, "NumberGroupReplace", netWeight);
        setAttribute(element, "GuidOfP", product.getGuid());
        setAttribute(element, "GuidOfTM", technologicalMap.getGuid());
    }

    @Override
    protected TechnologicalMapProduct parseAttributes(Node node) {
        //Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        //if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerGrossMass = getIntegerAttributeValue(node, "GWeight");
        if (integerGrossMass != null) {
            setGrossWeight(integerGrossMass);
        }

        Integer integerNetMass = getIntegerAttributeValue(node, "NWeight");
        if (integerNetMass != null) {
            setNetWeight(integerNetMass);
        }
        Integer integerNumberGroupReplace = getIntegerAttributeValue(node, "NumberGroupReplace");
        if (integerNumberGroupReplace != null) {
            setNumberGroupReplace(integerNumberGroupReplace);
        }


        guidOfP = getStringAttributeValue(node, "GuidOfP", 36);
        guidOfTM = getStringAttributeValue(node, "GuidOfTM", 36);
        return this;
    }

    @Override
    public void preProcess() throws DistributedObjectException {
        Product p = DAOService.getInstance().findDistributedObjectByRefGUID(Product.class, guidOfP);
        TechnologicalMap tm = DAOService.getInstance().findDistributedObjectByRefGUID(TechnologicalMap.class, guidOfTM);
        if(tm==null || p==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setProduct(p);
        setTechnologicalMap(tm);
    }

    //Масса брутто, г
    private Integer grossWeight;

    //Масса нетто, г
    private Integer netWeight;

    private TechnologicalMap technologicalMap;

    private Product product;

    private String guidOfP;
    private String guidOfTM;
    private Long idOfConfigurationProvider;
    private Integer numberGroupReplace;

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

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

}
