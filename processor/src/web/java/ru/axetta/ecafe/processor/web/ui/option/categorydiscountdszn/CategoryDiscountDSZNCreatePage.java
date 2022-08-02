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
import java.util.UUID;

@Component
@Scope("session")
public class CategoryDiscountDSZNCreatePage extends BasicWorkspacePage implements CategorySelectPage.CompleteHandler {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public String getPageFilename() {
        return "option/categorydiscountdszn/create";
    }

    private String code = "";
    private String description = "";
    private CategoryDiscount categoryDiscount;
    private String categoryName;
    private String ETPCode = "";
    private String ETPTextCode = "";
    private Integer priority;

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
        String guid;
        Integer convertCode;
        Long convertETPCode;

        if(StringUtils.isEmpty(code)) {
            printError("Добавьте код льготы ДТиСЗН");
            return null;
        }

        if(StringUtils.isEmpty(ETPCode)) {
            printError("Добавьте код льготы ЕТП");
            return null;
        }

        try {
            convertCode = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            printError("Код льготы ДТиСЗН должен быть числом");
            convertCode = null;
        }

        try {
            convertETPCode = Long.parseLong(ETPCode);
        } catch (NumberFormatException e) {
            printError("Код льготы ЕТП должен быть числом");
            convertETPCode = null;
        }

        if(convertCode == null || convertETPCode == null)
            return null;

        if(convertCode < 0) {
            printError("Неверный код ДТиСЗН");
            return null;
        }

        if (categoryDiscount != null && (categoryDiscount.getIdOfCategoryDiscount() < 0
                || !categoryDiscount.getCategoryType().equals(CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT))) {
            printError("Неверная категория льготы ИСПП");
            return null;
        }
        if(StringUtils.isEmpty(description)) {
            printError("Добавьте описание льготы ДТиСЗН");
            return null;
        }
        try {
            Long nextVersion = DAOUtils.nextVersionByCategoryDiscountDSZN(entityManager);
            CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils.findCategoryDiscountDSZNByCode(entityManager, convertCode);
            if(categoryDiscountDSZN != null) {
                if(!categoryDiscountDSZN.getDeleted()) {
                    printError("Категория льготы ДТиСЗН с таким кодом уже существует");
                    return null;
                }
                categoryDiscountDSZN.setDeleted(false);
                categoryDiscountDSZN.setDescription(description);
                categoryDiscountDSZN.setCategoryDiscount(categoryDiscount);
                categoryDiscountDSZN.setVersion(nextVersion);
            } else {
                guid = UUID.randomUUID().toString();
                categoryDiscountDSZN = new CategoryDiscountDSZN(convertCode, description, categoryDiscount, convertETPCode,
                        StringUtils.isEmpty(ETPTextCode) ? null : ETPTextCode, priority, nextVersion, guid);
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

    public String getETPCode() {
        return ETPCode;
    }

    public void setETPCode(String ETPCode) {
        this.ETPCode = ETPCode;
    }

    public String getETPTextCode() {
        return ETPTextCode;
    }

    public void setETPTextCode(String ETPTextCode) {
        this.ETPTextCode = ETPTextCode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
