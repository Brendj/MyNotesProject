/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table
public class CodeMSP {
    @Id
    @Column(name = "idofcode")
    private Long idOfCode;

    @Column(name = "code")
    private Integer code;

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
