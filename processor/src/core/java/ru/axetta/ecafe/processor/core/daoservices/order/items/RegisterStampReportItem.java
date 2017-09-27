package ru.axetta.ecafe.processor.core.daoservices.order.items;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.04.13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStampReportItem implements Comparable<RegisterStampReportItem> {

    public RegisterStampReportItem() {

    }

    @Override
    public int compareTo(RegisterStampReportItem o) {
        return this.dateTime.compareTo(o.getDateTime());
    }

    public static class RegisterStampReportData {

        private List<RegisterStampReportItem> headerList = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> list153 = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> list37 = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> list14 = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> list511 = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> listTotal37 = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> listTotal511 = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> listTotalAll = new ArrayList<RegisterStampReportItem>();

        private List<RegisterStampReportItem> list153Header = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> list37Header = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> list14Header = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> list511Header = new ArrayList<RegisterStampReportItem>();
        private List<RegisterStampReportItem> listTotalAllHeader = new ArrayList<RegisterStampReportItem>();

        public List<RegisterStampReportItem> getHeaderList() {
            return headerList;
        }

        public void setHeaderList(List<RegisterStampReportItem> headerList) {
            this.headerList = headerList;
        }

        public List<RegisterStampReportItem> getList153() {
            return list153;
        }

        public void setList153(List<RegisterStampReportItem> list153) {
            this.list153 = list153;
        }

        public List<RegisterStampReportItem> getList37() {
            return list37;
        }

        public void setList37(List<RegisterStampReportItem> list37) {
            this.list37 = list37;
        }

        public List<RegisterStampReportItem> getList14() {
            return list14;
        }

        public void setList14(List<RegisterStampReportItem> list14) {
            this.list14 = list14;
        }

        public List<RegisterStampReportItem> getList511() {
            return list511;
        }

        public void setList511(List<RegisterStampReportItem> list511) {
            this.list511 = list511;
        }

        public List<RegisterStampReportItem> getListTotal37() {
            return listTotal37;
        }

        public void setListTotal37(List<RegisterStampReportItem> listTotal37) {
            this.listTotal37 = listTotal37;
        }

        public List<RegisterStampReportItem> getListTotal511() {
            return listTotal511;
        }

        public void setListTotal511(List<RegisterStampReportItem> listTotal511) {
            this.listTotal511 = listTotal511;
        }

        public List<RegisterStampReportItem> getListTotalAll() {
            return listTotalAll;
        }

        public void setListTotalAll(List<RegisterStampReportItem> listTotalAll) {
            this.listTotalAll = listTotalAll;
        }

        public List<RegisterStampReportItem> getList153Header() {
            return list153Header;
        }

        public void setList153Header(List<RegisterStampReportItem> list153Header) {
            this.list153Header = list153Header;
        }

        public List<RegisterStampReportItem> getList37Header() {
            return list37Header;
        }

        public void setList37Header(List<RegisterStampReportItem> list37Header) {
            this.list37Header = list37Header;
        }

        public List<RegisterStampReportItem> getList14Header() {
            return list14Header;
        }

        public void setList14Header(List<RegisterStampReportItem> list14Header) {
            this.list14Header = list14Header;
        }

        public List<RegisterStampReportItem> getList511Header() {
            return list511Header;
        }

        public void setList511Header(List<RegisterStampReportItem> list511Header) {
            this.list511Header = list511Header;
        }

        public List<RegisterStampReportItem> getListTotalAllHeader() {
            return listTotalAllHeader;
        }

        public void setListTotalAllHeader(List<RegisterStampReportItem> listTotalAllHeader) {
            this.listTotalAllHeader = listTotalAllHeader;
        }
    }

    private String caption = "Сведения о реализованных Рационах питания, шт.";
    private String level1;
    private String level2;
    private String level3;
    private String level4;
    private Long qty;
    private String date;
    private String number;
    private Date dateTime;
    private Integer orderType;
    private String datePlusNumber;

    public RegisterStampReportItem(GoodItem goodItem, Long qty, String date, Date dateTime) {
        this(goodItem, qty, date, null, dateTime);
    }

    public RegisterStampReportItem(GoodItem goodItem, Long qty, String date, String number, Date dateTime) {
        this.level1 = goodItem.getPathPart1();
        this.level2 = goodItem.getPathPart2();
        this.level3 = goodItem.getPathPart3();
        this.level4 = goodItem.getPathPart4();
        this.qty = qty;
        this.date = date;
        this.number = number;
        this.dateTime = dateTime;
        this.orderType = goodItem.getOrderType(); //.ordinal();
        if (number != null && !number.equals("")) {
            this.datePlusNumber = date + " № " + number;
        } else {
            this.datePlusNumber = date;
        }
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

    public String getLevel3() {
        return level3;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    public String getLevel4() {
        return level4;
    }

    public void setLevel4(String level4) {
        this.level4 = level4;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getDatePlusNumber() {
        return datePlusNumber;
    }

    public void setDatePlusNumber(String datePlusNumber) {
        this.datePlusNumber = datePlusNumber;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
