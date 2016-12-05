package ru.axetta.ecafe.processor.core.report;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 08.05.13
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
public class DeliveredServicesItem {

    public static class DeliveredServicesData {
        private List<DeliveredServicesItem> headerList = new ArrayList<DeliveredServicesItem>();
        private List<DeliveredServicesItem> list153 = new ArrayList<DeliveredServicesItem>();
        private List<DeliveredServicesItem> list37 = new ArrayList<DeliveredServicesItem>();
        private List<DeliveredServicesItem> list14 = new ArrayList<DeliveredServicesItem>();
        private List<DeliveredServicesItem> list511 = new ArrayList<DeliveredServicesItem>();
        private List<DeliveredServicesItem> listTotal37 = new ArrayList<DeliveredServicesItem>();
        private List<DeliveredServicesItem> listTotal511 = new ArrayList<DeliveredServicesItem>();
        private List<DeliveredServicesItem> listTotalAll = new ArrayList<DeliveredServicesItem>();

        public List<DeliveredServicesItem> getHeaderList() {
            return headerList;
        }

        public void setHeaderList(List<DeliveredServicesItem> headerList) {
            this.headerList = headerList;
        }

        public List<DeliveredServicesItem> getList153() {
            return list153;
        }

        public void setList153(List<DeliveredServicesItem> list153) {
            this.list153 = list153;
        }

        public List<DeliveredServicesItem> getList37() {
            return list37;
        }

        public void setList37(List<DeliveredServicesItem> list37) {
            this.list37 = list37;
        }

        public List<DeliveredServicesItem> getList14() {
            return list14;
        }

        public void setList14(List<DeliveredServicesItem> list14) {
            this.list14 = list14;
        }

        public List<DeliveredServicesItem> getList511() {
            return list511;
        }

        public void setList511(List<DeliveredServicesItem> list511) {
            this.list511 = list511;
        }

        public List<DeliveredServicesItem> getListTotal37() {
            return listTotal37;
        }

        public void setListTotal37(List<DeliveredServicesItem> listTotal37) {
            this.listTotal37 = listTotal37;
        }

        public List<DeliveredServicesItem> getListTotal511() {
            return listTotal511;
        }

        public void setListTotal511(List<DeliveredServicesItem> listTotal511) {
            this.listTotal511 = listTotal511;
        }

        public List<DeliveredServicesItem> getListTotalAll() {
            return listTotalAll;
        }

        public void setListTotalAll(List<DeliveredServicesItem> listTotalAll) {
            this.listTotalAll = listTotalAll;
        }
    }

    private String level1;
    private String level2;
    private String level3;
    private String nameOfGood;
    private Long price;
    private Integer count;
    private Long summary;
    private String officialname;
    private String orgnum;
    private String address;
    private long idoforg;
    private Long priceWater;
    private Integer countWater;
    private Long summaryWater;
    private Integer countOrg;
    private Date createdDate;

    public DeliveredServicesItem() {
        this.priceWater = 0L;
        this.countWater = 0;
        this.summaryWater = 0L;
    }

    public DeliveredServicesItem(String level1, String level2, String level3, String nameOfGood, Long price,
            Integer count, Long summary, String officialname, String orgnum, String address, long idoforg, Date createdDate) {
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.nameOfGood = nameOfGood;
        this.price = price;
        this.count = count;
        this.summary = summary;
        this.officialname = officialname;
        this.orgnum = orgnum;
        this.address = address;
        this.idoforg = idoforg;
        this.priceWater = 0L;
        this.countWater = 0;
        this.summaryWater = 0L;
        this.createdDate = createdDate;
    }

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

    public String getNameOfGood() {
        return nameOfGood;
    }

    public void setNameOfGood(String nameOfGood) {
        this.nameOfGood = nameOfGood;
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

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    public Long getPriceWater() {
        return priceWater;
    }

    public void setPriceWater(Long priceWater) {
        this.priceWater = priceWater;
    }

    public Integer getCountWater() {
        return countWater;
    }

    public void setCountWater(Integer countWater) {
        this.countWater = countWater;
    }

    public Long getSummaryWater() {
        return summaryWater;
    }

    public void setSummaryWater(Long summaryWater) {
        this.summaryWater = summaryWater;
    }

    public Integer getCountOrg() {
        return countOrg;
    }

    public void setCountOrg(Integer countOrg) {
        this.countOrg = countOrg;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}