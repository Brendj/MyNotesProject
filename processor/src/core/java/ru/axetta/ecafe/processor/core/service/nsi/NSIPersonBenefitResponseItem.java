/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NSIPersonBenefitResponseItem extends NSIResponseItem {
    @JsonProperty("person")
    private NSIResponseItem person;
    @JsonProperty("benefit-form")
    private NSIBenefitDictionaryResponseItem benefit;
    @JsonProperty("dszn-date-begin")
    private String dsznDateBegin;
    @JsonProperty("dszn-date-end")
    private String dsznDateEnd;
    @JsonProperty("benefit-confirmed")
    private Boolean benefitConfirmed;

    public NSIPersonBenefitResponseItem(Long version, String id, String entityId, String createdAt, String updatedAt,
            NSIResponseItem person, NSIBenefitDictionaryResponseItem benefit, String dsznDateBegin, String dsznDateEnd,
            Boolean benefitConfirmed) {
        super(version, id, entityId, createdAt, updatedAt);
        this.person = person;
        this.benefit = benefit;
        this.dsznDateBegin = dsznDateBegin;
        this.dsznDateEnd = dsznDateEnd;
        this.benefitConfirmed = benefitConfirmed;
    }

    public NSIPersonBenefitResponseItem() {

    }

    public NSIResponseItem getPerson() {
        return person;
    }

    public void setPerson(NSIResponseItem person) {
        this.person = person;
    }

    public NSIBenefitDictionaryResponseItem getBenefit() {
        return benefit;
    }

    public void setBenefit(NSIBenefitDictionaryResponseItem benefit) {
        this.benefit = benefit;
    }

    public String getDsznDateBegin() {
        return dsznDateBegin;
    }

    public void setDsznDateBegin(String dsznDateBegin) {
        this.dsznDateBegin = dsznDateBegin;
    }

    public String getDsznDateEnd() {
        return dsznDateEnd;
    }

    public void setDsznDateEnd(String dsznDateEnd) {
        this.dsznDateEnd = dsznDateEnd;
    }

    public Boolean getBenefitConfirmed() {
        return benefitConfirmed;
    }

    public void setBenefitConfirmed(Boolean benefitConfirmed) {
        this.benefitConfirmed = benefitConfirmed;
    }

    public Date getDsznDateBeginAsDate() {
        return javax.xml.bind.DatatypeConverter.parseDateTime(dsznDateBegin).getTime();
    }

    public Date getDsznDateEndAsDate() {
        return javax.xml.bind.DatatypeConverter.parseDateTime(dsznDateEnd).getTime();
    }
}
