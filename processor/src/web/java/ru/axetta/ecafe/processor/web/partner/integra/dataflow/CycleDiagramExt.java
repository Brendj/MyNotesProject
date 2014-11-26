package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.14
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CycleDiagramExt")
public class CycleDiagramExt {
    @XmlElement(name = "Monday")
    private String monday;
    @XmlElement(name = "Tuesday")
    private String tuesday;
    @XmlElement(name = "Wednesday")
    private String wednesday;
    @XmlElement(name = "Thursday")
    private String thursday;
    @XmlElement(name = "Friday")
    private String friday;
    @XmlElement(name = "Saturday")
    private String saturday;
    @XmlElement(name = "Sunday")
    private String sunday;
    @XmlElement(name = "DateActivationDiagram")
    @XmlSchemaType(name = "dateTime")
    private Date dateActivationDiagram;

    public CycleDiagramExt(CycleDiagram diagram) {
        this.monday = diagram.getMonday();
        this.tuesday = diagram.getTuesday();
        this.wednesday = diagram.getWednesday();
        this.thursday = diagram.getThursday();
        this.friday = diagram.getFriday();
        this.saturday = diagram.getSaturday();
        this.sunday = diagram.getSunday();
        this.dateActivationDiagram = diagram.getDateActivationDiagram();
    }

    public CycleDiagramExt() {
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public Date getDateActivationDiagram() {
        return dateActivationDiagram;
    }

    public void setDateActivationDiagram(Date dateActivationDiagram) {
        this.dateActivationDiagram = dateActivationDiagram;
    }
}
