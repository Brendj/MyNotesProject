/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 31.03.2017.
 */
public class InfoMessageDetail {
    private CompositeIdOfInfoMessageDetail compositeIdOfInfoMessageDetail;
    private Date sendDate;

    public InfoMessageDetail() {

    }

    public InfoMessageDetail(CompositeIdOfInfoMessageDetail compositeIdOfInfoMessageDetail) {
        this.compositeIdOfInfoMessageDetail = compositeIdOfInfoMessageDetail;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public CompositeIdOfInfoMessageDetail getCompositeIdOfInfoMessageDetail() {
        return compositeIdOfInfoMessageDetail;
    }

    public void setCompositeIdOfInfoMessageDetail(CompositeIdOfInfoMessageDetail compositeIdOfInfoMessageDetail) {
        this.compositeIdOfInfoMessageDetail = compositeIdOfInfoMessageDetail;
    }

}
