/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import com.ibm.jms.JMSTextMessage;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


@Component
@Scope("singleton")
@DependsOn("runtimeContext")
public class ETPProaktivClient implements MessageListener,ExceptionListener {
    private JmsFactoryFactory jmsFactoryFactory;
    private JmsConnectionFactory jmsConnectionFactory;
    private Connection jmsConnection;
    private Session jmsProducerSession;
    private Session jmsStatusSession;
    private MessageProducer producer;
    private MessageConsumer consumerStatus;
    private Queue queueProducer;
    private Queue queueStatusConsumer;

    private static final Logger logger = LoggerFactory.getLogger(ETPProaktivClient.class);

    @PostConstruct
    private synchronized void init() {
        //Настройка активности сервиса ЕТП для Проактива
        if (!RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.queue.out.notification.isOn", "false").equals("true")) return;
        String nodes = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.queue.out.notification.nodes", "");
        if (StringUtils.isEmpty(nodes)) return;
        String[] arr = nodes.split(",");
        List<String> nodesList = Arrays.asList(arr);
        //Проверка вохможности запуска сервиса на текущей ноде
        if (!nodesList.contains(RuntimeContext.getInstance().getNodeName())) {
            return;
        }

        logger.info("Start init ETP Proactive connection");
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        try {
            //Настраиваем подключение к ЕТП. Данный конфиг пока такой же как в ETPMVClient
            jmsFactoryFactory = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            jmsConnectionFactory = jmsFactoryFactory.createConnectionFactory();
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_CONNECTION_NAME_LIST, properties.getProperty("ecafe.processor.etp.hosts", "etp3.sm-soft.ru(2424)"));
            jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_OPTIONS, WMQConstants.WMQ_CLIENT_RECONNECT);
            jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_TIMEOUT, Integer.parseInt(properties.getProperty("ecafe.processor.etp.reconnect_timeout", "600")));
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getProperty("ecafe.processor.etp.channel", "CLNT.SAMPLE.SVRCONN"));
            jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, properties.getProperty("ecafe.processor.etp.qmgr", "GU01QM"));
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, properties.getProperty("ecafe.processor.etp.application", "ISPP"));
            jmsConnectionFactory.setStringProperty(WMQConstants.USERID, properties.getProperty("ecafe.processor.etp.user", "sample"));
            jmsConnectionFactory.setStringProperty(WMQConstants.PASSWORD, properties.getProperty("ecafe.processor.etp.password", "sample"));

            //Создаем само соединение
            jmsConnection = jmsConnectionFactory.createConnection();
            //Создаем сессию для отправки сообщений в очередь
            jmsProducerSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //Создаем сессию для получения статусов
            jmsStatusSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //Создаем саму очередь
            queueProducer = jmsProducerSession.createQueue(properties.getProperty("ecafe.processor.etp.queue.out.notification", "PP.NOTIFICATION_OUT"));
            //Запуск соединения
            jmsConnection.start();
            //Создаем отправщика в очередь
            producer = jmsProducerSession.createProducer(queueProducer);

            //Создаем очередь на чтение
            queueStatusConsumer = jmsStatusSession.createQueue(properties.getProperty("ecafe.processor.etp.queue.in.notification.status", "pp.notification_ack"));
            //Создаем читальщика
            consumerStatus = jmsStatusSession.createConsumer(queueStatusConsumer);
            //Указываем, что будем использовать метод onMessage из текущего класса
            consumerStatus.setMessageListener(this);

            logger.info("End init ETP Proactive connection");
        } catch (Exception e) {
            logger.error("Error in ETP Proactive connection init: ", e);
        }
    }

    @PreDestroy
    private void close() {
        try {
            jmsConnection.stop();
        } catch (Exception e) {
            logger.error("Error in stop ETP Proactive connection: ", e);
        }
    }

    public ETPProaktivClient() throws Exception {

    }
    @Override
    public void onMessage(Message message) {
        try {
            String content = ((JMSTextMessage) message).getText();
            RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).processIncoming(content);
        } catch (JMSException e) {
            logger.error("Error in parse ETP Proaktiv message: " + message.toString() + " Stack trace: ", e);
        }
    }

    @Override
    public void onException(JMSException ex) {
        logger.error("Exception in ETPProaktivClient. Try to reconnect");
        init();
    }

    public void sendMessage(String message) throws Exception {
        TextMessage textMessage = jmsProducerSession.createTextMessage(message);
        producer.send(textMessage);
    }

    public void sendStatus(String message) throws Exception {
        TextMessage textMessage = jmsProducerSession.createTextMessage(message);
        producer.send(textMessage);
    }
}
