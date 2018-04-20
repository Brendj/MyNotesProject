/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import ru.axetta.ecafe.processor.core.persistence.MenuDetail;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ReqMenuDetailTest {

    @Test
    public void areMenudetailsEqual() {
        MenuDetail menuDetail = new MenuDetail(null, "path", "detailName", 2, 3, 0);
        menuDetail.setPrice(100L);
        menuDetail.setGroupName("group");
        menuDetail.setMenuDetailOutput("output");
        menuDetail.setShortName("shortName");
        menuDetail.setProtein(1d);
        menuDetail.setFat(2d);
        menuDetail.setCarbohydrates(3d);
        menuDetail.setCalories(4d);
        SyncRequest.ReqMenu.Item.ReqMenuDetail reqMenuDetail = new SyncRequest.ReqMenu.Item.ReqMenuDetail(0L, "path",
                "detailName", "group", "output", 100L, 1, 2, 0, 1, 1d, 2d,
                3d, 4d, 5d, 6d, 7d, 8d, 9d, 10d, 11d, 12d, 13d, 14d, "gBasket", "shortName");
        assertTrue(SyncRequest.ReqMenu.Item.ReqMenuDetail.areMenuDetailsEqual(menuDetail, reqMenuDetail));
    }
}