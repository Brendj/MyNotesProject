/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.msp;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CodeMSP;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("session")
public class CodeMSPListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener {
    private static final Logger log = LoggerFactory.getLogger(CodeMSPListPage.class);

    private List<CodeMSP> items;

    public List<CodeMSP> getItems() {
        return items;
    }

    public void setItems(List<CodeMSP> items) {
        this.items = items;
    }

    @Override
    public String getPageFilename() {
        return "option/msp/list";
    }

    @Override
    public void onShow() throws Exception {
        reload();
    }

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long idOfCode = confirmDeletePage.getEntityId();
            CodeMSP codeMSP = (CodeMSP) persistenceSession.get(CodeMSP.class, idOfCode);
            persistenceSession.delete(codeMSP);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            reload();
        } catch (Exception e) {
            log.error("Ошибка при удалении кода МСП", e);
            printError("Ошибка при удалении кода МСП: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, getLogger());
            HibernateUtils.close(persistenceSession, getLogger());
        }
    }

    private void reload() {
        Session session = null;

        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            items = DAOUtils.getAllCodeMSP(session);

            session.close();
        } catch (Exception e){
            logAndPrintMessage("Ошибка при удалении кода МСП ", e);
        } finally {
            HibernateUtils.close(session, log);
        }
    }
}
