/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CodeMSP {
    private Long idOfCode;
    private Integer code;
    private String description;
    private CategoryDiscount categoryDiscount;
    private Set<CodeMspAgeTypeGroup> codeMspAgeTypeGroupSet = new HashSet<>();

    public Set<CodeMspAgeTypeGroup> getCodeMspAgeTypeGroupSet() {
        return codeMspAgeTypeGroupSet;
    }

    public void setCodeMspAgeTypeGroupSet(Set<CodeMspAgeTypeGroup> codeMspAgeTypeGroupSet) {
        this.codeMspAgeTypeGroupSet = codeMspAgeTypeGroupSet;
    }

    public Long getIdOfCode() {
        return idOfCode;
    }

    public void setIdOfCode(Long idOfCode) {
        this.idOfCode = idOfCode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryDiscount getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(CategoryDiscount categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodeMSP codeMSP = (CodeMSP) o;
        return Objects.equals(idOfCode, codeMSP.idOfCode) && Objects.equals(code, codeMSP.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfCode, code);
    }

    public boolean containsAgeTypeGroup(String type) {
        CodeMspAgeTypeGroup group = new CodeMspAgeTypeGroup();
        group.setAgeTypeGroup(type);
        return this.getCodeMspAgeTypeGroupSet().contains(group);
    }
}
