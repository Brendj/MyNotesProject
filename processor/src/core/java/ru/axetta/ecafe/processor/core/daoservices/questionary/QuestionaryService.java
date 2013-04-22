/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.questionary;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
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
@Service
public class QuestionaryService {

    private static Logger logger = LoggerFactory.getLogger(QuestionaryService.class);

    @Autowired
    private QuestionaryDAOService service;

    @SuppressWarnings("unchecked")
    public List<ClientAnswerByQuestionaryItem> generateReportByQuestionaryResultByOrg(Session session, Org org){
        Criteria criteria = session.createCriteria(ClientAnswerByQuestionary.class);
        criteria.createAlias("client","cl").add(Restrictions.eq("cl.org",org));
        criteria.createAlias("answer", "a");
        criteria.createAlias("a.questionary","q");
        criteria.setProjection(Projections.projectionList()
                .add(Projections.count("a.idOfAnswer"), "count")
                .add(Projections.groupProperty("a.answer"), "answer")
                .add(Projections.groupProperty("q.question"), "questionary")
        );
        criteria.setResultTransformer(Transformers.aliasToBean(ClientAnswerByQuestionaryItem.class));
        return (List<ClientAnswerByQuestionaryItem>) criteria.list();
    }

    public void registrationAnswerByClient(Session session, Long contractId, Long idOfAnswer) throws Exception{
        Criteria clientCriteria = session.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("contractId",contractId));
        Client client = (Client) clientCriteria.uniqueResult();

        /* Создаем под запрос но не выполняем его */
        /* Вытягиеваем все варианты ответов текущего вопроса */
        DetachedCriteria idOfQuestionCriteria = DetachedCriteria.forClass(Questionary.class);
        idOfQuestionCriteria.createAlias("answers","answer");
        idOfQuestionCriteria.add(Restrictions.eq("answer.idOfAnswer",idOfAnswer));
        idOfQuestionCriteria.setProjection(Property.forName("idOfQuestionary"));

        Criteria clientAnswerByQuestionaryCriteria = session.createCriteria(ClientAnswerByQuestionary.class);
        clientAnswerByQuestionaryCriteria.add(Restrictions.eq("client",client));
        clientAnswerByQuestionaryCriteria.createAlias("answer","an");
        clientAnswerByQuestionaryCriteria.createAlias("an.questionary","question");
        clientAnswerByQuestionaryCriteria.add( Property.forName("question.idOfQuestionary").eq(idOfQuestionCriteria));
        List list = clientAnswerByQuestionaryCriteria.list();
        Answer answer = (Answer) session.get(Answer.class, idOfAnswer);
        if(list.isEmpty()){
            ClientAnswerByQuestionary clientAnswerByQuestionary = new ClientAnswerByQuestionary(answer,client);
            session.persist(clientAnswerByQuestionary);
        } else {
            ClientAnswerByQuestionary clientAnswerByQuestionary = (ClientAnswerByQuestionary) list.get(0);
            clientAnswerByQuestionary.setAnswer(answer);
            session.saveOrUpdate(clientAnswerByQuestionary);
        }
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
                Questionary questionary = new Questionary(questionaryItem.getQuestionName(),questionaryItem.getQuestion(), questionaryItem.getDescription(), QuestionaryType.fromInteger(type));
                for (AnswerItem answerItem: questionaryItem.getAnswers()){
                    Answer answer = new Answer(answerItem.getAnswer(), answerItem.getDescription(), questionary, answerItem.getWeight());
                    questionary.getAnswers().add(answer);
                }
                service.registrationQuestionary(questionary,idOfOrgList);
                registrationItems.add(new RegistrationItem(questionary, "OK"));
            } catch (Exception e){
                logger.error("Error registration questionary: ",e);
                registrationItems.add(new RegistrationItem(questionaryItem, e.getMessage()));
            }
        }
        return registrationItems;
    }

}
