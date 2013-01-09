/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.questionaryservice;

import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class QuestionaryItem {

    @XmlAttribute(name = "id", required = false)
    protected Long idOfQuestionary;
    @XmlAttribute(name = "text", required = true)
    protected String question;
    @XmlAttribute(name = "status", required = false)
    protected Integer status;
    @XmlAttribute(name = "type", required = false)
    protected Integer type;
    @XmlElement(name="Answer")
    protected List<AnswerItem> answers;

    public QuestionaryItem() {
        answers = new ArrayList<AnswerItem>();
    }

    public QuestionaryItem(Questionary questionary) {
        this.idOfQuestionary = questionary.getIdOfQuestionary();
        this.question = questionary.getQuestion();
        this.status =  questionary.getStatus().getValue();
        this.type = questionary.getQuestionaryType().getValue();
        answers = new ArrayList<AnswerItem>();
    }

    public Long getIdOfQuestionary() {
        return idOfQuestionary;
    }

    public void setIdOfQuestionary(Long idOfQuestionary) {
        this.idOfQuestionary = idOfQuestionary;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<AnswerItem> getAnswers() {
        return answers;
   }

    @Override
    public String toString() {
        return "QuestionaryItem{" +
                ", question='" + question + '\'' +
                ", type=" + type +
                '}';
    }
}
