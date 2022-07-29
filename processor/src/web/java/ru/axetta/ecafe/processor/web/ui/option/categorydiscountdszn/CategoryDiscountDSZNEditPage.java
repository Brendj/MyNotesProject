/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountDSZN;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategorySelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
    private String code;
    private String description;
    private CategoryDiscount categoryDiscount;
    private String categoryName;
    private String ETPCode;
    private String ETPTextCode;

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

    public Object save() {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            if (categoryDiscount != null && (categoryDiscount.getIdOfCategoryDiscount() < 0 || !categoryDiscount.getCategoryType().equals(CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT))) {
                throw new Exception("Неверная категория льготы ИСПП");
            }
            Integer DSZNcode;
            if (code.isEmpty()) {
                throw new Exception("Неверный код ДТиСЗН");
            } else {
                try {
                    DSZNcode = Integer.parseInt(code);
                } catch (NumberFormatException e) {
                    throw new Exception("Неверный код ДТиСЗН");
                }
            }
            if (StringUtils.isEmpty(description)) {
                throw new Exception("Добавьте описание льготы ДТиСЗН");
            }
            Long etpCodeLong;
            if (ETPCode.isEmpty()) {
                etpCodeLong = null;
            } else {
                try {
                    etpCodeLong = Long.parseLong(ETPCode);
                } catch (NumberFormatException e) {
                    throw new Exception("Неверный код ЕТП");
                }
            }
            Long nextVersion = DAOUtils.nextVersionByCategoryDiscountDSZN(entityManager);

            CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils
                    .findCategoryDiscountDSZNById(entityManager, idOfCategoryDiscountDSZN);

            categoryDiscountDSZN.setDescription(description);
            categoryDiscountDSZN.setCategoryDiscount(categoryDiscount);
            categoryDiscountDSZN.setVersion(nextVersion);
            categoryDiscountDSZN.setETPCode(etpCodeLong);
            categoryDiscountDSZN.setETPTextCode(StringUtils.isEmpty(ETPTextCode) ? null : ETPTextCode);
            categoryDiscountDSZN.setCode(DSZNcode);

            persistenceSession.update(categoryDiscountDSZN);

            persistenceTransaction.commit();
            persistenceTransaction = null;

            printMessage("Данные обновлены.");
        } catch (Exception e){
            getLogger().error("Error saving changes", e);
            printError(e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
        return null;
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    @Transactional
    public void reload() {
        CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils.findCategoryDiscountDSZNById(entityManager, idOfCategoryDiscountDSZN);
        this.code = categoryDiscountDSZN.getCode().toString();
        this.description = categoryDiscountDSZN.getDescription();
        this.categoryDiscount = categoryDiscountDSZN.getCategoryDiscount();
        if(this.categoryDiscount != null) {
            this.categoryName = categoryDiscountDSZN.getCategoryDiscount().getCategoryName();
        } else {
            categoryName = "";
        }
        if (null != categoryDiscountDSZN.getETPCode()) {
            this.ETPCode = categoryDiscountDSZN.getETPCode().toString();
        } else {
            this.ETPCode = null;
        }
        this.ETPTextCode = categoryDiscountDSZN.getETPTextCode();
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

    public String getEntityName() {
        return "Льгота ДТиСЗН " + code;
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
}