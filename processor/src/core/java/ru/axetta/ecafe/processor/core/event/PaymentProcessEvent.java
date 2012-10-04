/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.event;

import ru.axetta.ecafe.processor.core.DailyFileCreator;
import ru.axetta.ecafe.processor.core.card.CardNoFormat;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.payment.PaymentResponse;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.ReportDocument;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 12:10:58
 * To change this template use File | Settings | File Templates.
 */
public class PaymentProcessEvent extends BasicEvent {

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 15.12.2009
     * Time: 12:10:58
     * To change this template use File | Settings | File Templates.
     */
    public static class RawEvent extends BasicEvent {

        private final PaymentRequest paymentRequest;
        private final PaymentResponse paymentResponse;

        public RawEvent(Date eventTime, PaymentRequest paymentRequest, PaymentResponse paymentResponse) {
            super(eventTime);
            this.paymentRequest = paymentRequest;
            this.paymentResponse = paymentResponse;
        }

        public PaymentRequest getPaymentRequest() {
            return paymentRequest;
        }

        public PaymentResponse getPaymentResponse() {
            return paymentResponse;
        }

        @Override
        public String toString() {
            return "RawEvent{" + "paymentRequest=" + paymentRequest + ", paymentResponse=" + paymentResponse + "} "
                    + super.toString();
        }
    }

    public static class UserItem {

        private final Long idOfUser;
        private final String userName;

        public UserItem(User user) {
            if (null == user) {
                this.idOfUser = null;
                this.userName = null;
            } else {
                this.idOfUser = user.getIdOfUser();
                this.userName = user.getUserName();
            }
        }

        public Long getIdOfUser() {
            return idOfUser;
        }

        public String getUserName() {
            return userName;
        }
    }

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            if (null == contragent) {
                this.idOfContragent = null;
                this.contragentName = null;
            } else {
                this.idOfContragent = contragent.getIdOfContragent();
                this.contragentName = contragent.getContragentName();
            }
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    public static class PersonItem {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

        public String getFirstName() {
            return firstName;
        }

        public String getSurname() {
            return surname;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getIdDocument() {
            return idDocument;
        }

        public PersonItem(Person person) {
            if (null == person) {
                this.firstName = null;
                this.surname = null;
                this.secondName = null;
                this.idDocument = null;
            } else {
                this.firstName = person.getFirstName();
                this.surname = person.getSurname();
                this.secondName = person.getSecondName();
                this.idDocument = person.getIdDocument();
            }
        }
    }

    public static class ClientItem {

        private final Long contractId;
        private final PersonItem person;
        private final Long balance;

        public Long getContractId() {
            return contractId;
        }

        public PersonItem getPerson() {
            return person;
        }

        public Long getBalance() {
            return balance;
        }

        public ClientItem(Client client) {
            this.contractId = client.getContractId();
            this.person = new PersonItem(client.getPerson());
            this.balance = client.getBalance();
        }
    }

    public static class CardItem {

        private final Long idOfCard;
        private final Long cardNo;

        public Long getIdOfCard() {
            return idOfCard;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public CardItem(Card card) {
            this.idOfCard = card.getIdOfCard();
            this.cardNo = card.getCardNo();
        }
    }

    public static class PaymentItem {

        private final String idOfPayment;
        private final Long contractId;
        private final Date payTime;
        private final long sum;
        private final int processResult;
        private final int paymentMethod;
        private final String addPaymentMethod;
        private final String addIdOfPayment;
        private final String processError;
        private final ClientItem client;
        private final CardItem card;

        public String getIdOfPayment() {
            return idOfPayment;
        }

        public Long getContractId() {
            return contractId;
        }

        public Date getPayTime() {
            return payTime;
        }

        public long getSum() {
            return sum;
        }

        public int getProcessResult() {
            return processResult;
        }

        public String getProcessError() {
            return processError;
        }

        public ClientItem getClient() {
            return client;
        }

        public CardItem getCard() {
            return card;
        }

        public int getPaymentMethod() {
            return paymentMethod;
        }

        public String getAddPaymentMethod() {
            return addPaymentMethod;
        }

        public String getAddIdOfPayment() {
            return addIdOfPayment;
        }

        public PaymentItem(PaymentResponse.ResPaymentRegistry.Item responseItem, Client client, Card card) {
            this.idOfPayment = responseItem.getPayment().getIdOfPayment();
            this.contractId = responseItem.getPayment().getContractId();
            this.payTime = responseItem.getPayment().getPayTime();
            this.sum = responseItem.getPayment().getSum();
            this.processResult = responseItem.getResult();
            this.processError = responseItem.getError();
            if (null == client) {
                this.client = null;
            } else {
                this.client = new ClientItem(client);
            }
            if (null == card) {
                this.card = null;
            } else {
                this.card = new CardItem(card);
            }
            this.paymentMethod = responseItem.getPayment().getPaymentMethod();
            this.addPaymentMethod = responseItem.getPayment().getAddPaymentMethod();
            this.addIdOfPayment = responseItem.getPayment().getAddIdOfPayment();
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
                org.hibernate.Transaction transaction = null;
                org.hibernate.Session session = sessionFactory.openSession();
                try {
                    transaction = session.beginTransaction();

                    User user = (User) session.get(User.class, rawEvent.getPaymentRequest().getIdOfUser());
                    Contragent contragent = (Contragent) session
                            .get(Contragent.class, rawEvent.getPaymentRequest().getIdOfContragent());

                    PaymentRequest paymentRequest = rawEvent.getPaymentRequest();
                    PaymentResponse paymentResponse = rawEvent.getPaymentResponse();
                    List<PaymentItem> paymentItems = new LinkedList<PaymentItem>();

                    Enumeration<PaymentResponse.ResPaymentRegistry.Item> paymentResponses = paymentResponse
                            .getResAccRegistry().getItems();
                    while (paymentResponses.hasMoreElements()) {
                        PaymentResponse.ResPaymentRegistry.Item responseItem = paymentResponses.nextElement();
                        Long idOfClient = responseItem.getIdOfClient();
                        Client client = null;
                        if (null != idOfClient) {
                            client = (Client) session.get(Client.class, idOfClient);
                        }
                        Long idOfCard = responseItem.getIdOfCard();
                        Card card = null;
                        if (null != idOfCard) {
                            card = (Card) session.get(Card.class, idOfCard);
                        }
                        paymentItems.add(new PaymentItem(responseItem, client, card));
                    }

                    PaymentProcessEvent paymentProcessEvent = new PaymentProcessEvent(rawEvent.getEventTime(),
                            new UserItem(user), new ContragentItem(contragent), paymentRequest.getVersion(),
                            paymentRequest.getSyncTime(), paymentRequest.getIdOfPacket(), paymentItems);

                    Properties properties = new Properties();
                    ReportPropertiesUtils.addProperties(properties, paymentProcessEvent, timeFormat);
                    ReportPropertiesUtils.addProperties(properties, user, "user.");
                    ReportPropertiesUtils.addProperties(properties, contragent, "contragent.");

                    transaction.commit();
                    transaction = null;

                    eventProcessor.processEvent(paymentProcessEvent, properties, eventDocumentBuilders);
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
            String fullName = PaymentProcessEvent.class.getCanonicalName();
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
            PaymentProcessEvent paymentProcessEvent = (PaymentProcessEvent) event;
            FileOutputStream outputStream = new FileOutputStream(eventDocumentFile);
            try {
                writeReportDocumentTo(paymentProcessEvent, outputStream, timeFormat);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
            return new ReportDocument(eventDocumentFile);
        }

        private static void writeReportDocumentTo(PaymentProcessEvent event, OutputStream outputStream,
                DateFormat timeFormat) throws IOException {
            Writer writer = new OutputStreamWriter(outputStream, "utf-8");
            writer.write("<html>");
            writer.write("<head>");
            writer.write("<title>");
            writer.write(StringEscapeUtils.escapeHtml(String.format("Платежи от контрагента \"%s\"",
                    StringUtils.defaultString(event.getContragent().getContragentName()))));
            writer.write("</title>");
            writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            writer.write("<meta http-equiv=\"Content-Language\" content=\"ru\">");
            writer.write("</head>");
            writer.write("<body>");
            writer.write("<table>");
            writer.write("<tr>");
            writer.write("<td align=\"center\">");
            writer.write(StringEscapeUtils.escapeHtml(
                    String.format("Платежи от контрагента \"%s\", проведенные от имени пользователя \"%s\" %s",
                            StringUtils.defaultString(event.getContragent().getContragentName()),
                            StringUtils.defaultString(event.getUser().getUserName()),
                            timeFormat.format(event.getSyncTime()))));
            writer.write("</td>");
            writer.write("</tr>");
            writer.write("<tr>");
            writer.write("<td align=\"center\">");
            writer.write("<table>");
            writer.write("<tr>");
            writer.write("<td align=\"right\">");
            writer.write(StringEscapeUtils.escapeHtml("Идентификатор платежа в системе контрагента"));
            writer.write("</td>");
            writer.write("<td>");
            writer.write(StringEscapeUtils.escapeHtml("Время совершения платежа по данным контрагента"));
            writer.write("</td>");
            writer.write("<td align=\"right\">");
            writer.write(StringEscapeUtils.escapeHtml("Номер счета, указанный контрагентом"));
            writer.write("</td>");
            writer.write("<td align=\"right\">");
            writer.write(StringEscapeUtils.escapeHtml("Номер договора"));
            writer.write("</td>");
            writer.write("<td>");
            writer.write(StringEscapeUtils.escapeHtml("Фамилия"));
            writer.write("</td>");
            writer.write("<td>");
            writer.write(StringEscapeUtils.escapeHtml("Имя"));
            writer.write("</td>");
            writer.write("<td>");
            writer.write(StringEscapeUtils.escapeHtml("Отчество"));
            writer.write("</td>");
            writer.write("<td align=\"right\">");
            writer.write(StringEscapeUtils.escapeHtml("Сумма, указанная контрагентом, руб."));
            writer.write("</td>");
            writer.write("<td>");
            writer.write(StringEscapeUtils.escapeHtml("Метод оплаты"));
            writer.write("</td>");
            writer.write("<td>");
            writer.write(StringEscapeUtils.escapeHtml("Метод оплаты (доп.)"));
            writer.write("</td>");
            writer.write("<td>");
            writer.write(StringEscapeUtils.escapeHtml("Идентификатор платежа (доп.)"));
            writer.write("</td>");
            writer.write("<td align=\"right\">");
            writer.write(StringEscapeUtils.escapeHtml("Физический номер карты, на которую перечислены средства"));
            writer.write("</td>");
            writer.write("<td align=\"right\">");
            writer.write(StringEscapeUtils.escapeHtml("Баланс клиента на момент отправки уведомления, руб."));
            writer.write("</td>");
            writer.write("<td align=\"right\">");
            writer.write(StringEscapeUtils.escapeHtml("Код результата обработки"));
            writer.write("</td>");
            writer.write("<td>");
            writer.write(StringEscapeUtils.escapeHtml("Сообщение об ошибке"));
            writer.write("</td>");

            writer.write("</tr>");
            for (PaymentItem paymentItem : event.getPayments()) {
                writer.write("<tr>");
                writer.write("<td valign=\"top\" align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(paymentItem.getIdOfPayment()));
                writer.write("</td>");
                writer.write("<td valign=\"top\">");
                writer.write(StringEscapeUtils.escapeHtml(timeFormat.format(paymentItem.getPayTime())));
                writer.write("</td>");
                writer.write("<td valign=\"top\" align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(paymentItem.getContractId().toString()));
                writer.write("</td>");
                ClientItem client = paymentItem.getClient();
                if (null != client) {
                    writer.write("<td valign=\"top\" align=\"right\">");
                    writer.write(StringEscapeUtils.escapeHtml(ContractIdFormat.format(client.getContractId())));
                    writer.write("</td>");
                } else {
                    writer.write("<td valign=\"top\" align=\"right\">");
                    writer.write("</td>");
                }
                PersonItem person = null;
                if (null != client) {
                    person = client.getPerson();
                }
                writer.write("<td valign=\"top\">");
                if (null != person) {
                    writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(person.getSurname())));
                }
                writer.write("</td>");
                writer.write("<td valign=\"top\">");
                if (null != person) {
                    writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(person.getFirstName())));
                }
                writer.write("</td>");
                writer.write("<td valign=\"top\">");
                if (null != person) {
                    writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(person.getSecondName())));
                }
                writer.write("</td>");
                writer.write("<td valign=\"top\" align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(paymentItem.getSum())));
                writer.write("</td>");
                writer.write("<td valign=\"top\">");
                writer.write(StringEscapeUtils.escapeHtml(
                        ClientPayment.PAYMENT_METHOD_NAMES[paymentItem.getPaymentMethod()]));
                writer.write("</td>");
                writer.write("<td valign=\"top\">");
                writer.write(
                        StringEscapeUtils.escapeHtml(StringUtils.defaultString(paymentItem.getAddPaymentMethod())));
                writer.write("</td>");
                writer.write("<td valign=\"top\">");
                writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(paymentItem.getAddIdOfPayment())));
                writer.write("</td>");
                CardItem card = paymentItem.getCard();
                writer.write("<td valign=\"top\" align=\"right\">");
                if (null != card) {
                    writer.write(StringEscapeUtils.escapeHtml(
                            StringUtils.defaultString(CardNoFormat.format(card.getCardNo()))));
                }
                writer.write("</td>");
                Long cardBalance = null;
                if (null != client) {
                    cardBalance = client.getBalance();
                }
                writer.write("<td valign=\"top\" align=\"right\">");
                if (null != cardBalance) {
                    writer.write(StringEscapeUtils.escapeHtml(CurrencyStringUtils.copecksToRubles(cardBalance)));
                }
                writer.write("</td>");
                writer.write("<td valign=\"top\" align=\"right\">");
                writer.write(StringEscapeUtils.escapeHtml(toString(paymentItem.getProcessResult())));
                writer.write("</td>");
                writer.write("<td valign=\"top\">");
                writer.write(StringEscapeUtils.escapeHtml(StringUtils.defaultString(paymentItem.getProcessError())));
                writer.write("</td>");
                writer.write("</tr>");
            }
            writer.write("</table>");
            writer.write("</td>");
            writer.write("</tr>");
            writer.write("</table>");
            writer.write("</body>");
            writer.write("</html>");
            writer.flush();
        }

        private static String toString(int value) {
            return Integer.toString(value);
        }

    }

    private final UserItem user;
    private final ContragentItem contragent;
    private final long version;
    private final Date syncTime;
    private final long idOfPacket;
    private final List<PaymentItem> payments;

    public PaymentProcessEvent(Date eventTime, UserItem user, ContragentItem contragent, long version, Date syncTime,
            long idOfPacket, List<PaymentItem> payments) {
        super(eventTime);
        this.user = user;
        this.contragent = contragent;
        this.version = version;
        this.syncTime = syncTime;
        this.idOfPacket = idOfPacket;
        this.payments = payments;
    }

    public UserItem getUser() {
        return user;
    }

    public ContragentItem getContragent() {
        return contragent;
    }

    public long getVersion() {
        return version;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public long getIdOfPacket() {
        return idOfPacket;
    }

    public List<PaymentItem> getPayments() {
        return payments;
    }
}