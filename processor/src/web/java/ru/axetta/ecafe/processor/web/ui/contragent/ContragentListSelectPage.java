/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 10:20:25
 * To change this template use File | Settings | File Templates.
 */
public class ContragentListSelectPage extends BasicPage {

    private final static String OPERATOR = "Оператор";

    public interface CompleteHandler {

        void completeContragentListSelection(Session session, List<Long> idOfContragent, int multiContrFlag,
                String classTypes) throws Exception;
    }

    public static class Item {

        private final Long idOfContragent;
        private final String contragentName;
        private boolean selected;

        public Item() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Item(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    //private Item selectedItem = new Item();
    private String filter, classTypesString;
    private int multiContrFlag;
    private String selectedIds;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeContragentSelection(Session session) throws Exception {
        List<String> list = Arrays.asList(StringUtils.split(selectedIds, ","));
        List<Long> selected = new ArrayList<Long>();
        for(String s : list){
            selected.add(Long.parseLong(s));
        }

        if (!completeHandlers.empty()) {
            completeHandlers.peek()
                    .completeContragentListSelection(session, selected/*selectedItem.getIdOfContragent()*/,
                            multiContrFlag, classTypesString);
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
            str.append(it.getContragentName());
        }
        return str.toString();
    }

    public void updateSelectedIds(Long id, boolean selected) {
        List<String> list = Arrays.asList(StringUtils.split(selectedIds, ","));
        List<String> selectedIdsList = new ArrayList<String>(list);
        if (selectedIdsList.contains(id.toString()) && !selected) {
            selectedIdsList.remove(id.toString());
        } else {
            if (!selectedIdsList.contains(id.toString()) && selected) {
                selectedIdsList.add(id.toString());
            }
        }
        StringBuilder str = new StringBuilder();
        for (String s : selectedIdsList) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append(s);
        }
        selectedIds = str.toString();
    }

    /*public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selected) {
        if (null == selected) {
            this.selectedItem = new Item();
        } else {
            this.selectedItem = selected;
        }
    }*/

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

    public String getClassTypesString() {
        return classTypesString;
    }

    public void setClassTypesString(String classTypesString) {
        this.classTypesString = classTypesString;
    }

    public void fill(Session session, int multiContrFlag, String classTypes) throws Exception {
        fill(session, multiContrFlag, classTypes, null);
    }

    public void fill(Session session, int multiContrFlag, String classTypes, List<Long> idOfOrgs) throws Exception {
        this.multiContrFlag = multiContrFlag;
        this.classTypesString = classTypes;
        List<Item> items = new ArrayList<Item>();
        List<String> selectedIdsList = Arrays.asList(StringUtils.split(selectedIds, ","));
        List contragents = retrieveContragents(session, classTypes, idOfOrgs);
        for (Object object : contragents) {
            Contragent contragent = (Contragent) object;
            Item item = new Item(contragent);
            items.add(item);
            if (selectedIdsList.contains(item.getIdOfContragent().toString())) {
                item.setSelected(true);
            }
        }
        this.items = items;
    }

    private List retrieveContragents(Session session, String classTypesString, List<Long> idOfOrgs) throws HibernateException {
        Criteria criteria = session.createCriteria(Contragent.class).addOrder(Order.asc("contragentName"));

        if(!"1".equals(classTypesString)) {
            //  Ограничение на просмотр только тех контрагентов, которые доступны пользователю
            try {
                Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
                ContextDAOServices.getInstance().buildContragentRestriction(idOfUser, criteria);
            } catch (Exception e) {
            }
        }
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.ilike("contragentName", filter, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotEmpty(classTypesString)) {
            String[] classTypes;
            if (classTypesString.equals("3")) //Класс типа 3 используется только для отсечения ОПЕРАТОРА
                classTypes = new String[]{"2"};
            else
                classTypes = classTypesString.split(",");
            Criterion exp = Restrictions.eq("classId", Integer.parseInt(classTypes[0]));
            for (int i = 1; i < classTypes.length; i++) {
                exp = Restrictions.or(exp, Restrictions.eq("classId", Integer.parseInt(classTypes[i])));
            }
            criteria.add(exp);
        }
        if (idOfOrgs != null)
        {
            criteria.createAlias("orgsInternal", "orgs");
            criteria.add(Restrictions.in("orgs.idOfOrg", idOfOrgs));
        }
        criteria.addOrder(Order.asc("contragentName"));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Contragent> contragentsByCriteria = criteria.list();
        if(!"1".equals(classTypesString) && !"3".equals(classTypesString)) {
            Criteria criteria1 = session.createCriteria(Contragent.class);
            criteria1.add(Restrictions.eq("contragentName", OPERATOR));
            Contragent operator = (Contragent) criteria1.uniqueResult();
            if (operator != null) {
                if (filter == null) {
                    filter = "";
                }
                if (operator.getContragentName().toLowerCase().contains(filter.toLowerCase())) {
                    contragentsByCriteria.add(0, operator);
                }
            }
        }
        return contragentsByCriteria; // criteria.list();
    }

    public void selectAllItems() {
        for (Item item : getItems()) {
            item.setSelected(true);
        }
        selectedIds = "";
        StringBuilder str = new StringBuilder();
        for (Item item : items) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append(item.getIdOfContragent());
        }
        selectedIds = str.toString();
    }

    public void deselectAllItems() {
        selectedIds = "";
        for (Item item : getItems()) {
            item.setSelected(false);
        }
    }

}