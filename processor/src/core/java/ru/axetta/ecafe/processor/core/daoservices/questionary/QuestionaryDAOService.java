/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.questionary;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.Answer;
import ru.axetta.ecafe.processor.core.persistence.questionary.ClientAnswerByQuestionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryStatus;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @PersistenceContext(unitName = "processorPU")
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
        return q.getResultList();
    }

    public List<Questionary> getQuestionaries(){
        TypedQuery<Questionary> query = entityManager.createQuery("select q from Questionary q where q.status!=:status", Questionary.class);
        query.setParameter("status", QuestionaryStatus.DELETED);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<OrgItem> getOrgs(Questionary questionary){
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(Questionary.class);
        criteria.createAlias("orgs","org");
        criteria.add(Restrictions.eq("idOfQuestionary",questionary.getIdOfQuestionary()));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("org.idOfOrg"),"idOfOrg")
                .add(Projections.property("org.shortName"),"shortName")
        );
        criteria.setResultTransformer(Transformers.aliasToBean(OrgItem.class));
        return (List<OrgItem>) criteria.list();
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
        //currentQuestionary.setQuestionaryType(QuestionaryType.fromInteger(type));
        currentQuestionary.setType(type);
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

    public Questionary getQuestionary(Questionary questionary) {
        TypedQuery<Questionary> query = entityManager.createQuery("select distinct question from Questionary question left join question.answers answer where question.idOfQuestionary=:idOfQuestionary", Questionary.class);
        query.setParameter("idOfQuestionary",questionary.getIdOfQuestionary());
        return query.getSingleResult();

    }

    public Questionary saveOrUpdate(Questionary questionary){
        return entityManager.merge(questionary);
    }

}
