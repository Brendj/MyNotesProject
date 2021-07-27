/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.persistence.InfoMessage;
import ru.axetta.ecafe.processor.core.persistence.dao.model.OrgDeliveryInfo;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Scope("session")
public class InfoMessagePage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(InfoMessagePage.class);

    private List<InfoMessageItem> items;

    private String header;
    private String content;

    private InfoMessageItem itemToShow;

    public String getPageFilename() {
        return "service/info_message_list";
    }

    public void fill(Session session) throws Exception {
        if (items == null) {
            items = new ArrayList<InfoMessageItem>();
        } else {
            items.clear();
        }
        Criteria criteria = session.createCriteria(InfoMessage.class);
        criteria.addOrder(Order.desc("createdDate"));
        List<InfoMessage> list = criteria.list();
        for (InfoMessage message : list) {
            InfoMessageItem item = new InfoMessageItem();
            item.setIdOfMessage(message.getIdOfInfoMessage());
            item.setCreatedDate(message.getCreatedDate());
            item.setType(message.getMtype().toString());
            item.setHeader(message.getHeader());
            item.setContent(message.getContent());
            item.setAuthor(message.getUser().getUserName());
            items.add(item);
        }
    }

    public List<InfoMessageItem> getItems() {
        return items;
    }

    public void setItems(List<InfoMessageItem> items) {
        this.items = items;
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

    public InfoMessageItem getItemToShow() {
        return itemToShow;
    }

    public void setItemToShow(InfoMessageItem itemToShow) {
        this.itemToShow = itemToShow;
    }

    public static class InfoMessageItem {
        private Long idOfMessage;
        private Date createdDate;
        private String type;
        private String header;
        private String author;
        private String content;
        private List<OrgDeliveryInfo> details;

        public Long getIdOfMessage() {
            return idOfMessage;
        }

        public void setIdOfMessage(Long idOfMessage) {
            this.idOfMessage = idOfMessage;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<OrgDeliveryInfo> getDetails() {
            return DAOReadonlyService.getInstance().getInfoMessageDetails(idOfMessage);
        }

        public void setDetails(List<OrgDeliveryInfo> details) {
            this.details = details;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


}