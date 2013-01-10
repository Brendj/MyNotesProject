/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.questionary.ClientAnswerByQuestionary;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.01.13
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
public class QuestionaryData {

    private List<QuestionaryElement> questionaryElementList;

    public QuestionaryData() {}

    public List<QuestionaryElement> getQuestionaryElementList() {
        return questionaryElementList;
    }

    public void process(Session session, Long idOfOrg) throws Exception{
        Criteria criteriaClientAnswerByQuestionary = session.createCriteria(ClientAnswerByQuestionary.class);
        criteriaClientAnswerByQuestionary.createAlias("client", "cl");
        criteriaClientAnswerByQuestionary.createAlias("cl.org", "o");
        criteriaClientAnswerByQuestionary.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        List clientAnswerByQuestionaries = criteriaClientAnswerByQuestionary.list();
        questionaryElementList = new ArrayList<QuestionaryElement>(clientAnswerByQuestionaries.size());
        for (Object object: clientAnswerByQuestionaries){
            ClientAnswerByQuestionary clientAnswerByQuestionary = (ClientAnswerByQuestionary) object;
            questionaryElementList.add(new QuestionaryElement(clientAnswerByQuestionary.getAnswer().getAnswer(),clientAnswerByQuestionary.getClient().getIdOfClient()));
        }

    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResQuestionary");
        for (QuestionaryElement questionaryElement : this.questionaryElementList) {
            element.appendChild(questionaryElement.toElement(document));
        }
        return element;
    }

}
