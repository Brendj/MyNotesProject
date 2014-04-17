package ru.axetta.ecafe.processor.core.daoservices.order.items;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 30.04.13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStampReportItem {
    private String level1;
    private String level2;
    private String level3;
    private String level4;
    private Long qty;
    private String date;
    private String number;

    public RegisterStampReportItem(GoodItem goodItem, Long qty, String date) {
        this(goodItem, qty, date, null);
    }

    public RegisterStampReportItem(GoodItem goodItem, Long qty, String date, String number) {
        this.level1 = goodItem.getPathPart1();
        this.level2 = goodItem.getPathPart2();
        this.level3 = goodItem.getPathPart3();
        this.level4 = goodItem.getPathPart4();
        this.qty = qty;
        this.date = date;
        this.number = number;
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
}
