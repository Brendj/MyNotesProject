package ru.axetta.ecafe.processor.web.ui.report.summary;

import ru.axetta.ecafe.processor.core.service.SummaryDownloadMakerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 08.12.16
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(
        name = "SummaryDownloadServlet",
        description = "SummaryDownloadServlet",
        urlPatterns = {"/summary/download"}
)
public class SummaryDownloadServlet extends SummaryBaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(SummaryDownloadServlet.class);
    private static final String PURCHASE_REPORT_DAY = "purchaseReportDate";

    protected String getUserNameSettingName() {
        return SummaryDownloadMakerService.USER;
    }

    protected String getUserPasswordSettingName(){
        return SummaryDownloadMakerService.PASSWORD;
    }

    protected String getFolderSettingName() {
        return SummaryDownloadMakerService.FOLDER_PROPERTY;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected String getFileBaseName() {
        return PURCHASE_REPORT_DAY;
    }

    /*protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = RuntimeContext.getInstance().getPropertiesValue(SummaryDownloadMakerService.USER, null);
        String password = RuntimeContext.getInstance().getPropertiesValue(SummaryDownloadMakerService.PASSWORD, null);
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            logger.error("SummaryDownloadServlet empty username or password in config");
            response.sendError(400, "Unauthorized access");
            return;
        }
        boolean granted = false;
        try {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Basic")) {
                String base64Credentials = authorization.substring("Basic".length()).trim();
                String credentials = new String(new Base64().decode(base64Credentials.getBytes()), Charset.forName("UTF-8"));
                String[] values = credentials.trim().split(":",2);
                if (userName.equals(values[0]) && password.equals(values[1])) {
                    granted = true;
                } else {
                    logger.error(String.format("SummaryDownloadServlet access forbidden. user=%s, pass=%s", values[0], values[1]));
                }
            }
        } catch (Exception e) {
            logger.error("Error in SummaryDownloadServlet", e);
            response.sendError(400, "Unauthorized access");
            return;
        }
        if (!granted) {
            response.sendError(400, "Unauthorized access.");
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

        response.setHeader("Content-Type", "application/csv");
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
        out.flush();
        out.close();
        logger.error("SummaryDownloadServlet file transferred OK");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request,  response);
    }

    public File getFileToDownload(String day) {
        return new File(RuntimeContext.getInstance().getPropertiesValue(SummaryDownloadMakerService.FOLDER_PROPERTY, "") + "/" + day + ".csv");
    }*/
}
