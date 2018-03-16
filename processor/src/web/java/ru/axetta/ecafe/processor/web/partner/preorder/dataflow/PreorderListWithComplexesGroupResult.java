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
    //private String group;
    //private List<PreorderListWithComplexesResult> complexes;
    private Map<String, List<PreorderComplexItemExt>> complexesWithGroups;

    public PreorderListWithComplexesGroupResult() {
        //this.complexes = new ArrayList<PreorderListWithComplexesResult>();
    }

    public Map<String, List<PreorderComplexItemExt>> getComplexesWithGroups() {
        return complexesWithGroups;
    }

    public void setComplexesWithGroups(Map<String, List<PreorderComplexItemExt>> complexesWithGroups) {
        this.complexesWithGroups = complexesWithGroups;
    }
}
