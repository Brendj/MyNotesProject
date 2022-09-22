package ru.axetta.ecafe.processor.core.service;

import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.persistence.AppMezhvedErrorSendKafka;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;
import ru.axetta.ecafe.processor.core.zlp.kafka.ZlpLoggingListenableFutureCallback;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.PersonBenefitCheckRequest;

import java.util.*;

@Component
@Scope("singleton")
public class ReSendKafkaService {
    final static String RESEND_KAFKA = "ReSendKafkaService";
    private static final Logger logger = LoggerFactory.getLogger(ReSendKafkaService.class);
    private final KafkaTemplate<String, String> reSendKafkaTemplate;

    public ReSendKafkaService(KafkaTemplate<String, String> reSendKafkaTemplate) {
        this.reSendKafkaTemplate = reSendKafkaTemplate;
    }

    public static class ResendService implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(ReSendKafkaService.class)
                        .start();
            } catch (Exception e) {
                logger.error("Failed execute ResendService for ZLP kafka ", e);
            }
        }
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().
                getProperty("ecafe.processor.zlp.kafka.server", "199");
        String[] nodes = reqInstance.split(",");
        for (String node : nodes) {
            if (!StringUtils.isBlank(instance) && !StringUtils.isBlank(reqInstance)
                    && instance.trim().equals(node.trim())) {
                return true;
            }
        }
        return false;
    }

    public void scheduleSync() throws Exception {
        if (!isOn())
            return;
        String syncScheduleSync = RuntimeContext.getInstance().getConfigProperties().
                getProperty("ecafe.processor.zlp.kafka.server.time", "0 0/10 0 ? * *");
        try {
            JobDetail jobDetailSync = new JobDetail(RESEND_KAFKA, Scheduler.DEFAULT_GROUP, ResendService.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();

            CronTrigger triggerSync = new CronTrigger(RESEND_KAFKA, Scheduler.DEFAULT_GROUP);
            triggerSync.setCronExpression(syncScheduleSync);
            if (scheduler.getTrigger(RESEND_KAFKA, Scheduler.DEFAULT_GROUP) != null) {
                scheduler.deleteJob(RESEND_KAFKA, Scheduler.DEFAULT_GROUP);
            }
            scheduler.scheduleJob(jobDetailSync, triggerSync);
            scheduler.start();
        } catch (Exception e) {
            logger.error("Failed auto scheduleSync ReSendKafkaService", e);
        }
    }


    public void start() throws Exception {
        //Получаем все данные, которые не получилось отправить в прошлый раз
        List<AppMezhvedErrorSendKafka> appMezhvedErrorSendKafkas =
                RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getMezvedKafkaError();
        for (AppMezhvedErrorSendKafka appMezhvedErrorSendKafka: appMezhvedErrorSendKafkas) {
            String topic = appMezhvedErrorSendKafka.getTopic();
            String msg = appMezhvedErrorSendKafka.getMsg();
            ApplicationForFood applicationForFood = appMezhvedErrorSendKafka.getApplicationForFood();
            //Собираем message
            RequestValidationData data = BenefitKafkaService.getBenefitData(applicationForFood);
            Integer type = appMezhvedErrorSendKafka.getType();
            AbstractPushData request = null;
            if (type == 1)
                request = GuardianshipValidationRequest.class.
                        getDeclaredConstructor(RequestValidationData.class).newInstance(data);
            if (type == 2)
                request = new PersonBenefitCheckRequest(applicationForFood);
            if (type == 3)
                request = DocValidationRequest.class.
                        getDeclaredConstructor(RequestValidationData.class).newInstance(data);
            if (request != null) {
                Message<AbstractPushData> message = MessageBuilder.withPayload(request).build();
                //Повторная отправка
                ListenableFuture<SendResult<String, String>> future = reSendKafkaTemplate.send(topic, msg);
                future.addCallback(new ZlpLoggingListenableFutureCallback(message, msg, topic, type, data.getIdOfApplicationForFood(), applicationForFood.getServiceNumber(), appMezhvedErrorSendKafka));
            }
        }
    }
}
