/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.report.builders;

import net.sf.jasperreports.engine.JRDataSource;

import ru.axetta.ecafe.processor.web.internal.report.items.ReportItem;

import org.hibernate.Session;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.01.13
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class ReportBuilder<I extends ReportItem> {

    private Class<I> clazz;
    public String templateFilename;

    protected ReportBuilder(Class<I> clazz) {
        this.clazz = clazz;
    }

    protected abstract JRDataSource createDataSource(Session session, Date startTime, Date endTime,
            Calendar calendar, Map<String, Object> parameterMap) throws Exception;

    public abstract void build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception;

}
