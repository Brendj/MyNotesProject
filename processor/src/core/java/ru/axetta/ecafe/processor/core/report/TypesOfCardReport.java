/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.hibernate.Session;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 03.11.14
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class TypesOfCardReport extends BasicReportForAllOrgJob {

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Logger getLogger() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public TypesOfCardReport build(Session session, Date startTime, Date endTime, Calendar calendar)
            throws Exception {
        return doBuild(session, startTime, endTime, calendar);
    }

    public TypesOfCardReport doBuild(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
         return null;
    }
}
