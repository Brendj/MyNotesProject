/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
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

    public static final String[] UNIT_SCALES = {"граммы", "миллиметры", "порции", "единицы"};

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        GoodGroup gg = (GoodGroup) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGG);
        if(gg == null) throw new DistributedObjectException("GoodGroup NOT_FOUND_VALUE");
        setGoodGroup(gg);
        Product p = (Product) DAOUtils.findDistributedObjectByRefGUID(session, guidOfP);
        TechnologicalMap tm = (TechnologicalMap) DAOUtils.findDistributedObjectByRefGUID(session, guidOfTM);
        if(p == null && tm == null) throw new DistributedObjectException("Product or TechnologicalMap NOT_FOUND_VALUE");
        if(p != null) setProduct(p);
        if(tm != null) setTechnologicalMap(tm);

        DistributedObjectException distributedObjectException = new DistributedObjectException("BasicGood NOT_FOUND_VALUE");
        distributedObjectException.setData(guidOfBasicGood);
        GoodsBasicBasket basicGood;
        try {
            basicGood = DAOUtils.findBasicGood(session, guidOfBasicGood);
        } catch (Exception e) {
            throw distributedObjectException;
        }
        if (basicGood == null) throw distributedObjectException;
        setBasicGood(basicGood);
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
        if(basicGood != null) setAttribute(element, "GuidOfBasicGood", basicGood.getGuid());
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
        guidOfBasicGood = getStringAttributeValue(node, "GuidOfBasicGood", 36);
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
    private GoodsBasicBasket basicGood;
    private String guidOfBasicGood;

    private User userCreate;
    private User userEdit;
    private User userDelete;

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

    public String getUnitScaleString(){
        return UNIT_SCALES[unitsScale];
    }
}
