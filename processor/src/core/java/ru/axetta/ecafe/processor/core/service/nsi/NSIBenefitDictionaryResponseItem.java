/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NSIBenefitDictionaryResponseItem extends NSIResponseItem {
    @JsonProperty("benefit-form")
    private String benefitForm;
    @JsonProperty("dszn-code")
    private Long dsznCode;

    public NSIBenefitDictionaryResponseItem(Long version, String id, String entityId, String createdAt, String updatedAt,
            String benefitForm, Long dsznCode) {
        super(version, id, entityId, createdAt, updatedAt);
        this.benefitForm = benefitForm;
        this.dsznCode = dsznCode;
    }

    public NSIBenefitDictionaryResponseItem() {

    }

    public String getBenefitForm() {
        return benefitForm;
    }

    public void setBenefitForm(String benefitForm) {
        this.benefitForm = benefitForm;
    }

    public Long getDsznCode() {
        return dsznCode;
    }

    public void setDsznCode(Long dsznCode) {
        this.dsznCode = dsznCode;
    }
}
