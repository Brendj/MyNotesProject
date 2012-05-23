/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.productGuide;

import ru.axetta.ecafe.processor.core.persistence.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 13.05.12
 * Time: 9:51
 * To change this template use File | Settings | File Templates.
 */
public class Item {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static Long NOT_SAVED_IN_DB_ID = -1L;

    private Long idOfProductGuide;
    private String code;
    private String fullName;
    private String productName;
    private String okpCode;
    private User userCreate;
    private User userEdit;
    private User userDelete;
    private Date createTime;
    private Date editTime;
    private Date deleteTime;
    private boolean deleted;
    private Long idofconfigurationprovider;
    private boolean edited = false;

    public Item() {}

    public Item(Long idOfProductGuide, String code, String fullName, String productName, String okpCode,
            User userCreate, User userEdit, User userDelete, Date createTime, Date editTime,
            Date deleteTime, boolean deleted, Long idofconfigurationprovider) {
        this.idOfProductGuide = idOfProductGuide;
        this.code = code;
        this.fullName = fullName;
        this.productName = productName;
        this.okpCode = okpCode;
        this.userCreate = userCreate;
        this.userEdit = userEdit;
        this.userDelete = userDelete;
        this.createTime = createTime;
        this.editTime = editTime;
        this.deleteTime = deleteTime;
        this.deleted = deleted;
        this.idofconfigurationprovider = idofconfigurationprovider;
    }


    public Long getIdOfProductGuide() {
        return idOfProductGuide;
    }

    public void setIdOfProductGuide(Long idOfProductGuide) {
        this.idOfProductGuide = idOfProductGuide;
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
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getIdofconfigurationprovider() {
        return idofconfigurationprovider;
    }

    public void setIdofconfigurationprovider(Long idofconfigurationprovider) {
        this.idofconfigurationprovider = idofconfigurationprovider;
    }

    public String getGetAdditionInfo() {
        if (code == null || code.equals("") || idOfProductGuide==Item.NOT_SAVED_IN_DB_ID)
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<span  style=\"white-space:nowrap\">");
        sb.append("Создан: ").append(this.userCreate.getUserName()).append("<br/>")
                .append("Дата создания: ").append(dateFormat.format(this.createTime));
        if (this.userEdit!=null)
            sb.append("<br/>").append("Изменен: ").append(this.userEdit.getUserName())
                    .append("<br/>").append("Дата изменения: ").append(dateFormat.format(this.editTime));
        if (this.userDelete!=null)
            sb.append("<br/>").append('\n').append("Удален: ").append(this.userDelete.getUserName())
                    .append("<br/>").append("Дата удаления: ").append(dateFormat.format(this.deleteTime));
        sb.append("</span>");
        return sb.toString();
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
