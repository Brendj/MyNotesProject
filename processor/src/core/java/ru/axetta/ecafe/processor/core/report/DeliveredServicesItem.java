package ru.axetta.ecafe.processor.core.report;


import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 08.05.13
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
public class DeliveredServicesItem {

    private String level1;
    private String level2;
    private String level3;
    private String level4;
    private Long price;
    private Integer count;
    private Long summary;
    private String officialname;
    private String orgnum;
    private String address;

    public String getLevel1() {
        return level1;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public String getLevel4() {
        return level4;
    }

    public void setLevel4(String level4) {
        this.level4 = level4;
    }

    public String getLevel3() {
        return level3;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price.longValue();
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getOfficialname() {
        return officialname;
    }

    public void setOfficialname(String officialname) {
        this.officialname = officialname;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getSummary() {
        return summary;
    }

    public void setSummary(Long summary) {
        this.summary = summary;
    }

    public String getOrgnum() {
        return orgnum;
    }

    public void setOrgnum(String orgnum) {
        this.orgnum = orgnum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}