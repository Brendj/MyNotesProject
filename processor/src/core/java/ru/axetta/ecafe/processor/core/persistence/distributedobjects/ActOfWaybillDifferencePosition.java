/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class ActOfWaybillDifferencePosition extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        Good g = DAOService.getInstance().findDistributedObjectByRefGUID(Good.class, guidOfG);
        if(g==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setGood(g);
        ActOfWaybillDifference awd = DAOService.getInstance().findDistributedObjectByRefGUID(ActOfWaybillDifference.class, guidOfAWD);
        if(awd==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setActOfWaybillDifference(awd);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"UnitsScale", unitsScale);
        setAttribute(element,"TotalCount", totalCount);
        setAttribute(element,"NetWeight", netWeight);
        setAttribute(element,"GrossWeight", grossWeight);
        setAttribute(element,"GoodsCreationDate", getDateFormat().format(goodsCreationDate));
        setAttribute(element,"LifeTime", lifeTime);
        setAttribute(element,"Price", price);
        setAttribute(element,"NDS", nds);
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GuidOfG", good.getGuid());
        setAttribute(element, "GuidOfAWD", actOfWaybillDifference.getGuid());
    }

    @Override
    protected ActOfWaybillDifferencePosition parseAttributes(Node node) throws ParseException, IOException {
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
        Date dateNameOfGood = getDateAttributeValue(node,"GoodsCreationDate");
        if(dateNameOfGood!=null) setGoodsCreationDate(dateNameOfGood);
        Long longLifeTime = getLongAttributeValue(node, "LifeTime");
        if(longLifeTime != null) setLifeTime(longLifeTime);
        Long longPrice = getLongAttributeValue(node, "Price");
        if(longPrice != null) setPrice(longPrice);
        Long longNDS = getLongAttributeValue(node,"NDS");
        if(longNDS != null) setNds(longNDS);
        guidOfG = getStringAttributeValue(node,"GuidOfG",36);
        guidOfAWD = getStringAttributeValue(node,"GuidOfAWD",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ActOfWaybillDifferencePosition) distributedObject).getOrgOwner());
        setGoodsCreationDate(((ActOfWaybillDifferencePosition) distributedObject).getGoodsCreationDate());
        setLifeTime(((ActOfWaybillDifferencePosition) distributedObject).getLifeTime());
        setUnitsScale(((ActOfWaybillDifferencePosition) distributedObject).getUnitsScale());
        setTotalCount(((ActOfWaybillDifferencePosition) distributedObject).getTotalCount());
        setNetWeight(((ActOfWaybillDifferencePosition) distributedObject).getNetWeight());
        setGrossWeight(((ActOfWaybillDifferencePosition) distributedObject).getGrossWeight());
        setPrice(((ActOfWaybillDifferencePosition) distributedObject).getPrice());
        setNds(((ActOfWaybillDifferencePosition) distributedObject).getNds());
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
    private ActOfWaybillDifference actOfWaybillDifference;
    private String guidOfAWD;

    public String getGuidOfAWD() {
        return guidOfAWD;
    }

    public void setGuidOfAWD(String guidOfAWD) {
        this.guidOfAWD = guidOfAWD;
    }

    public ActOfWaybillDifference getActOfWaybillDifference() {
        return actOfWaybillDifference;
    }

    public void setActOfWaybillDifference(ActOfWaybillDifference actOfWaybillDifference) {
        this.actOfWaybillDifference = actOfWaybillDifference;
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
