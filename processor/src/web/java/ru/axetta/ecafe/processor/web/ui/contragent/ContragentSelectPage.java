/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 10:20:25
 * To change this template use File | Settings | File Templates.
 */
public class ContragentSelectPage extends BasicPage {

    public interface CompleteHandler {

        void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception;
    }

    public static class Item {

        private final Long idOfContragent;
        private final String contragentName;

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
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private Item selectedItem = new Item();
    private String name = "";
    private String filter, classTypesString;
    private int multiContrFlag;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeContragentSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeContragentSelection(session, selectedItem.getIdOfContragent(),
                    multiContrFlag, classTypesString);
            completeHandlers.pop();
        }
        clear(session);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public Object cancelFilter() {
        selectedItem = new Item();
        name = "";
        return null;
    }

    public void setSelectedItem(Item selected) {
        if (null == selected) {
            this.selectedItem = new Item();
        } else {
            this.selectedItem = selected;
        }
        this.name = selectedItem.contragentName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void fill(Session session, int multiContrFlag, String classTypes) throws Exception {
        this.multiContrFlag = multiContrFlag;
        List<Item> items = new LinkedList<Item>();
        List contragents = retrieveContragents(session, classTypes);
        for (Object object : contragents) {
            Contragent contragent = (Contragent) object;
            Item item = new Item(contragent);
            items.add(item);
        }
        this.items = items;
    }

    public void clear(Session session) {
        this.multiContrFlag = 0;
        filter = "";
        selectedItem = new Item();
        try {
            fill(session, multiContrFlag, "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List retrieveContragents(Session session, String classTypesString) throws HibernateException {
        this.classTypesString = classTypesString;
        Criteria criteria = session.createCriteria(Contragent.class).addOrder(Order.asc("contragentName"));
        //  Ограничение на просмотр только тех контрагентов, которые доступны пользователю
        try {
            Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
            ContextDAOServices.getInstance().buildContragentRestriction(idOfUser, criteria);
        } catch (Exception e) {

        }
        Criterion byFilter = null;
        if (StringUtils.isNotEmpty(filter)) {
            //criteria.add(Restrictions.ilike("contragentName", filter, MatchMode.ANYWHERE));
            byFilter = Restrictions.ilike("contragentName", filter, MatchMode.ANYWHERE);
        }
        Criterion byClassType = null;
        if(StringUtils.isNotEmpty(classTypesString)) {
            String[] classTypes = classTypesString.split(",");
            byClassType = Restrictions.eq("classId", Integer.parseInt(classTypes[0]));
            for (int i = 1; i < classTypes.length; i++) {
                byClassType = Restrictions.or(byClassType, Restrictions.eq("classId", Integer.parseInt(classTypes[i])));
            }
            //criteria.add(exp);
        }

        Criterion finalCriterion = null;
        if (byFilter != null && byClassType != null) {
            finalCriterion = Restrictions.and(byFilter, byClassType);
        }
        else if (byFilter != null) {
            finalCriterion = byFilter;
        }
        else if (byClassType != null) {
            finalCriterion = byClassType;
        }
        Boolean useOperator = DAOUtils.getOptionValueBool(session, Option.OPTION_WITH_OPERATOR, false); //флаг - Включена ли схема с Оператором в настройках
        if (useOperator) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.eq("contragentName", Contragent.CLASS_NAMES[Contragent.OPERATOR]));
            Criterion byOperator = Restrictions.disjunction().add(Restrictions.eq("contragentName", Contragent.CLASS_NAMES[Contragent.OPERATOR]));
            if (finalCriterion != null) {
                finalCriterion = Restrictions.or(finalCriterion, byOperator);
            }
            else {
                finalCriterion = byOperator;
            }
        }
        if (finalCriterion != null) {
            criteria.add(finalCriterion);
        }
        criteria.addOrder(Order.asc("contragentName"));
        return criteria.list();
    }

}