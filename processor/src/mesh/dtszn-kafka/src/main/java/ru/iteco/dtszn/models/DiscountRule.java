/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import javax.persistence.*;

@Entity
@Table
public class DiscountRule {
    @Id
    @Column(name = "idofrule")
    private Long idOfRule;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "idofcode")
    private CodeMSP codeMSP;

    public Long getIdOfRule() {
        return idOfRule;
    }

    public void setIdOfRule(Long idOfRule) {
        this.idOfRule = idOfRule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CodeMSP getCodeMSP() {
        return codeMSP;
    }

    public void setCodeMSP(CodeMSP codeMSP) {
        this.codeMSP = codeMSP;
    }
}
