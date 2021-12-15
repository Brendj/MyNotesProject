/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.repository;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalReport;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

@WebServlet(
        name = "ReportRepositoryDownloadServlet",
        description = "ReportRepositoryDownloadServlet",
        urlPatterns = {"/repository/download"}
)
public class ReportRepositoryDownloadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        ReportRepositoryListPage reportRepositoryListPage = RuntimeContext.getAppContext().getBean(ReportRepositoryListPage.class);
        File f = reportRepositoryListPage.getFileToDownload();

        SecurityJournalReport process = SecurityJournalReport.createJournalRecord(extractTemplateName(f.getName()), new Date());

        if (!f.exists()) {
            process.saveWithSuccess(false);
            response.sendError(404, "Извините, данный файл уже удален");
            return;
        }
        process.saveWithSuccess(true);
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

    public static String extractTemplateName(String filename) {
        try {
            return filename.substring(0, filename.indexOf('-')).concat(filename.substring(filename.lastIndexOf('.')));
        } catch (Exception e) {
            return filename;
        }
    }
}
