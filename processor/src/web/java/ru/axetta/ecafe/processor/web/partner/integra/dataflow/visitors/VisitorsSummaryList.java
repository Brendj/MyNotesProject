/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;

/**
 * User: Shamil
 * Date: 16.09.14
 */
public class VisitorsSummaryList {
    @XmlAttribute(name = "orgsInList")
    public int orgCount = 0;
    public List<VisitorsSummary> org;


}
