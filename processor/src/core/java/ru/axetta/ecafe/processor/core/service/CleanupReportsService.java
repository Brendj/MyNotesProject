package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 06.11.13
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class CleanupReportsService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CleanupReportsService.class);

    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_CLEANUP_REPOSITORY_REPORTS);
    }

    public void run() throws IOException {
        return;
        /*if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            //logger.info ("BI data export is turned off. You have to activate this tool using common Settings");
            return;
        }


        try {
            List<BigInteger> idOfReportInfoList = DAOService.getInstance().getCleanupRepositoryReportsByDate();
            for (BigInteger idOfReportInfo : idOfReportInfoList) {
                ReportInfo ri = DAOService.getInstance().findReportInfoById(idOfReportInfo.longValue());

            }
        } catch (Exception e) {
            logger.error("Failed to cleanup reports", e);
        }*/
    }
}
