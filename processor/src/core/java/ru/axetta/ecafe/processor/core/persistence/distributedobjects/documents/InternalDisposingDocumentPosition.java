/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TradeMaterialGood;
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
public class InternalDisposingDocumentPosition extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        InternalDisposingDocument idd  = (InternalDisposingDocument) DAOUtils.findDistributedObjectByRefGUID(session, guidOfIDD);
        if(idd==null) throw new DistributedObjectException("NOT_FOUND_InternalDisposingDocument");
        setInternalDisposingDocument(idd);
        TradeMaterialGood tmg  = (TradeMaterialGood) DAOUtils.findDistributedObjectByRefGUID(session, guidOfTMG);
        if(tmg!=null) setTradeMaterialGood(tmg);

        Good g  = (Good) DAOUtils.findDistributedObjectByRefGUID(session, guidOfGood);
        if(g==null)  throw new DistributedObjectException("NOT_FOUND_Good");
        setGood(g);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "UnitsScale", unitsScale.ordinal());
        setAttribute(element, "TotalCount", totalCount);
        setAttribute(element, "NetWeight", netWeight);
        setAttribute(element, "DisposePrice", disposePrice);
        setAttribute(element, "NDS", nds);
        setAttribute(element, "GuidOfInternalDisposingDocument", internalDisposingDocument.getGuid());
        if(tradeMaterialGood!=null) setAttribute(element, "GuidOfTradeMaterialGoods", tradeMaterialGood.getGuid());
        setAttribute(element, "GuidOfGoods", good.getGuid());
    }

    @Override
    protected InternalDisposingDocumentPosition parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Integer integerUnitsScale = getIntegerAttributeValue(node,"UnitsScale");
        if(integerUnitsScale!=null) setUnitsScale(UnitScale.fromInteger(integerUnitsScale));
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
        guidOfGood = getStringAttributeValue(node,"GuidOfGoods",36);
        setSendAll(SendToAssociatedOrgs.SendToMain);
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

    private UnitScale unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long disposePrice;
    private Long nds;
    private InternalDisposingDocument internalDisposingDocument;
    private String guidOfIDD;
    private TradeMaterialGood tradeMaterialGood;
    private String guidOfTMG;
    private String guidOfGood;
    private Good good;

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public String getGuidOfGood() {
        return guidOfGood;
    }

    public void setGuidOfGood(String guidOfGood) {
        this.guidOfGood = guidOfGood;
    }

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

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }
}
