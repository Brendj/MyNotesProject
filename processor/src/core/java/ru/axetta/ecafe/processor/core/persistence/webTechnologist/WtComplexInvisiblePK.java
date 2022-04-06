package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.io.Serializable;
import java.util.Objects;

public class WtComplexInvisiblePK implements Serializable {

    private Long idOfComplex;

    private Long idOfOrg;

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WtComplexInvisiblePK that = (WtComplexInvisiblePK) o;
        return Objects.equals(idOfComplex, that.idOfComplex) && Objects.equals(idOfOrg, that.idOfOrg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfComplex, idOfOrg);
    }
}
