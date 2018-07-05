/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VersionV2Test {

    @Test
    public void compareVersions() {
        VersionV2 a = new VersionV2("1.1");
        VersionV2 b = new VersionV2("1.1.1");
        assertEquals(-1, a.compareTo(b));   // return -1 (a<b)
        assertEquals(Boolean.FALSE, a.equals(b));    // return false

        VersionV2 a1 = new VersionV2("2.0");
        VersionV2 b1 = new VersionV2("1.9.9");
        assertEquals(1, a1.compareTo(b1));     // return 1 (a>b)
        assertEquals(Boolean.FALSE, a1.equals(b1));     // return false

        VersionV2 a2 = new VersionV2("1.0");
        VersionV2 b2 = new VersionV2("1");
        assertEquals(0, a2.compareTo(b2));     // return 0 (a=b)
        assertEquals(Boolean.TRUE, a2.equals(b2));      // return true

        VersionV2 a3 = new VersionV2("1");
        VersionV2 b3 = null;
        assertEquals(1, a3.compareTo(b3));    // return 1 (a>b)
        assertEquals(Boolean.FALSE, a3.equals(b3));    // return false

        List<VersionV2> versions = new ArrayList<VersionV2>();
        versions.add(new VersionV2("2"));
        versions.add(new VersionV2("1.0.5"));
        versions.add(new VersionV2("1.01.0"));
        versions.add(new VersionV2("1.00.1"));
        System.out.println(Collections.min(versions));
        System.out.println(Collections.max(versions));

        // WARNING
        VersionV2 a4 = new VersionV2("2.06");
        VersionV2 b4 = new VersionV2("2.060");
        assertEquals(Boolean.FALSE, a4.equals(b4));    // return false
    }
}
