/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.03.2010
 * Time: 10:26:56
 * To change this template use File | Settings | File Templates.
 */
public class CardPrintedNoFormat {

    private CardPrintedNoFormat() {

    }

    public static String format(long contractId) {
        NumberFormat decimalFormat = new DecimalFormat("0000000000000000");
        return decimalFormat.format(contractId);
    }

    public static long parse(String contractId) throws Exception {
        return Long.parseLong(contractId);
    }

}