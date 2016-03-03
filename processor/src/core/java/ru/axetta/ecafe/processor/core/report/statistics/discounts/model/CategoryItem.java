/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discounts.model;

import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 24.02.16
 * Time: 18:03
 */
public class CategoryItem implements Comparable<CategoryItem> {

    private String category;
    private int totalByCategory;
    private static Map<String, Long> allCategories = new HashMap<String, Long>();

    //load all idOfCategory to sort categories by id
    public static void loadCategoriesMap(Session session) {
        String q =
                "select idOfCategoryDiscount, categoryName from CategoryDiscount" + " where idOfCategoryDiscount >= 0";
        List categoriesList = session.createQuery(q).list();
        for (Object record : categoriesList) {
            Object[] currentCategory = (Object[]) record;
            allCategories.put((String) currentCategory[1], (Long) currentCategory[0]);
        }
    }

    @Override
    public int compareTo(CategoryItem o) {
        return getAllCategories().get(category).compareTo(getAllCategories().get(o.getCategory()));
    }

    public CategoryItem(String category) {
        this.category = category;
        totalByCategory = 1;
    }

    public void count() {
        totalByCategory++;
    }

    public void count(int amount) {
        totalByCategory += amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalByCategory() {
        return totalByCategory;
    }

    public void setTotalByCategory(int totalByCategory) {
        this.totalByCategory = totalByCategory;
    }

    public static Map<String, Long> getAllCategories() {
        return allCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CategoryItem that = (CategoryItem) o;

        if (category != null ? !category.equals(that.category) : that.category != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return category != null ? category.hashCode() : 0;
    }
}
