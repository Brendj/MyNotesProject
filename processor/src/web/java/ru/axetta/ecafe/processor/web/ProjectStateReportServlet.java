/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.ProjectStateReportService;

import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 13.11.12
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class ProjectStateReportServlet extends DataSourceServlet
    {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectStateReportServlet.class);


    @Override
    public DataTable generateDataTable (Query query, HttpServletRequest request) throws TypeMismatchException
        {
        RuntimeContext runtimeContext = null;
        ProjectStateReportService.Type t = null;
        try
            {
            runtimeContext = RuntimeContext.getInstance ();
            String reportType = request.getParameter ("type");
            t = ProjectStateReportService.TYPES.get (reportType);
            if (runtimeContext == null || t == null)
                {
                throw new TypeMismatchException ("Incorrect type of report was required: '" + reportType + "'");
                }
            }
        catch (TypeMismatchException tme)
            {
            throw tme;
            }
        catch (Exception e)
            {
            logger.error ("Failed to build report " + request.getParameter ("type"), e);
            return null;
            }


        Calendar dateAt  = new GregorianCalendar ();
        dateAt.setTimeInMillis (System.currentTimeMillis ());
        String inDate = request.getParameter ("dateAt");
        try
            {
            if (inDate != null && inDate.trim ().length () > 0)
                {
                Date d = new SimpleDateFormat("dd.MM.yyyy").parse (inDate.trim ());
                    dateAt.setTime (d);
                }
            }
        catch (Exception e)
            {
            dateAt = ProjectStateReportService.getStartDate ();
            }

        Calendar dateTo = new GregorianCalendar ();
        dateTo.setTimeInMillis (System.currentTimeMillis ());
        inDate = request.getParameter ("dateTo");
            try
            {
            if (inDate != null && inDate.trim ().length () > 0)
                {
                Date d = new SimpleDateFormat("dd.MM.yyyy").parse (inDate.trim ());
                dateTo.setTime (d);
                }
            }
        catch (Exception e)
            {
            dateTo = ProjectStateReportService.getStartDate ();
            }

        DataTable data = ProjectStateReportService.generateReport (runtimeContext, dateAt, dateTo, t);
        return data;
        }

    @Override
    protected boolean isRestrictedAccessMode ()
        {
        return false;
        }
    }
