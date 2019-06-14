/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;

public class PropertyUtils {

    public static Long getIdOfESZOrg() throws Exception {
        String strNSIOrg = RuntimeContext
                .getInstance().getConfigProperties().getProperty("ecafe.processor.esz.migrants.eszOrg", "");
        if (StringUtils.isEmpty(strNSIOrg)) throw new Exception("Не найдена организация ЕСЗ");
        return Long.parseLong(strNSIOrg);
    }
}
