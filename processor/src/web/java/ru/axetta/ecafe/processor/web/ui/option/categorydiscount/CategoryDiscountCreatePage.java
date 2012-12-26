/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.discountrule.RuleListSelectPage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Component
@Scope("session")
public class CategoryDiscountCreatePage extends BasicWorkspacePage {


    @PersistenceContext
    EntityManager entityManager;

    public String getPageFilename() {
        return "option/categorydiscount/create";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String description;
    private Integer categoryType;
    private final CategoryDiscountEnumTypeMenu categoryDiscountEnumTypeMenu = new CategoryDiscountEnumTypeMenu();

    public CategoryDiscountEnumTypeMenu getCategoryDiscountEnumTypeMenu() {
        return categoryDiscountEnumTypeMenu;
    }

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    //private Set<DiscountRule> discountRuleSet;

    /*public Set<DiscountRule> getDiscountRuleSet() {
        return discountRuleSet;
    }

    public void setDiscountRuleSet(Set<DiscountRule> discountRuleSet) {
        this.discountRuleSet = discountRuleSet;
    }  */

    @Override
    public void onShow() throws Exception {
        idOfCategoryDiscount = DAOUtils.getCategoryDiscountMaxId(entityManager)+1;
        if (idOfCategoryDiscount<1) idOfCategoryDiscount=1;
        categoryName = "";
        description ="";
        categoryType = CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT.getValue();
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

    public Object onSave(){
        createCategory();
        return null;
    }

    protected void createCategory() {
        idOfCategoryDiscount = DAOUtils.getCategoryDiscountMaxId(entityManager)+1;
        if (idOfCategoryDiscount<1) idOfCategoryDiscount=1;
        if (idOfCategoryDiscount<0) {
            printError("Идентификатор должен быть больше 0");
            return;
        }
        /*List<Long> ids = new LinkedList<Long>();
        ids.add(idOfCategoryDiscount);
        int count = DAOUtils.getCategoryDiscountListWithIds(entityManager,ids).size();
        if(count>0){
            printError("Идентификатор "+idOfCategoryDiscount+" зарегстрирован");
            return;
        } */
        try {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            Date createdDate = new Date();
            CategoryDiscount categoryDiscount = new CategoryDiscount(idOfCategoryDiscount, categoryName, "", description,
                    createdDate, createdDate);
            categoryDiscount.setCategoryType(CategoryDiscountEnumType.fromInteger(categoryType));
            entityManager.persist(categoryDiscount);
            transactionManager.commit(status);
            categoryName = "";
            description = "";
            printMessage("Категория успешно создана");
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при создании категории", e);
        }
    }

    @Autowired
    private PlatformTransactionManager transactionManager;
}
