/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.test;

import junit.framework.TestCase;

import org.apache.commons.lang3.StringUtils;

public class StringFuzzy extends TestCase {

    public void testComapre() {
        System.out.println(StringUtils.getLevenshteinDistance("ХодинаАллаВасильевна", "ХодинаАннаВасильевна"));
        System.out.println(StringUtils.getLevenshteinDistance("ХодинаАллаВасильевна", "ЛевыйЧувакАбсолютно"));
        System.out.println(StringUtils.getLevenshteinDistance("ЧерниенкоАртемВитальевич", "ЧерниченкоАртемВитальевич"));
        System.out.println(StringUtils.getLevenshteinDistance("МамедоваЖалеКеримкызы", "МамедоваЖалеКеримовна"));
    }
}
