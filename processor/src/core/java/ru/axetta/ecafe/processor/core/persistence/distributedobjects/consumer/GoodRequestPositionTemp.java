/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ConsumerRequestDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.08.12
 * Time: 11:00
 * Позиции требований
 */
public class GoodRequestPositionTemp extends ConsumerRequestDistributedObject {

    private UnitScale unitsScale;
    private Long totalCount;
    private Long dailySampleCount; // суточная проба
    /* старые значения всего и суточной пробы */
    private Long lastTotalCount;
    private Long lastDailySampleCount; // суточная проба
    private Long lastTempClientsCount;
    private Long tempClientsCount; //временные клиенты
    private Long netWeight;
    private Product product;
    private String guidOfP;
    private GoodRequestTemp goodRequest;
    private String guidOfGR;
    private Good good;
    private String guidOfG;
    private Boolean notified;
    private InformationContents informationContent = InformationContents.ONLY_CURRENT_ORG;


    @Override
    public void createProjections(Criteria criteria) {

    }

    @Override
    protected void appendAttributes(Element element) {

    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {

    }

    @Override
    protected GoodRequestPositionTemp parseAttributes(Node node) throws Exception {
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {

    }

    @Override
    public void setNewInformationContent(InformationContents informationContent) {
        this.informationContent = informationContent;
    }

    public String getGuidOfP() {
        return guidOfP;
    }

    public void setGuidOfP(String guidOfP) {
        this.guidOfP = guidOfP;
    }

    public String getGuidOfGR() {
        return guidOfGR;
    }

    public void setGuidOfGR(String guidOfGR) {
        this.guidOfGR = guidOfGR;
    }

    public String getGuidOfG() {
        return guidOfG;
    }

    public void setGuidOfG(String guidOfG) {
        this.guidOfG = guidOfG;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public GoodRequestTemp getGoodRequest() {
        return goodRequest;
    }

    public void setGoodRequest(GoodRequestTemp goodRequest) {
        this.goodRequest = goodRequest;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public Long getDailySampleCount() {
        return dailySampleCount;
    }

    public void setDailySampleCount(Long dailySampleCount) {
        this.dailySampleCount = dailySampleCount;
    }

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Boolean getFloatScale() {
        return unitsScale.equals(UnitScale.UNITS) || unitsScale.equals(UnitScale.PORTIONS);
    }

    public String getCurrentElementValue() {
        if (product != null) {
            return product.getProductName();
        } else {
            return good.getNameOfGood();
        }
    }

    public Long getCurrentElementId() {
        if (product != null) {
            return product.getGlobalId();
        } else {
            return good.getGlobalId();
        }
    }

    public Long getLastTotalCount() {
        return lastTotalCount;
    }

    public void setLastTotalCount(Long lastTotalCount) {
        this.lastTotalCount = lastTotalCount;
    }

    public Long getLastDailySampleCount() {
        return lastDailySampleCount;
    }

    public void setLastDailySampleCount(Long lastDailySampleCount) {
        this.lastDailySampleCount = lastDailySampleCount;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public Long getTempClientsCount() {
        return tempClientsCount;
    }

    public void setTempClientsCount(Long tempClientsCount) {
        this.tempClientsCount = tempClientsCount;
    }

    public Long getLastTempClientsCount() {
        return lastTempClientsCount;
    }

    public void setLastTempClientsCount(Long lastTempClientsCount) {
        this.lastTempClientsCount = lastTempClientsCount;
    }
}
