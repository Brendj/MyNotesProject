package ru.axetta.ecafe.processor.web.ui.report.online.items.good.request;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.05.13
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
public class AggregateGoodRequestReportItem {

    private final String number;
    private final Long idOfSupplier;
    private final String supplierName;
    private final Long idOfEducation;
    private final String educationNumber;
    private final String educationName;
    private final List<Commodity> commodities;
    private final Date doneDate;

    public AggregateGoodRequestReportItem(String number, Long idOfSupplier, String supplierName, Long idOfEducation,
            String educationNumber, String educationName, List<Commodity> commodities, Date doneDate) {
        this.number = number;
        this.idOfSupplier = idOfSupplier;
        this.supplierName = supplierName;
        this.idOfEducation = idOfEducation;
        this.educationNumber = educationNumber;
        this.educationName = educationName;
        this.commodities = commodities;
        this.doneDate = doneDate;
    }

    public String getNumber() {
        return number;
    }

    public Long getIdOfSupplier() {
        return idOfSupplier;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public Long getIdOfEducation() {
        return idOfEducation;
    }

    public String getEducationNumber() {
        return educationNumber;
    }

    public String getEducationName() {
        return educationName;
    }

    public List<Commodity> getCommodities() {
        return commodities;
    }

    public Integer getCommoditiesCounts() {
        return commodities.size()+1;
    }

    public Date getDoneDate() {
        return doneDate;
    }

}
