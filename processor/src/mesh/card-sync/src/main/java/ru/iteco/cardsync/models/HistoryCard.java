/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_history_card")
public class HistoryCard {
    @GenericGenerator(
            name = "cf_history_card_idofhistorycard",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "cf_history_card_idofhistorycard_seq"),
                    @org.hibernate.annotations.Parameter(name = "INCREMENT", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MINVALUE", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MAXVALUE", value = "2147483647"),
                    @org.hibernate.annotations.Parameter(name = "CACHE", value = "1")
            }
    )

    @Id
    @Column(name = "idofhistorycard")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "cf_history_card_idofhistorycard")
    private Long id;

    @Column(name = "updatetime", nullable = false)
    private Long updateTime;

    @Column(name = "informationaboutcard", length = 1024, nullable = false)
    private String infoAboutCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formerowner", nullable = false)
    private Client oldOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "newowner", nullable = false)
    private Client newOwner;

    @ManyToOne
    @JoinColumn(name = "idofcard", nullable = false)
    private Card card;

    public HistoryCard(){

    }

    public static HistoryCard buildHistoryCard(Card c){
        HistoryCard historyCard = new HistoryCard();
        historyCard.setNewOwner(c.getClient());
        historyCard.setOldOwner(c.getClient());
        historyCard.setCard(c);
        return historyCard;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getInfoAboutCard() {
        return infoAboutCard;
    }

    public void setInfoAboutCard(String infoAboutCard) {
        this.infoAboutCard = infoAboutCard;
    }

    public Client getOldOwner() {
        return oldOwner;
    }

    public void setOldOwner(Client oldOwner) {
        this.oldOwner = oldOwner;
    }

    public Client getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(Client newOwner) {
        this.newOwner = newOwner;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoryCard)) {
            return false;
        }
        HistoryCard that = (HistoryCard) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
