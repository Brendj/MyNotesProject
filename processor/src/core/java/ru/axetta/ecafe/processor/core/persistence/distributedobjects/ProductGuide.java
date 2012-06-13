/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.User;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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
    public Element toElement(Document document) {
        Element element = null;
        if(this.getVersion()==0){
            element = document.createElement("C");
        } else {
            element = document.createElement("M");
        }
        element.setAttribute("GID", Long.toString(this.getGlobalId()));
        return element;
    }

    @Override
    public ProductGuide parseXML(Node node) {
        ProductGuide productGuide = new ProductGuide();
        NamedNodeMap namedNodeMap = node.getAttributes();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public long getVersion() {
        return getGlobalVersion();
    }

    public void setVersion(long version) {
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

    public boolean isDeleted() {
        return super.isStatus();
    }

    public void setDeleted(boolean deleted) {
        super.setStatus(deleted);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
