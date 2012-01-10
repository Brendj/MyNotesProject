/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.management.relation.Relation;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class CategoryCreatePage extends BasicWorkspacePage {
    public String getPageFilename() {
        return "client/category/create";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String description;
    private Date createdDate;
    private Date lastUpdate = new Date();

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

    public void fill(Session session) throws Exception {

    }

    public void createCategory(Session session) throws Exception {
        int size=session.createCriteria(CategoryDiscount.class).add(
                Restrictions.eq("idOfCategoryDiscount", idOfCategoryDiscount)
        ).list().size();
        if (size==0){
            CategoryDiscount categoryDiscount = new CategoryDiscount(idOfCategoryDiscount, categoryName, description,
                    createdDate, lastUpdate);
            session.save(categoryDiscount);
        } else throw new Exception("This category is exists.");
    }
}
