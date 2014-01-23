/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestionaryItem", propOrder = {
        "idOfQuestionary", "question", "name", "description", "status", "type", "viewDate", "checkedAnswer","answers"
})
public class QuestionaryItem {

    @XmlAttribute(name = "IdOfQuestionary", required = false)
    protected Long idOfQuestionary;
    @XmlAttribute(name = "Question", required = true)
    protected String question;
    @XmlAttribute(name = "Name", required = true)
    protected String name;
    @XmlAttribute(name = "Description", required = false)
    protected String description;
    @XmlAttribute(name = "Status", required = false)
    protected Integer status;
    @XmlAttribute(name = "Type", required = false)
    protected Integer type;
    @XmlAttribute(name = "ViewDate", required = false)
    protected Date viewDate;
    @XmlAttribute(name = "CheckedAnswer", required = false)
    protected Long checkedAnswer;
    @XmlElement(name="Answer")
    protected List<AnswerItem> answers;

    public QuestionaryItem() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getViewDate() {
        return viewDate;
    }

    public void setViewDate(Date viewDate) {
        this.viewDate = viewDate;
    }

    public Long getCheckedAnswer() {
        return checkedAnswer;
    }

    public void setCheckedAnswer(Long checkedAnswer) {
        this.checkedAnswer = checkedAnswer;
    }

    @Override
    public String toString() {
        return "QuestionaryItem{" +
                "idOfQuestionary=" + idOfQuestionary +
                ", question='" + question + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
