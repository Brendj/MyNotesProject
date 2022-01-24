/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CardOperationListPage extends BasicWorkspacePage {

    private Long idOfCard;
    private Long cardNo;
    private Date startTime;
    private Date endTime;
    private final CardPaymentList cardPaymentList = new CardPaymentList();
    private final CardOrderList cardOrderList = new CardOrderList();
    private final CardSmsList cardSmsList = new CardSmsList();

    public String getPageFilename() {
        return "card/operation_list";
    }

    public CardOperationListPage() {
        Date currDate = new Date();
        startTime = currDate;
        endTime = DateUtils.addDays(currDate, 1);
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public CardPaymentList getCardPaymentListViewer() {
        return cardPaymentList;
    }

    public CardOrderList getCardOrderListViewer() {
        return cardOrderList;
    }

    public CardSmsList getCardSmsList() {
        return cardSmsList;
    }

    public void fill(Session session, Long idOfCard) throws Exception {
        Card card = (Card) session.load(Card.class, idOfCard);
        this.idOfCard = card.getIdOfCard();
        this.cardNo = card.getCardNo();
        this.cardPaymentList.fill(session, card, this.startTime, this.endTime);
        this.cardOrderList.fill(session, card, this.startTime, this.endTime);
        this.cardSmsList.fill(session, card, this.startTime, this.endTime);
        if (this.cardPaymentList.getItemCount() == 0 &&
                this.cardOrderList.getItemCount() == 0 &&
                this.cardSmsList.getItemCount() == 0) {
            this.printMessage("Данных для построения отчёта не найдено");
        }
    }
}