/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.03.2010
 * Time: 10:26:56
 * To change this template use File | Settings | File Templates.
 */
public class ContractIdFormat {

    public static final int MAX_LENGTH = 10;
    public static final int MIN_LENGTH = 8;

    private ContractIdFormat() {

    }

    public static String format(long contractId) {
        NumberFormat decimalFormat = new DecimalFormat("##00000000");
        return decimalFormat.format(contractId);
    }

    public static long parse(String contractId) throws Exception {
        return Long.parseLong(contractId);
    }

}
