/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

public class ClientGroupListSelectPage extends BasicPage {

    public interface CompleteHandler  {
        void completeClientGroupListSelection(Session session, Long idOfClientGroup) throws Exception;
    }

    public static class Item {

        private Long idOfClientGroup;
        private final String groupName;
        private Long idoforg;
        private Boolean selected;

        public Item() {
            this.idOfClientGroup = null;
            this.groupName = null;
            this.idoforg = null;
            this.selected = null;
        }

        public Item(ClientGroup clientGroup) {
            this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
            this.groupName = clientGroup.getGroupName();
            this.idoforg = clientGroup.getOrg().getIdOfOrg();
            this.selected = false;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public String getGroupName() {
            return groupName;
        }

        public Long getIdoforg() {
            return idoforg;
        }

        public void setIdoforg(Long idoforg) {
            this.idoforg = idoforg;
        }

        public Long getIdOfClientGroup() {
            return idOfClientGroup;
        }
    }

    private String selectedGroupName = "Не выбрано";
    private List<Long> selectedGroupId = new ArrayList<>();
    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private List<Item> saveItems = new ArrayList<>();
    private Item selectedItem = new Item();
    private String filter;
    private String idOfOrg;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void clear(){
        items = Collections.emptyList();
        saveItems = new ArrayList<>();
        selectedGroupName = "Не выбрано";
        selectedGroupId = new ArrayList<>();
    }

    public void clearSelect(){
        for (Item item: items)
            item.setSelected(false);
        for (Item item: saveItems)
            item.setSelected(false);
        selectedGroupName = "Не выбрано";
        selectedGroupId = new ArrayList<>();
    }

    public void selectAllItems() {
        for (Item item : getItems()) {
            item.setSelected(true);
        }
        selectedGroupName = "";
        StringBuilder str = new StringBuilder();
        for (Item item : items) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append(item.getGroupName());
            selectedGroupId.add(item.idOfClientGroup);
        }
        selectedGroupName = str.toString();
    }

    public String getSelectedItems() {
        StringBuilder str = new StringBuilder();
        for (Item it : saveItems) {
            if (!it.selected) {
                continue;
            }
            if (str.length() > 0) {
                str.append("; ");
            }
            str.append(it.getGroupName());
        }
        if(str.length() == 0)
            str.append("Не выбрано");
        return str.toString();
    }

    public void updateSelectedIds(String groupName, boolean selected) {
        List<String> list = Arrays.asList(StringUtils.split(selectedGroupName, ","));
        List<String> selectedIdsList = new ArrayList<String>(list);
        if (selectedIdsList.contains(groupName) && !selected) {
            selectedIdsList.remove(groupName);
        } else {
            if (!selectedIdsList.contains(groupName) && selected) {
                selectedIdsList.add(groupName);
            }
        }
        StringBuilder str = new StringBuilder();
        for (String s : selectedIdsList) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append(s);
        }
        selectedGroupName = str.toString();
    }

    public void completeClientGroupListSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeClientGroupListSelection(session, selectedItem.getIdOfClientGroup());
            completeHandlers.pop();
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selected) {
        if (null == selected) {
            this.selectedItem = new Item();
        } else {
            this.selectedItem = selected;
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(String idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Object cancelFilter() {
        selectedItem = new Item();
        return null;
    }

    public void fill(Session session, String idOfOrg) throws Exception {
        this.idOfOrg = idOfOrg;
        List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrg, ","));
        List<Long> orgList = new ArrayList<>();
        for(String org: stringOrgList)
            orgList.add(Long.valueOf(org));
        List<Item> items = new ArrayList<Item>();
        if (filter == null) {
            filter = "";
        }
        if (filter.equals("") && saveItems.size() > 0){
            this.items = updateItems(this.items, saveItems);
        }
        List<ClientGroup> clientGroups = retrieveClientGroups(session, orgList);
        for (ClientGroup clientGroup : clientGroups) {
            Item item = new Item(clientGroup);
            if (!(item.getGroupName().isEmpty() || item.getGroupName() == null || filter.isEmpty())) {
                if (item.getGroupName().toLowerCase().contains(filter.toLowerCase())) {
                    items.add(item);
                }
            } else {
                items.add(item);
            }
        }
        saveItems = this.items;
        this.items = updateItems(this.items, items);
    }

    public void update(){
        if (saveItems.size() > 0){
            this.items = updateItems(this.items, saveItems);
        }
    }

    private List<Item> updateItems(List<Item> currentItems, List<Item> newItems){
        List<Item> items = new ArrayList<Item>();
        if(newItems.size() <= 0)
            return currentItems;
        if(currentItems.size() <= 0)
            return newItems;
        for(Item ni: newItems){
            int index = containsItem(currentItems, ni);
            if(index != -1)
                items.add(currentItems.get(index));
            else
                items.add(ni);
        }
        return items;
    }
    private Integer containsItem(List<Item> currentItems, Item newItem){
        for(int s = 0; s < currentItems.size(); s++){
            if(currentItems.get(s).getIdOfClientGroup().toString().equals(newItem.getIdOfClientGroup().toString())
                    && currentItems.get(s).idoforg.toString().equals(newItem.getIdoforg().toString()))
                return s;
        }
        return -1;
    }

    private List<ClientGroup> retrieveClientGroups(Session session, List<Long> orgList) throws HibernateException {
        List<ClientGroup> clientGroups = new ArrayList<>();
        for (Long org: orgList) {
            Criteria criteria = session.createCriteria(ClientGroup.class);
            criteria.add(Restrictions.eq("org.idOfOrg", org));
            clientGroups.addAll(criteria.list());
        }
        return clientGroups;
    }

    public String getSelectedGroupName() {
        return selectedGroupName;
    }

    public void setSelectedGroupName(String selectedGroupName) {
        this.selectedGroupName = selectedGroupName;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Long> getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(List<Long> selectedGroupId) {
        this.selectedGroupId = selectedGroupId;
    }
}
