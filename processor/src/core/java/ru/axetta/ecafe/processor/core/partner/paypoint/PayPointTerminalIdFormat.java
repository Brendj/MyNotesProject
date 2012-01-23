/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.03.2010
 * Time: 10:26:56
 * To change this template use File | Settings | File Templates.
 */
public class PayPointTerminalIdFormat {

    private NumberFormat decimalFormat;

    public PayPointTerminalIdFormat() {
        decimalFormat = new DecimalFormat("0000000000000000000");
    }

    public String format(long terminalId) {
        return decimalFormat.format(terminalId);
    }


}