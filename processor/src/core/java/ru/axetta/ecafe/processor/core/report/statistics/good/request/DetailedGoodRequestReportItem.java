package ru.axetta.ecafe.processor.core.report.statistics.good.request;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.05.13
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
public class DetailedGoodRequestReportItem {

    private final String number;
    private final Long idOfSupplier;
    private final String supplierName;
    private final Long idOfEducation;
    private final String educationNumber;
    private final String educationName;
    private final List<Commodity> commodities;
    private final Date doneDate;
    private final Date lastCreate;
    private final Date lastUpdate;

    public DetailedGoodRequestReportItem(String number, Long idOfSupplier, String supplierName, Long idOfEducation,
            String educationNumber, String educationName, List<Commodity> commodities, Date doneDate, Date lastCreate,
            Date lastUpdate) {
        this.number = number;
        this.idOfSupplier = idOfSupplier;
        this.supplierName = supplierName;
        this.idOfEducation = idOfEducation;
        this.educationNumber = educationNumber;
        this.educationName = educationName;
        this.commodities = commodities;
        this.doneDate = doneDate;
        this.lastCreate = lastCreate;
        this.lastUpdate = lastUpdate;
    }

    public DetailedGoodRequestReportItem(String number, Long idOfSupplier, String supplierName, Long idOfEducation,
            String educationName, List<Commodity> commodities, RequestItem requestItem) {
        this.number = number;
        this.idOfSupplier = idOfSupplier;
        this.supplierName = supplierName;
        this.idOfEducation = idOfEducation;
        this.educationNumber = Org.extractOrgNumberFromName(educationName);
        this.educationName = educationName;
        this.commodities = commodities;
        this.doneDate = requestItem.doneDate;
        this.lastCreate = requestItem.lastCreate;
        this.lastUpdate = requestItem.lastUpdate;
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

    public Date getLastCreate() {
        return lastCreate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
}
