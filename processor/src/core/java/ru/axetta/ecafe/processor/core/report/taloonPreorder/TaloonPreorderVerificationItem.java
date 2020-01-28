/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonPreorder;

import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.web.ui.report.online.TaloonPreorderVerificationPage;

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
    private TaloonPreorderVerificationPage page;

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

    public TaloonPreorderVerificationPage getPage() {
        return page;
    }

    public void setPage(TaloonPreorderVerificationPage page) {
        this.page = page;
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
                    if (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED && detail.allowedSetFirstFlag()) {
                        if (!detail.isPpStateCanceled()) {
                            detail.setPpState(ppState);
                            this.ppState = ppState;
                        }
                    }
                    if (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED && detail.allowedClearFirstFlag()) {
                        detail.setPpState(ppState);
                        this.ppState = ppState;
                    }
                }
            }
        }
    }

    // разрешаем, если хотя бы у одной записи разрешен
    public boolean allowedSetFirstFlag() {
        for (TaloonPreorderVerificationComplex complex : this.getComplexes()) {
            for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                if (detail.allowedSetFirstFlag()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean allowedClearFirstFlag() {
        for (TaloonPreorderVerificationComplex complex : this.getComplexes()) {
            for (TaloonPreorderVerificationDetail detail : complex.getDetails()) {
                if (detail.allowedClearFirstFlag()) {
                    return true;
                }
            }
        }
        return false;
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

    public int getDetailsSize() {
        int size = 0;
        for (TaloonPreorderVerificationComplex complex : complexes) {
            size += complex.getDetails().size();
        }
        return size;
    }

    public int getRowInItem(int complexId, int rowId) {
        int complexesSize = 0;
        for (int i = 0; i < complexId; i++) {
            complexesSize += this.complexes.get(i).getDetails().size();
        }
        return complexesSize + rowId;
    }

    public boolean isPpStateNotSelected() {
        return (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED);
    }

    public boolean isPpStateConfirmed() {
        return (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED);
    }

}
