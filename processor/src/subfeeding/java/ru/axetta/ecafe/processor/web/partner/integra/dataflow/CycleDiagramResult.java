/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 03.02.12
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class CycleDiagramResult extends Result{
    public CycleDiagramExt cycleDiagramExt;

    public CycleDiagramResult(CycleDiagramExt cycleDiagramExt, Long resultCode, String description) {
        super(resultCode, description);
        this.cycleDiagramExt = cycleDiagramExt;
    }

    public CycleDiagramResult() {}
}
