/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException;
import ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectsEnum;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * Создает  одного из потомков элемента <Pr>  в секции <RO> в выходном xml документе по объекту this.
     * Атрибуты данного элемента приравниваются соответствующим полям объекта this.
     * @param element  выходной xml документ, создаваемый сервлетом SyncServlet при синхронизации
     */
    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"FullName", fullName);
        setAttribute(element,"ProductName", productName);
        setAttribute(element,"Code", code);
        setAttribute(element,"OkpCode", okpCode);
        setAttribute(element,"OrgOwner", orgOwner);
        setAttribute(element,"Density", density);
        setAttribute(element,"ClassificationCode", classificationCode);
        setAttribute(element,"GuidOfPG", productGroup.getGuid());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DistributedObject> getDistributedObjectChildren(HashMap<String, Long> currentMaxVersions) throws Exception {
        List<DistributedObject> list = new ArrayList<DistributedObject>(0);
        list.addAll(DAOService.getInstance().getDistributedObjectsWithOutVersionStatus(ProductGroup.class, currentMaxVersions.get(
                DistributedObjectsEnum.ProductGroup.name()), orgOwner));
        List<DistributedObject> temp = new ArrayList<DistributedObject>(0);
        for (DistributedObject distributedObject: list){
            temp.addAll(distributedObject.getDistributedObjectChildren(currentMaxVersions));
        }
        list.addAll(temp);
        return list;
    }

    @Override
    protected Product parseAttributes(Node node) {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringCode = getStringAttributeValue(node,"Code",16);
        if(stringCode!=null) setCode(stringCode);
        String stringFullName= getStringAttributeValue(node,"FullName",1024);
        if(stringFullName!=null) setFullName(stringFullName);
        String stringOkpCode= getStringAttributeValue(node,"OkpCode",128);
        if(stringOkpCode!=null) setOkpCode(stringOkpCode);
        String stringProductName= getStringAttributeValue(node,"ProductName",512);
        if(stringProductName!=null) setProductName(stringProductName);
        String stringClassificationCode = getStringAttributeValue(node,"ClassificationCode",32);
        if(stringClassificationCode!=null) setClassificationCode(stringClassificationCode);
        Float floatDensity = getFloatAttributeValue(node,"Density");
        if(floatDensity!=null) setDensity(floatDensity);
        guidOfPG = getStringAttributeValue(node,"GuidOfPG",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((StateChange) distributedObject).getOrgOwner());
        setCode( ((Product) distributedObject).getCode());
        setFullName (((Product) distributedObject).getFullName());
        setProductName( ((Product) distributedObject).getProductName());
        setOkpCode (((Product) distributedObject).getOkpCode());
        setIdOfConfigurationProvider(((Product) distributedObject).getIdOfConfigurationProvider());
        setClassificationCode(((Product) distributedObject).getClassificationCode());
        setDensity(((Product) distributedObject).getDensity());
    }

    @Override
    public void preProcess() throws DistributedObjectException {
        ProductGroup pg = DAOService.getInstance().findDistributedObjectByRefGUID(ProductGroup.class,guidOfPG);
        if(pg==null) throw new DistributedObjectException(DistributedObjectException.ErrorType.NOT_FOUND_VALUE);
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
