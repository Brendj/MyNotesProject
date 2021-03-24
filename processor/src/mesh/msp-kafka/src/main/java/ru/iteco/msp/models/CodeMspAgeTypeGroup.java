/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import javax.persistence.*;

@Entity
@Table(name = "cf_code_msp_agetypegroup")
public class CodeMspAgeTypeGroup {
    @Id
    @Column(name = "idofcodemspagetypegroup")
    private Long idOfCodeMSPAgeTypeGroup;

    @Column(name = "agetypegroup", length = 128)
    private String ageTypeGroup;

    @ManyToOne
    @JoinColumn(name = "idofcode")
    private CodeMSP codeMSP;

    public CodeMspAgeTypeGroup() {
    }

    public Long getIdOfCodeMSPAgeTypeGroup() {
        return idOfCodeMSPAgeTypeGroup;
    }

    public void setIdOfCodeMSPAgeTypeGroup(Long idOfCodeMSPAgeTypeGroup) {
        this.idOfCodeMSPAgeTypeGroup = idOfCodeMSPAgeTypeGroup;
    }

    public String getAgeTypeGroup() {
        return ageTypeGroup;
    }

    public void setAgeTypeGroup(String ageTypeGroup) {
        this.ageTypeGroup = ageTypeGroup;
    }

    public CodeMSP getCodeMSP() {
        return codeMSP;
    }

    public void setCodeMSP(CodeMSP codeMSP) {
        this.codeMSP = codeMSP;
    }
}
