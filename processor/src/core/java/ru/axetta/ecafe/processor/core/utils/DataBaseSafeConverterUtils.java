/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.math.BigInteger;
import java.util.Date;

public class DataBaseSafeConverterUtils {

    public static Long getLongFromBigIntegerOrNull(Object o){
        return o != null ? ((BigInteger)o).longValue() : null;
    }

    public static Date getDateFromBigIntegerOrNull(Object o){
        return o != null ? new Date(((BigInteger)o).longValue()) : null;
    }

}
