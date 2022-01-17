package ru.axetta.ecafe.processor.web.ui.guardianservice;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CGCardItem implements Comparable {
    private Long idOfCard;
    private Long idOfClient;
    private Long cardLastUpdate;

    public CGCardItem(Long idOfCard, Long idOfClient, Long cardLastUpdate) {
        this.idOfCard = idOfCard;
        this.idOfClient = idOfClient;
        this.cardLastUpdate = cardLastUpdate;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CGCardItem)) {
            return 1;
        }
        CGCardItem item = (CGCardItem) o;
        if (this.cardLastUpdate == null && item.getCardLastUpdate() == null) return 0;
        if (this.cardLastUpdate != null && item.getCardLastUpdate() == null) return 1;
        if (this.cardLastUpdate == null && item.getCardLastUpdate() != null) return -1;
        return cardLastUpdate.compareTo(item.getCardLastUpdate());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CGCardItem)) {
            return false;
        }
        CGCardItem item = (CGCardItem) o;
        return idOfClient.equals(item.getIdOfClient()) && equalsByCard(item);
    }

    private boolean equalsByCard(CGCardItem item) {
        if (this.idOfCard == null && item.getIdOfCard() == null) return true;
        if (this.idOfCard != null && item.getIdOfCard() == null) return false;
        if (this.idOfCard == null && item.getIdOfCard() != null) return false;
        return idOfCard.equals(item.getIdOfCard());
    }

    public Long getCardLastUpdate() {
        return cardLastUpdate;
    }

    public void setCardLastUpdate(Long cardLastUpdate) {
        this.cardLastUpdate = cardLastUpdate;
    }
}
