/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.repository;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class ReportRepositoryDownloadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        ReportRepositoryListPage reportRepositoryListPage = RuntimeContext.getAppContext().getBean(ReportRepositoryListPage.class);
        File f = reportRepositoryListPage.getFileToDownload();
        if (!f.exists()) {
            response.sendError(404, "Извините, данный файл уже удален");
            return;
        }
        response.setHeader("Content-Type", getServletContext().getMimeType(reportRepositoryListPage.getFileToDownload().getName()));
        response.setHeader("Content-disposition", "inline;filename="+ URLEncoder.encode(reportRepositoryListPage.getFileToDownload().getName(), "UTF-8"));
        ServletOutputStream out = response.getOutputStream();
        FileInputStream fis = new FileInputStream(reportRepositoryListPage.getFileToDownload());
        try {
            byte[] buf = new byte[2048];
            int len = 0;
            while ((len = fis.read(buf)) >= 0)
            {
                out.write(buf, 0, len);
            }
        } finally {
            fis.close();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request,  response);
    }
}
