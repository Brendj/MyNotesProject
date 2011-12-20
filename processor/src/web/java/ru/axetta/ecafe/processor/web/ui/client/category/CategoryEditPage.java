/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class CategoryEditPage extends BasicWorkspacePage {
    public String getPageFilename() {
        return "client/category/edit";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String description;
    private Date createdDate;
    private Date lastUpdate;

    public long getIdOfCategoryDiscount() {
        return idOfCategoryDiscount;
    }

    public void setIdOfCategoryDiscount(long idOfCategoryDiscount) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void fill(Session session, Long idOfCategoryDiscount) throws Exception {
        CategoryDiscount categoryDiscount = (CategoryDiscount) session.load(CategoryDiscount.class, idOfCategoryDiscount);
        fill(categoryDiscount);
    }

    public void updateCategory(Session persistenceSession, Long idOfCategory) throws Exception {
        CategoryDiscount categoryDiscount = (CategoryDiscount) persistenceSession.load(CategoryDiscount.class, idOfCategory);
        categoryDiscount.setIdOfCategoryDiscount(idOfCategoryDiscount);
        categoryDiscount.setCategoryName(categoryName);
        categoryDiscount.setDescription(description);
        categoryDiscount.setCreatedDate(createdDate);
        categoryDiscount.setLastUpdate(lastUpdate);
        persistenceSession.update(categoryDiscount);
        fill(categoryDiscount);
    }

    private void fill(CategoryDiscount categoryDiscount) throws Exception {
        this.idOfCategoryDiscount = categoryDiscount.getIdOfCategoryDiscount();
        this.categoryName = categoryDiscount.getCategoryName();
        this.description = categoryDiscount.getDescription();
        this.createdDate = categoryDiscount.getCreatedDate();
        this.lastUpdate = categoryDiscount.getLastUpdate();
    }
}