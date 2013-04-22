/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.questionary;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.04.13
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class QuestionaryDAOService {

    @PersistenceContext
    private EntityManager entityManager;

    /* истена если клиент не ответил на данный вопрос или нет доступных для него анкет*/
    public Boolean checkClientToQuestionary(Long contractId, Long idOfAnswer){
        TypedQuery<Client> clientTypedQuery = entityManager.createQuery("from Client where contractId=:contractId",Client.class);
        clientTypedQuery.setParameter("contractId",contractId);
        Client client = clientTypedQuery.getSingleResult();
        Answer answer = entityManager.find(Answer.class, idOfAnswer);
        if(answer==null) return true;
        Questionary questionary = entityManager.find(Questionary.class, answer.getQuestionary().getIdOfQuestionary());
        if(!questionary.getStartStatus()) return false;
        TypedQuery<Answer> q = entityManager.createQuery("from Answer where questionary=:questionary", Answer.class);
        q.setParameter("questionary",questionary);
        List<Answer> answers = q.getResultList();
        TypedQuery<ClientAnswerByQuestionary> clientAnswerByQuestionaryTypedQuery = entityManager.createQuery("from ClientAnswerByQuestionary where client=:client and answer in :answer",ClientAnswerByQuestionary.class);
        clientAnswerByQuestionaryTypedQuery.setParameter("client",client);
        clientAnswerByQuestionaryTypedQuery.setParameter("answer",answers);
        return !clientAnswerByQuestionaryTypedQuery.getResultList().isEmpty();
    }

    public List<Answer> getAnswers(Questionary questionary){
        TypedQuery<Answer> q = entityManager.createQuery("from Answer where questionary=:questionary", Answer.class);
        q.setParameter("questionary",questionary);
        List<Answer> answers = q.getResultList();
        return answers;
    }

    public List<Questionary> getQuestionaries(){
        TypedQuery<Questionary> query = entityManager.createQuery("from Questionary where status!=:status", Questionary.class);
        query.setParameter("status", QuestionaryStatus.DELETED);
        List<Questionary> questionaries = query.getResultList();
        return questionaries;
    }

    public List<Org> getOrgs(Questionary questionary){
        Questionary questionary1 = entityManager.find(Questionary.class, questionary.getIdOfQuestionary());
        List<Org> orgs = new ArrayList<Org>();
        for (Org org: questionary1.getOrgs()){
            orgs.add(org);
        }
        return orgs;
    }

    public Questionary updateQuestionary(Long id,String question, String name, String description,
            List<Long> idOfOrgList, Integer type, List<Answer> answers,
            Date viewDate) throws Exception{
        if(answers.isEmpty()) throw new  Exception("Questionnaire can not be registered without answers");
        Questionary currentQuestionary = entityManager.find(Questionary.class,id);
        currentQuestionary = currentQuestionary.update(name, question, description);
        currentQuestionary.getOrgs().clear();
        Set<Org> orgs = getOrgs(idOfOrgList);
        currentQuestionary.setOrgs(orgs);
        currentQuestionary.setQuestionaryType(QuestionaryType.fromInteger(type));
        currentQuestionary.setViewDate(viewDate);
        entityManager.persist(currentQuestionary);
        Query q = entityManager.createQuery("delete from Answer where questionary=:questionary");
        q.setParameter("questionary",currentQuestionary);
        q.executeUpdate();
        for (Answer answer: answers){
            answer.setQuestionary(currentQuestionary);
            entityManager.persist(answer);
        }
        return currentQuestionary;
    }

    public void registrationQuestionary(Questionary questionary, List<Long> idOfOrgList) throws Exception{
        if(questionary.getAnswers().isEmpty()) throw new  Exception("Questionnaire can not be registered without answers");
        Set<Org> orgs = getOrgs(idOfOrgList);
        questionary.setOrgs(orgs);
        entityManager.persist(questionary);
        for (Answer answer: questionary.getAnswers()){
            entityManager.persist(answer);
        }
    }

    public Set<Org> getOrgs(List<Long> idOfOrgList) {
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
        Questionary currentQuestionary = entityManager.find(Questionary.class,questionary.getIdOfQuestionary());
        Query q = entityManager.createQuery("delete from Answer where questionary=:questionary");
        q.setParameter("questionary",currentQuestionary);
        q.executeUpdate();
        currentQuestionary.getOrgs().clear();
        entityManager.remove(currentQuestionary);
    }

    public void changeStatusQuestionary(Questionary q, QuestionaryStatus status) throws Exception{
        Questionary questionary = entityManager.find(Questionary.class, q.getIdOfQuestionary());
        switch (status){
            case START: questionary = questionary.start(); break;
            case STOP: questionary = questionary.stop(); break;
            case DELETED: questionary = questionary.deleted(); break;
        }
        entityManager.persist(questionary);
    }

    public Boolean getStatus(Questionary questionary){
        Boolean result = false;
        questionary = entityManager.merge(questionary);
        result = questionary.getStatus() == QuestionaryStatus.INACTIVE;
        return result;
    }


}
