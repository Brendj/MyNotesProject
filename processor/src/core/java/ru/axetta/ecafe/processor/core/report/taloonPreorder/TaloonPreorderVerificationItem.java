/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonPreorder;

import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by o.petrova on 09.12.2019.
 */
public class TaloonPreorderVerificationItem {

    public static final String MAKE_CONFIRM = "Согласие";
    public static final String MAKE_CANCEL = "Отказ";
    public static final String MAKE_CLEAR = "Очистить";
    public static final String DAY_FORMAT = "dd.MM.yyyy";

    private Date taloonDate;
    private TaloonPPStatesEnum ppState;
    private List<TaloonPreorderVerificationComplex> complexes = new ArrayList<>();

    public TaloonPreorderVerificationItem() {
    }

    public Date getTaloonDate() {
        return taloonDate;
    }

    public void setTaloonDate(Date taloonDate) {
        this.taloonDate = taloonDate;
    }

    public TaloonPPStatesEnum getPpState() {
        return ppState;
    }

    public void setPpState(TaloonPPStatesEnum ppState) {
        this.ppState = ppState;
    }

    public List<TaloonPreorderVerificationComplex> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<TaloonPreorderVerificationComplex> complexes) {
        this.complexes = complexes;
    }

    public boolean taloonDateEmpty() {
        return taloonDate == null;
    }

}
