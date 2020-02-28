/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

public class WtSelectedComplex {
    WtComplex wtComplex;
    private boolean isChecked;

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
}
