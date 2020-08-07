/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.internal.FrontControllerProcessor;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 07.10.13
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class NSIOrgRegistrySyncErrorPage extends BasicWorkspacePage {
    private Logger logger = LoggerFactory.getLogger(NSIOrgRegistrySyncErrorPage.class);
    private long idOfOrg;
    private long revisionCreateDate;
    private int errorType;
    private Map<Integer, String> errors;
    private String errorMessages;
    private String infoMessages;
    private String errorDetails;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Override
    public void onShow() {
        errorType = 0;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public List<SelectItem> getErrors() {
        if (errors == null) {
            errors = new HashMap<Integer, String>();
            errors.put(1, "Слишком большое количество удалений клиентов");
            errors.put(2, "Слишком большое количество изменений клиентов");
            errors.put(3, "Не верное изменение данных у клиента");
        }

        List<SelectItem> items = new ArrayList<SelectItem>();
        for (Integer k : errors.keySet()) {
            items.add(new SelectItem(k, errors.get(k)));
        }
        return items;
    }

    public void save() {
        try {
            resetMessages();
            if (errorType < 1) {
                errorMessages = "Невозможно создать запись об ошибке. Необходимо выбрать тип ошибки";
                return;
            }
            if (revisionCreateDate < 1L) {
                errorMessages = "Невозможно создать запись об ошибке. Попробуйте обновить данные из Реестров";
                return;
            }
            if (idOfOrg < 1L) {
                errorMessages = "Невозможно создать запись об ошибке. Не выбрана организация";
                return;
            }
            RuntimeContext.getAppContext().getBean(FrontControllerProcessor.class).
                    addRegistryChangeError(idOfOrg, revisionCreateDate, errors.get(errorType), errorDetails);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void doApply () {
        RuntimeContext.getAppContext().getBean(NSIOrgRegistrySyncErrorPage.class).save();
    }

    public void doClose () {
        errorType = 0;
    }

    public void resetMessages() {
        errorMessages = "";
        infoMessages = "";
    }

    public void sendError(String message) {
        errorMessages = message;
    }

    public void sendInfo(String message) {
        infoMessages = message;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public long getRevisionCreateDate() {
        return revisionCreateDate;
    }

    public void setRevisionCreateDate(long revisionCreateDate) {
        this.revisionCreateDate = revisionCreateDate;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
}
