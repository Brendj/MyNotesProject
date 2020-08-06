/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.models;

import javax.persistence.*;

@Entity
@Table(name = "cf_history_card")
public class HistoryCard {
    @Id
    @Column(name = "idofhistorycard")
    private Long id;

    @Column(name = "updatetime", nullable = false)
    private Long updateTime;

    @Column(name = "informationaboutcard", length = 1024, nullable = false)
    private String infoAboutCard;

    @Column(name = "formerowner", nullable = false)
    private Long formerOwner;

    @Column(name = "newowner", nullable = false)
    private Long newOwner;

    @ManyToOne
    @JoinColumn(name = "idofcard", nullable = false)
    private Card card;
}
