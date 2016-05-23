/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.persistence.SecurityJournalReport;
import ru.axetta.ecafe.processor.web.ui.report.repository.ReportRepositoryDownloadServlet;

import org.apache.catalina.connector.ResponseFacade;

import javax.servlet.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 20.02.16
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public class XLSReportsFilter implements Filter {

    private static final String CONTENT_DISPOSITION = "Content-disposition";
    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        Collection<String> headers = ((ResponseFacade) response).getHeaderNames();
        if (headers != null && headers.contains(CONTENT_DISPOSITION) && headers.contains(CONTENT_TYPE)) {
            if (((ResponseFacade) response).getHeader(CONTENT_TYPE).equals("application/xls")) {
                String filename = extractFilename(((ResponseFacade) response).getHeader(CONTENT_DISPOSITION));
                if (filename != null) {
                    SecurityJournalReport process = SecurityJournalReport.createJournalRecord(filename, new Date());
                    process.saveWithSuccess(true);
                }
            }
        }
    }

    private String extractFilename(String str) {
        try {
            int p = str.indexOf("filename=");
            if (p > 0) {
                p = p + "filename=".length();
                return ReportRepositoryDownloadServlet.extractTemplateName(str.substring(p));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

}
