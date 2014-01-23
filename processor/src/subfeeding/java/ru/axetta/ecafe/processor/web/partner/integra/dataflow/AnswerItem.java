/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

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
@XmlType(name = "AnswerItem", propOrder = {
        "idOfAnswer", "answer", "weight", "description"
})
public class AnswerItem {

    @XmlAttribute(name = "id", required = false)
    protected Long idOfAnswer;
    @XmlAttribute(name = "answer", required = true)
    protected String answer;
    @XmlAttribute(name = "description", required = false)
    protected String description;
    @XmlAttribute(name = "weight", required = false)
    protected Integer weight;

    public AnswerItem() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
