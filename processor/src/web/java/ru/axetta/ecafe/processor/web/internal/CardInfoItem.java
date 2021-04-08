/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import java.util.Objects;

public class CardInfoItem {
    private Long processingCardId; //идентификатор карты на процессинге. Клиент этот идентификатор знает
    private Long cardNo; //короткий физ. номер номер. В принципе, большой необходимости в нем нет, больше для проверки. В таблице cf_cards, в записи с идентификатором ProcessingCardId поле CardNo должно совпадать с этим значением. Если не совпадает - сообщаем об ошибке.
    private Long longCardId; //длинный физ. номер. Если на процессинге эта колонка уже заполнена и значения не совпадают - все равно перезаписываем значением, пришедшим от клиента, поскольку эта информация от клиента более достоверная (физически считана с реальной карты)
    private Boolean isLongId; //флаг длиного идентификатора. Если TRUE, то длинный идентификатор содержит более 4 значащих (ненулевых) байт

    public Long getProcessingCardId() {
        return processingCardId;
    }

    public void setProcessingCardId(Long processingCardId) {
        this.processingCardId = processingCardId;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public Long getLongCardId() {
        return longCardId;
    }

    public void setLongCardId(Long longCardId) {
        this.longCardId = longCardId;
    }

    public Boolean getIsLongId() {
        return isLongId;
    }

    public void setIsLongId(Boolean isLongId) {
        this.isLongId = isLongId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CardInfoItem that = (CardInfoItem) o;
        return Objects.equals(processingCardId, that.processingCardId) && Objects.equals(cardNo, that.cardNo) && Objects
                .equals(longCardId, that.longCardId) && Objects.equals(isLongId, that.isLongId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processingCardId, cardNo, longCardId, isLongId);
    }
}
