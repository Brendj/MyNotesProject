/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.msp;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CodeMSP;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class CodeMSPEditPage extends BasicWorkspacePage {
    private static final Logger log = LoggerFactory.getLogger(CodeMSPEditPage.class);

    private Long idOfCode;
    private Integer code;
    private String description;
    private CodeMSP codeMSP;

    public void save(){
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            if (StringUtils.isEmpty(description)) {
                throw new Exception("Добавьте описание кода МСП");
            }

            CodeMSP codeMSP = (CodeMSP) persistenceSession.merge(this.codeMSP);

            codeMSP.setCode(code);
            codeMSP.setDescription(description);

            persistenceSession.update(codeMSP);

            persistenceTransaction.commit();
            persistenceTransaction = null;

            printMessage("Данные обновлены.");
        } catch (Exception e){
            log.error("Error saving changes", e);
            printError(e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, log);
            HibernateUtils.close(persistenceSession, log);
        }
    }

    @Override
    public void onShow() throws Exception {
        reload();
    }

    public void reload(){
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            this.codeMSP = (CodeMSP) session.get(CodeMSP.class, idOfCode);
            this.code = this.codeMSP.getCode();
            this.description = this.codeMSP.getDescription();

            session.close();
        } catch (Exception e) {
            log.error("Can't reload code MSP", e);
        } finally {
            HibernateUtils.close(session, log);
        }
    }

    public Long getIdOfCode() {
        return idOfCode;
    }

    public void setIdOfCode(Long idOfCode) {
        this.idOfCode = idOfCode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getPageFilename() {
        return "option/msp/edit";
    }
}
