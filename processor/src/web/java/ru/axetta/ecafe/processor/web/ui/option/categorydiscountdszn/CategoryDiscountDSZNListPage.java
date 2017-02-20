/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountDSZN;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;

import org.hibernate.ObjectDeletedException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Scope("session")
public class CategoryDiscountDSZNListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener {

    @Autowired
    private DAOService service;

    private List<CategoryDiscountDSZNItem> items = Collections.emptyList();

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<CategoryDiscountDSZNItem> getItems() {
        return items;
    }

    public String getPageFilename() {
        return "option/categorydiscountdszn/list";
    }

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            DAOUtils.deleteCategoryDiscount(persistenceSession, confirmDeletePage.getEntityId());
            persistenceTransaction.commit();
            persistenceTransaction = null;

            reload();
        } catch (ConstraintViolationException vce){
            logAndPrintMessage(
                    "Ошибка при удалении категории: имеются зарегистрированные Правила скидок или Клиенты привязанные к категории",
                    vce);
        } catch (ObjectDeletedException ode){
            logAndPrintMessage("Ошибка при удалении категории: имеются зарегистрированные Правила скидок или Клиенты привязанные к категории", ode);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при удалении категории ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
    }

    @Override
    public void onShow() throws Exception {
        reload();
    }

    private void reload() {
        items = new ArrayList<CategoryDiscountDSZNItem>();
        List<CategoryDiscountDSZN> list = service.getCategoryDiscountDSZNList();
        for(CategoryDiscountDSZN discountDSZN : list) {
            items.add(new CategoryDiscountDSZNItem(discountDSZN));
        }

    }

    public static class CategoryDiscountDSZNItem {
        private long idOfCategoryDiscountDSZN;
        private Integer code;
        private String description;
        private Long idOfCategoryDiscount;
        private String categoryName;

        public CategoryDiscountDSZNItem(CategoryDiscountDSZN categoryDiscountDSZN) {
            this.idOfCategoryDiscountDSZN = categoryDiscountDSZN.getIdOfCategoryDiscountDSZN();
            this.code = categoryDiscountDSZN.getCode();
            this.description = categoryDiscountDSZN.getDescription();
            if(categoryDiscountDSZN.getCategoryDiscount() != null) {
                this.idOfCategoryDiscount = categoryDiscountDSZN.getCategoryDiscount().getIdOfCategoryDiscount();
                this.categoryName = categoryDiscountDSZN.getCategoryDiscount().getCategoryName();
            } else {
                this.idOfCategoryDiscount = null;
                this.categoryName = null;
            }
        }

        public long getIdOfCategoryDiscountDSZN() {
            return idOfCategoryDiscountDSZN;
        }

        public void setIdOfCategoryDiscountDSZN(long idOfCategoryDiscountDSZN) {
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

        public Long getIdOfCategoryDiscount() {
            return idOfCategoryDiscount;
        }

        public void setIdOfCategoryDiscount(Long idOfCategoryDiscount) {
            this.idOfCategoryDiscount = idOfCategoryDiscount;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
    }

}
