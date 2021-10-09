/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_code_msp")
public class CodeMSP {
    @Id
    @Column(name = "idofcode")
    private Long idOfCode;

    @Column(name = "code")
    private Integer code;

    @ManyToOne
    @JoinColumn(name = "idofcategorydiscount")
    private CategoryDiscount categoryDiscount;

    @OneToMany(mappedBy = "codeMSP", fetch = FetchType.EAGER)
    private Set<CodeMspAgeTypeGroup> ageTypeGroupList = new HashSet<>();

    public Set<CodeMspAgeTypeGroup> getAgeTypeGroupList() {
        return ageTypeGroupList;
    }

    public void setAgeTypeGroupList(Set<CodeMspAgeTypeGroup> ageTypeGroupList) {
        this.ageTypeGroupList = ageTypeGroupList;
    }

    public CategoryDiscount getCategoryDiscount() {
        return categoryDiscount;
    }

    public void setCategoryDiscount(CategoryDiscount categoryDiscount) {
        this.categoryDiscount = categoryDiscount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodeMSP codeMSP = (CodeMSP) o;
        return Objects.equals(idOfCode, codeMSP.idOfCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfCode);
    }
}