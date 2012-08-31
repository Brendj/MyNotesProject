/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class Good extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        GoodGroup gg = DAOService.getInstance().findDistributedObjectByRefGUID(GoodGroup.class, guidOfGG);
        if(gg == null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setGoodGroup(gg);
        Product p = DAOService.getInstance().findDistributedObjectByRefGUID(Product.class, guidOfP);
        TechnologicalMap tm = DAOService.getInstance().findDistributedObjectByRefGUID(TechnologicalMap.class, guidOfTM);
        if(p == null && tm == null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        if(p != null) setProduct(p);
        if(tm != null) setTechnologicalMap(tm);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Name", nameOfGood);
        setAttribute(element,"FullName", fullName);
        setAttribute(element,"GoodsCode", goodsCode);
        setAttribute(element,"UnitsScale", unitsScale);
        setAttribute(element,"NetWeight", netWeight);
        setAttribute(element,"LifeTime", lifeTime);
        setAttribute(element,"Margin", margin);
        setAttribute(element,"GuidOfGroup", goodGroup.getGuid());
        if(product != null) setAttribute(element,"GuidOfBaseProduct", product.getGuid());
        if(technologicalMap != null) setAttribute(element,"GuidOfTechMap", technologicalMap.getGuid());
    }
    @Override
    protected Good parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringNameOfGood = getStringAttributeValue(node,"Name",512);
        if(stringNameOfGood!=null) setNameOfGood(stringNameOfGood);
        String stringFullName = getStringAttributeValue(node,"FullName",1024);
        if(stringFullName!=null) setFullName(stringFullName);
        String stringGoodsCode = getStringAttributeValue(node,"GoodsCode",32);
        if(stringGoodsCode!=null) setGoodsCode(stringGoodsCode);
        Integer integerUnitsScale = getIntegerAttributeValue(node,"UnitsScale");
        if(integerUnitsScale!=null) setUnitsScale(integerUnitsScale);
        Long longNetWeight = getLongAttributeValue(node, "NetWeight");
        if( longNetWeight != null) setNetWeight(longNetWeight);
        Long longLifeTime = getLongAttributeValue(node, "LifeTime");
        if(longLifeTime != null) setLifeTime(longLifeTime);
        Long longMargin = getLongAttributeValue(node, "Margin");
        if(longMargin != null) setMargin(longMargin);
        guidOfGG = getStringAttributeValue(node,"GuidOfGroup",36);
        guidOfP = getStringAttributeValue(node,"GuidOfBaseProduct",36);
        guidOfTM = getStringAttributeValue(node,"GuidOfTechMap",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Good) distributedObject).getOrgOwner());
        setNameOfGood(((Good) distributedObject).getNameOfGood());
        setFullName(((Good) distributedObject).getFullName());
        setGoodsCode(((Good) distributedObject).getGoodsCode());
        setUnitsScale(((Good) distributedObject).getUnitsScale());
        setNetWeight(((Good) distributedObject).getNetWeight());
        setLifeTime(((Good) distributedObject).getLifeTime());
        setMargin(((Good) distributedObject).getMargin());
    }

    private String nameOfGood;
    private String fullName;
    private String goodsCode;
    private Integer unitsScale;
    private Long netWeight;
    private Long lifeTime;
    private Long margin;
    private GoodGroup goodGroup;
    private String guidOfGG;
    private Product product;
    private String guidOfP;
    private TechnologicalMap technologicalMap;
    private String guidOfTM;

    public String getGuidOfP() {
        return guidOfP;
    }

    public void setGuidOfP(String guidOfP) {
        this.guidOfP = guidOfP;
    }

    public String getGuidOfTM() {
        return guidOfTM;
    }

    public void setGuidOfTM(String guidOfTM) {
        this.guidOfTM = guidOfTM;
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public void setTechnologicalMap(TechnologicalMap technologicalMap) {
        this.technologicalMap = technologicalMap;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getGuidOfGG() {
        return guidOfGG;
    }

    public void setGuidOfGG(String guidOfGG) {
        this.guidOfGG = guidOfGG;
    }

    public GoodGroup getGoodGroup() {
        return goodGroup;
    }

    public void setGoodGroup(GoodGroup goodGroup) {
        this.goodGroup = goodGroup;
    }

    public Long getMargin() {
        return margin;
    }

    public void setMargin(Long margin) {
        this.margin = margin;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    public Integer getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(Integer unitsScale) {
        this.unitsScale = unitsScale;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNameOfGood() {
        return nameOfGood;
    }

    public void setNameOfGood(String nameOfGood) {
        this.nameOfGood = nameOfGood;
    }
}
