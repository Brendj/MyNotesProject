/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.10.15
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
public class SmsAddressesReport extends BasicReportForAllOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по адресам доставки сообщений";
    public static final String[] TEMPLATE_FILE_NAMES = {"SmsAddressesReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


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

            HashMap<Long, List<String>> mapGuardians = new HashMap<Long, List<String>>();
            List<Client> clients = new ArrayList<Client>();
            for (Long id : clientsIds) {
                Client client =  (Client)session.load(Client.class, id);
                clients.add(client);
                //mapGuardians.put(client.getIdOfClient(), new ArrayList<String>());
                List<Client> guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient(), null);
                List<String> guardiansFIO = new ArrayList<String>();
                for (Client qqq : guardians) {
                    clients.add(qqq);
                    guardiansFIO.add(qqq.getPerson().getFullName());
                }
                mapGuardians.put(client.getIdOfClient(), guardiansFIO);

                List<ClientGuardianItem> wards = ClientManager.loadWardsByClient(session, client.getIdOfClient(), false);
                //List<String> wardsFIO = new ArrayList<String>();
                for (ClientGuardianItem item : wards) {
                    clients.add((Client)session.load(Client.class, item.getIdOfClient()));
                    List<Client> guardianOfWard = ClientManager.findGuardiansByClient(session, item.getIdOfClient(), null);
                    List<String> guardiansOfWardFIO = new ArrayList<String>();
                    for (Client qqq : guardianOfWard) {
                        //clients.add(qqq);
                        guardiansOfWardFIO.add(qqq.getPerson().getFullName());
                    }
                    mapGuardians.put(item.getIdOfClient(), guardiansOfWardFIO);
                    //wardsFIO.add(item.getPersonName());
                }

            }

            JRDataSource dataSource = createDataSource(session, startTime, endTime, clients, mapGuardians);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new SmsAddressesReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, List<Client> clients, HashMap<Long, List<String>> mapGuardians) throws Exception {

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
