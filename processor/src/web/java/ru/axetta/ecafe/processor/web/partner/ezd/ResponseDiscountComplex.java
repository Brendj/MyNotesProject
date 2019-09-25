/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import java.util.LinkedList;
import java.util.List;

public class ResponseDiscountComplex extends Result{
    private List<DiscountComplexOrg> org ;


    public ResponseDiscountComplex(){
        this.org  = new LinkedList<DiscountComplexOrg>();
    }


    public List<DiscountComplexOrg> getOrg() {
        return org;
    }

    public void setOrg(List<DiscountComplexOrg> org) {
        this.org = org;
    }
}
