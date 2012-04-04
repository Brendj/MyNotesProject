/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.event;

import ru.axetta.ecafe.processor.core.DailyFileCreator;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.ReportDocument;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.SyncResponse;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 12:10:58
 * To change this template use File | Settings | File Templates.
 */
public class SyncEvent extends BasicEvent {

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 15.12.2009
     * Time: 12:10:58
     * To change this template use File | Settings | File Templates.
     */
    public static class RawEvent extends BasicEvent {

        private final SyncRequest syncRequest;
        private final SyncResponse syncResponse;

        public RawEvent(Date eventTime, SyncRequest syncRequest, SyncResponse syncResponse) {
            super(eventTime);
            this.syncRequest = syncRequest;
            this.syncResponse = syncResponse;
        }

        public SyncRequest getSyncRequest() {
            return syncRequest;
        }

        public SyncResponse getSyncResponse() {
            return syncResponse;
        }

        @Override
        public String toString() {
            return "RawEvent{" + "syncRequest=" + syncRequest + ", syncResponse=" + syncResponse + "} " + super
                    .toString();
        }
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }
    }

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 23.12.2009
     * Time: 11:35:52
     * To change this template use File | Settings | File Templates.
     */
    public static class ProcessTask implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(ProcessTask.class);
        private final EventProcessor eventProcessor;
        private final SessionFactory sessionFactory;
        private final RawEvent rawEvent;
        private final DateFormat timeFormat;
        private final Map<Integer, EventDocumentBuilder> eventDocumentBuilders;

        public ProcessTask(EventProcessor eventProcessor, SessionFactory sessionFactory, RawEvent rawEvent,
                DateFormat timeFormat, Map<Integer, EventDocumentBuilder> eventDocumentBuilders) {
            this.eventProcessor = eventProcessor;
            this.sessionFactory = sessionFactory;
            this.rawEvent = rawEvent;
            this.timeFormat = timeFormat;
            this.eventDocumentBuilders = eventDocumentBuilders;
        }

        public void run() {
            try {
                Transaction transaction = null;
                Session session = sessionFactory.openSession();
                try {
                    transaction = session.beginTransaction();
                    Org org = (Org) session.get(Org.class, rawEvent.getSyncRequest().getIdOfOrg());
                    //todo - build it
                    SyncEvent syncEvent = new SyncEvent(rawEvent.getEventTime(), new OrgItem(org));

                    Properties properties = new Properties();
                    ReportPropertiesUtils.addProperties(properties, syncEvent, timeFormat);
                    ReportPropertiesUtils.addProperties(session, properties, org, "org.");

                    transaction.commit();
                    transaction = null;

                    eventProcessor.processEvent(syncEvent, properties, eventDocumentBuilders);
                } finally {
                    HibernateUtils.rollback(transaction, logger);
                    HibernateUtils.close(session, logger);
                }
            } catch (Exception e) {
                logger.error("Failed to handle event", e);
            }
        }

    }

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 15.12.2009
     * Time: 12:10:58
     * To change this template use File | Settings | File Templates.
     */
    public static class HtmlEventBuilder extends DailyFileCreator implements EventDocumentBuilder {

        private static final Logger logger = LoggerFactory.getLogger(HtmlEventBuilder.class);
        private static final String FILENAME;

        static {
            String fullName = SyncEvent.class.getCanonicalName();
            int i = fullName.lastIndexOf('.');
            FILENAME = fullName.substring(i + 1);
        }

        private final DateFormat timeFormat;

        public HtmlEventBuilder(String basePath, DateFormat dateFormat, DateFormat timeFormat) {
            super(basePath, dateFormat);
            this.timeFormat = timeFormat;
        }

        public DateFormat getTimeFormat() {
            synchronized (this.timeFormat) {
                return (DateFormat) timeFormat.clone();
            }
        }

        /**
         * Warning: has to be threadsafe
         *
         * @param event
         * @return
         * @throws Exception
         */
        public ReportDocument buildDocument(BasicEvent event) throws Exception {
            DateFormat timeFormat = getTimeFormat();
            String filename = String.format("%s-%s", FILENAME, timeFormat.format(event.getEventTime()));
            File eventDocumentFile;
            try {
                eventDocumentFile = createFile(filename, "html");
            } catch (Exception e) {
                logger.error("Failed to create report document file", e);
                throw e;
            }
            SyncEvent syncEvent = (SyncEvent) event;
            FileOutputStream outputStream = new FileOutputStream(eventDocumentFile);
            try {
                writeReportDocumentTo(syncEvent, outputStream, timeFormat);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
            return new ReportDocument(Collections.singletonList(eventDocumentFile));
        }

        private static void writeReportDocumentTo(SyncEvent event, OutputStream outputStream, DateFormat timeFormat)
                throws IOException {
            //todo
            //Writer writer = new OutputStreamWriter(outputStream, "utf-8");
            //writer.write("<html>");
            //writer.write("<head>");
            //writer.write("<title>");
            //writer.write(StringEscapeUtils.escapeHtml(String.format("Платежи от контрагента \"%s\"",
            //        StringUtils.defaultString(event.getContragent().getContragentName()))));
            //writer.write("</title>");
            //writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            //writer.write("<meta http-equiv=\"Content-Language\" content=\"ru\">");
            //writer.write("</head>");
            //writer.write("<body>");
            //writer.write("<table>");
            //writer.write("<tr>");
            //writer.write("<td align=\"center\">");
            //writer.write(StringEscapeUtils.escapeHtml(
            //        String.format("Платежи от контрагента \"%s\", проведенные от имени пользователя \"%s\" %s",
            //                StringUtils.defaultString(event.getContragent().getContragentName()),
            //                StringUtils.defaultString(event.getUser().getUserName()),
            //                timeFormat.format(event.getSyncTime()))));
            //writer.write("</td>");
            //writer.write("</tr>");
            //writer.write("<tr>");
            //writer.write("<td align=\"center\">");
            //writer.write("<table>");
            //writer.write("<tr>");
            //writer.write("<td align=\"right\">");
            //writer.write("Идентификатор платежа в системе контрагента");
            //writer.write("</td>");
            //writer.write("<td>");
            //writer.write("Время совершения платежа по данным контрагента");
            //writer.write("</td>");
            //writer.write("<td align=\"right\">");
            //writer.write("Номер договора");
            //writer.write("</td>");
            //writer.write("<td>");
            //writer.write("Фамилия");
            //writer.write("</td>");
            //writer.write("<td>");
            //writer.write("Имя");
            //writer.write("</td>");
            //writer.write("<td>");
            //writer.write("Отчество");
            //writer.write("</td>");
            //writer.write("<td align=\"right\">");
            //writer.write("Сумма, указанная контрагентом, руб.");
            //writer.write("</td>");
            //writer.write("<td align=\"right\">");
            //writer.write("Физический номер карты, на которую перечислены средства");
            //writer.write("</td>");
            //writer.write("<td align=\"right\">");
            //writer.write("Баланс карты на момент отправки уведомления, руб.");
            //writer.write("</td>");
            //writer.write("<td align=\"right\">");
            //writer.write("Код результата обработки");
            //writer.write("</td>");
            //writer.write("<td>");
            //writer.write("Сообщение об ошибке");
            //writer.write("</td>");
            //
            //writer.write("</tr>");
            //for (PaymentProcessEvent.PaymentItem paymentItem : event.getPayments()) {
            //    writer.write("<tr>");
            //    writer.write("<td valign=\"top\" align=\"right\">");
            //    writer.write(StringEscapeUtils.escapeHtml(paymentItem.getIdOfPayment()));
            //    writer.write("</td>");
            //    writer.write("<td valign=\"top\">");
            //    writer.write(StringEscapeUtils.escapeHtml(timeFormat.format(paymentItem.getPayTime())));
            //    writer.write("</td>");
            //    writer.write("<td valign=\"top\" align=\"right\">");
            //    writer.write(StringEscapeUtils.escapeHtml(ContractIdFormat.format(paymentItem.getContractId())));
            //    writer.write("</td>");
            //    PaymentProcessEvent.ClientItem client = paymentItem.getClient();
            //    PaymentProcessEvent.PersonItem person = null;
            //    if (null != client) {
            //        person = client.getPerson();
            //    }
            //    writer.write("<td valign=\"top\">");
            //    if (null != person) {
            //        writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(person.getSurname())));
            //    }
            //    writer.write("</td>");
            //    writer.write("<td valign=\"top\">");
            //    if (null != person) {
            //        writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(person.getFirstName())));
            //    }
            //    writer.write("</td>");
            //    writer.write("<td valign=\"top\">");
            //    if (null != person) {
            //        writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(person.getSecondName())));
            //    }
            //    writer.write("</td>");
            //    writer.write("<td valign=\"top\" align=\"right\">");
            //    writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(paymentItem.getSum())));
            //    writer.write("</td>");
            //    PaymentProcessEvent.CardItem card = paymentItem.getCard();
            //    writer.write("<td valign=\"top\" align=\"right\">");
            //    if (null != card) {
            //        writer.write(
            //                StringEscapeUtils.escapeHtml(StringUtils.defaultString(CardNoFormat.format(card.getCardNo()))));
            //    }
            //    writer.write("</td>");
            //    Long cardBalance = null;
            //    if (null != card) {
            //        cardBalance = card.getBalance();
            //    }
            //    writer.write("<td valign=\"top\" align=\"right\">");
            //    if (null != cardBalance) {
            //        writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(cardBalance)));
            //    }
            //    writer.write("</td>");
            //    writer.write("<td valign=\"top\" align=\"right\">");
            //    writer.write(StringEscapeUtils.escapeHtml(toString(paymentItem.getProcessResult())));
            //    writer.write("</td>");
            //    writer.write("<td valign=\"top\">");
            //    writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(paymentItem.getProcessError())));
            //    writer.write("</td>");
            //    writer.write("</tr>");
            //}
            //writer.write("</table>");
            //writer.write("</td>");
            //writer.write("</tr>");
            //writer.write("</table>");
            //writer.write("</body>");
            //writer.write("</html>");
            //writer.flush();
        }

        private static String toString(int value) {
            return Integer.toString(value);
        }

    }

    private final OrgItem org;

    public SyncEvent(Date eventTime, OrgItem org) {
        super(eventTime);
        this.org = org;
    }

    public OrgItem getOrg() {
        return org;
    }

}