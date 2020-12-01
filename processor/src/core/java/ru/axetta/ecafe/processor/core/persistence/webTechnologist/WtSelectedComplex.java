/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import java.util.Objects;

public class WtSelectedComplex {
    WtComplex wtComplex;
    private boolean isChecked;

    public WtSelectedComplex() {
        wtComplex = new WtComplex();
        isChecked = false;
    }

    public WtSelectedComplex(WtComplex wtComplex) {
        this.wtComplex = wtComplex;
        this.isChecked = false;
    }

    public WtSelectedComplex(WtComplex wtComplex, boolean isChecked) {
        this.wtComplex = wtComplex;
        this.isChecked = isChecked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtSelectedComplex that = (WtSelectedComplex) o;
        return isChecked == that.isChecked && wtComplex.equals(that.wtComplex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wtComplex, isChecked);
    }

    public WtComplex getWtComplex() {
        return wtComplex;
    }

    public void setWtComplex(WtComplex wtComplex) {
        this.wtComplex = wtComplex;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSupplierName() {
        return DAOService.getInstance().getSupplierName(wtComplex);
    }

    public Long getIdOfSupplier() {
        return DAOService.getInstance().getIdOfSupplier(wtComplex);
    }
}
