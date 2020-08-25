/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountDSZN;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;

import org.hibernate.ObjectDeletedException;
import org.hibernate.Session;
import org.hibernate.Transaction;
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

    private String categoryNameFilter = "";

    public String getPageFilename() {
        return "option/categorydiscount/list";
    }

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        try {
            DiscountManager.deleteCategoryDiscount(confirmDeletePage.getEntityId());
            reload();
        } catch (ObjectDeletedException ode){
            logAndPrintMessage("Ошибка при удалении категории: имеются зарегистрированные Правила скидок или Клиенты привязанные к категории", ode);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при удалении категории ", e);
        }
    }

    @Override
    public void onShow() throws Exception {
        reload();
    }

    private void reload() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<CategoryDiscount> list = DAOUtils.getCategoryDiscountList(persistenceSession);

            items = new ArrayList<CategoryDiscountItem>();
            for (CategoryDiscount categoryDiscount : list) {
                items.add(new CategoryDiscountItem(categoryDiscount));
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при обновлении льгот ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
    }

    public String getCategoryNameFilter() {
        return categoryNameFilter;
    }

    public void setCategoryNameFilter(String categoryNameFilter) {
        this.categoryNameFilter = categoryNameFilter;
    }

    public Object clear() {
        categoryNameFilter = "";
        reload();
        return null;
    }

    public Object search() {
        if(categoryNameFilter.isEmpty()){
            return null;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<CategoryDiscount> list = DAOUtils.getCategoryDiscountListByCategoryName(persistenceSession, categoryNameFilter);

            items = new LinkedList<CategoryDiscountItem>();
            for(CategoryDiscount categoryDiscount : list) {
                items.add(new CategoryDiscountItem(categoryDiscount));
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при фильтрации льгот ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
        return null;
    }

    public String getStatus() {
        return categoryNameFilter.isEmpty() ? "Нет" : "Установлен";
    }

    public static class CategoryDiscountItem {
        private long idOfCategoryDiscount;
        private String categoryName;
        private String description;
        private String organizationTypeString;
        private String categoriesDSZN;
        private boolean blockedToChange;
        private Boolean eligibleToDelete;
        private String discountRate;
        private CategoryDiscountEnumType categoryType;
        private String filter = "-";
        private String discountRules;
        private List<Long> idOfRuleList = new ArrayList<Long>();
        private Set<DiscountRule> discountRuleSet;
        private Boolean deletedState;
        public static final String DISCOUNT_START = "Платное питание[";
        public static final String DISCOUNT_END = "%]";

        public CategoryDiscountItem(CategoryDiscount categoryDiscount) {
            this.idOfCategoryDiscount = categoryDiscount.getIdOfCategoryDiscount();
            this.categoryName = categoryDiscount.getCategoryName();
            this.description = categoryDiscount.getDescription();
            this.organizationTypeString = categoryDiscount.getOrganizationTypeString();
            this.blockedToChange = categoryDiscount.getBlockedToChange();
            this.eligibleToDelete = categoryDiscount.getEligibleToDelete();
            this.categoryType = categoryDiscount.getCategoryType();
            this.discountRules = categoryDiscount.getDiscountRules();
            this.discountRuleSet = categoryDiscount.getDiscountsRules();
            this.deletedState = categoryDiscount.getDeletedState();

            if(description.indexOf(DISCOUNT_START) == 0) {
                String discount = description.substring(
                        description.indexOf(DISCOUNT_START) + DISCOUNT_START.length(),
                        description.indexOf(DISCOUNT_END));
                this.setDiscountRate(discount+"%");
                description = "";
            } else {
                this.setDiscountRate("100%");
            }
            if(categoryDiscount.getDiscountsRules().isEmpty()){
                this.setFilter("-");
            } else {
                StringBuilder sb=new StringBuilder();
                sb.append("{");
                for (DiscountRule discountRule: categoryDiscount.getDiscountsRules()){
                    if (!discountRule.getDeletedState()) {
                        this.idOfRuleList.add(discountRule.getIdOfRule());
                        sb.append(discountRule.getDescription());
                        sb.append(";");
                    }
                }
                sb.append("} ");
                this.setFilter(sb.substring(0, sb.length()-1));
            }
            this.categoriesDSZN = "";
            for (CategoryDiscountDSZN categoryDiscountDSZN : categoryDiscount.getCategoriesDiscountDSZN()) {
                this.categoriesDSZN += categoryDiscountDSZN.getDescription() + ", ";
            }
            if (this.categoriesDSZN.length() > 0) this.categoriesDSZN = this.categoriesDSZN.substring(0, this.categoriesDSZN.length()-2);
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

        public Boolean getEligibleToDelete() {
            return eligibleToDelete;
        }

        public void setEligibleToDelete(Boolean eligibleToDelete) {
            this.eligibleToDelete = eligibleToDelete;
        }

        public String getDiscountRate() {
            return discountRate;
        }

        public void setDiscountRate(String discountRate) {
            this.discountRate = discountRate;
        }

        public CategoryDiscountEnumType getCategoryType() {
            return categoryType;
        }

        public void setCategoryType(CategoryDiscountEnumType categoryType) {
            this.categoryType = categoryType;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public String getFilter() {
            return filter;
        }

        public Set<DiscountRule> getDiscountRuleSet() {
            return discountRuleSet;
        }

        public void setDiscountRuleSet(Set<DiscountRule> discountRuleSet) {
            this.discountRuleSet = discountRuleSet;
        }

        public String getDiscountRules() {
            return discountRules;
        }

        public void setDiscountRules(String discountRules) {
            this.discountRules = discountRules;
        }

        public String getDeleted() {
            return deletedState ? "Да" : "Нет";
        }

        public Boolean getDeletedState() {
            return deletedState;
        }

        public void setDeletedState(Boolean deletedState) {
            this.deletedState = deletedState;
        }
    }
}
