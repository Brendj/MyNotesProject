package ru.axetta.ecafe.processor.web.ui.card.items;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.HistoryCard;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 */
public class HistoryCardItem {

    private Long idOfHistoryCard;
    private Date upDateTime;
    private ClientItem formerOwner;
    private ClientItem newOwner;
    private String informationAboutCard;

    public HistoryCardItem(Long idOfHistoryCard, Date upDateTime, ClientItem formerOwner,
            ClientItem newOwner, String informationAboutCard) {
        this.idOfHistoryCard = idOfHistoryCard;
        this.upDateTime = upDateTime;
        this.formerOwner = formerOwner;
        this.newOwner = newOwner;
        this.informationAboutCard = informationAboutCard;
    }

    public Long getIdOfHistoryCard() {
        return idOfHistoryCard;
    }

    public void setIdOfHistoryCard(Long idOfHistoryCard) {
        this.idOfHistoryCard = idOfHistoryCard;
    }

    public Date getUpDateTime() {
        return upDateTime;
    }

    public void setUpDateTime(Date upDateTime) {
        this.upDateTime = upDateTime;
    }

    public ClientItem getFormerOwner() {
        return formerOwner;
    }

    public void setFormerOwner(ClientItem formerOwner) {
        this.formerOwner = formerOwner;
    }

    public ClientItem getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(ClientItem newOwner) {
        this.newOwner = newOwner;
    }

    public String getInformationAboutCard() {
        return informationAboutCard;
    }

    public void setInformationAboutCard(String informationAboutCard) {
        this.informationAboutCard = informationAboutCard;
    }
}
