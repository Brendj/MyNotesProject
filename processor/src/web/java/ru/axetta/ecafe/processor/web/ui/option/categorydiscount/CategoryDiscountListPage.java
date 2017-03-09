/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
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

import java.util.*;

@Component
@Scope("session")
public class CategoryDiscountListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener {

    @Autowired
    private DAOService service;

    private List<CategoryDiscountItem> items = Collections.emptyList();

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<CategoryDiscountItem> getItems() {
        return items;
    }

    public String getPageFilename() {
        return "option/categorydiscount/list";
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
        List<CategoryDiscount> list = service.getCategoryDiscountList();
        items = new ArrayList<CategoryDiscountItem>();
        for(CategoryDiscount categoryDiscount : list) {
            items.add(new CategoryDiscountItem(categoryDiscount));
        }
    }

    public static class CategoryDiscountItem {
        private long idOfCategoryDiscount;
        private String categoryName;
        private String description;
        private String organizationTypeString;
        private String categoriesDSZN;
        private boolean blockedToChange;

        public CategoryDiscountItem(CategoryDiscount categoryDiscount) {
            this.idOfCategoryDiscount = categoryDiscount.getIdOfCategoryDiscount();
            this.categoryName = categoryDiscount.getCategoryName();
            this.description = categoryDiscount.getDescription();
            this.organizationTypeString = categoryDiscount.getOrganizationTypeString();
            if(categoryDiscount.getCategoriesDiscountDSZN() != null
                    && categoryDiscount.getCategoriesDiscountDSZN().size() > 0) {
                Map<Integer, String> map = new TreeMap<Integer, String>();
                for (CategoryDiscountDSZN discountDSZN : categoryDiscount.getCategoriesDiscountDSZN()) {
                    if(!discountDSZN.getDeleted()) {
                        map.put(discountDSZN.getCode(), discountDSZN.getDescription());
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (Integer code : map.keySet()) {
                    sb.append(code);
                    sb.append(" - ");
                    sb.append(map.get(code));
                    sb.append("; ");
                }
                this.categoriesDSZN = sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
            } else {
                this.categoriesDSZN = "";
            }
            this.blockedToChange = categoryDiscount.getBlockedToChange();
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

        public String getOrganizationTypeString() {
            return organizationTypeString;
        }

        public void setOrganizationTypeString(String organizationTypeString) {
            this.organizationTypeString = organizationTypeString;
        }

        public String getCategoriesDSZN() {
            return categoriesDSZN;
        }

        public void setCategoriesDSZN(String categoriesDSZN) {
            this.categoriesDSZN = categoriesDSZN;
        }

        public boolean isBlockedToChange() {
            return blockedToChange;
        }

        public void setBlockedToChange(boolean blockedToChange) {
            this.blockedToChange = blockedToChange;
        }
    }

}
