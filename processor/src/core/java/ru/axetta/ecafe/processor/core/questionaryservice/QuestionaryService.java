/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.questionaryservice;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.Answer;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryStatus;

import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class QuestionaryService {

    private static Logger logger = LoggerFactory.getLogger(QuestionaryService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public List<Answer> getAnswers(Questionary questionary){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        TypedQuery<Answer> q = entityManager.createQuery("from Answer where questionary=:questionary", Answer.class);
        q.setParameter("questionary",questionary);
        List<Answer> answers = q.getResultList();
        transactionManager.commit(status);
        return answers;
    }

    public List<Questionary> getQuestionaries(){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        TypedQuery<Questionary> query = entityManager.createQuery("from Questionary", Questionary.class);
        List<Questionary> questionaries = query.getResultList();
        transactionManager.commit(status);
        return questionaries;
    }

    public QuestionariesRootElement parseQuestionaryByXML(File file) throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(QuestionariesRootElement.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return  (QuestionariesRootElement) jaxbUnmarshaller.unmarshal(file);
    }

    public List<RegistrationItem> registrationQuestionariesFromXML(QuestionariesRootElement questionariesRootElement, List<Long> idOfOrgList){
        List<QuestionaryItem> questionaryItemList = questionariesRootElement.getQuestionaryItemList();
        List<RegistrationItem> registrationItems = new ArrayList<RegistrationItem>(questionaryItemList.size());
        for (QuestionaryItem questionaryItem: questionaryItemList){
            try{
                Integer type = (questionaryItem.getType()==null?0:questionaryItem.getType());
                Questionary questionary = new Questionary(questionaryItem.getQuestion(), type);
                for (AnswerItem answerItem: questionaryItem.getAnswers()){
                    Answer answer = new Answer(answerItem.getAnswer(),questionary, answerItem.getWeight());
                    questionary.getAnswers().add(answer);
                }
                registrationQuestionary(questionary,idOfOrgList);
                registrationItems.add(new RegistrationItem(questionary, "OK"));
            } catch (Exception e){
                logger.error("Error registration questionary: ",e);
                registrationItems.add(new RegistrationItem(questionaryItem, e.getMessage()));
            }
        }
        return registrationItems;
    }

    public List<Org> getOrgs(Questionary questionary){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        Questionary questionary1 = entityManager.find(Questionary.class, questionary.getIdOfQuestionary());
        List<Org> orgs = new ArrayList<Org>();
        for (Org org: questionary1.getOrgs()){
            orgs.add(org);
        }
        transactionManager.commit(status);
        return orgs;
    }

    public Questionary updateQuestionary(Long id,String question, List<Long> idOfOrgList, List<Answer> answers) throws Exception{
        if(answers.isEmpty()) throw new  Exception("Questionnaire can not be registered without answers");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        Questionary currentQuestionary = entityManager.find(Questionary.class,id);
        currentQuestionary = currentQuestionary.update(question);
        currentQuestionary.getOrgs().clear();
        Set<Org> orgs = getOrgs(idOfOrgList);
        currentQuestionary.setOrgs(orgs);
        entityManager.persist(currentQuestionary);
        Query q = entityManager.createQuery("delete from Answer where questionary=:questionary");
        q.setParameter("questionary",currentQuestionary);
        q.executeUpdate();
        for (Answer answer: answers){
            answer.setQuestionary(currentQuestionary);
            entityManager.persist(answer);
        }
        transactionManager.commit(status);
        return currentQuestionary;
    }

    public void registrationQuestionary(Questionary questionary, List<Long> idOfOrgList) throws Exception{
        if(questionary.getAnswers().isEmpty()) throw new  Exception("Questionnaire can not be registered without answers");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        Set<Org> orgs = getOrgs(idOfOrgList);
        questionary.setOrgs(orgs);
        entityManager.persist(questionary);
        for (Answer answer: questionary.getAnswers()){
            entityManager.persist(answer);
        }
        transactionManager.commit(status);
    }

    protected Set<Org> getOrgs(List<Long> idOfOrgList) {
        TypedQuery<Org> query;
        if(!idOfOrgList.isEmpty()){
            query = entityManager.createQuery("from Org where idOfOrg in :curId order by idOfOrg asc", Org.class);
            query.setParameter("curId", idOfOrgList);
        } else {
            query = entityManager.createQuery("from Org order by idOfOrg asc", Org.class);
        }
        return new HashSet<Org>(query.getResultList());
    }

    public void extractQuestionary(Questionary questionary) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        Questionary currentQuestionary = entityManager.find(Questionary.class,questionary.getIdOfQuestionary());
        Query q = entityManager.createQuery("delete from Answer where questionary=:questionary");
        q.setParameter("questionary",currentQuestionary);
        q.executeUpdate();
        currentQuestionary.getOrgs().clear();
        entityManager.remove(currentQuestionary);
        transactionManager.commit(status);
    }

    public void changeStatusQuestionary(Questionary q, QuestionaryStatus status) throws Exception{
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(def);
        Questionary questionary = entityManager.find(Questionary.class, q.getIdOfQuestionary());
        switch (status){
            case START: questionary = questionary.start(); break;
            case STOP: questionary = questionary.stop(); break;
        }
        entityManager.persist(questionary);
        transactionManager.commit(transactionStatus);
    }

    public Boolean getStatus(Questionary questionary){
        Boolean result = false;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(def);
        questionary = entityManager.merge(questionary);
        result = questionary.getStatus() != QuestionaryStatus.START;
        transactionManager.commit(transactionStatus);
        return result;
    }
}
