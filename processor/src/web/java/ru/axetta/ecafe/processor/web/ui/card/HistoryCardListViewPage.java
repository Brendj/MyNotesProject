package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.HistoryCard;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 */

public class HistoryCardListViewPage {

    public static class Item {

        private long formerOwnerIdOfClient;
        private long formerOwnerContractId;
        private long newOwnerIdOfClient;
        private long newOwnerContractId;

        private Date upDateTime;
        private String informationAboutCard;

        public Item(HistoryCard historyCard) {
            this.upDateTime = historyCard.getUpDatetime();
            this.formerOwnerIdOfClient = historyCard.getFormerOwner().getIdOfClient();
            this.formerOwnerContractId = historyCard.getFormerOwner().getContractId();
            this.newOwnerIdOfClient = historyCard.getNewOwner().getIdOfClient();
            this.newOwnerContractId = historyCard.getNewOwner().getContractId();
            this.informationAboutCard = historyCard.getInformationAboutCard();
        }

        public long getFormerOwnerIdOfClient() {
            return formerOwnerIdOfClient;
        }

        public void setFormerOwnerIdOfClient(long formerOwnerIdOfClient) {
            this.formerOwnerIdOfClient = formerOwnerIdOfClient;
        }

        public long getFormerOwnerContractId() {
            return formerOwnerContractId;
        }

        public void setFormerOwnerContractId(long formerOwnerContractId) {
            this.formerOwnerContractId = formerOwnerContractId;
        }

        public long getNewOwnerIdOfClient() {
            return newOwnerIdOfClient;
        }

        public void setNewOwnerIdOfClient(long newOwnerIdOfClient) {
            this.newOwnerIdOfClient = newOwnerIdOfClient;
        }

        public long getNewOwnerContractId() {
            return newOwnerContractId;
        }

        public void setNewOwnerContractId(long newOwnerContractId) {
            this.newOwnerContractId = newOwnerContractId;
        }

        public Date getUpDateTime() {
            return upDateTime;
        }

        public void setUpDateTime(Date upDateTime) {
            this.upDateTime = upDateTime;
        }

        public String getInformationAboutCard() {
            return informationAboutCard;
        }

        public void setInformationAboutCard(String informationAboutCard) {
            this.informationAboutCard = informationAboutCard;
        }
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public void fill(Card card) throws Exception {
        List<Item> items = new LinkedList<Item>();
        Set<HistoryCard> historyCards = card.getHistoryCards();
        for (HistoryCard historyCard : historyCards) {
            items.add(new Item(historyCard));
        }
        this.items = items;
    }

}
