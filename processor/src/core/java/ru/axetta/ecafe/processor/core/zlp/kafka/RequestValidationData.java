package ru.axetta.ecafe.processor.core.zlp.kafka;

import generated.etp.*;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestValidationData {
    private String childLastName;
    private String childFirstName;
    private String childBirthDate;
    private String childDocumentTypeCode;
    private String childDocumentSeries;
    private String childDocumentNumber;
    private String childDocumentIssueDate;

    private String parentLastName;
    private String parentFirstName;
    private String parentBirthDate;
    private String parentPassportSeries;
    private String parentPassportNumber;
    private String parentPassportIssueDate;

    private Long idOfApplicationForFood;

    public static final String DOC_KIND_SVIDETELSTVO = "20017";
    public static final String DOC_KIND_PASSPORT = "20001";
    public static final String TYPE_CODE_SVIDETELSTVO = "10";
    public static final String TYPE_CODE_PASSPORT = "1";

    public RequestValidationData(CoordinateMessage coordinateMessage, Long idOfApplicationForFood) {
        CoordinateData coordinateData = coordinateMessage.getCoordinateDataMessage();
        RequestServiceForSign requestServiceForSign = coordinateData.getSignService();
        ArrayOfBaseDeclarant contacts = requestServiceForSign.getContacts();
        BaseDeclarant baseDeclarantChild = getBaseDeclarant(contacts, ContactType.CHILD);
        BaseDeclarant baseDeclarantParent = getBaseDeclarant(contacts, ContactType.DECLARANT);
        this.childLastName = ((RequestContact) baseDeclarantChild).getLastName();
        this.childFirstName = ((RequestContact) baseDeclarantChild).getFirstName();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.childBirthDate = df.format(getDate(((RequestContact) baseDeclarantChild).getBirthDate()));
        ServiceDocument serviceDocument = baseDeclarantChild.getDocuments().getServiceDocument().stream()
                .filter(sd -> sd.getDocKind().getCode().equals(DOC_KIND_SVIDETELSTVO) || sd.getDocKind().getCode().equals(DOC_KIND_PASSPORT))
                .findFirst().orElse(null);
        this.childDocumentNumber = serviceDocument.getDocNumber();
        this.childDocumentSeries = serviceDocument.getDocSerie();
        this.childDocumentIssueDate = df.format(getDate(serviceDocument.getDocDate().getValue()));
        this.childDocumentTypeCode = serviceDocument.getDocKind().equals(DOC_KIND_SVIDETELSTVO) ? TYPE_CODE_SVIDETELSTVO : TYPE_CODE_PASSPORT;

        this.parentLastName = ((RequestContact) baseDeclarantParent).getLastName();
        this.parentFirstName = ((RequestContact) baseDeclarantParent).getFirstName();
        this.parentBirthDate = df.format(getDate(((RequestContact) baseDeclarantParent).getBirthDate()));
        ServiceDocument serviceDocument2 = baseDeclarantParent.getDocuments().getServiceDocument().stream()
                .filter(sd -> sd.getDocKind().getCode().equals(DOC_KIND_PASSPORT)).findFirst().orElse(null);
        this.parentPassportNumber = serviceDocument2.getDocNumber();
        this.parentPassportSeries = serviceDocument2.getDocSerie();
        this.parentPassportIssueDate = df.format(getDate(serviceDocument2.getDocDate().getValue()));

        this.idOfApplicationForFood = idOfApplicationForFood;
    }

    private Date getDate(XMLGregorianCalendar cal) {
        return cal.toGregorianCalendar().getTime();
    }

    private BaseDeclarant getBaseDeclarant(ArrayOfBaseDeclarant contacts, ContactType contactType) {
        BaseDeclarant bd = null;
        for (BaseDeclarant baseDeclarant : contacts.getBaseDeclarant()) {
            if (baseDeclarant.getType().equals(contactType)) {
                bd = baseDeclarant;
                break;
            }
        }
        return bd;
    }

    public String getChildLastName() {
        return childLastName;
    }

    public void setChildLastName(String childLastName) {
        this.childLastName = childLastName;
    }

    public String getChildFirstName() {
        return childFirstName;
    }

    public void setChildFirstName(String childFirstName) {
        this.childFirstName = childFirstName;
    }

    public String getChildBirthDate() {
        return childBirthDate;
    }

    public void setChildBirthDate(String childBirthDate) {
        this.childBirthDate = childBirthDate;
    }

    public String getChildDocumentTypeCode() {
        return childDocumentTypeCode;
    }

    public void setChildDocumentTypeCode(String childDocumentTypeCode) {
        this.childDocumentTypeCode = childDocumentTypeCode;
    }

    public String getChildDocumentSeries() {
        return childDocumentSeries;
    }

    public void setChildDocumentSeries(String childDocumentSeries) {
        this.childDocumentSeries = childDocumentSeries;
    }

    public String getChildDocumentNumber() {
        return childDocumentNumber;
    }

    public void setChildDocumentNumber(String childDocumentNumber) {
        this.childDocumentNumber = childDocumentNumber;
    }

    public String getChildDocumentIssueDate() {
        return childDocumentIssueDate;
    }

    public void setChildDocumentIssueDate(String childDocumentIssueDate) {
        this.childDocumentIssueDate = childDocumentIssueDate;
    }

    public String getParentLastName() {
        return parentLastName;
    }

    public void setParentLastName(String parentLastName) {
        this.parentLastName = parentLastName;
    }

    public String getParentFirstName() {
        return parentFirstName;
    }

    public void setParentFirstName(String parentFirstName) {
        this.parentFirstName = parentFirstName;
    }

    public String getParentBirthDate() {
        return parentBirthDate;
    }

    public void setParentBirthDate(String parentBirthDate) {
        this.parentBirthDate = parentBirthDate;
    }

    public String getParentPassportSeries() {
        return parentPassportSeries;
    }

    public void setParentPassportSeries(String parentPassportSeries) {
        this.parentPassportSeries = parentPassportSeries;
    }

    public String getParentPassportNumber() {
        return parentPassportNumber;
    }

    public void setParentPassportNumber(String parentPassportNumber) {
        this.parentPassportNumber = parentPassportNumber;
    }

    public String getParentPassportIssueDate() {
        return parentPassportIssueDate;
    }

    public void setParentPassportIssueDate(String parentPassportIssueDate) {
        this.parentPassportIssueDate = parentPassportIssueDate;
    }

    public Long getIdOfApplicationForFood() {
        return idOfApplicationForFood;
    }

    public void setIdOfApplicationForFood(Long idOfApplicationForFood) {
        this.idOfApplicationForFood = idOfApplicationForFood;
    }
}
