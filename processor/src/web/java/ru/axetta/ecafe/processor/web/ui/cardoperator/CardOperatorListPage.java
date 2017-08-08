/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anvarov on 27.07.2017.
 */
public class CardOperatorListPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

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

    public static class Item {

        private String shortNameInfoService;
        private Long contractId;
        private Long cardNo;
        private Long cardPrintedNo;
        private String personName;
        private Integer state;
        private Date date;

        public Item(Org org, Card card, String personName) {
            this.shortNameInfoService = org.getShortNameInfoService();
            this.contractId = card.getClient().getContractId();
            this.cardNo = card.getCardNo();
            this.cardPrintedNo = card.getCardPrintedNo();
            this.personName = personName;
            this.state = card.getState();
            this.date = card.getCreateTime();
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

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!cardOperatorFilter.isEmpty()) {
            List cards = cardOperatorFilter.retrieveCards(session);
            for (Object object : cards) {
                Card card = (Card) object;
                Org org = card.getOrg();
                String personName = "";
                if (card.getClient() != null) {
                    Person person = DAOService.getInstance().getPersonByClient(card.getClient());
                    personName = person.getFullName();
                }
                items.add(new Item(org, card, personName));
            }
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

    public void saveToFile() throws Exception {

       /* try {
            FileWriter writer = new FileWriter("\\DIAMOND-PC\\one.csv", false);
            // запись всей строки
            String text = String.valueOf(action);
            writer.write(text);
            // запись по символам
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }*/

       String fn = actionContractId + "-" + CalendarUtils.dateShortToStringFullYear(dateAction) + ".csv";

        String pathToFile = "\\\\cardprinterforschools\\jobs";
        File file = new File(pathToFile);
        file.mkdirs();

        File fileNew = new File(pathToFile + "/" + fn);

        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileNew));
            // пишем данные
            bw.write(String.valueOf(actionContractId) + ";" + personNameAction + ";" + String.valueOf(action) + ";" + String.valueOf(actionContractId));
            // закрываем поток
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
