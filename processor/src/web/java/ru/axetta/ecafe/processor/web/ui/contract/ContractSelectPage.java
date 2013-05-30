/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contract;

import ru.axetta.ecafe.processor.core.persistence.Contract;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 23.05.13
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
public class ContractSelectPage extends BasicPage {

    public interface CompleteHandler {

        void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes) throws Exception;
    }

    public static class Item {

        private final Long idOfContract;
        private final String contractName;

        public Item() {
            this.idOfContract = null;
            this.contractName = null;
        }

        public Item(Contract contract) {
            this.idOfContract = contract.getIdOfContract();
            java.text.DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            this.contractName = contract.getContractNumber() + "/" +
                                contract.getPerformer() + "/" +
                                df.format(contract.getDateOfConclusion());
        }

        public Long getIdOfContract() {
            return idOfContract;
        }

        public String getContractName() {
            return contractName;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private Item selectedItem = new Item();
    private String filter, classTypesString;
    private int multiContrFlag;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeContractSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeContractSelection(session, selectedItem.getIdOfContract(), multiContrFlag,
                    classTypesString);
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

    public void fill(Session session, int multiContrFlag, String classTypes, String contragentName) throws Exception {
        this.multiContrFlag = multiContrFlag;
        List<Item> items = new LinkedList<Item>();
        List contracts = retrieveContracts(session, classTypes, contragentName);
        for (Object object : contracts) {
            Contract contract = (Contract) object;
            Item item = new Item(contract);
            items.add(item);
        }
        this.items = items;
    }

    /*public void fill(Session session, Long idOfContragent) throws HibernateException {
        List<Item> items = new LinkedList<Item>();
        List contragents = retrieveContragents(session);
        for (Object object : contragents) {
            Contragent contragent = (Contragent) object;
            Item item = new Item(contragent);
            items.add(item);
        }
        Item selectedItem = new Item();
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            selectedItem = new Item(contragent);
        }
        this.items = items;
        this.selectedItem = selectedItem;
    }*/

    private List retrieveContracts(Session session, String classTypesString, String contragentName) throws HibernateException {
        this.classTypesString = classTypesString;
        Criteria criteria = session.createCriteria(Contract.class);
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.like("contractNumber", filter, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotEmpty(contragentName)) {
            criteria.add(Restrictions.like("performer", contragentName, MatchMode.ANYWHERE));
        }
        if(!classTypesString.isEmpty()) {
            String[] classTypes = classTypesString.split(",");
            Criterion exp = Restrictions.eq("classId", Integer.parseInt(classTypes[0]));
            for (int i = 1; i < classTypes.length; i++) {
                exp = Restrictions.or(exp, Restrictions.eq("classId", Integer.parseInt(classTypes[i])));
            }
            criteria.add(exp);
        }
        return criteria.list();
    }

}