/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import java.util.Comparator;

/**
 * Created by i.semenov on 19.03.2018.
 */
public class PreorderDateComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        if (s1.length() != 10 || s2.length() != 10) {
            return s1.compareTo(s2);
        }
        return (s1.substring(6, 10) + s1.substring(3,5) + s1.substring(0,2)).compareTo(s2.substring(6, 10) + s2.substring(3,5) + s2.substring(0,2));
    }
}
