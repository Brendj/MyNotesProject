/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku.dataflow;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;

import org.codehaus.jackson.annotate.JsonProperty;

public class Organization {

    private String name;
    private String address;
    private String district;
    @JsonProperty(value = "organization_type")
    private String organizationType;
    @JsonProperty(value = "supplier_id")
    private Long idOfContragent;
    @JsonProperty(value = "supplier_name")
    private String contragentName;

    public Organization(String name, String address, String district, Integer organizationType, Long idOfContragent,
            String contragentName) {
        this.name = name;
        this.address = address;
        this.district = district;
        this.organizationType = OrganizationType.getCodeTypeByCode(organizationType);
        this.idOfContragent = idOfContragent;
        this.contragentName = contragentName;
    }

    public Organization(Org org) {
        this(org.getShortNameInfoService(), org.getShortAddress(), org.getDistrict(), org.getType().getCode(),
                org.getDefaultSupplier().getIdOfContragent(), org.getDefaultSupplier().getContragentName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    public void setIdOfContragent(Long idOfContragent) {
        this.idOfContragent = idOfContragent;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }
}
