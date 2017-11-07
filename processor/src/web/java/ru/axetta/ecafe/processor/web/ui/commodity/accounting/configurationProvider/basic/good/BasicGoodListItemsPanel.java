/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
@Scope("session")
public class BasicGoodListItemsPanel extends BasicPage {

    private final static Logger logger = LoggerFactory.getLogger(BasicGoodListItemsPanel.class);
    private List<BasicGoodListItem> basicGoodList = new ArrayList<BasicGoodListItem>();
    private String filter;
    private String selectedIds = "";
    private List<BasicGoodListItem> selectedList = new ArrayList<BasicGoodListItem>();
    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();

    public List<BasicGoodListItem> getBasicGoodList() {
        return basicGoodList;
    }

    public void setBasicGoodList(List<BasicGoodListItem> basicGoodList) {
        this.basicGoodList = basicGoodList;
    }

    public interface CompleteHandler {
        void completeBasicGoodListSelection(List<BasicGoodListItem> idOfBasicGoodList) throws Exception;
    }

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.add(handler);
    }

    public void completeBasicGoodSelection() throws Exception {
        if (!completeHandlers.empty()) {
            selectedList.clear();
            for (BasicGoodListItem item : basicGoodList) {
                if (item.getSelected()) selectedList.add(item);
            }
            completeHandlers.peek()
                    .completeBasicGoodListSelection(selectedList);
            completeHandlers.pop();
        }
    }

    public Object cancel(){
        if (!completeHandlers.empty()) {
            completeHandlers.pop();
        }
        return null;
    }

    public Object selectAll() {
        for (BasicGoodListItem item : basicGoodList) {
            item.setSelected(true);
        }
        return null;
    }

    public Object clearAll() {
        for (BasicGoodListItem item : basicGoodList) {
            item.setSelected(false);
        }
        return null;
    }

    public void reload(List<BasicGoodListItem> idOfBasicGoods) throws Exception {
        try {
            retrieveBasicGood();
            for (BasicGoodListItem item : basicGoodList) {
                if (idOfBasicGoods.contains(item)) {
                    item.setSelected(true);
                } else {
                    item.setSelected(false);
                }
            }
            selectedList = idOfBasicGoods;
            filter="";
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
    }

    public Object updateBasicGoodSelectPage(){
        try {
            retrieveBasicGood();
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
        return null;
    }

    private void retrieveBasicGood() throws Exception {
        basicGoodList.clear();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(GoodsBasicBasket.class);
            if (!StringUtils.isEmpty(filter)) {
                criteria.add(Restrictions.ilike("nameOfGood", filter, MatchMode.ANYWHERE));
            }
            criteria.addOrder(Order.asc("idOfBasicGood"));
            List<GoodsBasicBasket> list = criteria.list();
            for (GoodsBasicBasket cp : list) {
                BasicGoodListItem it = new BasicGoodListItem(cp);
                if (selectedList.contains(it)) it.setSelected(true);
                basicGoodList.add(it);
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public String getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(String selectedIds) {
        this.selectedIds = selectedIds;
    }
}
