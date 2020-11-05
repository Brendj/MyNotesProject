/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_card_sync")
public class CardSync {
    @GenericGenerator(
            name = "cf_card_sync_idcardsync",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "cf_card_sync_idcardsync_seq"),
                    @org.hibernate.annotations.Parameter(name = "INCREMENT", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MINVALUE", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MAXVALUE", value = "2147483647"),
                    @org.hibernate.annotations.Parameter(name = "CACHE", value = "1")
            }
    )


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cf_card_sync_idcardsync")
    @Column(name = "idcardsync")
    private Long idcardsync;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofcard")
    private Card card;

    @Column(name = "idoforg")
    private Long idoforg;

    @Column(name = "statechange")
    private Long statechange;

    public CardSync() {
        // for Hibernate
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardSync)) {
            return false;
        }
        CardSync card = (CardSync) o;
        return Objects.equals(getIdcardsync(), card.getIdcardsync());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdcardsync());
    }

    public Long getIdcardsync() {
        return idcardsync;
    }

    public void setIdcardsync(Long idcardsync) {
        this.idcardsync = idcardsync;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Long getStatechange() {
        return statechange;
    }

    public void setStatechange(Long statechange) {
        this.statechange = statechange;
    }

    public Long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(Long idoforg) {
        this.idoforg = idoforg;
    }
}
