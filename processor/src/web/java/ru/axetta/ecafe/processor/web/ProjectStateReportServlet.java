/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import com.google.visualization.datasource.*;
import com.google.visualization.datasource.base.*;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableCell;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.ProjectStateReportService;

import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 13.11.12
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(
        name = "ProjectStateReportServlet",
        description = "ProjectStateReportServlet",
        urlPatterns = {"/prj-state"}
)
public class ProjectStateReportServlet extends HttpServlet {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectStateReportServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String out = req.getParameter("tqx");
        if (out != null && out.toLowerCase().indexOf("csv") > -1)
            {
            prepareCSV (req, resp);
            }
        else
            {
            prepareCharts(req, resp);
            }
    }


    public void prepareCSV (HttpServletRequest req, HttpServletResponse resp) throws IOException{
        DataSourceRequest dsRequest = null;

        try {
            dsRequest = new DataSourceRequest(req);
            QueryPair query = DataSourceHelper.splitQuery(dsRequest.getQuery(), Capabilities.SELECT);

            // Generate the data table.
            DataTable data = generateDataTable(query.getDataSourceQuery(), req);


            resp.setCharacterEncoding("winwdows-1251");
            resp.setHeader("Content-Type", "text/csv");
            resp.setHeader("Content-Disposition", "attachment;filename=\"data.csv\"");
            buildCSV(data, ';', resp.getOutputStream());
        } catch (Exception e) {
        }
    }


    public void prepareCharts (HttpServletRequest req, HttpServletResponse resp) throws IOException
        {
        DataSourceRequest dsRequest = null;


        try {
            dsRequest = new DataSourceRequest(req);
            QueryPair query = DataSourceHelper.splitQuery(dsRequest.getQuery(), Capabilities.SELECT);
            DataTable data = generateDataTable(query.getDataSourceQuery(), req);
            DataTable newData = DataSourceHelper.applyQuery(query.getCompletionQuery(), data, dsRequest.getUserLocale());

            String encoding = req.getParameter("encoding");
            if (encoding != null && encoding.equals("cyr"))
                {
                resp.setContentType("text/csv; charset=windows-1251");
                resp.setCharacterEncoding("charset=windows-1251");
                }


            DataSourceHelper.setServletResponse(newData, dsRequest, resp);
        } catch (RuntimeException rte) {
            ResponseStatus status = new ResponseStatus(StatusType.ERROR, ReasonType.INTERNAL_ERROR, rte.getMessage());
            if (dsRequest == null) {
                dsRequest = DataSourceRequest.getDefaultDataSourceRequest(req);
            }
            DataSourceHelper.setServletErrorResponse(status, dsRequest, resp);
        } catch (DataSourceException e) {
            if (dsRequest != null) {
                DataSourceHelper.setServletErrorResponse(e, dsRequest, resp);
            } else {
                DataSourceHelper.setServletErrorResponse(e, req, resp);
            }
        } catch (Exception e) {

        }
    }


    public DataTable generateDataTable(Query query, HttpServletRequest request) throws TypeMismatchException {
        //logger.info("Parsing request");
        RuntimeContext runtimeContext = null;
        ProjectStateReportService.Type t = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            String reportType = request.getParameter("type");
            t = ProjectStateReportService.getChartType(reportType);
            if (runtimeContext == null || t == null) {
                throw new TypeMismatchException("Incorrect type of report was required: '" + reportType + "'");
            }
        } catch (TypeMismatchException tme) {
            throw tme;
        } catch (Exception e) {
            logger.error("Failed to build report " + request.getParameter("type"), e);
            return null;
        }


        Calendar dateAt = ProjectStateReportService.getStartDate();
        Calendar dateTo = new GregorianCalendar();
        dateTo.setTimeInMillis(System.currentTimeMillis());
        String period = request.getParameter("period");
        String region = request.getParameter("region");
        try
            {
            region = URLDecoder.decode(new String (region.getBytes("iso-8859-1"), "UTF-8"));
            } catch (Exception e) {}
        if (period != null && period.length() > 0) {
            period = period.trim();
            try {
                int half = Integer.parseInt(period.substring(0, 1));
                int year = Integer.parseInt(period.substring(2));
                dateAt.set(Calendar.YEAR, year);
                dateTo.set(Calendar.YEAR, year);
                if (half == 1) {
                    dateAt.set(Calendar.MONTH, Calendar.JANUARY);
                    dateTo.set(Calendar.MONTH, Calendar.JULY);
                } else if (half == 2) {
                    dateAt.set(Calendar.MONTH, Calendar.JULY);
                    dateTo.set(Calendar.MONTH, Calendar.JANUARY);
                    dateTo.set(Calendar.YEAR, year + 1);
                }
                dateAt.set(Calendar.DAY_OF_MONTH, 1);
                dateTo.set(Calendar.DAY_OF_MONTH, 1);
            } catch (Exception e) {
                logger.error("Failed to build report using period " + period + ". Use default period rules", e);
            }
        }
        String encoding = request.getParameter("encoding");
        if (encoding != null && encoding.equals("cyr"))
            {
            encoding = "windows-1251";
            }
        else
            {
            encoding = null;
            }

        ProjectStateReportService projectStateReportService = RuntimeContext.getAppContext().getBean(ProjectStateReportService.class);
        DataTable data = projectStateReportService.generateReport(runtimeContext, dateAt, dateTo, region, t, encoding);
        return data;
    }


    protected boolean isRestrictedAccessMode() {
        return false;
    }


    public void buildCSV (DataTable data, char delimeter, ServletOutputStream out)  throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "windows-1251"));
        StringBuilder builder = new StringBuilder("");
        for (int i=0; i<data.getNumberOfColumns(); i++) {
            ColumnDescription col = data.getColumnDescription(i);
            if (builder.length() > 0) {
                builder.append(";");
            }
            builder.append("\"").append(col.getLabel()).append("\"");
        }
        writer.write(builder.toString());
        writer.newLine();
        writer.flush();

        for (TableRow r : data.getRows()) {
            builder.delete(0, builder.length());
            for (TableCell cell : r.getCells()) {
                if (builder.length() > 0) {
                    builder.append(";");
                }
                String v = cell.getValue().toString();
                if (cell.getValue().getType() == ValueType.NUMBER) {
                    v = v.replace(".", ",");
                }
                builder.append("\"").append(v).append("\"");
            }
        writer.write(builder.toString());
        writer.newLine();
        writer.flush();
        }
    }
}
