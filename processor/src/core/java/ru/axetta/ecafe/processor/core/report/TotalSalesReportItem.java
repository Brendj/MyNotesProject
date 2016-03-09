package ru.axetta.ecafe.processor.core.report;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 01.03.16
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public class TotalSalesReportItem {

    public String productTitle;
    public Double sumTotals;


    public TotalSalesReportItem() {
    }

    public TotalSalesReportItem(String productTitle, Double sumTotals) {
        this.productTitle = productTitle;
        this.sumTotals = sumTotals;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Double getSumTotals() {
        return sumTotals;
    }

    public void setSumTotals(Double sumTotals) {
        this.sumTotals = sumTotals;
    }
}
