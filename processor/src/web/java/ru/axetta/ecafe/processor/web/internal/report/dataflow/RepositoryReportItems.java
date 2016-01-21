/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 19.01.16
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryReportItems {
    @XmlElement
    protected List<RepositoryReportItem> repositoryReportItem;

    public RepositoryReportItems() {

    }

    public List<RepositoryReportItem> getRepositoryReportItem() {
        return repositoryReportItem;
    }

    public void setRepositoryReportItem(List<RepositoryReportItem> repositoryReportItem) {
        this.repositoryReportItem = repositoryReportItem;
    }
}
