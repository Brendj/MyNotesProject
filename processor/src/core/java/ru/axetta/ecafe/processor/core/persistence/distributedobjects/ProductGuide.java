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
        element.setAttribute("FullName", this.getFullName());
        element.setAttribute("ProductName", this.getProductName());
        element.setAttribute("Code", this.getCode());
        element.setAttribute("OkpCode", this.getOkpCode());
        if(getIdOfConfigurationProvider()!=null) element.setAttribute("IdOfConfigurationProvider", Long.toString(this.getIdOfConfigurationProvider()));
    }

    @Override
    public ProductGuide build(Node node) {
        String stringGid = getAttributeValue(node,"GID");
        if(stringGid!=null) {
            setIdOfProductGuide(Long.valueOf(stringGid));
        }
        String stringVersion = getAttributeValue(node,"V");
        if(stringVersion!=null) {
            setGlobalVersion(Long.valueOf(stringVersion));
        }
        String stringCode = getAttributeValue(node,"Code");
        if(stringCode!=null) setCode(stringCode);
        String stringFullName= getAttributeValue(node,"FullName");
        if(stringFullName!=null) setFullName(stringFullName);
        String stringOkpCode= getAttributeValue(node,"OkpCode");
        if(stringOkpCode!=null) setOkpCode(stringOkpCode);
        String stringLocalId= getAttributeValue(node,"LID");
        if(stringLocalId!=null) setLocalID(Long.parseLong(stringLocalId));
        String stringProductName= getAttributeValue(node,"ProductName");
        if(stringProductName!=null) setProductName(stringProductName);
        String stringStatus= getAttributeValue(node,"D");
        setStatus(stringStatus!=null);
        String stringIdOfConfigurationProvider= getAttributeValue(node,"IdOfConfigurationProvider");
        if(stringIdOfConfigurationProvider!=null) setIdOfConfigurationProvider(Long.parseLong(stringIdOfConfigurationProvider));
        return this;
    }

    @Override
    public String getNodeName() {
        return "Pr";
    }

    //private Long idOfProductGuide;
    private String code;

    private String fullName;
    private String productName;
    private String okpCode;
    // private long version;
    private User userCreate;
    private User userEdit;
    private User userDelete;
    /*private Date createTime;
    private Date editTime;
    private Date deleteTime;
    private boolean deleted;*/
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
