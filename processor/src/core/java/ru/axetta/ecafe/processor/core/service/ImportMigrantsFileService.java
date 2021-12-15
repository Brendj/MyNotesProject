/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("ImportMigrantsFileService")
@Scope("singleton")
public class ImportMigrantsFileService {
    public static class ClientIsExpelled extends Exception {

        public ClientIsExpelled(String message) {
            super(message);
        }
    }

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportMigrantsFileService.class);

    public final String FILENAME_PROPERTY = "ecafe.processor.esz.migrants.filename";
    public final String CRON_EXPRESSION_PROPERTY = "ecafe.processor.esz.migrants.cronExpression";

    private final String INITIAL_SQL =
            "INSERT INTO cf_esz_migrants_requests(idofserviceclass, groupname, clientguid, visitorginn, "
          + "   visitorgunom, dateend, datelearnstart, datelearnend, firstname, surname, secondname, idofesz) "
          + "VALUES ";

    public void run() throws Exception {
        if (!isOn())
            return;
        loadMigrantsFile();
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.esz.migrants.fileservice.node", "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void loadMigrantsFile() throws Exception {
        logger.info("Start loadMigrantsFile");
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            String filename = RuntimeContext.getInstance().getPropertiesValue(FILENAME_PROPERTY, null);
            if (filename == null) {
                throw new Exception(String.format("Not found property %s in application config", FILENAME_PROPERTY));
            }
            File file = new File(filename);
            if (!file.exists()) {
                throw new Exception(String.format("Файл выгрузки ESZ с мигрантами %s не найден", filename));
            }
            fileInputStream = new FileInputStream(filename);
            inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            fillTable(bufferedReader);

        } catch (Exception e) {
          logger.error("Error in loadMigrantsFile: ", e);
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(fileInputStream);
        }
        logger.info("End loadMigrantsFile");
    }

    public void fillTable(BufferedReader bufferedReader) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            logger.info("Clear esz migrants table");
            Query truncateQuery = session.createSQLQuery("truncate table cf_esz_migrants_requests");
            truncateQuery.executeUpdate();
            logger.info("End of clearing esz migrants table");

            StringBuilder sqlQueryBuilder = new StringBuilder(INITIAL_SQL);

            String line;                // считанная строка
            String insertString = "";   // sql строка с данными
            int counter = 0;            // считанных строк в порции
            int processed = 0;          // всего обработано
            int errors = 0;             // количество ошибок
            logger.info("Start fill esz migrants table");
            long begin = System.currentTimeMillis();
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replace("\uFEFF", "");
                if (counter == 0 && !session.getTransaction().isActive())
                    transaction = session.beginTransaction();

                try {
                    String[] arr = line.split("\t", -1);
                    insertString = buildInsertString(arr);
                } catch (Exception e) {
                    errors++;
                    processed++;
                    logger.error(String.format("Error in process ESZ migrants file. Line %s ", processed), e);
                    continue;
                }

                sqlQueryBuilder.append("(").append(insertString).append(")");
                counter++;
                if (counter == 1000) {
                    Query query = session.createSQLQuery(sqlQueryBuilder.toString());
                    query.executeUpdate();
                    transaction.commit();
                    counter = 0;
                    sqlQueryBuilder.setLength(0);
                    sqlQueryBuilder.append(INITIAL_SQL);
                    logger.info(String.format("Lines processed: %s", processed));
                } else {
                    sqlQueryBuilder.append(", ");
                }
                processed++;
            }

            if (counter > 0) {
                Query query = session.createSQLQuery(sqlQueryBuilder.substring(0, sqlQueryBuilder.length() - 2));
                query.executeUpdate();
                transaction.commit();
                logger.info(String.format("Lines processed: %s", processed));
            }
            transaction = null;

            logger.info(String.format("End fill esz migrants table. Time taken %s ms, processed %s lines, error lines: %s", System.currentTimeMillis() - begin, processed, errors));
        } catch (Exception e) {
            logger.error("Error in loadMigrantsFile.fillTable: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private String buildInsertString(String[] array) throws Exception {

        StringBuilder sb = new StringBuilder();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        sb.append(getQuotedStr(array[12])).append(", ");            // idofserviceclass
        sb.append("'").append(getQuotedStr(array[0])).append("'").append(", "); // groupname
        if (array[5].equals("null")) {
            sb.append("NULL, ");
        } else {
            sb.append("'").append(getQuotedStr(array[5])).append("'").append(", "); // clientguid
        }
        sb.append("'").append(getQuotedStr(array[7])).append("'").append(", "); // visitorginn
        sb.append(getQuotedStr(array[8])).append(", ");             // visitorgunom

        //dateend
        // если не null и дата меньше\равна текущей - клиент отчислен, не обрабатываем
        // если null или дата больше текущей - все ок
        if (array[9].equals("null")) {
            Date dateLearnEnd = simpleDateFormat.parse(array[11]);
            Date currentDate = new Date();
            if (dateLearnEnd.getTime() <= currentDate.getTime()) {
                throw new ClientIsExpelled(String.format("client with guid={%s} is expelled", getQuotedStr(array[5])));
            } else {
                sb.append(getQuotedStr("null")).append(", ");
            }
        } else {
            Date dateEnd = simpleDateFormat.parse(array[9]);
            sb.append(dateEnd.getTime()).append(", ");
        }

        sb.append(getQuotedStr(String.valueOf(simpleDateFormat.parse(array[10]).getTime()))).append(", ");  // datelearnstart
        sb.append(getQuotedStr(String.valueOf(simpleDateFormat.parse(array[11]).getTime()))).append(", ");  // datelearnend
        if (array[2].equals("null")) {
            sb.append("NULL, ");
        } else {
            sb.append("'").append(getQuotedStr(array[2])).append("'").append(", "); // firstname
        }
        if (array[1].equals("null")) {
            sb.append("NULL, ");
        } else {
            sb.append("'").append(getQuotedStr(array[1])).append("'").append(", "); // surname
        }
        if (array[3].equals("null")) {
            sb.append("NULL, ");
        } else {
            sb.append("'").append(getQuotedStr(array[3])).append("'").append(", "); // secondname
        }
        sb.append(getQuotedStr(array[4]));                                      // idofesz

        return sb.toString();
    }

    protected String getQuotedStr(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length()-1);
        }
        return str.replaceAll("\"\"", "\"");
    }

    public void scheduleSync() throws Exception {
        String syncSchedule = RuntimeContext.getInstance().getConfigProperties().getProperty(CRON_EXPRESSION_PROPERTY, "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            logger.info("Scheduling import migrants file service job: " + syncSchedule);
            JobDetail job = new JobDetail("ImportMigrantsFile", Scheduler.DEFAULT_GROUP, ImportMigrantsFileJob.class);

            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncSchedule.equals("")) {
                CronTrigger trigger = new CronTrigger("ImportMigrantsFile", Scheduler.DEFAULT_GROUP);
                trigger.setCronExpression(syncSchedule);
                if (scheduler.getTrigger("ImportMigrantsFile", Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob("ImportMigrantsFile", Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(job, trigger);
            }
            scheduler.start();
        } catch(Exception e) {
            logger.error("Failed to schedule import migrants file service job:", e);
        }
    }

    public static class ImportMigrantsFileJob implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(ImportMigrantsFileService.class).run();
                RuntimeContext.getAppContext().getBean(ImportMigrantsService.class).run();
            } catch (JobExecutionException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Failed to run import migrants file service job:", e);
            }
        }
    }
}
