/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.IConfigProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 09.05.12
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */

public class Product extends DistributedObject implements IConfigProvider {

    private String code;
    private String fullName;
    private String productName;
    private String okpCode;
    private User userCreate;
    private User userEdit;
    private User userDelete;
    private Long idOfConfigurationProvider;
    private ProductGroup productGroup;
    private String guidOfPG;
    private String classificationCode;
    private Float density;
    private Set<Good> goodInternal;
    private Set<TechnologicalMapProduct> technologicalMapProductInternal;
    private Set<Prohibition> prohibitionInternal;
    private Set<GoodRequestPosition> goodRequestPositionInternal;
    private String productGuid;

    public String getProductGuid() {
        return productGuid;
    }

    public void setProductGuid(String productGuid) {
        this.productGuid = productGuid;
    }

    public Set<GoodRequestPosition> getGoodRequestPositionInternal() {
        return goodRequestPositionInternal;
    }

    public void setGoodRequestPositionInternal(Set<GoodRequestPosition> goodRequestPositionInternal) {
        this.goodRequestPositionInternal = goodRequestPositionInternal;
    }

    public Set<Prohibition> getProhibitionInternal() {
        return prohibitionInternal;
    }

    public void setProhibitionInternal(Set<Prohibition> prohibitionInternal) {
        this.prohibitionInternal = prohibitionInternal;
    }

    public Set<TechnologicalMapProduct> getTechnologicalMapProductInternal() {
        return technologicalMapProductInternal;
    }

    public void setTechnologicalMapProductInternal(Set<TechnologicalMapProduct> technologicalMapProductInternal) {
        this.technologicalMapProductInternal = technologicalMapProductInternal;
    }

    public Set<Good> getGoodInternal() {
        return goodInternal;
    }

    public void setGoodInternal(Set<Good> goodInternal) {
        this.goodInternal = goodInternal;
    }

    /**
     * Создает  одного из потомков элемента <Pr>  в секции <RO> в выходном xml документе по объекту this.
     * Атрибуты данного элемента приравниваются соответствующим полям объекта this.
     * @param element  выходной xml документ, создаваемый сервлетом SyncServlet при синхронизации
     */
    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "FullName", fullName);
        XMLUtils.setAttributeIfNotNull(element, "ProductName", productName);
        XMLUtils.setAttributeIfNotNull(element, "Code", code);
        XMLUtils.setAttributeIfNotNull(element, "OkpCode", okpCode);
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "Density", density);
        XMLUtils.setAttributeIfNotNull(element, "ClassificationCode", classificationCode);
        XMLUtils.setAttributeIfNotNull(element, "GuidOfPG", productGroup.getGuid());
        //setAttribute(element,"GuidOfPG", productGuid);
    }

    @Override
    protected Product parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        String stringCode = XMLUtils.getStringAttributeValue(node, "Code", 16);
        if (stringCode != null) {
            setCode(stringCode);
        } else {
            throw new DistributedObjectException("Code column is not null");
        }
        String stringFullName = XMLUtils.getStringAttributeValue(node, "FullName", 1024);
        if (stringFullName != null)
            setFullName(stringFullName);
        String stringOkpCode = XMLUtils.getStringAttributeValue(node, "OkpCode", 128);
        if (stringOkpCode != null)
            setOkpCode(stringOkpCode);
        String stringProductName = XMLUtils.getStringAttributeValue(node, "ProductName", 512);
        if (stringProductName != null) {
            setProductName(stringProductName);
        } else {
            throw new DistributedObjectException("ProductName column is not null");
        }
        String stringClassificationCode = XMLUtils.getStringAttributeValue(node, "ClassificationCode", 32);
        if (stringClassificationCode != null)
            setClassificationCode(stringClassificationCode);
        Float floatDensity = XMLUtils.getFloatAttributeValue(node, "Density");
        if (floatDensity != null)
            setDensity(floatDensity);
        guidOfPG = XMLUtils.getStringAttributeValue(node, "GuidOfPG", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setCode( ((Product) distributedObject).getCode());
        setFullName (((Product) distributedObject).getFullName());
        setProductName( ((Product) distributedObject).getProductName());
        setOkpCode (((Product) distributedObject).getOkpCode());
        setIdOfConfigurationProvider(((Product) distributedObject).getIdOfConfigurationProvider());
        setClassificationCode(((Product) distributedObject).getClassificationCode());
        setDensity(((Product) distributedObject).getDensity());
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        ProductGroup pg = DAOUtils.findDistributedObjectByRefGUID(ProductGroup.class, session, guidOfPG);
        if(pg==null) throw new DistributedObjectException("NOT_FOUND_VALUE");
        setProductGroup(pg);
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOkpCode() {
        return okpCode;
    }

    public void setOkpCode(String okpCode) {
        this.okpCode = okpCode;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductGroup getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(ProductGroup productGroup) {
        this.productGroup = productGroup;
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public Float getDensity() {
        return density;
    }

    public void setDensity(Float density) {
        this.density = density;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Product");
        sb.append("{code='").append(code).append('\'');
        sb.append(", fullName='").append(fullName).append('\'');
        sb.append(", productName='").append(productName).append('\'');
        sb.append(", okpCode='").append(okpCode).append('\'');
        sb.append(", idOfConfigurationProvider=").append(idOfConfigurationProvider);
        sb.append(", guidOfPG='").append(guidOfPG).append('\'');
        sb.append(", classificationCode='").append(classificationCode).append('\'');
        sb.append(", density=").append(density);
        sb.append('}');
        return sb.toString();
    }
}
