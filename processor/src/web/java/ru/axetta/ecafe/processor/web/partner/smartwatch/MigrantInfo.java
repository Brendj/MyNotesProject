/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.persistence.Org;


public class MigrantInfo {
    private String shortNameVisit;
    private String shortAddressVisit;
    private String section;
    private Long visitStartDate;
    private Long visitEndDate;

    public MigrantInfo(Migrant migrant){
        this.visitStartDate = migrant.getVisitStartDate().getTime();
        this.visitEndDate = migrant.getVisitEndDate().getTime();
        this.section = migrant.getSection();

        if(migrant.getOrgVisit() != null){
            Org orgVisit = migrant.getOrgVisit();
            this.shortAddressVisit = orgVisit.getShortAddress();
            this.shortNameVisit = orgVisit.getShortName();
        }
    }

    public String getShortNameVisit() {
        return shortNameVisit;
    }

    public void setShortNameVisit(String shortNameVisit) {
        this.shortNameVisit = shortNameVisit;
    }

    public String getShortAddressVisit() {
        return shortAddressVisit;
    }

    public void setShortAddressVisit(String shortAddressVisit) {
        this.shortAddressVisit = shortAddressVisit;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Long getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(Long visitStartDate) {
        this.visitStartDate = visitStartDate;
    }

    public Long getVisitEndDate() {
        return visitEndDate;
    }

    public void setVisitEndDate(Long visitEndDate) {
        this.visitEndDate = visitEndDate;
    }
}
