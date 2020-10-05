/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Component
@Scope("session")
public class CategoryDiscountCreatePage extends BasicWorkspacePage {


    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public String getPageFilename() {
        return "option/categorydiscount/create";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String description;
    private Integer categoryType;
    private Integer organizationType;
    private Integer discountRate = 100;
    private boolean blockedToChange = false;
    private Boolean eligibleToDelete = false;
    private final CategoryDiscountEnumTypeMenu categoryDiscountEnumTypeMenu = new CategoryDiscountEnumTypeMenu();

    public CategoryDiscountEnumTypeMenu getCategoryDiscountEnumTypeMenu() {
        return categoryDiscountEnumTypeMenu;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
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

    public SelectItem[] getOrganizationItems() {
        OrganizationType[] organizationTypes = OrganizationType.values();
        SelectItem[] items = new SelectItem[3];
        items[0] = new SelectItem(CategoryDiscount.SCHOOL_KINDERGARTEN_ID, CategoryDiscount.SCHOOL_KINDERGARTEN_STRING);
        items[1] = new SelectItem(0, organizationTypes[0].toString());
        items[2] = new SelectItem(1, organizationTypes[1].toString());
        return items;
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
        categoryName = categoryName.trim();
        if (categoryName.equals(""))
        {
            printMessage("Неверное название катеории");
            return;
        }
        if (!DAOService.getInstance().getCategoryDiscountListByCategoryName(categoryName).isEmpty() )
        {
            printMessage("Категория с данным названием уже зарегистрирована");
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
            if(discountRate != null && discountRate != 100) {
                description = "Платное питание[" + discountRate + "%]";
            }
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            Date createdDate = new Date();
            CategoryDiscount categoryDiscount = new CategoryDiscount(idOfCategoryDiscount, categoryName, "", description,
                    createdDate, createdDate, blockedToChange, eligibleToDelete);
            categoryDiscount.setCategoryType(CategoryDiscountEnumType.fromInteger(categoryType));
            categoryDiscount.setOrgType(organizationType);
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
    @Qualifier(value = "txManager")
    private PlatformTransactionManager transactionManager;
}
