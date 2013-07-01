/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractListPage<E, I extends AbstractEntityItem> extends BasicWorkspacePage {
    @PersistenceContext
    protected EntityManager entityManager;
    private static final int MAX_ITEMS_IN_LIST = 200;

    protected abstract String getPageFileName();
    protected abstract Class<E> getEntityClass();
    protected abstract I createItem();
    protected abstract String getSortField();

    protected List<I> itemList;
    
    // override to prevent data loading without filter (for large tables)
    protected boolean loadDataWithoutFilter() {
        return true;
    }

    @Override
    public String getPageFilename() {
        return getPageFileName();
    }

    @Override
    public String getPageTitle() {
        return String.format("%s (%d)",super.getPageTitle(), itemList.size());
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(this.getClass()).reload();
    }

    @Transactional
    public Object reload(){
        if (!loadDataWithoutFilter()) {
            if (getFilter().isEmpty()) {
                printMessage("Установите фильтр для вывода данных");
                return null;
            }
        }
        
        Session hiberSession = (Session) entityManager.getDelegate();

        Criteria crit = hiberSession.createCriteria(getEntityClass());
        processRestrictions (entityManager, crit);
        processSearchCriteria(entityManager, crit);
        crit = crit.setFirstResult(0);
        crit = crit.setMaxResults(MAX_ITEMS_IN_LIST);
        Order order = Order.desc(getSortField());
        crit.addOrder(order);
        List entities = crit.list();
        itemList = new LinkedList<I>();
        for (Object o : entities) {
            I item = createItem();
            item.fillForList(entityManager, (E)o);
            itemList.add(item);
        }
        if (itemList.size()==MAX_ITEMS_IN_LIST) {
            printMessage("Выборка ограничена до "+MAX_ITEMS_IN_LIST+" записей. Используйте фильтр.");
        }
        return null;
    }

    
    public Object resetFilter() {
        if (getFilter()!=null) getFilter().clear();
        RuntimeContext.getAppContext().getBean(this.getClass()).reload();
        return null;
    }

    protected void processRestrictions (EntityManager entityManager, Criteria crit) {

    }

    protected void processSearchCriteria(EntityManager entityManager, Criteria crit) {
        AbstractFilter filter = getFilter();
        if (filter!=null) filter.apply(entityManager, crit);
    }

    public List<I> getItemList() {
        return itemList;
    }

    public void setItemList(List<I> itemList) {
        this.itemList = itemList;
    }


    public abstract AbstractFilter getFilter();
}
