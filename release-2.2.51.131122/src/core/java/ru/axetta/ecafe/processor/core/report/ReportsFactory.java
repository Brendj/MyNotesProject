/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.06.13
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public class ReportsFactory {
    public static BasicReportJob craeteReportInstance (String className) {
        try {
            Class reportClass = null;
            try {
                reportClass = Class.forName(className);
            } catch (Exception e) {
                throw e;
            }
            BasicReportJob clearReport = (BasicReportJob) reportClass.newInstance();
            return clearReport;
        } catch (Exception e) {
            return null;
        }
    }
}
