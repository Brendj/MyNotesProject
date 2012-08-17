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

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class InternalDisposingDocumentPosition extends DistributedObject {

    @Override
    public void preProcess() throws DistributedObjectException {
        InternalDisposingDocument idd = DAOService.getInstance().findDistributedObjectByRefGUID(InternalDisposingDocument.class,guidOfIDD);
        if(idd==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setInternalDisposingDocument(idd);
        TradeMaterialGood tmg = DAOService.getInstance().findDistributedObjectByRefGUID(TradeMaterialGood.class,guidOfTMG);
        if(tmg==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
        setTradeMaterialGood(tmg);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "UnitsScale", unitsScale);
        setAttribute(element, "TotalCount", totalCount);
        setAttribute(element, "NetWeight", netWeight);
        setAttribute(element, "DisposePrice", disposePrice);
        setAttribute(element, "NDS", nds);
        setAttribute(element, "GuidOfInternalDisposingDocument", internalDisposingDocument.getGuid());
        setAttribute(element, "GuidOfTradeMaterialGoods", tradeMaterialGood.getGuid());
    }

    @Override
    protected InternalDisposingDocumentPosition parseAttributes(Node node) throws ParseException, IOException {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerUnitsScale = getIntegerAttributeValue(node,"UnitsScale");
        if(integerUnitsScale!=null) setUnitsScale(integerUnitsScale);
        Long longTotalCount = getLongAttributeValue(node,"TotalCount");
        if(longTotalCount!=null) setTotalCount(longTotalCount);
        Long longNetWeight = getLongAttributeValue(node,"NetWeight");
        if(longNetWeight!=null) setNetWeight(longNetWeight);
        Long longDisposePrice = getLongAttributeValue(node,"DisposePrice");
        if(longDisposePrice!=null) setDisposePrice(longDisposePrice);
        Long longNDS = getLongAttributeValue(node,"NDS");
        if(longNDS!=null) setNds(longNDS);
        guidOfIDD = getStringAttributeValue(node,"GuidOfInternalDisposingDocument",36);
        guidOfTMG = getStringAttributeValue(node,"GuidOfTradeMaterialGoods",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((InternalDisposingDocumentPosition) distributedObject).getOrgOwner());
        setUnitsScale(((InternalDisposingDocumentPosition) distributedObject).getUnitsScale());
        setTotalCount(((InternalDisposingDocumentPosition) distributedObject).getTotalCount());
        setNetWeight(((InternalDisposingDocumentPosition) distributedObject).getNetWeight());
        setDisposePrice(((InternalDisposingDocumentPosition) distributedObject).getDisposePrice());
        setNds(((InternalDisposingDocumentPosition) distributedObject).getNds());
    }

    private Integer unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long disposePrice;
    private Long nds;
    private InternalDisposingDocument internalDisposingDocument;
    private String guidOfIDD;
    private TradeMaterialGood tradeMaterialGood;
    private String guidOfTMG;

    public String getGuidOfIDD() {
        return guidOfIDD;
    }

    public void setGuidOfIDD(String guidOfIDD) {
        this.guidOfIDD = guidOfIDD;
    }

    public String getGuidOfTMG() {
        return guidOfTMG;
    }

    public void setGuidOfTMG(String guidOfTMG) {
        this.guidOfTMG = guidOfTMG;
    }

    public TradeMaterialGood getTradeMaterialGood() {
        return tradeMaterialGood;
    }

    public void setTradeMaterialGood(TradeMaterialGood tradeMaterialGood) {
        this.tradeMaterialGood = tradeMaterialGood;
    }

    public InternalDisposingDocument getInternalDisposingDocument() {
        return internalDisposingDocument;
    }

    public void setInternalDisposingDocument(InternalDisposingDocument internalDisposingDocument) {
        this.internalDisposingDocument = internalDisposingDocument;
    }

    public Long getNds() {
        return nds;
    }

    public void setNds(Long nds) {
        this.nds = nds;
    }

    public Long getDisposePrice() {
        return disposePrice;
    }

    public void setDisposePrice(Long disposePrice) {
        this.disposePrice = disposePrice;
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
}
