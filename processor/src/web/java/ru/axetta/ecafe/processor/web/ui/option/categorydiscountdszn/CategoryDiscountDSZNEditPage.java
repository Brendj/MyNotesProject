/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountDSZN;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategorySelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Scope("session")
public class CategoryDiscountDSZNEditPage extends BasicWorkspacePage implements CategorySelectPage.CompleteHandler {
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public String getPageFilename() {
        return "option/categorydiscountdszn/edit";
    }

    private int idOfCategoryDiscountDSZN;
    private Integer code;
    private String description;
    private CategoryDiscount categoryDiscount;
    private String categoryName;
    private Long ETPCode;

    @Override
    public void completeCategorySelection(Session session, Long idOfCategory) throws Exception {
        if(null != idOfCategory) {
            CategoryDiscount categoryDiscount = DAOUtils.findCategoryDiscountById(entityManager, idOfCategory);
            this.categoryDiscount = categoryDiscount;
            this.categoryName = categoryDiscount.getCategoryName();
        } else {
            this.categoryDiscount = null;
            this.categoryName = null;
        }
    }

    @Transactional
    public Object save() {
        if (categoryDiscount != null && (categoryDiscount.getIdOfCategoryDiscount() < 0
                || !categoryDiscount.getCategoryType().equals(CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT))) {
            printError("Неверная категория льготы ИСПП");
            return null;
        }
        if(StringUtils.isEmpty(description)) {
            printError("Добавьте описание льготы ДСЗН");
            return null;
        }
        Long nextVersion = DAOUtils.nextVersionByCategoryDiscountDSZN(entityManager);
        CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils.findCategoryDiscountDSZNById(entityManager, idOfCategoryDiscountDSZN);
        categoryDiscountDSZN.setDescription(description);
        categoryDiscountDSZN.setCategoryDiscount(categoryDiscount);
        categoryDiscountDSZN.setVersion(nextVersion);
        categoryDiscountDSZN.setETPCode(ETPCode);
        entityManager.persist(categoryDiscountDSZN);
        printMessage("Данные обновлены.");
        return null;
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    @Transactional
    public void reload() {
        CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils.findCategoryDiscountDSZNById(entityManager, idOfCategoryDiscountDSZN);
        this.code = categoryDiscountDSZN.getCode();
        this.description = categoryDiscountDSZN.getDescription();
        this.categoryDiscount = categoryDiscountDSZN.getCategoryDiscount();
        if(this.categoryDiscount != null) {
            this.categoryName = categoryDiscountDSZN.getCategoryDiscount().getCategoryName();
        } else {
            categoryName = "";
        }
        this.ETPCode = categoryDiscountDSZN.getETPCode();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public int getIdOfCategoryDiscountDSZN() {
        return idOfCategoryDiscountDSZN;
    }

    public void setIdOfCategoryDiscountDSZN(int idOfCategoryDiscountDSZN) {
        this.idOfCategoryDiscountDSZN = idOfCategoryDiscountDSZN;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryDiscount getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(CategoryDiscount categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getEntityName() {
        return "Льгота ДСЗН " + code;
    }

    public Long getETPCode() {
        return ETPCode;
    }

    public void setETPCode(Long ETPCode) {
        this.ETPCode = ETPCode;
    }
}