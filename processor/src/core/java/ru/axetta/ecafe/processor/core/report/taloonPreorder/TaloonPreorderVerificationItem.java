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

    private int detailsSize;
    private boolean allowedSetFirstFlag;
    private boolean allowedClearFirstFlag;

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

    public void setPpState() {
        TaloonPPStatesEnum ppState = TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED;
        for (TaloonPreorderVerificationComplex complex : this.getComplexes()) {
            for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                if (!detail.isSummaryDay() && !detail.isPpStateConfirmed()) {
                    this.ppState = TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED;
                    return;
                }
            }
        }
        this.ppState = ppState;

    }

    public void confirmPpState() {
        changePpState(TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED);
    }

    public void deselectPpState() {
        changePpState(TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED);
    }

    // меняем статусы только там, где это разрешено
    public void changePpState(TaloonPPStatesEnum ppState) {
        for (TaloonPreorderVerificationComplex complex : this.getComplexes()) {
            for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                if (!detail.isSummaryDay()) {
                    // согласование - только для записей, у которых нет отказа
                    if (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED && detail.isAllowedSetFirstFlag()) {
                        if (!detail.isPpStateCanceled()) {
                            detail.setPpState(ppState);
                            this.ppState = ppState;
                        }
                    }
                    if (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED && detail.isAllowedClearFirstFlag()) {
                        detail.setPpState(ppState);
                        this.ppState = ppState;
                    }
                }
            }
        }
    }

    // разрешаем, если хотя бы у одной записи разрешен
    public boolean isAllowedSetFirstFlag() {
        for (TaloonPreorderVerificationComplex complex : this.getComplexes()) {
            for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                if (!detail.isSummaryDay()) {
                    if (detail.isAllowedClearFirstFlag()) {
                        return allowedSetFirstFlag = true;
                    }
                }
            }
        }
        return allowedSetFirstFlag = false;
    }

    public boolean isAllowedClearFirstFlag() {
        for (TaloonPreorderVerificationComplex complex : this.getComplexes()) {
            for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                if (!detail.isSummaryDay()) {
                    if (detail.isAllowedClearFirstFlag()) {
                        return allowedClearFirstFlag = true;
                    }
                }
            }
        }
        return allowedClearFirstFlag = false;
    }

    public List<TaloonPreorderVerificationComplex> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<TaloonPreorderVerificationComplex> complexes) {
        this.complexes = complexes;
    }

    public int getDetailsSize() {
        int size = 0;
        for (TaloonPreorderVerificationComplex complex : complexes) {
            size += complex.getDetails().size();
        }
        return detailsSize = size;
    }

    public int getRowInItem(int complexId, int rowId) {
        int complexesSize = 0;
        for (int i = 0; i < complexId; i++) {
            complexesSize += this.complexes.get(i).getDetails().size();
        }
        return complexesSize + rowId;
    }

    public String getPpStateToTurnOnFirst() {
        return MAKE_CONFIRM;
    }

    public String getPpStateToTurnOnSecond() {
        return MAKE_CANCEL;
    }

    public String getPpStateToClear() {
        return MAKE_CLEAR;
    }

}
