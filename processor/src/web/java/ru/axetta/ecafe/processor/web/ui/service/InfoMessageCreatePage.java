/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfInfoMessageDetail;
import ru.axetta.ecafe.processor.core.persistence.InfoMessage;
import ru.axetta.ecafe.processor.core.persistence.InfoMessageDetail;
import ru.axetta.ecafe.processor.core.persistence.InfoMessageType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Scope("session")
public class InfoMessageCreatePage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private static final Logger logger = LoggerFactory.getLogger(InfoMessageCreatePage.class);

    private List<Long> idOfOrgList = new ArrayList<Long>();
    private String filter = "Не выбрано";

    private String header;
    private String content;
    private Integer type;

    public String getPageFilename() {
        return "service/info_message_create";
    }

    public void fill() {
        idOfOrgList.clear();
        header = "";
        content = "";
        filter = "Не выбрано";
    }

    public void doSave() {
        if (StringUtils.isEmpty(header) || StringUtils.isEmpty(content) || idOfOrgList.isEmpty()) {
            printError("Все поля являются обязательными и должны быть заполнены. Необходимо выбрать хотя бы одну организацию.");
            return;
        }
        InfoMessage infoMessage = new InfoMessage();
        Session persistenceSession = null;
        Transaction transaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            transaction = persistenceSession.beginTransaction();
            infoMessage.setMtype(InfoMessageType.fromInteger(type));
            infoMessage.setHeader(header);
            infoMessage.setContent(content);
            infoMessage.setCreatedDate(new Date());
            infoMessage.setUser(DAOReadonlyService.getInstance().getUserFromSession());
            Long version = DAOUtils.nextVersionByInfoMessage(persistenceSession);
            infoMessage.setVersion(version + 1);
            persistenceSession.save(infoMessage);
            //Set details = new HashSet<InfoMessageDetail>();
            for (Long idOfOrg : idOfOrgList) {
                CompositeIdOfInfoMessageDetail compositeIdOfInfoMessageDetail = new CompositeIdOfInfoMessageDetail(infoMessage.getIdOfInfoMessage(), idOfOrg);
                InfoMessageDetail infoMessageDetail = new InfoMessageDetail(compositeIdOfInfoMessageDetail);
                persistenceSession.save(infoMessageDetail);
            }

            persistenceSession.flush();
            transaction.commit();
            transaction = null;
            printMessage("Сообщение создано");
            fill();
        } catch (Exception e) {
            printError(String.format("Во время создания сообщения возникла ошибка. Текст ошибки: ", e.getMessage()));
            logger.error("Error creating info message", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>();
            if (orgMap.isEmpty())
                filter = "Не выбрано";
            else {
                filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
            }
        }
    }

    public List<SelectItem> getTypes() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (InfoMessageType statusEnumType : InfoMessageType.values()) {
            items.add(new SelectItem(statusEnumType.ordinal(), statusEnumType.toString()));
        }
        return items;
    }

    public String getGetStringIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}