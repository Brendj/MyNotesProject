/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.card.CardFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientFilter;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anvarov on 27.07.2017.
 */
@Component
@Scope(value = "session")
public class CardOperationListPage extends BasicWorkspacePage implements ClientSelectListPage.CompleteHandler {

    private CardFilter cardFilter = new CardFilter();

    private final List<ClientSelectListPage.Item> clientList = new ArrayList<ClientSelectListPage.Item>();

    private final ClientFilter clientFilter = new ClientFilter();

    protected String filterClient = "Не выбрано";

    private Boolean showOperationsAllPeriod = false;

    public String getPageFilename() {
        return "cardoperator/card_list";
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
}
