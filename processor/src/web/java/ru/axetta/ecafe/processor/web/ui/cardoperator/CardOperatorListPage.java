/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.card.items.ClientItem;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anvarov on 27.07.2017.
 */
public class CardOperatorListPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler, ClientSelectPage.CompleteHandler {

    private static final String configString = "ecafe.processor.ws.message.server.path";
    private ClientItem client;

    private String serverPath = RuntimeContext.getInstance().getConfigProperties()
            .getProperty(configString, "");

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    private CardOperatorFilter cardOperatorFilter = new CardOperatorFilter();

    public CardOperatorFilter getCardOperatorFilter() {
        return cardOperatorFilter;
    }

    public void setCardOperatorFilter(CardOperatorFilter cardOperatorFilter) {
        this.cardOperatorFilter = cardOperatorFilter;
    }

    public String getPageFilename() {
        return "cardoperator/card_list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.cardOperatorFilter.completeOrgSelection(session, idOfOrg);
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            this.cardOperatorFilter.setClient(new ClientItem(client));
        }
    }

    public static class Item {

        private String shortNameInfoService;
        private Long contractId;
        private Long cardNo;
        private Long cardPrintedNo;
        private String personName;
        private String operation;
        private Date date;

        public Item(Org org, HistoryCard history, String personName) {
            Card card = history.getCard();
            this.shortNameInfoService = org.getShortNameInfoService();
            if (history.getNewOwner() != null) {
                this.contractId = history.getNewOwner().getContractId();
            }
            this.cardNo = card.getCardNo();
            this.cardPrintedNo = card.getCardPrintedNo();
            this.personName = ContractIdFormat.format(contractId) + " (): " + personName;
            this.operation = history.getInformationAboutCard();
            this.date = history.getUpDatetime();
        }

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }

        public void setShortNameInfoService(String shortNameInfoService) {
            this.shortNameInfoService = shortNameInfoService;
        }

        public Long getContractId() {
            return contractId;
        }

        public void setContractId(Long contractId) {
            this.contractId = contractId;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public void setCardNo(Long cardNo) {
            this.cardNo = cardNo;
        }

        public Long getCardPrintedNo() {
            return cardPrintedNo;
        }

        public void setCardPrintedNo(Long cardPrintedNo) {
            this.cardPrintedNo = cardPrintedNo;
        }

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        List<HistoryCard> history = cardOperatorFilter.retrieveCards(session);
        for (HistoryCard hist : history) {
            Card card = hist.getCard();
            Org org = card.getOrg();
            String personName = "";
            if (card.getClient() != null) {
                Person person = DAOReadonlyService.getInstance().getPersonByClient(card.getClient());
                personName = person.getFullName();
            }
            items.add(new Item(org, hist, personName));
        }
        this.items = items;
    }

    public Long actionContractId;

    public Long getActionContractId() {
        return actionContractId;
    }

    public void setActionContractId(Long actionContractId) {
        this.actionContractId = actionContractId;
    }

    public Long action;

    public Long getAction() {
        return action;
    }

    public void setAction(Long action) {
        this.action = action;
    }

    public Date dateAction;

    public Date getDateAction() {
        return dateAction;
    }

    public void setDateAction(Date dateAction) {
        this.dateAction = dateAction;
    }

    public Long actionCardPrintedNo;

    public Long getActionCardPrintedNo() {
        return actionCardPrintedNo;
    }

    public void setActionCardPrintedNo(Long actionCardPrintedNo) {
        this.actionCardPrintedNo = actionCardPrintedNo;
    }

    public String personNameAction;

    public String getPersonNameAction() {
        return personNameAction;
    }

    public void setPersonNameAction(String personNameAction) {
        this.personNameAction = personNameAction;
    }

   /* public void saveToFile() throws Exception {

       String fn = actionContractId + "-" + CalendarUtils.dateShortToStringFullYear(dateAction) + ".csv";

        String pathToFile = "\\\\cardprinterforschools\\jobs";
        File file = new File(pathToFile);
        file.mkdirs();

        File fileNew = new File(pathToFile + "/" + fn);

        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileNew));
            // пишем данные
            bw.write(String.valueOf(actionContractId) + ";" + personNameAction + ";" + String.valueOf(action) + ";" + String.valueOf(actionCardPrintedNo));
            // закрываем поток
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public String getMessage() {
        String message = String.valueOf(actionContractId) + ";" + personNameAction + ";" + String.valueOf(action) + ";" + String.valueOf(actionCardPrintedNo);
        return message;
    }
}
