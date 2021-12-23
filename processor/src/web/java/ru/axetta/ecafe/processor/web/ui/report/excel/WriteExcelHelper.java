package ru.axetta.ecafe.processor.web.ui.report.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ContentDisposition;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WriteExcelHelper {
    public static void saveExcelReport(Workbook wb, HttpServletResponse response) throws Exception {
        try {
            ContentDisposition cd = ContentDisposition
                    .builder("attachment")
                    .filename(wb.getSheetAt(0).getSheetName(), StandardCharsets.UTF_8)
                    .build();
            response.setHeader("Content-disposition", cd.toString());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");

            wb.write(response.getOutputStream());

            response.getOutputStream().flush();
            response.getOutputStream().close();
            response.flushBuffer();
        }
        catch (IOException e) {
            throw new Exception("Error with response output stream");
        }
    }
}
