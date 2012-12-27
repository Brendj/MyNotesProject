/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.questionary.Answer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "AnswerItem")
@XmlType(name = "AnswerItem", propOrder = {
        "idOfAnswer", "answer", "weight"
})
public class AnswerItem {

    @XmlAttribute(name = "id", required = false)
    protected Long idOfAnswer;
    @XmlAttribute(name = "answer", required = true)
    protected String answer;
    @XmlAttribute(name = "weight", required = false)
    protected Integer weight;

    public AnswerItem() {}

    public AnswerItem(Answer answer) {
        this.idOfAnswer = answer.getIdOfAnswer();
        this.answer = answer.getAnswer();
        this.weight = answer.getWeight();
    }

    public Long getIdOfAnswer() {
        return idOfAnswer;
    }

    public void setIdOfAnswer(Long idOfAnswer) {
        this.idOfAnswer = idOfAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
