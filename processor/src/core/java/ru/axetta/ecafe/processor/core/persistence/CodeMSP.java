/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Objects;

public class CodeMSP {
    private Long idOfCode;
    private Integer code;
    private String description;

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
}
