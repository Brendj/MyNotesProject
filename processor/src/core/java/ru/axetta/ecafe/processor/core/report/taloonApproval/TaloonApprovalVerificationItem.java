/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonApproval;

import ru.axetta.ecafe.processor.core.persistence.TaloonISPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 19.07.2016.
 */
public class TaloonApprovalVerificationItem {

    public static final String MAKE_CONFIRM = "Согласие";
    public static final String MAKE_CANCEL = "Отказ";
    public static final String MAKE_CLEAR = "Очистить";
    //public static final String MAKE_CONFIRM_ENTIRE_DAY = "Подтвердить ";
    public static final String DAY_FORMAT = "dd.MM.yyyy";

    private Date taloonDate;
    private final List<TaloonApprovalVerificationItemDetail> details = new ArrayList<TaloonApprovalVerificationItemDetail>();

    public Date getTaloonDate() {
        return taloonDate;
    }

    public void setTaloonDate(Date taloonDate) {
        this.taloonDate = taloonDate;
    }

    public List<TaloonApprovalVerificationItemDetail> getDetails() {
        return details;
    }

    public static class TaloonApprovalVerificationItemDetail {
        private String taloonName;
        private Integer soldedQty;
        private Integer requestedQty;
        private Integer shippedQty;
        private Long price;
        private Long summa;
        private TaloonISPPStatesEnum isppState;
        private TaloonPPStatesEnum ppState;
        private Long idOfOrg;
        private Date taloonDate;
        private boolean summaryDay;
        private String goodsGuid;

        public TaloonApprovalVerificationItemDetail() {}

        public TaloonApprovalVerificationItemDetail(String taloonName, Integer soldedQty, Integer requestedQty,
                Integer shippedQty, Long price, Long summa, TaloonISPPStatesEnum isppState, TaloonPPStatesEnum ppState,
                Long idOfOrg, Date taloonDate, boolean summaryDay, String goodsGuid) {
            this.setTaloonName(taloonName);
            this.setRequestedQty(requestedQty);
            this.setSoldedQty(soldedQty);
            this.setShippedQty(shippedQty);
            this.setPrice(price);
            setSumma(summa);
            this.setIsppState(isppState);
            this.setPpState(ppState);
            this.setIdOfOrg(idOfOrg);
            this.setTaloonDate(taloonDate);
            this.setSummaryDay(summaryDay);
            this.setGoodsGuid(goodsGuid);
        }

        public String getTaloonName() {
            return taloonName;
        }

        public void setTaloonName(String taloonName) {
            this.taloonName = taloonName;
        }

        public Integer getSoldedQty() {
            return soldedQty;
        }

        public void setSoldedQty(Integer soldedQty) {
            this.soldedQty = soldedQty;
        }

        public Integer getRequestedQty() {
            return requestedQty;
        }

        public void setRequestedQty(Integer requestedQty) {
            this.requestedQty = requestedQty;
        }

        public Integer getShippedQty() {
            return shippedQty == null ? 0 : shippedQty;
        }

        public void setShippedQty(Integer shippedQty) {
            this.shippedQty = shippedQty;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        public TaloonISPPStatesEnum getIsppState() {
            return isppState;
        }

        public void setIsppState(TaloonISPPStatesEnum isppState) {
            this.isppState = isppState;
        }

        public TaloonPPStatesEnum getPpState() {
            return ppState;
        }

        public void setPpState(TaloonPPStatesEnum ppState) {
            this.ppState = ppState;
        }

        public Long getSumma() {
            return summa;
        }

        public void setSumma(Long summa) {
            this.summa = summa;
        }

        public boolean isIsppStateConfirmed() {
            return (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_CONFIRMED);
        }

        public boolean isPpStateConfirmed() {
            return (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED);
        }

        public boolean isPpStateCanceled() {
            return (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED);
        }

        public boolean isPpStateNotSelected() {
            return (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED);
        }

        public boolean isPpStateNull() {
            return (ppState == null && taloonDate != null);
        }

        public Boolean needFillShippedQty() {
            return (shippedQty == null || shippedQty == 0);
        }

        public int getPeriod() {
            Date currentDate = new Date();
            Date firstDayOfMonth = CalendarUtils.getFirstDayOfMonth(currentDate);
            Date minDate = CalendarUtils.getFirstDayOfMonth(currentDate);
            Date maxDate = CalendarUtils.addDays(CalendarUtils.getLastDayOfMonth(currentDate), 5);
            int day = CalendarUtils.getDayOfMonth(currentDate);
            if (day <= 5) {
                minDate = CalendarUtils.addMonth(firstDayOfMonth, -1);
            }
            if (taloonDate.before(minDate)) {
                return 1;
            } else if (taloonDate.after(minDate) && taloonDate.before(maxDate)) {
                return 2;
            } else {
                return 3;
            }
        }

        public Boolean allowedSetFirstFlag() {
            int period = getPeriod();
            if (period == 1) {
                if (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_CONFIRMED && ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED) {
                    return false;
                } else {
                    return true;
                }
            }
            if (period == 2 || period == 3) {
                if (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED) {
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        }

        public Boolean allowedClearFirstFlag() {
            int period = getPeriod();
            if (period == 1) {
                return false;
            }
            if (period == 2 || period == 3) {
                if (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED) {
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        }

        public Boolean allowedSetSecondFlag() {
            int period = getPeriod();
            if (period == 1) {
                return false;
            }
            if (period == 2 || period == 3) {
                if (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED) {
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        }

        public Boolean allowedClearSecondFlag() {
            int period = getPeriod();
            if (period == 1) {
                if (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_CONFIRMED && ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED) {
                    return true;
                } else {
                    return false;
                }
            }
            if (period == 2 || period == 3) {
                if (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED) {
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        }

        public Boolean enableEditShippedQty() {
            if (summaryDay) return false;
            Date currentDate = new Date();
            Date firstMonthDate = CalendarUtils.getFirstDayOfNextMonth(taloonDate);
            Date redDate = CalendarUtils.addDays(firstMonthDate, 5);
            if (currentDate.after(redDate)) return false;

            if (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED || ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED) {
                return true;
            } else {
                return false;
            }
        }

        public void performConfirm() {
            if (this.ppState == null) return;
            this.setPpState(TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED);
        }

        public String getPpStateToTurnOnFirst() {
            return MAKE_CONFIRM;
        }

        public String getPpStateForAllDay() {
            DateFormat df = new SimpleDateFormat(DAY_FORMAT);
            return df.format(taloonDate);
        }

        public String getPpStateToTurnOnSecond() {
            return MAKE_CANCEL;
        }

        public String getPpStateToClear() {
            return MAKE_CLEAR;
        }

        public boolean taloonDateIsEmpty() {
            return taloonDate == null;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Date getTaloonDate() {
            return taloonDate;
        }

        public void setTaloonDate(Date taloonDate) {
            this.taloonDate = taloonDate;
        }

        public boolean isSummaryDay() {
            return summaryDay;
        }

        public void setSummaryDay(boolean summaryDay) {
            this.summaryDay = summaryDay;
        }

        public String getGoodsGuid() {
            return goodsGuid;
        }

        public void setGoodsGuid(String goodsGuid) {
            this.goodsGuid = goodsGuid;
        }
    }

}
