package ru.axetta.ecafe.processor.web.ui.report.summary;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.service.SummaryDownloadMakerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 08.12.16
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class SummaryDownloadServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SummaryDownloadServlet.class);
    private static final String PURCHASE_REPORT_DAY = "purchaseReportDate";


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requiredDN = RuntimeContext.getInstance().getPropertiesValue(SummaryDownloadMakerService.SSLCERT_DN_PROPERTY, null);
        if (requiredDN == null) {
            response.sendError(400, "Unauthorized access");
            return;
        }
        boolean granted = false;
        X509Certificate[] certificates = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        if (certificates != null && certificates.length > 0) {
            for (int n = 0; n < certificates.length; ++n) {
                String dn = certificates[0].getSubjectDN().getName();
                if (dn.equals(requiredDN)) {
                    granted = true;
                    break;
                }
            }
        }
        if (!granted) {
            response.sendError(400, "Unauthorized access");
            return;
        }

        String day = null;
        try {
            String[] dates = request.getParameterMap().get(PURCHASE_REPORT_DAY);
            if (dates == null) {
                throw new Exception(String.format("Can't find parameter %s", PURCHASE_REPORT_DAY));
            }
            day = dates[0];
        } catch (Exception e) {
            response.sendError(400, "Error parsing parameters");
            logger.error("Error parsing parameters SummaryDownloadServlet", e);
            return;
        }
        File f = getFileToDownload(day);
        if (!f.exists()) {
            response.sendError(404, "Requested file not found");
            return;
        }

        response.setHeader("Content-Type", getServletContext().getMimeType(f.getName()));
        response.setHeader("Content-disposition", "inline;filename="+ URLEncoder.encode(f.getName(), "UTF-8"));
        ServletOutputStream out = response.getOutputStream();
        FileInputStream fis = new FileInputStream(f);
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

    public File getFileToDownload(String day) {
        return new File(RuntimeContext.getInstance().getPropertiesValue(SummaryDownloadMakerService.FOLDER_PROPERTY, "") + "/" + day + ".csv");
    }
}
