package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.01.14
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesDataOnOrdersAndPaymentItem {

    private String orgShortName;
    private String address;
    private String orgTypeCategory;
    private Integer countActs;
    private Long differentSum;
    private Date currentDate;

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgTypeCategory() {
        return orgTypeCategory;
    }

    public void setOrgTypeCategory(String orgTypeCategory) {
        this.orgTypeCategory = orgTypeCategory;
    }

    public Integer getCountActs() {
        return countActs;
    }

    public void setCountActs(Integer countActs) {
        this.countActs = countActs;
    }

    public Long getDifferentSum() {
        return differentSum;
    }

    public void setDifferentSum(Long differentSum) {
        this.differentSum = differentSum;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
}
