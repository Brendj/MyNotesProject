/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.taskexecutor;

import org.springframework.context.annotation.DependsOn;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.logic.MeshClientCardRefService;
import ru.axetta.ecafe.processor.core.partner.mesh.json.CardPropertiesEnum;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Category;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Parameter;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@DependsOn("runtimeContext")
@Service
public class MeshCardNotifyTaskExecutor {
    private static final String CRON_EXP_PROPERTY = "ecafe.processing.mesh.card.taskexecutor.cron";
    private static final String TARGET_NODE_PROPERTY = "ecafe.processing.mesh.card.taskexecutor.node";
    private static final String JOB_NAME = "MeshCardNotify";
    private String targetNode = "";
    private final Logger log = LoggerFactory.getLogger(MeshCardNotifyTaskExecutor.class);
    private Scheduler scheduler;

    private MeshClientCardRefService meshClientCardRefService;

    public static class MeshCardNotify implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            RuntimeContext.getAppContext().getBean(MeshCardNotifyTaskExecutor.class).run();
        }
    }

    public void scheduleSync() throws Exception {
        targetNode = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(TARGET_NODE_PROPERTY, "");
        if(!RuntimeContext.getInstance().getNodeName().equals(targetNode)){ // Если не целевая нода, то шедулер даже не пытаемся запускать
            return;
        }
        String cronExp = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(CRON_EXP_PROPERTY, "");

        if(!cronExp.isEmpty()) { // Если крона не задана, то так же не пытаемся создавать шедулер
            meshClientCardRefService = RuntimeContext.getAppContext().getBean(MeshClientCardRefService.class);

            JobDetail jobDetailCardNotify = new JobDetail(JOB_NAME, Scheduler.DEFAULT_GROUP,
                    MeshCardNotifyTaskExecutor.MeshCardNotify.class);

            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();

            CronTrigger trigger = new CronTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP);
            trigger.setCronExpression(cronExp);

            scheduler.scheduleJob(jobDetailCardNotify, trigger);
            scheduler.start();
        }
    }

    public void run(){
        Session session = null;
        Transaction transaction = null;
        try {
            Date lastProcessing = getProcessingTime();
            session = RuntimeContext.getInstance().createPersistenceSession();
            List<Card> cards = DAOUtils.getAllCardForMESHByTime(session, lastProcessing);
            for(Card card : cards){
                if(card.getClient() != null && StringUtils.isEmpty(card.getClient().getMeshGUID())){
                    continue;
                }

                try {
                    transaction = session.beginTransaction();

                    MeshClientCardRef ref = DAOUtils.findMeshClientCardRefByCard(session, card.getIdOfCard());

                    errorCorrection(card, ref, session); // Устранение ошибок дублей связок и неоднозначности связей

                    if(ref == null && card.getClient() == null) {
                        continue;
                    }
                    if(ref == null && card.getClient() != null) { // Client exists, but no ref
                        MeshClientCardRef newRef = meshClientCardRefService.createRef(card);
                        session.save(newRef);
                    } else if(card.getClient() == null){ // Ref exists, but card without owner
                        MeshClientCardRef deletedRef = meshClientCardRefService.deleteRef(ref);
                        session.delete(deletedRef);
                    } else if(!card.getClient().equals(ref.getClient())){ // If the card owner has changed
                        MeshClientCardRef changeRef = meshClientCardRefService.changeRef(ref);
                        session.merge(changeRef);
                    } else {
                        MeshClientCardRef updatedRef = meshClientCardRefService.updateRef(ref);
                        session.merge(updatedRef);
                    }

                    transaction.commit();
                    transaction = null;

                    session.flush();
                } catch (Exception e){
                    log.error("", e);
                } finally {
                    HibernateUtils.rollback(transaction, log);
                }
            }
        } catch (Exception e){
            log.error("Critical exception, task skipped ", e);
        } finally {
            HibernateUtils.close(session, log);
        }
    }

    private void errorCorrection(Card card, MeshClientCardRef ref, Session session) {
        if(ref == null){
            return;
        }

        List<Category> categories = getDuplicateCards(
                meshClientCardRefService.getCardCategoryByClient(ref.getClient()), card);
        if(CollectionUtils.isEmpty(categories)){
            return;
        }

        if(ref.getIdOfRefInExternalSystem() == null){
            for(Category category : categories){
                meshClientCardRefService.deleteRef(category.getId(), ref.getClient().getMeshGUID());
            }
            session.delete(ref);
        } else {
            for(Category category : categories){
                if(category.getId().equals(ref.getIdOfRefInExternalSystem())){
                    continue;
                }
                meshClientCardRefService.deleteRef(category.getId(), ref.getClient().getMeshGUID());
            }
        }
    }

    private List<Category> getDuplicateCards(List<Category> cardCategory, Card card) {
        List<Category> result = new LinkedList<>();

        for(Category c : cardCategory){
            if(isDuplicate(c, card.getCardNo(), card.getCardPrintedNo())){
                result.add(c);
            }
        }
        return result;
    }

    private boolean isDuplicate(Category category, Long cardNo, Long cardPrintedNo) {
        boolean firstCoincidence = false;
        boolean secondCoincidence = false;

        for(Object o : category.getParameterValues()){
            Parameter p = (Parameter) o;
            if(p.getName().equals(CardPropertiesEnum.CARD_UID.getFieldName())){
                Long uid = Long.valueOf(p.getValue());
                firstCoincidence = uid.equals(cardNo);
            } else if (p.getName().equals(CardPropertiesEnum.BOARD_CARD_NUMBER.getFieldName())){
                Long bordNum = Long.valueOf(p.getValue());
                secondCoincidence = bordNum.equals(cardPrintedNo);
            }
        }

        return firstCoincidence && secondCoincidence;
    }

    private Date getProcessingTime() {
        try{
            Trigger trigger = scheduler.getTrigger(JOB_NAME, Scheduler.DEFAULT_GROUP);
            long  delta = trigger.getNextFireTime().getTime() - trigger.getPreviousFireTime().getTime();
            return new Date(trigger.getPreviousFireTime().getTime() - delta);
        } catch (Exception e){
            return CalendarUtils.startOfDay(new Date());
        }
    }
}
