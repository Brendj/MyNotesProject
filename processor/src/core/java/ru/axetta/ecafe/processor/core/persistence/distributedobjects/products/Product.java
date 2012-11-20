/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.IConfigProvider;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
    protected Product parseAttributes(Node node) throws Exception{
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringCode = getStringAttributeValue(node,"Code",16);
        if(stringCode!=null) {
            setCode(stringCode);
        } else {
            throw new DistributedObjectException("Code column is not null");
        }
        String stringFullName= getStringAttributeValue(node,"FullName",1024);
        if(stringFullName!=null) setFullName(stringFullName);
        String stringOkpCode= getStringAttributeValue(node,"OkpCode",128);
        if(stringOkpCode!=null) setOkpCode(stringOkpCode);
        String stringProductName= getStringAttributeValue(node,"ProductName",512);
        if(stringProductName!=null) {
            setProductName(stringProductName);
        } else {
            throw new DistributedObjectException("ProductName column is not null");
        }
        String stringClassificationCode = getStringAttributeValue(node,"ClassificationCode",32);
        if(stringClassificationCode!=null) setClassificationCode(stringClassificationCode);
        Float floatDensity = getFloatAttributeValue(node,"Density");
        if(floatDensity!=null) setDensity(floatDensity);
        guidOfPG = getStringAttributeValue(node,"GuidOfPG",36);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((Product) distributedObject).getOrgOwner());
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
        //ProductGroup pg = DAOService.getInstance().findDistributedObjectByRefGUID(ProductGroup.class,guidOfPG);
        ProductGroup pg = (ProductGroup) DAOUtils.findDistributedObjectByRefGUID(session, guidOfPG);
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
