package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.01.14
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */

public class Item {

    private Long idOfOrg;
    private String orgShortName;
    private String address;
    private String orgTypeCategory;
    private Integer countActs;
    private BigDecimal differentSum;
    private Date currentDate;

    public Item() {
    }

    public Item(Long idOfOrg, Date currentDate) {
        this.idOfOrg = idOfOrg;
        this.currentDate = currentDate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

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

    public BigDecimal getDifferentSum() {
        return differentSum;
    }

    public void setDifferentSum(BigDecimal differentSum) {
        this.differentSum = differentSum;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return currentDate.equals(item.currentDate) && idOfOrg.equals(item.idOfOrg);
    }

    @Override
    public int hashCode() {
        int result = idOfOrg.hashCode();
        result = 31 * result + currentDate.hashCode();
        return result;
    }
}
