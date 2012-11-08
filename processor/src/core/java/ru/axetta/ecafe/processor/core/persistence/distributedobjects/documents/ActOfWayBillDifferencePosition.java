/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class ActOfWayBillDifferencePosition extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        //Good g = DAOService.getInstance().findDistributedObjectByRefGUID(Good.class, guidOfG);
        Good g  = (Good) DAOUtils.findDistributedObjectByRefGUID(session, guidOfG);
        if(g==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("NOT_FOUND_GOOD");
            distributedObjectException.setData(guidOfG);
            throw distributedObjectException;
        }
        setGood(g);
        //ActOfWayBillDifference awd = DAOService.getInstance().findDistributedObjectByRefGUID(ActOfWayBillDifference.class, guidOfAWD);
        ActOfWayBillDifference awd  = (ActOfWayBillDifference) DAOUtils.findDistributedObjectByRefGUID(session, guidOfAWD);
        if(awd==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("NOT_FOUND_"+actOfWayBillDifference.getClass().getSimpleName().toUpperCase());
            distributedObjectException.setData(guidOfG);
            throw distributedObjectException;
        }
        setActOfWayBillDifference(awd);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"UnitsScale", unitsScale);
        setAttribute(element,"TotalCount", totalCount);
        setAttribute(element,"NetWeight", netWeight);
        setAttribute(element,"GrossWeight", grossWeight);
        setAttribute(element,"GoodsCreationDate", getDateFormat().format(goodsCreationDate));
        setAttribute(element,"LifeTime", lifeTime);
        setAttribute(element,"Price", price);
        setAttribute(element,"NDS", nds);
        setAttribute(element, "GuidOfGoods", good.getGuid());
        setAttribute(element, "GuidOfActOfDifference", actOfWayBillDifference.getGuid());
    }

    @Override
    protected ActOfWayBillDifferencePosition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerUnitsScale = getIntegerAttributeValue(node, "UnitsScale");
        if(integerUnitsScale != null) setUnitsScale(integerUnitsScale);
        Long longTotalCount = getLongAttributeValue(node, "TotalCount");
        if(longTotalCount != null) setTotalCount(longTotalCount);
        Long longNetWeight = getLongAttributeValue(node, "NetWeight");
        if(longTotalCount != null) setTotalCount(longNetWeight);
        Long longGrossWeight = getLongAttributeValue(node,"GrossWeight");
        if(longGrossWeight != null) setGrossWeight(longGrossWeight);
        Date dateNameOfGood = getDateTimeAttributeValue(node, "GoodsCreationDate");
        if(dateNameOfGood!=null) setGoodsCreationDate(dateNameOfGood);
        Long longLifeTime = getLongAttributeValue(node, "LifeTime");
        if(longLifeTime != null) setLifeTime(longLifeTime);
        Long longPrice = getLongAttributeValue(node, "Price");
        if(longPrice != null) setPrice(longPrice);
        Long longNDS = getLongAttributeValue(node,"NDS");
        if(longNDS != null) setNds(longNDS);
        guidOfG = getStringAttributeValue(node,"GuidOfGoods",36);
        guidOfAWD = getStringAttributeValue(node,"GuidOfActOfDifference",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ActOfWayBillDifferencePosition) distributedObject).getOrgOwner());
        setGoodsCreationDate(((ActOfWayBillDifferencePosition) distributedObject).getGoodsCreationDate());
        setLifeTime(((ActOfWayBillDifferencePosition) distributedObject).getLifeTime());
        setUnitsScale(((ActOfWayBillDifferencePosition) distributedObject).getUnitsScale());
        setTotalCount(((ActOfWayBillDifferencePosition) distributedObject).getTotalCount());
        setNetWeight(((ActOfWayBillDifferencePosition) distributedObject).getNetWeight());
        setGrossWeight(((ActOfWayBillDifferencePosition) distributedObject).getGrossWeight());
        setPrice(((ActOfWayBillDifferencePosition) distributedObject).getPrice());
        setNds(((ActOfWayBillDifferencePosition) distributedObject).getNds());
    }

    private Good good;
    private String guidOfG;
    private Date goodsCreationDate;
    private Long lifeTime;
    private Integer unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long grossWeight;
    private Long price;
    private Long nds;
    private ActOfWayBillDifference actOfWayBillDifference;
    private String guidOfAWD;

    public String getGuidOfAWD() {
        return guidOfAWD;
    }

    public void setGuidOfAWD(String guidOfAWD) {
        this.guidOfAWD = guidOfAWD;
    }

    public ActOfWayBillDifference getActOfWayBillDifference() {
        return actOfWayBillDifference;
    }

    public void setActOfWayBillDifference(ActOfWayBillDifference actOfWayBillDifference) {
        this.actOfWayBillDifference = actOfWayBillDifference;
    }

    public Long getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Long grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getGuidOfG() {
        return guidOfG;
    }

    public void setGuidOfG(String guidOfG) {
        this.guidOfG = guidOfG;
    }

    public Long getNds() {
        return nds;
    }

    public void setNds(Long nds) {
        this.nds = nds;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(Integer unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Date getGoodsCreationDate() {
        return goodsCreationDate;
    }

    public void setGoodsCreationDate(Date goodsCreationDate) {
        this.goodsCreationDate = goodsCreationDate;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }
}
