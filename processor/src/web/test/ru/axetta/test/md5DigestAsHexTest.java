/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.test;

import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.utils.CryptoUtils;

public class md5DigestAsHexTest extends TestCase {
    public void testMd5Digest(){
        System.out.println(CryptoUtils.MD5((""+System.currentTimeMillis())));
    }

}
