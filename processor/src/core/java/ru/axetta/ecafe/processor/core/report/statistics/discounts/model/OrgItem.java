/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discounts.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 24.02.16
 * Time: 18:01
 */
public class OrgItem {

    private String shortName;
    private List<GroupItem> groupItemList;
    private List<CategoryItem> categoryItemOrg = new ArrayList<CategoryItem>();

    public OrgItem() {
    }

    public OrgItem(String shortName) {
        this.shortName = shortName;
    }

    public void countCategory() {
        for (GroupItem group : groupItemList) {
            List<CategoryItem> categoryItems = group.getCategoryItemGroup();
            for (CategoryItem categoryItem : categoryItems) {
                if (categoryItemOrg.contains(categoryItem)) {
                    categoryItemOrg.get(categoryItemOrg.indexOf(categoryItem)).count(categoryItem.getTotalByCategory());
                } else {
                    CategoryItem newCategoryItem = new CategoryItem(categoryItem.getCategory());
                    newCategoryItem.setTotalByCategory(categoryItem.getTotalByCategory());
                    categoryItemOrg.add(newCategoryItem);
                }
            }
        }
    }

    public void removeEmptyGroups(){
        Iterator<GroupItem> it = getGroupItemList().iterator();
        while (it.hasNext()) {
            GroupItem group = it.next();
            if(group.getClientItemList().size() == 0) it.remove();
        }
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<GroupItem> getGroupItemList() {
        return groupItemList;
    }

    public void setGroupItemList(List<GroupItem> groupItemList) {
        this.groupItemList = groupItemList;
    }

    public List<CategoryItem> getCategoryItemOrg() {
        return categoryItemOrg;
    }

    public void setCategoryItemOrg(List<CategoryItem> categoryItemOrg) {
        this.categoryItemOrg = categoryItemOrg;
    }
}
