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

    @Override
    public Element toElement(Document document, String action) {
        Element element =  document.createElement(action);
        element.setAttribute("GID", Long.toString(this.getGlobalId()));
        element.setAttribute("V", Long.toString(this.getGlobalVersion()));
        if(getLocalID()!=null){
            element.setAttribute("LID", Long.toString(this.getLocalID()));
        }
        if(isStatus() && !action.equals("O")){
            element.setAttribute("D", isStatus()?"1":"0");
        } else {
            element.setAttribute("Code".toUpperCase(), this.getCode());
            element.setAttribute("FullName".toUpperCase(), this.getFullName());
            element.setAttribute("OkpCode".toUpperCase(), this.getOkpCode());
            if(getIdofconfigurationprovider()!=null) element.setAttribute("IdOfConfigurationProvider".toUpperCase(), Long.toString(this.getIdofconfigurationprovider()));
        }
        return element;
    }

    @Override
    public String parseXML(Node node) {
        String result ="";
        String stringVersion = getAttributeValue(node,"V");
        if(stringVersion!=null) {
            this.setVersion(Long.valueOf(stringVersion));
            result = "M";
        } else {
            this.setVersion(Long.valueOf("0"));
            result = "C";
        }
        //setVersion(Long.parseLong(stringVersion));
        String stringGid = getAttributeValue(node,"GID");
        if(stringGid!=null) {
            this.setGlobalId(Long.valueOf(stringGid));
        }
        String stringCode = getAttributeValue(node,"Code");
        if(stringCode!=null) setCode(stringCode);
        String stringFullName= getAttributeValue(node,"FullName");
        if(stringFullName!=null) setFullName(stringFullName);
        String stringOkpCode= getAttributeValue(node,"OkpCode");
        if(stringOkpCode!=null) setOkpCode(stringOkpCode);
        String stringLocalId= getAttributeValue(node,"LID");
        if(stringLocalId!=null) setLocalID(Long.parseLong(stringLocalId));
        String stringStatus= getAttributeValue(node,"D");
        setStatus(stringLocalId!=null);
        String stringIdOfConfigurationProvider= getAttributeValue(node,"IdOfConfigurationProvider");
        if(stringIdOfConfigurationProvider!=null) setIdofconfigurationprovider(Long.parseLong(stringIdOfConfigurationProvider));
        return result;
        //return false;  //To change body of implemented methods use File | Settings | File Templates.
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
    private Long idofconfigurationprovider;
    public Long getIdofconfigurationprovider() {
        return idofconfigurationprovider;
    }

    public void setIdofconfigurationprovider(Long idofconfigurationprovider) {
        this.idofconfigurationprovider = idofconfigurationprovider;
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

    public Long getVersion() {
        return getGlobalVersion();
    }
    public void setVersion(Long version) {
        setGlobalVersion(version);
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
                ", idofconfigurationprovider=" + idofconfigurationprovider +
                '}';
    }
}
