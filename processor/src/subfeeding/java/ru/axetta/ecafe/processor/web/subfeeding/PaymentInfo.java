/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Payment;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.06.14
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class PaymentInfo implements Serializable {
    private Date time;
    private Long sum;
    private String origin;

    public PaymentInfo() {}

    PaymentInfo(Payment payment) {
        this.time = payment.getTime().toGregorianCalendar().getTime();
        this.sum = payment.getSum();
        this.origin = payment.getOrigin();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
