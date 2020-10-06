/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Scope("session")
public class CategoryDiscountEditPage extends BasicWorkspacePage {
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public String getPageFilename() {
        return "option/categorydiscount/edit";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String discountRules;
    private String description;
    private Integer categoryType;
    private Integer organizationType;
    private Integer discountRate = 100;
    private boolean blockedToChange = false;
    private Boolean eligibleToDelete = false;
    private final CategoryDiscountEnumTypeMenu categoryDiscountEnumTypeMenu = new CategoryDiscountEnumTypeMenu();
    private String filter = "-";
    private List<Long> idOfRuleList = new ArrayList<Long>();
    private Set<DiscountRule> discountRuleSet;
    private Boolean deletedState;
    
    public static final String DISCOUNT_START = "Платное питание[";
    public static final String DISCOUNT_END = "%]";

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

    public CategoryDiscountEnumTypeMenu getCategoryDiscountEnumTypeMenu() {
        return categoryDiscountEnumTypeMenu;
    }

    public String getEntityName() {
        return categoryName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Long> getIdOfRuleList() {
        return idOfRuleList;
    }

    public void setIdOfRuleList(List<Long> idOfRuleList) {
        this.idOfRuleList = idOfRuleList;
    }

    public Set<DiscountRule> getDiscountRuleSet() {
        return discountRuleSet;
    }

    public void setDiscountRuleSet(Set<DiscountRule> discountRuleSet) {
        this.discountRuleSet = discountRuleSet;
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

    public String getDiscountRules() {
        return discountRules;
    }

    public void setDiscountRules(String discountRules) {
        this.discountRules = discountRules;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
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
        items[1] = new SelectItem(CategoryDiscount.SCHOOL_ID, organizationTypes[0].toString());
        items[2] = new SelectItem(CategoryDiscount.KINDERGARTEN_ID, organizationTypes[1].toString());
        return items;
    }

    @Transactional
    public Object save() {
        categoryName = categoryName.trim();
        if (categoryName.equals(""))
        {
            printError("Неверное название категории");
            return null;
        }
        if (!DAOService.getInstance().getCategoryDiscountListByCategoryName(categoryName).isEmpty() )
        {
            printError("Категория с данным названием уже зарегистрирована");
            return null;
        }
        if (discountRate != null && discountRate != 100) {
            description = DISCOUNT_START + discountRate + DISCOUNT_END;
        }
        CategoryDiscount categoryDiscount = DAOUtils.findCategoryDiscountById(entityManager, idOfCategoryDiscount);
        categoryDiscount.setIdOfCategoryDiscount(idOfCategoryDiscount);
        categoryDiscount.setCategoryName(categoryName);
        categoryDiscount.setDescription(description);
        categoryDiscount.setCategoryType(CategoryDiscountEnumType.fromInteger(categoryType));
        categoryDiscount.setOrgType(organizationType);
        categoryDiscount.setLastUpdate(new Date());
        categoryDiscount.setBlockedToChange(blockedToChange);
        categoryDiscount.setEligibleToDelete(eligibleToDelete);
        entityManager.persist(categoryDiscount);
        printMessage("Данные обновлены.");
        return null;
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    @Transactional
    public void reload() {
        CategoryDiscount categoryDiscount = DAOUtils.findCategoryDiscountById(entityManager, idOfCategoryDiscount);
        this.categoryName = categoryDiscount.getCategoryName();
        this.discountRules = categoryDiscount.getDiscountRules();
        this.description = categoryDiscount.getDescription();
        this.discountRuleSet = categoryDiscount.getDiscountsRules();
        this.categoryType = categoryDiscount.getCategoryType().getValue();
        this.organizationType = categoryDiscount.getOrgType();
        if(categoryDiscount.getDiscountsRules().isEmpty() || categoryDiscount.isRulesSetDeleted()){
            this.setFilter("-");
        } else {
            StringBuilder sb=new StringBuilder();
            for (DiscountRule discountRule: categoryDiscount.getDiscountsRules()){
                if (!discountRule.getDeletedState()) {
                    this.idOfRuleList.add(discountRule.getIdOfRule());
                    sb.append(discountRule.getDescription());
                    sb.append("; ");
                }
            }
            this.setFilter(sb.substring(0, sb.length()-1));
        }

        if(description.indexOf(DISCOUNT_START) == 0) {
            String discount = description.substring(
                    description.indexOf(DISCOUNT_START) + DISCOUNT_START.length(),
                    description.indexOf(DISCOUNT_END));
            discountRate = Integer.parseInt(discount);
            description = "";
        } else {
            discountRate = 100;
        }
        this.blockedToChange = categoryDiscount.getBlockedToChange();
        this.eligibleToDelete = categoryDiscount.getEligibleToDelete();
        this.deletedState = categoryDiscount.getDeletedState();
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

}