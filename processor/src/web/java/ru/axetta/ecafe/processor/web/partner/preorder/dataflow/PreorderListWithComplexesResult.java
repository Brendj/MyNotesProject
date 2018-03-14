/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import java.util.List;

public class PreorderListWithComplexesResult {
    private List<PreorderComplexItemExt> complexItemExtList;

    public PreorderListWithComplexesResult() {

    }

    public List<PreorderComplexItemExt> getComplexItemExtList() {
        return complexItemExtList;
    }

    public void setComplexItemExtList(List<PreorderComplexItemExt> complexItemExtList) {
        this.complexItemExtList = complexItemExtList;
    }
}
