/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.card.CardFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by anvarov on 27.07.2017.
 */
@Component
@Scope(value = "session")
public class CardOperationListPage extends BasicWorkspacePage implements ClientSelectListPage.CompleteHandler {

    protected Date startDate;

    private Calendar localCalendar;

    private CardFilter cardFilter = new CardFilter();

    private final List<ClientSelectListPage.Item> clientList = new ArrayList<ClientSelectListPage.Item>();

    private final ClientFilter clientFilter = new ClientFilter();

    protected String filterClient = "Не выбрано";

    private Boolean showOperationsAllPeriod = false;

    public String getPageFilename() {
        return "cardoperator/card_list";
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Calendar getLocalCalendar() {
        return localCalendar;
    }

    public void setLocalCalendar(Calendar localCalendar) {
        this.localCalendar = localCalendar;
    }

    public CardFilter getCardFilter() {
        return cardFilter;
    }

    public void setCardFilter(CardFilter cardFilter) {
        this.cardFilter = cardFilter;
    }

    public List<ClientSelectListPage.Item> getClientList() {
        return clientList;
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    public String getFilterClient() {
        return filterClient;
    }

    public void setFilterClient(String filterClient) {
        this.filterClient = filterClient;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.cardFilter.completeOrgSelection(session, idOfOrg);
    }

    public CardOperationListPage() {
        super();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = runtimeContext
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));
        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();
        localCalendar.setTime(this.startDate);

        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
    }

    @Override
    public void completeClientSelection(Session session, List<ClientSelectListPage.Item> items) throws Exception {
        Client cl = null;
        if (items != null) {
            getClientList().clear();
            for (ClientSelectListPage.Item item : items) {
                getClientList().add(item);
            }
        }
        filterClient = getStringClientList();
    }

    public String getStringClientList() {
        List<String> val = new ArrayList<String>();
        for (ClientSelectListPage.Item item : getClientList()) {
            val.add(item.getCaption());
        }
        if (val.isEmpty()) {
            return "";
        } else {
            return val.toString();
        }
    }

    public Boolean getShowOperationsAllPeriod() {
        return showOperationsAllPeriod;
    }

    public Object updateCardOperationsListPage() {
        return null;
    }

    public Object clearCardOperationsListPageFilter() {
        return null;
    }

    public static class Item {

        private String shortNameInfoService;
        private Long cardNo;
        private String personName;
        private String status;
        private String date;

        public Item(Org org, Card card, Client client, CardFilter cardFilter) {
            this.shortNameInfoService = org.getShortNameInfoService();
            this.cardNo = card.getCardNo();
            this.personName = client.getPerson().getFullName();
            this.status = cardFilter.getStatus();
            this.date = CalendarUtils.formatToDateShortUnderscoreFormat(card.getCreateTime());
        }

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }

        public void setShortNameInfoService(String shortNameInfoService) {
            this.shortNameInfoService = shortNameInfoService;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public void setCardNo(Long cardNo) {
            this.cardNo = cardNo;
        }

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
