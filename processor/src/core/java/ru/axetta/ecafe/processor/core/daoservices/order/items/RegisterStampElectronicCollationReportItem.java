/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order.items;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.07.16
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStampElectronicCollationReportItem implements Comparable<RegisterStampElectronicCollationReportItem> {

    public RegisterStampElectronicCollationReportItem() {

    }

    @Override
    public int compareTo(RegisterStampElectronicCollationReportItem o) {
        return this.dateTime.compareTo(o.getDateTime());
    }

    public static class RegisterStampReportData {

        private List<RegisterStampElectronicCollationReportItem> headerList = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> list153 = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> list37 = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> list14 = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> list511 = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> listTotal37 = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> listTotal511 = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> listTotalAll = new ArrayList<RegisterStampElectronicCollationReportItem>();

        private List<RegisterStampElectronicCollationReportItem> list153Header = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> list37Header = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> list14Header = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> list511Header = new ArrayList<RegisterStampElectronicCollationReportItem>();
        private List<RegisterStampElectronicCollationReportItem> listTotalAllHeader = new ArrayList<RegisterStampElectronicCollationReportItem>();

        public List<RegisterStampElectronicCollationReportItem> getHeaderList() {
            return headerList;
        }

        public void setHeaderList(List<RegisterStampElectronicCollationReportItem> headerList) {
            this.headerList = headerList;
        }

        public List<RegisterStampElectronicCollationReportItem> getList153() {
            return list153;
        }

        public void setList153(List<RegisterStampElectronicCollationReportItem> list153) {
            this.list153 = list153;
        }

        public List<RegisterStampElectronicCollationReportItem> getList37() {
            return list37;
        }

        public void setList37(List<RegisterStampElectronicCollationReportItem> list37) {
            this.list37 = list37;
        }

        public List<RegisterStampElectronicCollationReportItem> getList14() {
            return list14;
        }

        public void setList14(List<RegisterStampElectronicCollationReportItem> list14) {
            this.list14 = list14;
        }

        public List<RegisterStampElectronicCollationReportItem> getList511() {
            return list511;
        }

        public void setList511(List<RegisterStampElectronicCollationReportItem> list511) {
            this.list511 = list511;
        }

        public List<RegisterStampElectronicCollationReportItem> getListTotal37() {
            return listTotal37;
        }

        public void setListTotal37(List<RegisterStampElectronicCollationReportItem> listTotal37) {
            this.listTotal37 = listTotal37;
        }

        public List<RegisterStampElectronicCollationReportItem> getListTotal511() {
            return listTotal511;
        }

        public void setListTotal511(List<RegisterStampElectronicCollationReportItem> listTotal511) {
            this.listTotal511 = listTotal511;
        }

        public List<RegisterStampElectronicCollationReportItem> getListTotalAll() {
            return listTotalAll;
        }

        public void setListTotalAll(List<RegisterStampElectronicCollationReportItem> listTotalAll) {
            this.listTotalAll = listTotalAll;
        }

        public List<RegisterStampElectronicCollationReportItem> getList153Header() {
            return list153Header;
        }

        public void setList153Header(List<RegisterStampElectronicCollationReportItem> list153Header) {
            this.list153Header = list153Header;
        }

        public List<RegisterStampElectronicCollationReportItem> getList37Header() {
            return list37Header;
        }

        public void setList37Header(List<RegisterStampElectronicCollationReportItem> list37Header) {
            this.list37Header = list37Header;
        }

        public List<RegisterStampElectronicCollationReportItem> getList14Header() {
            return list14Header;
        }

        public void setList14Header(List<RegisterStampElectronicCollationReportItem> list14Header) {
            this.list14Header = list14Header;
        }

        public List<RegisterStampElectronicCollationReportItem> getList511Header() {
            return list511Header;
        }

        public void setList511Header(List<RegisterStampElectronicCollationReportItem> list511Header) {
            this.list511Header = list511Header;
        }

        public List<RegisterStampElectronicCollationReportItem> getListTotalAllHeader() {
            return listTotalAllHeader;
        }

        public void setListTotalAllHeader(List<RegisterStampElectronicCollationReportItem> listTotalAllHeader) {
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

    public RegisterStampElectronicCollationReportItem(Long qty, String date, Date dateTime, String level1, String level2, String level3, String level4) {
        this(qty, date, null, dateTime, level1, level2, level3, level4);
    }

    public RegisterStampElectronicCollationReportItem(Long qty, String date, String number, Date dateTime, String level1, String level2, String level3, String level4) {
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.level4 = level4;
        this.qty = qty;
        this.date = date;
        this.number = number;
        this.dateTime = dateTime;
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

    public String getCaption() {
        return caption;
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

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
