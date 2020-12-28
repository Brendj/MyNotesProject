/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Objects;

public class CodeMspAgeTypeGroup {
    private Long idOfCodeMspAgeTypeGroup;
    private String ageTypeGroup;
    private CodeMSP codeMSP;

    public Long getIdOfCodeMspAgeTypeGroup() {
        return idOfCodeMspAgeTypeGroup;
    }

    public void setIdOfCodeMspAgeTypeGroup(Long idOfCodeMspAgeTypeGroup) {
        this.idOfCodeMspAgeTypeGroup = idOfCodeMspAgeTypeGroup;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodeMspAgeTypeGroup group = (CodeMspAgeTypeGroup) o;
        return Objects.equals(ageTypeGroup, group.ageTypeGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ageTypeGroup);
    }
}
