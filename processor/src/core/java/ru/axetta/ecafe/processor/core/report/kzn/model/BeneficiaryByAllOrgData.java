/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.kzn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User: regal
 * Date: 09.03.15
 * Time: 2:22
 */
public class BeneficiaryByAllOrgData {
    private List<BeneficiaryByAllOrgItem> items;

    public BeneficiaryByAllOrgData() {
        items = new ArrayList<BeneficiaryByAllOrgItem>();
    }

    public BeneficiaryByAllOrgData(List<BeneficiaryByAllOrgItem> items) {
        this.items = items;
    }

    public List<BeneficiaryByAllOrgItem> getItems() {
        return items;
    }

    public void setItems(List<BeneficiaryByAllOrgItem> items) {
        this.items = items;
    }
}
