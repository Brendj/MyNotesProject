/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 21.12.12
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class Answer {

    private Long idOfAnswer;
    private String answer;
    private Integer weight;
    private Date createdDate;
    private Date updatedDate;
    private Questionary questionary;

    protected Answer() {}

    public Answer(String answer, Questionary questionary) throws Exception{
        if(questionary==null) throw new Exception("You lost question");
        this.answer = answer;
        this.questionary = questionary;
        this.weight = 1;
        this.createdDate = new Date();
    }

    public Answer(String answer, Questionary questionary, Integer weight) throws Exception{
        if(questionary==null) throw new Exception("You lost question");
        this.answer = answer;
        this.questionary = questionary;
        this.weight = weight;
        this.createdDate = new Date();
    }

    public Long getIdOfAnswer() {
        return idOfAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public Integer getWeight() {
        return weight;
    }

    public Questionary getQuestionary() {
        return questionary;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    protected void setIdOfAnswer(Long idOfAnswer) {
        this.idOfAnswer = idOfAnswer;
    }

    protected void setAnswer(String answer) {
        this.answer = answer;
    }

    protected void setWeight(Integer weight) {
        this.weight = weight;
    }

    protected void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    protected void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setQuestionary(Questionary questionary) {
        this.questionary = questionary;
    }
}
