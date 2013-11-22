/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.01.13
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */
public class QuestionaryElement {

    private final String questionaryName;
    private final String questionary;
    private Date viewDate;
    private List<ClientAnswerElement> clientAnswerElementList;

    private final static DateFormat dateOnlyFormat;
    static {
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateOnlyFormat.setTimeZone(utcTimeZone);
    }

    public QuestionaryElement( String questionaryName, String questionary) {
        this.questionaryName = questionaryName;
        this.questionary = questionary;
        clientAnswerElementList = new ArrayList<ClientAnswerElement>();
    }

    public QuestionaryElement(Questionary questionary) {
        this.questionaryName = questionary.getQuestionName();
        this.questionary = questionary.getQuestion();
        this.viewDate = questionary.getViewDate();
        clientAnswerElementList = new ArrayList<ClientAnswerElement>();
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Questionary");
        element.setAttribute("Text", questionary);
        element.setAttribute("Name", questionaryName);
        element.setAttribute("ViewDate", dateOnlyFormat.format(viewDate));
        for (ClientAnswerElement clientAnswerElement: clientAnswerElementList){
            element.appendChild(clientAnswerElement.toElement(document));
        }
        return element;
    }

    public List<ClientAnswerElement> getClientAnswerElementList() {
        return clientAnswerElementList;
    }

    public String getQuestionary() {
        return questionary;
    }

    public String getQuestionaryName() {
        return questionaryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuestionaryElement that = (QuestionaryElement) o;

        if (clientAnswerElementList != null ? !clientAnswerElementList.equals(that.clientAnswerElementList)
                : that.clientAnswerElementList != null) {
            return false;
        }
        if (questionary != null ? !questionary.equals(that.questionary) : that.questionary != null) {
            return false;
        }
        if (questionaryName != null ? !questionaryName.equals(that.questionaryName) : that.questionaryName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = questionaryName != null ? questionaryName.hashCode() : 0;
        result = 31 * result + (questionary != null ? questionary.hashCode() : 0);
        result = 31 * result + (clientAnswerElementList != null ? clientAnswerElementList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QuestionaryElement{" +
                "clientAnswerElementList=" + clientAnswerElementList +
                ", questionaryName='" + questionaryName + '\'' +
                ", questionary='" + questionary + '\'' +
                '}';
    }
}
