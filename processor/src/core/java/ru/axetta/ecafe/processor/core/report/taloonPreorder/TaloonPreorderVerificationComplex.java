/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonPreorder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by o.petrova on 13.12.2019.
 */

public class TaloonPreorderVerificationComplex {
    private Date taloonDate;
    private Long complexId;
    private String complexName;
    private final List<TaloonPreorderVerificationDetail> details = new ArrayList<>();
    private TaloonPreorderVerificationItem item;

    public TaloonPreorderVerificationComplex() {
    }

    public Date getTaloonDate() {
        return taloonDate;
    }

    public void setTaloonDate(Date taloonDate) {
        this.taloonDate = taloonDate;
    }

    public List<TaloonPreorderVerificationDetail> getDetails() {
        return details;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public boolean isTaloonDateEmpty() {
        return taloonDate == null;
    }

    public TaloonPreorderVerificationItem getItem() {
        return item;
    }

    public void setItem(TaloonPreorderVerificationItem item) {
        this.item = item;
    }

    public int getDetailsSize() {
        return details.size();
    }
}
