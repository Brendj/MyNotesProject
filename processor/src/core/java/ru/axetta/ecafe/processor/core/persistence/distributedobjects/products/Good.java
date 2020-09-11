/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConfigurationProviderDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.ActOfWayBillDifferencePosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.InternalDisposingDocumentPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.InternalIncomingDocumentPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBillPosition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
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
public class Good extends ConfigurationProviderDistributedObject {

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
    private String[] parts;
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

    private GoodType goodType;
    private GoodAgeGroupType ageGroupType;
    private Boolean dailySale;

    public String[] getParts() {
        return parts;
    }

    protected void setParts(String[] parts) {
        this.parts = parts;
    }

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("goodGroup", "gg", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("product", "p", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("technologicalMap", "tm", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("basicGood", "bg", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("nameOfGood"), "nameOfGood");
        projectionList.add(Projections.property("fullName"), "fullName");

        projectionList.add(Projections.property("goodsCode"), "goodsCode");
        projectionList.add(Projections.property("unitsScale"), "unitsScale");
        projectionList.add(Projections.property("netWeight"), "netWeight");
        projectionList.add(Projections.property("margin"), "margin");
        projectionList.add(Projections.property("lifeTime"), "lifeTime");
        projectionList.add(Projections.property("gg.guid"), "guidOfGG");
        projectionList.add(Projections.property("p.guid"), "guidOfP");
        projectionList.add(Projections.property("tm.guid"), "guidOfTM");
        projectionList.add(Projections.property("bg.guid"), "guidOfBasicGood");
        projectionList.add(Projections.property("goodType"), "goodType");
        projectionList.add(Projections.property("ageGroupType"), "ageGroupType");
        projectionList.add(Projections.property("dailySale"), "dailySale");
        criteria.setProjection(projectionList);

    }

    @Override
    protected void beforeProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        GoodGroup gg = DAOUtils.findDistributedObjectByRefGUID(GoodGroup.class, session, guidOfGG);
        if (gg == null) {
            throw new DistributedObjectException("GoodGroup NOT_FOUND_VALUE");
        }
        //Удаление объекта из кеша сессии с одинаковым идентификатором
        GoodGroup ggFromSession = (GoodGroup) session.get(GoodGroup.class, gg.getGlobalId());
        if(ggFromSession != null){
            session.evict(ggFromSession);
        }
        //Если конфигурация у провайдера поменялась, то меняем и группы. Чтобы группа соответствовала товару
        if (gg.getIdOfConfigurationProvider() == null || !gg.getIdOfConfigurationProvider().equals(getIdOfConfigurationProvider()))
        {
            gg.setIdOfConfigurationProvider(getIdOfConfigurationProvider());
            session.update(gg);
        }
        setGoodGroup(gg);
        Product p = DAOUtils.findDistributedObjectByRefGUID(Product.class, session, guidOfP);
        TechnologicalMap tm = DAOUtils.findDistributedObjectByRefGUID(TechnologicalMap.class, session, guidOfTM);
        if (p == null && tm == null) {
            throw new DistributedObjectException("Product or TechnologicalMap NOT_FOUND_VALUE");
        }
        if (p != null) {
            setProduct(p);
        }
        if (tm != null) {
            setTechnologicalMap(tm);
        }

        GoodsBasicBasket basicGood = DAOUtils.findBasicGood(session, guidOfBasicGood);
        if (basicGood != null) {
            setBasicGood(basicGood);
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Name", nameOfGood);
        XMLUtils.setAttributeIfNotNull(element, "FullName", fullName);
        XMLUtils.setAttributeIfNotNull(element, "GoodsCode", goodsCode);
        XMLUtils.setAttributeIfNotNull(element, "UnitsScale", unitsScale.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "NetWeight", netWeight);
        XMLUtils.setAttributeIfNotNull(element, "LifeTime", lifeTime);
        XMLUtils.setAttributeIfNotNull(element, "Margin", margin);
        //XMLUtils.setAttributeIfNotNull(element, "GuidOfGroup", goodGroup.getGuid());
        XMLUtils.setAttributeIfNotNull(element, "GuidOfGroup", guidOfGG);
        if (StringUtils.isNotEmpty(guidOfP)) {
            //XMLUtils.setAttributeIfNotNull(element, "GuidOfBaseProduct", product.getGuid());
            XMLUtils.setAttributeIfNotNull(element, "GuidOfBaseProduct", guidOfP);
        }
        if (StringUtils.isNotEmpty(guidOfTM)) {
            //XMLUtils.setAttributeIfNotNull(element, "GuidOfTechMap", technologicalMap.getGuid());
            XMLUtils.setAttributeIfNotNull(element, "GuidOfTechMap", guidOfTM);
        }
        if (StringUtils.isNotEmpty(guidOfBasicGood)) {
            //XMLUtils.setAttributeIfNotNull(element, "GuidOfBasicGood", basicGood.getGuid());
            XMLUtils.setAttributeIfNotNull(element, "GuidOfBasicGood", guidOfBasicGood);
        }
        XMLUtils.setAttributeIfNotNull(element, "TypeGood", (null != goodType) ? goodType.getCode() : null);
        XMLUtils.setAttributeIfNotNull(element, "AgeGroup", (null != ageGroupType) ? ageGroupType.getCode() : null);
        XMLUtils.setAttributeIfNotNull(element, "DailySale", dailySale);
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Good) distributedObject).getOrgOwner());
        setProduct(((Good) distributedObject).getProduct());
        setGuidOfP(((Good) distributedObject).getGuidOfP());
        setGoodGroup(((Good) distributedObject).getGoodGroup());
        setGuidOfGG(((Good) distributedObject).getGuidOfGG());
        setTechnologicalMap(((Good) distributedObject).getTechnologicalMap());
        setGuidOfTM(((Good) distributedObject).getGuidOfTM());
        setBasicGood(((Good) distributedObject).getBasicGood());
        setGuidOfBasicGood(((Good) distributedObject).getGuidOfBasicGood());
        setNameOfGood(((Good) distributedObject).getNameOfGood());
        String stringFullName = ((Good) distributedObject).getFullName();
        if (stringFullName != null) {
            String[] tmp = stringFullName.split("/");
            if (tmp.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : tmp) {
                    sb.append(s.trim()).append("/");
                }
                String s = sb.toString();
                setFullName(s.substring(0, s.length() - 1));
            } else {
                setFullName(stringFullName);
            }
        }
        setGoodsCode(((Good) distributedObject).getGoodsCode());
        setNetWeight(((Good) distributedObject).getNetWeight());
        setLifeTime(((Good) distributedObject).getLifeTime());
        setMargin(((Good) distributedObject).getMargin());
        setUnitsScale(((Good) distributedObject).getUnitsScale());
        setIdOfConfigurationProvider(((Good) distributedObject).getIdOfConfigurationProvider());

        GoodType goodType = ((Good) distributedObject).getGoodType();
        setGoodType((null == goodType) ? GoodType.UNSPECIFIED : goodType);
        GoodAgeGroupType ageGroupType = ((Good)distributedObject).getAgeGroupType();
        setAgeGroupType((null == ageGroupType) ? GoodAgeGroupType.UNSPECIFIED : ageGroupType);
        Boolean dailySale = ((Good)distributedObject).getDailySale();
        setDailySale((null == dailySale) ? Boolean.FALSE : dailySale);
    }

    @Override
    protected Good parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        String stringNameOfGood = XMLUtils.getStringAttributeValue(node, "Name", 512);
        if (stringNameOfGood != null) {
            setNameOfGood(stringNameOfGood);
        }
        String stringFullName = XMLUtils.getStringAttributeValue(node, "FullName", 1024);
        if (stringFullName != null) {
            String[] tmp = stringFullName.split("/");
            if (tmp.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : tmp) {
                    sb.append(s.trim()).append("/");
                }
                String s = sb.toString();
                setFullName(s.substring(0, s.length() - 1));
            } else {
                setFullName(stringFullName);
            }
        }
        String stringGoodsCode = XMLUtils.getStringAttributeValue(node, "GoodsCode", 32);
        if (stringGoodsCode != null) {
            setGoodsCode(stringGoodsCode);
        }
        Integer integerUnitsScale = XMLUtils.getIntegerAttributeValue(node, "UnitsScale");
        if (integerUnitsScale != null) {
            setUnitsScale(UnitScale.fromInteger(integerUnitsScale));
        }
        Long longNetWeight = XMLUtils.getLongAttributeValue(node, "NetWeight");
        if (longNetWeight != null) {
            setNetWeight(longNetWeight);
        }
        Long longLifeTime = XMLUtils.getLongAttributeValue(node, "LifeTime");
        if (longLifeTime != null) {
            setLifeTime(longLifeTime);
        }
        Long longMargin = XMLUtils.getLongAttributeValue(node, "Margin");
        if (longMargin != null) {
            setMargin(longMargin);
        }
        guidOfGG = XMLUtils.getStringAttributeValue(node, "GuidOfGroup", 36);
        guidOfP = XMLUtils.getStringAttributeValue(node, "GuidOfBaseProduct", 36);
        guidOfTM = XMLUtils.getStringAttributeValue(node, "GuidOfTechMap", 36);
        guidOfBasicGood = XMLUtils.getStringAttributeValue(node, "GuidOfBasicGood", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);

        Integer typeGood = XMLUtils.getIntegerAttributeValue(node, "TypeGood");
        if (null != typeGood) {
            setGoodType(GoodType.fromInteger(typeGood));
        }

        Integer ageGroup = XMLUtils.getIntegerAttributeValue(node, "AgeGroup");
        if (null != ageGroup) {
            setAgeGroupType(GoodAgeGroupType.fromInteger(ageGroup));
        }

        Boolean dailySale = XMLUtils.getBooleanAttributeValue(node, "DailySale");
        if (null != dailySale) {
            setDailySale(dailySale);
        }

        return this;
    }

    public Set<ActOfWayBillDifferencePosition> getActOfWayBillDifferencePositionInternal() {
        return actOfWayBillDifferencePositionInternal;
    }

    public void setActOfWayBillDifferencePositionInternal(Set<ActOfWayBillDifferencePosition> actOfWayBillDifferencePositionInternal) {
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

    public void setInternalDisposingDocumentPositionInternal(Set<InternalDisposingDocumentPosition> internalDisposingDocumentPositionInternal) {
        this.internalDisposingDocumentPositionInternal = internalDisposingDocumentPositionInternal;
    }

    public Set<InternalIncomingDocumentPosition> getInternalIncomingDocumentPositionInternal() {
        return internalIncomingDocumentPositionInternal;
    }

    public void setInternalIncomingDocumentPositionInternal(Set<InternalIncomingDocumentPosition> internalIncomingDocumentPositionInternal) {
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

    public String getPathPart4() {
        return pathPart4;
    }

    void setPathPart4(String pathPart4) {
        this.pathPart4 = pathPart4;
    }

    public String getPathPart3() {
        return pathPart3;
    }

    void setPathPart3(String pathPart3) {
        this.pathPart3 = pathPart3;
    }

    public String getPathPart2() {
        return pathPart2;
    }

    void setPathPart2(String pathPart2) {
        this.pathPart2 = pathPart2;
    }

    public String getPathPart1() {
        return pathPart1;
    }

    void setPathPart1(String pathPart1) {
        this.pathPart1 = pathPart1;
    }

    public GoodType getGoodType() {
        return goodType;
    }

    public void setGoodType(GoodType goodType) {
        this.goodType = goodType;
    }

    public GoodAgeGroupType getAgeGroupType() {
        return ageGroupType;
    }

    public void setAgeGroupType(GoodAgeGroupType ageGroupType) {
        this.ageGroupType = ageGroupType;
    }

    public Boolean getDailySale() {
        return dailySale;
    }

    public void setDailySale(Boolean dailySale) {
        this.dailySale = dailySale;
    }
}
