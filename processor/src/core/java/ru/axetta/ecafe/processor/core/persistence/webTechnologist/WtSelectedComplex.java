/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

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
