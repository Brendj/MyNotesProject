/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDish;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.Session;

import javax.faces.model.SelectItem;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ComplexDishSelectPage extends BasicPage {

    public interface CompleteHandler {

        void completeDishSelection(Session session, Long idOfDish) throws Exception;
    }

    public static class Item {

        private Long idOfDish;
        private final String dishName;
        private Boolean selected;

        public Item() {
            this.idOfDish = null;
            this.dishName = null;
            this.selected = null;
        }

        public Item(ClientGroup clientGroup) {
            this.idOfDish = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
            this.dishName = clientGroup.getGroupName();
            this.selected = false;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public String getDishName() {
            return dishName;
        }

        public Long getIdOfDish() {
            return idOfDish;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<>();
    private List<Item> items = Collections.emptyList();
    private Item selectedItem = new Item();
    private String filter;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeDishSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeDishSelection(session, selectedItem.getIdOfDish());
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

    public Object cancelFilter() {
        selectedItem = new Item();
        return null;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<WtDish> getDish() {
        List<WtDish> dishItems = DAOService.getInstance().getWtDish();
        SelectItem[] items = new SelectItem[dishItems.size() + 1];
        items[0] = new SelectItem(-1, "Не выбрано");
        int n = 1;
        for (WtDish dish : dishItems) {
            items[n] = new SelectItem(dish.getIdOfDish(), dish.getDishName());
            ++n;
        }
        return dishItems;
    }


    //public void fill(Session session) throws Exception {
    //    List<Item> items = new ArrayList<Item>();
    //    if (filter == null) {
    //        filter = "";
    //    }
    //    List<Dish> clientGroups = retrieveClientGroups(session);
    //    for (ClientGroup clientGroup : clientGroups) {
    //        Item item = new Item(clientGroup);
    //        if (!(item.getDishName().isEmpty() || item.getDishName() == null || filter.isEmpty())) {
    //            if (item.getDishName().toLowerCase().contains(filter.toLowerCase())) {
    //                items.add(item);
    //            }
    //        } else {
    //            items.add(item);
    //        }
    //    }
    //    this.items = items;
    //}
    //
    //private List<ClientGroup> retrieveClientGroups(Session session) throws HibernateException {
    //    Criteria criteria = session.createCriteria(ClientGroup.class);
    //    return criteria.list();
    //}

}
