/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

public class WtSelectedComplex extends WtComplex {
    private boolean isChecked;

    public WtSelectedComplex() {
        super();
        isChecked = false;
    }

    public WtSelectedComplex(WtComplex complex) {
        super(complex);
        this.isChecked = false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
