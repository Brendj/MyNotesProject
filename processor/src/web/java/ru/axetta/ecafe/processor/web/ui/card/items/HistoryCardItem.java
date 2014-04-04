package ru.axetta.ecafe.processor.web.ui.card.items;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 */
public class HistoryCardItem {

    private Long idOfHistoryCard;
    private Date upDateTime;
    private final ClientItem formerOwner;
    private final ClientItem newOwner;
    private String informationAboutCard;

    public HistoryCardItem() {
        this.formerOwner = new ClientItem();
        this.newOwner = new ClientItem();
    }

    public HistoryCardItem(Long idOfHistoryCard, Date upDateTime, ClientItem formerOwner, ClientItem newOwner,
            String informationAboutCard) {
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

    public ClientItem getNewOwner() {
        return newOwner;
    }

    public String getInformationAboutCard() {
        return informationAboutCard;
    }

    public void setInformationAboutCard(String informationAboutCard) {
        this.informationAboutCard = informationAboutCard;
    }
}
