/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;

import org.hibernate.Criteria;
import org.hibernate.ObjectDeletedException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Component
@Scope("session")
public class CategoryDiscountListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener {

    @Autowired
    private DAOService service;

    private List<CategoryDiscount> items = Collections.emptyList();

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<CategoryDiscount> getItems() {
        return items;
    }

    public String getPageFilename() {
        return "option/categorydiscount/list";
    }

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            DAOUtils.deleteCategoryDiscount(persistenceSession, confirmDeletePage.getEntityId());
            //categoryListPage.fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;

            reload();
        } catch (ConstraintViolationException vce){
            logAndPrintMessage(
                    "Ошибка при удалении категории: имеются зарегистрированные Правила скидок или Клиенты привязанные к категории",
                    vce);
        } catch (ObjectDeletedException ode){
            logAndPrintMessage("Ошибка при удалении категории: имеются зарегистрированные Правила скидок или Клиенты привязанные к категории", ode);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при удалении категории ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
    }

    @Override
    public void onShow() throws Exception {
        reload();
    }

    private void reload() {
        items = service.getCategoryDiscountList();
    }


}
