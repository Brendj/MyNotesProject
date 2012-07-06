/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.chronopay;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.12.2009
 * Time: 11:44:57
 * To change this template use File | Settings | File Templates.
 */
public class ChronopayProtocolRequest {
    private final String productId;
    private final String total;
    private final Long orderId;
    private final String name;
    private final String customerId;
    private  final Long transactionId;
    private  final String transactionType;
    private final String sign;
    private final String date;

    public String getCustomerId() {
        return customerId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getSign() {
        return sign;
    }

    public String getProductId() {
        return productId;
    }

    public String getTotal() {
        return total;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public ChronopayProtocolRequest(HttpServletRequest request) throws Exception {
          this.productId=request.getParameter("product_id");
          this.total=request.getParameter("total") ;
          this.orderId=Long.parseLong(request.getParameter("order_id"));
          this.name=request.getParameter("name");
          this.customerId=request.getParameter("customer_id");
          this.transactionId=Long.parseLong(request.getParameter("transaction_id"))  ;
          this.transactionType=request.getParameter("transaction_type") ;
          this.sign=request.getParameter("sign") ;
          this.date=request.getParameter("date");


    }

}
