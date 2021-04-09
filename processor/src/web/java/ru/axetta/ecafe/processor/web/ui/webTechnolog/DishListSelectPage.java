/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.webTechnolog;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDish;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class DishListSelectPage extends BasicPage {

    private String filter;

    public String getFilterContagents() {
        return filterContagents;
    }

    public void setFilterContagents(String filterContagents) {
        this.filterContagents = filterContagents;
    }

    public interface CompleteHandler {
        void dishListSelection(Session session, List<Long> idOfDish) throws Exception;
    }

    public static class Item {
        private final Long idOfDish;
        private final String dishName;
        private boolean selected;
        private String code;
        BigDecimal price;
        String idOfAgeGroupItem;
        String idOfTypeOfProductionItem;
        String dateOfBeginMenuIncluding;
        String dateOfEndMenuIncluding;
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");

        public Item() {
            this.idOfDish = null;
            this.dishName = null;
        }

        public Item(WtDish wtDish) {
            this.idOfDish = wtDish.getIdOfDish();
            this.dishName = wtDish.getDishName();
            this.code = wtDish.getCode();
            this.price = wtDish.getPrice();
            this.idOfAgeGroupItem = wtDish.getWtAgeGroupItem().getDescription();
            this.idOfTypeOfProductionItem = wtDish.getWtTypeProductionItem().getDescription();
            this.dateOfBeginMenuIncluding = wtDish.getDateOfBeginMenuIncluding() == null ? "" : myFormat.format(wtDish.getDateOfBeginMenuIncluding());
            this.dateOfEndMenuIncluding = wtDish.getDateOfEndMenuIncluding() == null ? "" :myFormat.format(wtDish.getDateOfEndMenuIncluding());
        }

        public String getDateOfBeginMenuIncluding() {
            return dateOfBeginMenuIncluding;
        }

        public void setDateOfBeginMenuIncluding(String dateOfBeginMenuIncluding) {
            this.dateOfBeginMenuIncluding = dateOfBeginMenuIncluding;
        }

        public String getDateOfEndMenuIncluding() {
            return dateOfEndMenuIncluding;
        }

        public void setDateOfEndMenuIncluding(String dateOfEndMenuIncluding) {
            this.dateOfEndMenuIncluding = dateOfEndMenuIncluding;
        }

        public String getIdOfAgeGroupItem() {
            return idOfAgeGroupItem;
        }

        public void setIdOfAgeGroupItem(String idOfAgeGroupItem) {
            this.idOfAgeGroupItem = idOfAgeGroupItem;
        }

        public String getIdOfTypeOfProductionItem() {
            return idOfTypeOfProductionItem;
        }

        public void setIdOfTypeOfProductionItem(String idOfTypeOfProductionItem) {
            this.idOfTypeOfProductionItem = idOfTypeOfProductionItem;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
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
    private String selectedIds;
    private String selectedName;
    private String filterContagents;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeDishSelection(Session session) throws Exception {
        List<String> list = Arrays.asList(StringUtils.split(selectedIds, ","));
        List<Long> selected = new ArrayList<Long>();
        for(String s : list){
            selected.add(Long.parseLong(s));
        }

        if (!completeHandlers.empty()) {
            completeHandlers.peek()
                    .dishListSelection(session, selected);
            completeHandlers.pop();
        }
    }

    public void cancelContragentListSelection() {
        completeHandlers.clear();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getSelectedItems() {
        StringBuilder str = new StringBuilder();
        for (Item it : items) {
            if (!it.isSelected()) {
                continue;
            }
            if (str.length() > 0) {
                str.append("; ");
            }
            str.append(it.getDishName());
        }
        return str.toString();
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public void updateSelectedIds(Long id, String name) {
        selectedIds = id.toString();
        selectedName = name;
    }

    public Object cancelFilter() {
        items = Collections.emptyList();
        return null;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(String selectedIds) {
        this.selectedIds = selectedIds;
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new ArrayList<Item>();
        List<String> selectedIdsList = Arrays.asList(StringUtils.split(selectedIds, ","));
        List<String> filtercontagent = Arrays.asList(StringUtils.split(filterContagents, ","));
        List<Contragent> contragents = new ArrayList<>();
        if (!filtercontagent.isEmpty()) {
            for (String idContagents : filtercontagent) {
                Contragent contragent = (Contragent) session.load(Contragent.class, Long.valueOf(idContagents));
                contragents.add(contragent);
            }
        }

        List dishes = retrieveDishes(session, contragents);
        for (Object object : dishes) {
            WtDish wtDish = (WtDish) object;
            Item item = new Item(wtDish);
            items.add(item);
            if (selectedIdsList.contains(item.getIdOfDish().toString())) {
                item.setSelected(true);
            }
        }
        this.items = items;
    }

    private List retrieveDishes(Session session, List<Contragent> contragents) throws
            HibernateException {
        Criteria criteria = session.createCriteria(WtDish.class).addOrder(Order.asc("dishName"));
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.ilike("dishName", filter, MatchMode.ANYWHERE));
        }
        if (contragents != null && !contragents.isEmpty())
            criteria.add(Restrictions.in("contragent", contragents));

        criteria.add(Restrictions.eq("deleteState", 0));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (List<WtDish>) criteria.list(); // criteria.list();
    }

    public void deselectAllItems() {
        selectedIds = "";
        selectedName = "";
        for (Item item : getItems()) {
            item.setSelected(false);
        }
    }

}
