/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.hibernate.type.EnumType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class Good extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        GoodGroup gg = DAOUtils.findDistributedObjectByRefGUID(GoodGroup.class, session, guidOfGG);
        if(gg == null) throw new DistributedObjectException("GoodGroup NOT_FOUND_VALUE");
        setGoodGroup(gg);
        Product p = DAOUtils.findDistributedObjectByRefGUID(Product.class, session, guidOfP);
        TechnologicalMap tm = DAOUtils.findDistributedObjectByRefGUID(TechnologicalMap.class, session, guidOfTM);
        if(p == null && tm == null) throw new DistributedObjectException("Product or TechnologicalMap NOT_FOUND_VALUE");
        if(p != null) setProduct(p);
        if(tm != null) setTechnologicalMap(tm);

        GoodsBasicBasket basicGood = DAOUtils.findBasicGood(session, guidOfBasicGood);
        if (basicGood != null) {
            setBasicGood(basicGood);
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Name", nameOfGood);
        setAttribute(element, "FullName", fullName);
        setAttribute(element,"GoodsCode", goodsCode);
        setAttribute(element,"UnitsScale", unitsScale.ordinal());
        setAttribute(element,"NetWeight", netWeight);
        setAttribute(element,"LifeTime", lifeTime);
        setAttribute(element,"Margin", margin);
        setAttribute(element,"GuidOfGroup", goodGroup.getGuid());
        if(product != null) setAttribute(element,"GuidOfBaseProduct", product.getGuid());
        if(technologicalMap != null) setAttribute(element,"GuidOfTechMap", technologicalMap.getGuid());
        if(basicGood != null) setAttribute(element, "GuidOfBasicGood", basicGood.getGuid());
    }
    @Override
    protected Good parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringNameOfGood = getStringAttributeValue(node,"Name",512);
        if(stringNameOfGood!=null) setNameOfGood(stringNameOfGood);
        String stringFullName = getStringAttributeValue(node,"FullName",1024);
        if(stringFullName!=null) {
            String[] tmp = stringFullName.split("/");
            if(tmp.length>0){
                StringBuilder sb = new StringBuilder();
                for (String s: tmp){
                     sb.append(s.trim()).append("/");
                }
                String s = sb.toString();
                setFullName(s.substring(0,s.length()-1));
            } else {
                setFullName(stringFullName);
            }
        }
        String stringGoodsCode = getStringAttributeValue(node,"GoodsCode",32);
        if(stringGoodsCode!=null) setGoodsCode(stringGoodsCode);
        Integer integerUnitsScale = getIntegerAttributeValue(node,"UnitsScale");
        if(integerUnitsScale!=null) setUnitsScale(UnitScale.fromInteger(integerUnitsScale));
        Long longNetWeight = getLongAttributeValue(node, "NetWeight");
        if( longNetWeight != null) setNetWeight(longNetWeight);
        Long longLifeTime = getLongAttributeValue(node, "LifeTime");
        if(longLifeTime != null) setLifeTime(longLifeTime);
        Long longMargin = getLongAttributeValue(node, "Margin");
        if(longMargin != null) setMargin(longMargin);
        guidOfGG = getStringAttributeValue(node,"GuidOfGroup",36);
        guidOfP = getStringAttributeValue(node,"GuidOfBaseProduct",36);
        guidOfTM = getStringAttributeValue(node,"GuidOfTechMap",36);
        guidOfBasicGood = getStringAttributeValue(node, "GuidOfBasicGood", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Good) distributedObject).getOrgOwner());
        setNameOfGood(((Good) distributedObject).getNameOfGood());
        String stringFullName = ((Good) distributedObject).getFullName();
        if(stringFullName!=null) {
            String[] tmp = stringFullName.split("/");
            if(tmp.length>0){
                StringBuilder sb = new StringBuilder();
                for (String s: tmp){
                    sb.append(s.trim()).append("/");
                }
                String s = sb.toString();
                setFullName(s.substring(0,s.length()-1));
            } else {
                setFullName(stringFullName);
            }
        }
        setGoodsCode(((Good) distributedObject).getGoodsCode());
        setNetWeight(((Good) distributedObject).getNetWeight());
        setLifeTime(((Good) distributedObject).getLifeTime());
        setMargin(((Good) distributedObject).getMargin());
        setUnitsScale(((Good)distributedObject).getUnitsScale());
    }

    private Set<TradeMaterialGood> tradeMaterialGoodInternal;
    private Set<ProhibitionExclusion> prohibitionExclusionInternal;
    private Set<Prohibition> prohibitionInternal;
    private Set<GoodComplaintBook> goodComplaintBookInternal;
    private Set<GoodBasicBasketPrice> goodBasicBasketPriceInternal;
    private Set<WayBillPosition> wayBillPositionInternal;
    private Set<InternalIncomingDocumentPosition> internalIncomingDocumentPositionInternal;
    private Set<InternalDisposingDocumentPosition> internalDisposingDocumentPositionInternal;
    private Set<GoodRequestPosition> goodRequestPositionInternal;

    private Set<ActOfWayBillDifferencePosition> actOfWayBillDifferencePositionInternal;

    public Set<ActOfWayBillDifferencePosition> getActOfWayBillDifferencePositionInternal() {
        return actOfWayBillDifferencePositionInternal;
    }

    public void setActOfWayBillDifferencePositionInternal(
            Set<ActOfWayBillDifferencePosition> actOfWayBillDifferencePositionInternal) {
        this.actOfWayBillDifferencePositionInternal = actOfWayBillDifferencePositionInternal;
    }

    public Set<GoodRequestPosition> getGoodRequestPositionInternal() {
        return goodRequestPositionInternal;
    }

    public void setGoodRequestPositionInternal(Set<GoodRequestPosition> goodRequestPositionInternal) {
        this.goodRequestPositionInternal = goodRequestPositionInternal;
    }

    public Set<InternalDisposingDocumentPosition> getInternalDisposingDocumentPositionInternal() {
        return internalDisposingDocumentPositionInternal;
    }

    public void setInternalDisposingDocumentPositionInternal(
            Set<InternalDisposingDocumentPosition> internalDisposingDocumentPositionInternal) {
        this.internalDisposingDocumentPositionInternal = internalDisposingDocumentPositionInternal;
    }

    public Set<InternalIncomingDocumentPosition> getInternalIncomingDocumentPositionInternal() {
        return internalIncomingDocumentPositionInternal;
    }

    public void setInternalIncomingDocumentPositionInternal(
            Set<InternalIncomingDocumentPosition> internalIncomingDocumentPositionInternal) {
        this.internalIncomingDocumentPositionInternal = internalIncomingDocumentPositionInternal;
    }

    public Set<WayBillPosition> getWayBillPositionInternal() {
        return wayBillPositionInternal;
    }

    public void setWayBillPositionInternal(Set<WayBillPosition> wayBillPositionInternal) {
        this.wayBillPositionInternal = wayBillPositionInternal;
    }

    public Set<GoodBasicBasketPrice> getGoodBasicBasketPriceInternal() {
        return goodBasicBasketPriceInternal;
    }

    public void setGoodBasicBasketPriceInternal(Set<GoodBasicBasketPrice> goodBasicBasketPriceInternal) {
        this.goodBasicBasketPriceInternal = goodBasicBasketPriceInternal;
    }

    public Set<GoodComplaintBook> getGoodComplaintBookInternal() {
        return goodComplaintBookInternal;
    }

    public void setGoodComplaintBookInternal(Set<GoodComplaintBook> goodComplaintBookInternal) {
        this.goodComplaintBookInternal = goodComplaintBookInternal;
    }

    public Set<Prohibition> getProhibitionInternal() {
        return prohibitionInternal;
    }

    public void setProhibitionInternal(Set<Prohibition> prohibitionInternal) {
        this.prohibitionInternal = prohibitionInternal;
    }

    public Set<ProhibitionExclusion> getProhibitionExclusionInternal() {
        return prohibitionExclusionInternal;
    }

    public void setProhibitionExclusionInternal(Set<ProhibitionExclusion> prohibitionExclusionInternal) {
        this.prohibitionExclusionInternal = prohibitionExclusionInternal;
    }

    public Set<TradeMaterialGood> getTradeMaterialGoodInternal() {
        return tradeMaterialGoodInternal;
    }

    public void setTradeMaterialGoodInternal(Set<TradeMaterialGood> tradeMaterialGoodInternal) {
        this.tradeMaterialGoodInternal = tradeMaterialGoodInternal;
    }

    private String nameOfGood;
    private String fullName;
    private String goodsCode;
    private Long netWeight;
    private Long lifeTime;
    private Long margin;
    private GoodGroup goodGroup;
    private String guidOfGG;
    private Product product;
    private String guidOfP;
    private TechnologicalMap technologicalMap;
    private String guidOfTM;
    private GoodsBasicBasket basicGood;
    private String guidOfBasicGood;
    private User userCreate;
    private User userEdit;
    private User userDelete;
    private UnitScale unitsScale;
    private String pathPart1;
    private String pathPart2;
    private String pathPart3;
    private String pathPart4;

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }


    public GoodsBasicBasket getBasicGood() {
        return basicGood;
    }

    public void setBasicGood(GoodsBasicBasket basicGood) {
        this.basicGood = basicGood;
    }

    public String getGuidOfBasicGood() {
        return guidOfBasicGood;
    }

    public void setGuidOfBasicGood(String guidOfBasicGood) {
        this.guidOfBasicGood = guidOfBasicGood;
    }

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

    public User getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(User userCreate) {
        this.userCreate = userCreate;
    }

    public User getUserEdit() {
        return userEdit;
    }

    public void setUserEdit(User userEdit) {
        this.userEdit = userEdit;
    }

    public User getUserDelete() {
        return userDelete;
    }

    public void setUserDelete(User userDelete) {
        this.userDelete = userDelete;
    }

    String getPathPart4() {
        return pathPart4;
    }

    void setPathPart4(String pathPart4) {
        this.pathPart4 = pathPart4;
    }

    String getPathPart3() {
        return pathPart3;
    }

    void setPathPart3(String pathPart3) {
        this.pathPart3 = pathPart3;
    }

    String getPathPart2() {
        return pathPart2;
    }

    void setPathPart2(String pathPart2) {
        this.pathPart2 = pathPart2;
    }

    String getPathPart1() {
        return pathPart1;
    }

    void setPathPart1(String pathPart1) {
        this.pathPart1 = pathPart1;
    }

}
