/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn;

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
public class CategoryDiscountDSZNCreatePage extends BasicWorkspacePage implements CategorySelectPage.CompleteHandler {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public String getPageFilename() {
        return "option/categorydiscountdszn/create";
    }

    private Integer code;
    private String description = "";
    private CategoryDiscount categoryDiscount;
    private String categoryName;

    @Override
    public void onShow() throws Exception {

    }

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
    public Object onSave(){
        if (categoryDiscount != null && (categoryDiscount.getIdOfCategoryDiscount() < 0
                || categoryDiscount.getCategoryType().equals(CategoryDiscountEnumType.FEE_CATEGORY))) {
            printError("Неверная категория льготы ИСПП");
            return null;
        }
        if(StringUtils.isEmpty(description)) {
            printError("Добавьте описание льготы ДСЗН");
            return null;
        }
        if(code == null || code < 0) {
            printError("Неверный код ДСЗН");
            return null;
        }
        try {
            Long nextVersion = DAOUtils.nextVersionByCategoryDiscountDSZN(entityManager);
            CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils.findCategoryDiscountDSZNByCode(entityManager, code);
            if(categoryDiscountDSZN != null) {
                if(!categoryDiscountDSZN.getDeleted()) {
                    printError("Категория льготы ДСЗН с таким кодом уже существует");
                    return null;
                }
                categoryDiscountDSZN.setDeleted(false);
                categoryDiscountDSZN.setDescription(description);
                categoryDiscountDSZN.setCategoryDiscount(categoryDiscount);
                categoryDiscountDSZN.setVersion(nextVersion);
            } else {
                categoryDiscountDSZN = new CategoryDiscountDSZN(code, description, categoryDiscount, nextVersion);
            }
            entityManager.persist(categoryDiscountDSZN);
            code = null;
            description = "";
            categoryName = "";
            categoryDiscount = null;
            printMessage("Категория успешно создана");
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при создании категории", e);
        }
        return null;
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
}
