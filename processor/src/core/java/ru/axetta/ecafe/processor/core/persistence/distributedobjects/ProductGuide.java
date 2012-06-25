/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.User;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 09.05.12
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */

public class ProductGuide extends DistributedObject {

    /**
     * Создает  одного из потомков элемента <Pr>  в секции <RO> в выходном xml документе по объекту this. Имя потомка - action.
     * Атрибуты данного элемента приравниваются соответствующим полям объекта this.
     * @param element  выходной xml документ, создаваемый сервлетом SyncServlet при синхронизации
     * @return  созданный элемент
     */
    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"FullName", fullName);
        setAttribute(element,"ProductName", productName);
        setAttribute(element,"Code", code);
        setAttribute(element,"OkpCode", okpCode);
        setAttribute(element,"OrgOwner", idOfOrg);
        setAttribute(element,"IdOfConfigurationProvider", idOfConfigurationProvider);
    }

    @Override
    public ProductGuide build(Node node) {
        /* Begin required params */
        Long gid = getLongAttributeValue(node,"GID");
        if(gid!=null) setIdOfProductGuide(gid);

        Long version = getLongAttributeValue(node,"V");
        if(version!=null) setGlobalVersion(version);

        Long lid = getLongAttributeValue(node,"LID");
        if(lid!=null) setLocalID(lid);

        Integer status = getIntegerAttributeValue(node,"D");
        setStatus(status!=null);
        tagName = node.getNodeName();
        /* End required params */
        String stringCode = getStringAttributeValue(node,"Code",16);
        if(stringCode!=null) setCode(stringCode);
        String stringFullName= getStringAttributeValue(node,"FullName",1024);
        if(stringFullName!=null) setFullName(stringFullName);
        String stringOkpCode= getStringAttributeValue(node,"OkpCode",32);
        if(stringOkpCode!=null) setOkpCode(stringOkpCode);
        String stringProductName= getStringAttributeValue(node,"ProductName",512);
        if(stringProductName!=null) setProductName(stringProductName);
        Long idOfConfigurationProvider = getLongAttributeValue(node,"IdOfConfigurationProvider");
        if(idOfConfigurationProvider!=null) setIdOfConfigurationProvider(idOfConfigurationProvider);
        return this;
    }

    private String code;
    private String fullName;
    private String productName;
    private String okpCode;
    private User userCreate;
    private User userEdit;
    private User userDelete;
    private Long idOfConfigurationProvider;

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

    public Long getIdOfProductGuide() {
        return getGlobalId();
    }
    public void setIdOfProductGuide(Long idOfProductGuide) {
        setGlobalId(idOfProductGuide);
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

    public Boolean isDeleted() {
        return super.isStatus();
    }

    public void setDeleted(Boolean deleted) {
        super.setStatus(deleted);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /*
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }*/
    @Override
    public String toString() {
        return "ProductGuide{" +
                "code='" + code + '\'' +
                ", fullName='" + fullName + '\'' +
                ", productName='" + productName + '\'' +
                ", okpCode='" + okpCode + '\'' +
                ", userCreate=" + userCreate +
                ", userEdit=" + userEdit +
                ", userDelete=" + userDelete +
                ", idOfConfigurationProvider=" + idOfConfigurationProvider +
                '}';
    }
}
