package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 */

public class HistoryCard {

    /**
     * Номер истории карты
     */
    private Long idOfHistoryCard;

    /**
     * Карта
     */
    private Card card;

    /**
     * Дата и время
     */
    private Date upDatetime;

    /**
     * Прежний владелец
     */
    private Client formerOwner;

    /**
     * Новый владелец
     */
    private Client newOwner;

    /**
     * Информация об откреплении и прикреплении карты
     */
    private String informationAboutCard;

    /**
     * Информация о Операторе по картам
     */
    private User user;

    /**
     * Транзакция в случае покупки карты
     */
    private AccountTransaction transaction;

    public HistoryCard() {

    }

    public HistoryCard(Card card, Date upDatetime, Client formerOwner, Client newOwner, String informationAboutCard, User user) {
        this.card = card;
        this.upDatetime = upDatetime;
        this.formerOwner = formerOwner;
        this.newOwner = newOwner;
        this.informationAboutCard = informationAboutCard;
        this.user = user;
    }

    public Long getIdOfHistoryCard() {
        return idOfHistoryCard;
    }

    public void setIdOfHistoryCard(Long idOfHistoryCard) {
        this.idOfHistoryCard = idOfHistoryCard;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card idOfCard) {
        this.card = idOfCard;
    }

    public Date getUpDatetime() {
        return upDatetime;
    }

    public void setUpDatetime(Date upDatetime) {
        this.upDatetime = upDatetime;
    }

    public Client getFormerOwner() {
        return formerOwner;
    }

    public void setFormerOwner(Client formerOwner) {
        this.formerOwner = formerOwner;
    }

    public Client getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(Client newOwner) {
        this.newOwner = newOwner;
    }

    public String getInformationAboutCard() {
        return informationAboutCard;
    }

    public void setInformationAboutCard(String informationAboutCard) {
        this.informationAboutCard = informationAboutCard;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(AccountTransaction transaction) {
        this.transaction = transaction;
    }
}
