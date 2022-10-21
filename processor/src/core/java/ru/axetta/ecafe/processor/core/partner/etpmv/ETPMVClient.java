/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import com.ibm.jms.JMSTextMessage;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by nuc on 29.10.2018.
 */
@Component
@Scope("singleton")
@DependsOn("runtimeContext")
public class ETPMVClient implements MessageListener, ExceptionListener {

    private static final String HOST = "etp3.sm-soft.ru"; // Host name or IP address
    private static final int PORT = 2424; // Listener port for your queue manager
    private static final String CHANNEL = "CLNT.SAMPLE.SVRCONN"; // Channel name
    private static final String QMGR = "GU01QM"; // Queue manager name
    private static final String APP_USER = "sample"; // User name that application uses to connect to MQ
    private static final String APP_PASSWORD = "sample"; // Password that the application uses to connect to MQ
    private static final String QUEUE_NAME = "SAMPLE.APPLICATION_INC"; // Queue that the application uses to put and get messages to and from
    public static final String ERROR_STATUS = "103099";

    private JmsFactoryFactory jmsFactoryFactory;
    private JmsConnectionFactory jmsConnectionFactory;
    private Connection jmsConnection;
    private Session jmsConsumerSession;
    private Session jmsProducerSession;
    private Session jmsStatusSession;
    private MessageConsumer consumer;
    private MessageProducer producer;
    private MessageProducer consumerBK;
    private MessageProducer producerStatusBK;
    private MessageConsumer producerBK;
    private MessageConsumer consumerStatus;
    private Queue queueConsumer;
    private Queue queueProducer;
    private Queue bkQueueConsumer;
    private Queue bkStatusQueueConsumer;
    private Queue bkQueueProducer;
    private Queue queueStatusConsumer;

    private static final Logger logger = LoggerFactory.getLogger(ETPMVClient.class);

    @PostConstruct
    private synchronized void init() {

        if (!RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.isOn", "false").equals("true")) return;
        String nodes = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.nodes", "");
        if (StringUtils.isEmpty(nodes)) return;
        String[] arr = nodes.split(",");
        List<String> nodesList = Arrays.asList(arr);
        if (!nodesList.contains(RuntimeContext.getInstance().getNodeName())) {
            return;
        }

        logger.info("Start init ETP connection");
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        try {
            jmsFactoryFactory = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            jmsConnectionFactory = jmsFactoryFactory.createConnectionFactory();
            //jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_HOST_NAME, properties.getProperty("ecafe.processor.etp.host", "etp3.sm-soft.ru"));
            //jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_PORT, Integer.parseInt(properties.getProperty("ecafe.processor.etp.port", "2424")));
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_CONNECTION_NAME_LIST, properties.getProperty("ecafe.processor.etp.hosts", "etp3.sm-soft.ru(2424)"));
            jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_OPTIONS, WMQConstants.WMQ_CLIENT_RECONNECT);
            jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_TIMEOUT, Integer.parseInt(properties.getProperty("ecafe.processor.etp.reconnect_timeout", "600")));
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getProperty("ecafe.processor.etp.channel", "CLNT.SAMPLE.SVRCONN"));
            jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, properties.getProperty("ecafe.processor.etp.qmgr", "GU01QM"));
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, properties.getProperty("ecafe.processor.etp.application", "ISPP"));
            jmsConnectionFactory.setStringProperty(WMQConstants.USERID, properties.getProperty("ecafe.processor.etp.user", "sample"));
            jmsConnectionFactory.setStringProperty(WMQConstants.PASSWORD, properties.getProperty("ecafe.processor.etp.password", "sample"));

            jmsConnection = jmsConnectionFactory.createConnection();
            jmsConsumerSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            jmsProducerSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            jmsStatusSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            queueProducer = jmsProducerSession.createQueue(properties.getProperty("ecafe.processor.etp.queue.out", "SAMPLE.APPLICATION_OUT"));
            //bkQueueProducer = jmsConsumerSession.createQueue(properties.getProperty("ecafe.processor.etp.queue.out", "SAMPLE.APPLICATION_OUT"));

            jmsConnection.start();

            String consumerNode = properties.getProperty("ecafe.processor.etp.consumer.node", "");
            if (RuntimeContext.getInstance().getNodeName().equals(consumerNode)) {
                queueConsumer = jmsConsumerSession.createQueue(properties.getProperty("ecafe.processor.etp.queue.in", "SAMPLE.APPLICATION_INC"));
                consumer = jmsConsumerSession.createConsumer(queueConsumer);
                consumer.setMessageListener(this);
                jmsConnection.setExceptionListener(this);

                queueStatusConsumer = jmsStatusSession.createQueue(properties.getProperty("ecafe.processor.etp.queue.in.status", "SAMPLE.STATUS_INC"));
                consumerStatus = jmsStatusSession.createConsumer(queueStatusConsumer);
                consumerStatus.setMessageListener(this);

                bkQueueConsumer = jmsProducerSession.createQueue(properties.getProperty("ecafe.processor.etp.queue.in.bk", "SAMPLE.APPLICATION_INC.BK"));
                consumerBK = jmsProducerSession.createProducer(bkQueueConsumer);

                bkStatusQueueConsumer = jmsProducerSession.createQueue(properties.getProperty("ecafe.processor.etp.queue.in.status.bk", "SAMPLE.STATUS_INC.BK"));
                producerStatusBK = jmsProducerSession.createProducer(bkStatusQueueConsumer);
            }

            producer = jmsProducerSession.createProducer(queueProducer);

            logger.info("End init ETP connection");
        } catch (Exception e) {
            logger.error("Error in ETP connection init: ", e);
        }
    }

    @PreDestroy
    private void close() {
        try {
            jmsConnection.stop();
        } catch (Exception e) {
            logger.error("Error in stop ETP connection: ", e);
        }
    }

    public ETPMVClient() throws Exception {

    }

    @Override
    public void onMessage(Message message) {
        try {
            String content = ((JMSTextMessage) message).getText();
            RuntimeContext.getAppContext().getBean(ETPMVService.class).processIncoming(content);
        } catch (JMSException e) {
            logger.error("Error in parse ETP message: " + message.toString() + " Stack trace: ", e);
        }
    }

    @Override
    public void onException(JMSException ex) {
        logger.error("Exception in ETPMVClient. Try to reconnect");
        init();
    }

    public void watchdogRun() {
        if (!RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.isOn", "false").equals("true")) return;
        if (jmsConnection == null) init();
    }

    public void sendStatus(String message) throws Exception {
        TextMessage textMessage = jmsProducerSession.createTextMessage(message);
        producer.send(textMessage);
    }

    public void addToBKQueue(String message) throws Exception {
        TextMessage textMessage = jmsProducerSession.createTextMessage(message);
        consumerBK.send(textMessage);
    }

    public void addToStatusBKQueue(String message) throws Exception {
        TextMessage textMessage = jmsProducerSession.createTextMessage(message);
        producerStatusBK.send(textMessage);
    }
}
