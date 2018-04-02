/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import java.util.List;
import java.util.Map;

/**
 * Created by i.semenov on 14.03.2018.
 */
public class PreorderListWithComplexesGroupResult {
    private List<PreorderComplexGroup> complexesWithGroups;

    public PreorderListWithComplexesGroupResult() {

    }

    public List<PreorderComplexGroup> getComplexesWithGroups() {
        return complexesWithGroups;
    }

    public void setComplexesWithGroups(List<PreorderComplexGroup> complexesWithGroups) {
        this.complexesWithGroups = complexesWithGroups;
    }
}
