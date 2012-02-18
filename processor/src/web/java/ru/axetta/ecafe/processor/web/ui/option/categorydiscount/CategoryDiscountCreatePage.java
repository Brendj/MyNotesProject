/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleListSelectPage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Component
public class CategoryDiscountCreatePage extends BasicWorkspacePage {
    @PersistenceContext
    EntityManager entityManager;

    public String getPageFilename() {
        return "option/categorydiscount/create";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String description;
    //private Set<DiscountRule> discountRuleSet;

    /*public Set<DiscountRule> getDiscountRuleSet() {
        return discountRuleSet;
    }

    public void setDiscountRuleSet(Set<DiscountRule> discountRuleSet) {
        this.discountRuleSet = discountRuleSet;
    }  */

    @PersistenceContext
    EntityManager em;

    @Override
    public void onShow() throws Exception {
        idOfCategoryDiscount = DAOUtils.getCategoryDiscountMaxId(em)+1;
        if (idOfCategoryDiscount<1) idOfCategoryDiscount=1;
        categoryName = "";
        description ="";
    }

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

    @Transactional
    public void createCategory() {
        if (idOfCategoryDiscount<0) {
            printError("Идентификатор должен быть больше 0");
            return;
        }
        try {
            Date createdDate = new Date();
            CategoryDiscount categoryDiscount = new CategoryDiscount(idOfCategoryDiscount, categoryName, "", description,
                createdDate, createdDate);

            entityManager.persist(categoryDiscount);
            printMessage("Категория успешно создана");
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при создании категории", e);
        }
    }
}
