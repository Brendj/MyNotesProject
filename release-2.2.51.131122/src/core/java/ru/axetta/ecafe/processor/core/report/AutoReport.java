/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 12:10:58
 * To change this template use File | Settings | File Templates.
 */
public class AutoReport {

    private final BasicReport basicReport;
    private final Properties properties;

    public AutoReport(BasicReport basicReport, Properties properties) {
        this.basicReport = basicReport;
        this.properties = properties;
    }

    public BasicReport getBasicReport() {
        return basicReport;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return String.format("AutoReport{basicReport=%s, properties=%s}", basicReport, properties);
    }
}