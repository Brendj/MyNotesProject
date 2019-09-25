/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import java.util.LinkedList;
import java.util.List;

public class ResponseDiscountComplex extends Result{
    private List<DiscountComplexItem> days ;


    public ResponseDiscountComplex(){
        this.days  = new LinkedList<DiscountComplexItem>();
    }

    public List<DiscountComplexItem> getDays() {
        return days ;
    }

    public void setDays(List<DiscountComplexItem> days) {
        this.days  = days;
    }
}
