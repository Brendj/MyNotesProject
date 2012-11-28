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
        logger.info ("Parsing request");
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


        Calendar dateAt = ProjectStateReportService.getStartDate ();
        Calendar dateTo = new GregorianCalendar ();
        dateTo.setTimeInMillis (System.currentTimeMillis ());
        String period = request.getParameter ("period");
        if (period != null && period.length () > 0)
            {
            period = period.trim ();
            try
                {
                int half = Integer.parseInt (period.substring (0, 1));
                int year = Integer.parseInt (period.substring (2));
                dateAt.set (Calendar.YEAR, year);
                dateTo.set (Calendar.YEAR, year);
                if (half == 1)
                    {
                    dateAt.set (Calendar.MONTH, Calendar.JANUARY);
                    dateTo.set (Calendar.MONTH, Calendar.JULY);
                    }
                else if (half == 2)
                    {
                    dateAt.set (Calendar.MONTH, Calendar.JULY);
                    dateTo.set (Calendar.MONTH, Calendar.JANUARY);
                    dateTo.set (Calendar.YEAR, year + 1);
                    }
                dateAt.set (Calendar.DAY_OF_MONTH, 1);
                dateTo.set (Calendar.DAY_OF_MONTH, 1);
                }
            catch (Exception e)
                {
                logger.error ("Failed to build report using period " + period + ". Use default period rules", e);
                }
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
