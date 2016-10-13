/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discounts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 24.02.16
 * Time: 18:02
 */
public class GroupItem implements Comparable<GroupItem> {

    private String groupName;
    private List<ClientItem> clientItemList;
    private List<CategoryItem> categoryItemGroup;

    public GroupItem() {
    }

    public GroupItem(String groupName) {
        this.groupName = groupName;
        this.clientItemList = new ArrayList<ClientItem>();
        this.categoryItemGroup = new ArrayList<CategoryItem>();
    }

    @Override
    public int compareTo(GroupItem o) {
        //example 1-C < 5-B < 10-A < Выбывшие
        if (groupName.contains("-") && o.getGroupName().contains("-")) {
            String[] name1 = groupName.split("-");
            String[] name2 = o.getGroupName().split("-");
            int number1;
            int number2;
            try {
                number1 = Integer.valueOf(name1[0]);
                number2 = Integer.valueOf(name2[0]);
            } catch (NumberFormatException e) {
                return groupName.compareTo(o.getGroupName());
            }
            if (number1 != number2) {
                return Double.compare(number1, number2);
            } else {
                return name1[1].compareTo(name2[1]);
            }
        } else {
            return groupName.compareTo(o.getGroupName());
        }
    }

    public ClientItem addClientItem(String name, String category) {
        ClientItem newClient = new ClientItem(name, category);
        clientItemList.add(newClient);
        return newClient;
    }

    public void sortClients() {
        Collections.sort(clientItemList, new Comparator<ClientItem>() {
            @Override
            public int compare(ClientItem c1, ClientItem c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });
    }

    public void countCategory(String category) {
        CategoryItem currentItem = new CategoryItem(category);
        if (categoryItemGroup.contains(currentItem)) {
            categoryItemGroup.get(categoryItemGroup.indexOf(currentItem)).count();
        } else {
            categoryItemGroup.add(currentItem);
        }

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<ClientItem> getClientItemList() {
        return clientItemList;
    }

    public void setClientItemList(List<ClientItem> clientItemList) {
        this.clientItemList = clientItemList;
    }

    public List<CategoryItem> getCategoryItemGroup() {
        return categoryItemGroup;
    }

    public void setCategoryItemGroup(List<CategoryItem> categoryItemGroup) {
        this.categoryItemGroup = categoryItemGroup;
    }
}
