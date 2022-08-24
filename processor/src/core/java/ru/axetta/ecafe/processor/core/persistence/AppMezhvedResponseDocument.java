package ru.axetta.ecafe.processor.core.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.BenefitDocument;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppMezhvedResponseDocument {
    private Long idofmezhvedresponse;
    private String requestId;
    private Long benefit_category_id;
    private String name;
    private String series;
    private String number;
    private Date issue_date;
    private String issuer;
    private AppMezhvedResponseDocDirection type;
    private Date createdate;

    private final Logger log = LoggerFactory.getLogger(AppMezhvedResponseDocument.class);

    private static final ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); }
    };

    public AppMezhvedResponseDocument(BenefitDocument benefitDocument, String requestId, AppMezhvedResponseDocDirection type) {
        this.requestId = requestId;
        if (benefitDocument.getBenefit_category_id() != null)
            this.benefit_category_id = Long.valueOf(benefitDocument.getBenefit_category_id());
        this.name = benefitDocument.getName();
        this.series = benefitDocument.getSeries();
        this.number = benefitDocument.getNumber();
        if (benefitDocument.getIssue_date() != null) {
            try {
                this.issue_date = format.get().parse(benefitDocument.getIssue_date());
            } catch (ParseException e) {
                log.error("Error parse issue_date value = " + benefitDocument.getIssue_date(), e);
            }
        }
        this.issuer = benefitDocument.getIssuer();
        this.type = type;
        this.createdate = new Date();
    }

    public AppMezhvedResponseDocument() {

    }

    public Long getIdofmezhvedresponse() {
        return idofmezhvedresponse;
    }

    public void setIdofmezhvedresponse(Long idofmezhvedresponse) {
        this.idofmezhvedresponse = idofmezhvedresponse;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getBenefit_category_id() {
        return benefit_category_id;
    }

    public void setBenefit_category_id(Long benefit_category_id) {
        this.benefit_category_id = benefit_category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(Date issue_date) {
        this.issue_date = issue_date;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public AppMezhvedResponseDocDirection getType() {
        return type;
    }

    public void setType(AppMezhvedResponseDocDirection type) {
        this.type = type;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }
}
