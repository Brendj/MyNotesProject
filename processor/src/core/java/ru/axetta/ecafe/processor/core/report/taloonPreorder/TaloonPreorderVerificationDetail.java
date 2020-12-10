/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.taloonPreorder;

import ru.axetta.ecafe.processor.core.persistence.TaloonISPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

public class TaloonPreorderVerificationDetail {
    private String guid;
    private Long idOfOrg;
    private Long idOfOrgCreated;
    private Date taloonDate;
    private Long complexId;
    private String complexName;
    private String goodsName;
    private String goodsGuid;
    private Long price;
    private Integer requestedQty;
    private Long requestedSum;
    private Integer soldQty;
    private Long soldSum;
    private Integer shippedQty;
    private Long shippedSum;
    private Integer reservedQty;
    private Long reservedSum;
    private Integer blockedQty;
    private Long blockedSum;
    private TaloonISPPStatesEnum isppState;
    private TaloonPPStatesEnum ppState;
    private String remarks;
    private String comments;
    private boolean summaryDay;
    private TaloonPreorderVerificationComplex complex;
    private boolean changedData;

    public TaloonPreorderVerificationDetail() {
    }

    public TaloonPreorderVerificationDetail(String guid, Long idOfOrg, Long idOfOrgCreated, Date taloonDate,
            Long complexId, String complexName, String goodsName, String goodsGuid, Long price, Integer requestedQty,
            Long requestedSum, Integer soldQty, Long soldSum, Integer shippedQty, Long shippedSum, Integer reservedQty,
            Long reservedSum, Integer blockedQty, Long blockedSum, TaloonISPPStatesEnum isppState,
            TaloonPPStatesEnum ppState, String remarks, String comments,
            boolean summaryDay) {
        this.guid = guid;
        this.idOfOrg = idOfOrg;
        this.idOfOrgCreated = idOfOrgCreated;
        this.taloonDate = taloonDate;
        this.complexId = complexId;
        this.complexName = complexName;
        this.goodsName = goodsName;
        this.goodsGuid = goodsGuid;
        this.price = price;
        this.requestedQty = requestedQty;
        this.requestedSum = requestedSum;
        this.soldQty = soldQty;
        this.soldSum = soldSum;
        this.shippedQty = shippedQty;
        this.shippedSum = shippedSum;
        this.reservedQty = reservedQty;
        this.reservedSum = reservedSum;
        this.blockedQty = blockedQty;
        this.blockedSum = blockedSum;
        this.isppState = isppState;
        this.ppState = ppState;
        this.remarks = remarks;
        this.comments = comments;
        this.summaryDay = summaryDay;
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

    public boolean isAllowedSetFirstFlag() {
        int period = getPeriod();
        if (period == 1) {
            if (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_CONFIRMED
                    && ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED) {
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

    public boolean isAllowedClearFirstFlag() {
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

    public boolean isAllowedSetSecondFlag() {
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

    public boolean isAllowedClearSecondFlag() {
        int period = getPeriod();
        if (period == 1) {
            if (isppState == TaloonISPPStatesEnum.TALOON_ISPP_STATE_CONFIRMED
                    && ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED) {
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

    public boolean isEnableEditShippedQty() {
        if (summaryDay) {
            return false;
        }
        Date currentDate = new Date();
        Date firstMonthDate = CalendarUtils.getFirstDayOfNextMonth(taloonDate);
        Date redDate = CalendarUtils.addDays(firstMonthDate, 5);
        if (currentDate.after(redDate)) {
            return false;
        }
        if (ppState == TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED
                || ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean taloonDateIsEmpty() {
        return taloonDate == null;
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

    public boolean isNeedFillShippedQty() {
        return ppState == TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED && (shippedQty == null || shippedQty == 0);
    }

    public Boolean getRemarksEmpty() {
        return StringUtils.isEmpty(remarks);
    }

    public void addQtyAndGet(TaloonPreorderVerificationDetail arg) {
        this.setRequestedQty(getIntSum(this.getRequestedQty(), arg.getRequestedQty()));
        this.setRequestedSum(getLongSum(this.getRequestedSum(), arg.getRequestedSum()));
        this.setSoldQty(getIntSum(this.getSoldQty(), arg.getSoldQty()));
        this.setSoldSum(getLongSum(this.getSoldSum(), arg.getSoldSum()));
        this.setShippedQty(getIntSum(this.getShippedQty(), arg.getShippedQty()));
        this.setShippedSum(getLongSum(this.getShippedSum(), arg.getShippedSum()));
        this.setReservedQty(getIntSum(this.getReservedQty(), arg.getReservedQty()));
        this.setReservedSum(getLongSum(this.getReservedSum(), arg.getReservedSum()));
        this.setBlockedQty(getIntSum(this.getBlockedQty(), arg.getBlockedQty()));
        this.setBlockedSum(getLongSum(this.getBlockedSum(), arg.getBlockedSum()));
    }

    private Integer getIntSum(Integer value1, Integer value2) {
        return (value1 == null ? 0 : value1) + (value2 == null ? 0 : value2);
    }

    private Long getLongSum(Long value1, Long value2) {
        return (value1 == null ? 0L : value1) + (value2 == null ? 0L : value2);
    }

    public void deselectPpState() {
        changePpState(TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED);
    }

    public void cancelPpState() {
        this.setShippedQty(null);
        this.setShippedSum(0L);
        changePpState(TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED);
    }

    public void confirmPpState() {
        changePpState(TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED);
    }

    private void changePpState(TaloonPPStatesEnum ppState) {
        this.ppState = ppState;
    }

    public boolean isEmptyTotal() {
        return isSummaryDay() && complexId == null && complexName == null && goodsGuid == null && taloonDate == null;
    }

    public boolean isEmptyShippedQty() {
        return shippedQty == null || shippedQty == 0;
    }

    public boolean isTotal() {
        return isSummaryDay() && complexId != null && taloonDate == null;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getSoldQty() {
        return soldQty;
    }

    public void setSoldQty(Integer soldQty) {
        this.soldQty = soldQty;
    }

    public Integer getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(Integer reservedQty) {
        this.reservedQty = reservedQty;
    }

    public Integer getBlockedQty() {
        return blockedQty;
    }

    public void setBlockedQty(Integer blockedQty) {
        this.blockedQty = blockedQty;
    }

    public Long getRequestedSum() {
        return requestedSum;
    }

    public void setRequestedSum(Long requestedSum) {
        this.requestedSum = requestedSum;
    }

    public Long getSoldSum() {
        return soldSum;
    }

    public void setSoldSum(Long soldSum) {
        this.soldSum = soldSum;
    }

    public Long getShippedSum() {
        return shippedSum;
    }

    public void setShippedSum(Long shippedSum) {
        this.shippedSum = shippedSum;
    }

    public Long getReservedSum() {
        return reservedSum;
    }

    public void setReservedSum(Long reservedSum) {
        this.reservedSum = reservedSum;
    }

    public Long getBlockedSum() {
        return blockedSum;
    }

    public void setBlockedSum(Long blockedSum) {
        this.blockedSum = blockedSum;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public TaloonPreorderVerificationComplex getComplex() {
        return complex;
    }

    public void setComplex(TaloonPreorderVerificationComplex complex) {
        this.complex = complex;
    }

    public Long getIdOfOrgCreated() {
        return idOfOrgCreated;
    }

    public void setIdOfOrgCreated(Long idOfOrgCreated) {
        this.idOfOrgCreated = idOfOrgCreated;
    }

    public Integer getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(Integer requestedQty) {
        this.requestedQty = requestedQty;
    }

    public Integer getShippedQty() {
        return shippedQty;
    }

    public void setShippedQty(Integer shippedQty) {
        this.shippedQty = shippedQty;
    }

    public boolean isChangedData() {
        return changedData;
    }

    public void setChangedData(boolean changedData) {
        this.changedData = changedData;
    }
}
