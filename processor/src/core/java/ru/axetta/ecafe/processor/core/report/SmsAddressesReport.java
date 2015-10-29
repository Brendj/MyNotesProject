/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.10.15
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
public class SmsAddressesReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(SmsAddressesReport.class);
    private String htmlReport;

    public SmsAddressesReport() {}

    public SmsAddressesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        List<Long> clientsIds;

        public Builder(String templateFilename, List<Long> clientsIds) {
            this.templateFilename = templateFilename;
            this.clientsIds = clientsIds;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + SmsAddressesReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();

            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            HashMap<Long, String> mapGuardians = new HashMap<Long, String>();
            List<Client> clients = new ArrayList<Client>();
            for (Long id : clientsIds) {
                Client client =  (Client)session.load(Client.class, id);
                clients.add(client);
                mapGuardians.put(client.getIdOfClient(), "");
                List<Client> guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient(), null);
                for (Client qqq : guardians) {
                    clients.add(qqq);
                    mapGuardians.put(qqq.getIdOfClient(), client.getPerson().getFullName());
                }
            }

            JRDataSource dataSource = createDataSource(session, startTime, endTime, clients, mapGuardians);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new SmsAddressesReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, List<Client> clients, HashMap<Long, String> mapGuardians) throws Exception {

            ClientSmsList smsList = new ClientSmsList();
            smsList.fillWithClients(session, clients, startTime, endTime, mapGuardians);
            return new JRBeanCollectionDataSource(smsList.getItems());

            //RPRDataLoader dl = new RPRDataLoader(session);
            //return new JRBeanCollectionDataSource(dl.getReportData(startTime, endTime));

        }

    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new ActiveDiscountClientsReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(); // Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }
}
