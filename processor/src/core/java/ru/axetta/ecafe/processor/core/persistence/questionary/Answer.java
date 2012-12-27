/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import java.util.Date;
import java.util.Set;

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
    private Set<ClientAnswerByQuestionary> clientAnswerByQuestionary;

    public Set<ClientAnswerByQuestionary> getClientAnswerByQuestionary() {
        return clientAnswerByQuestionary;
    }

    public void setClientAnswerByQuestionary(Set<ClientAnswerByQuestionary> clientAnswerByQuestionary) {
        this.clientAnswerByQuestionary = clientAnswerByQuestionary;
    }

    protected Answer() {}

    public Answer(String answer, Questionary questionary) throws Exception{
        if(questionary==null) throw new NullPointerException("Не выбрана анкета для ответа");
        if(answer==null || answer.isEmpty()) throw new NullPointerException("Не задано текстовое значение для ответа");
        this.answer = answer;
        this.questionary = questionary;
        this.weight = 1;
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
    }

    public Answer(String answer, Questionary questionary, Integer weight) throws Exception{
        if(questionary==null) throw new NullPointerException("Не выбрана анкета для ответа");
        if(answer==null || answer.isEmpty()) throw new NullPointerException("Не задано текстовое значение для ответа");
        this.answer = answer;
        this.questionary = questionary;
        this.weight = weight;
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
    }

    public Answer update(String answer) throws Exception{
        if(answer==null || answer.isEmpty()) throw new NullPointerException("Не задано текстовое значение для ответа");
        this.answer = answer;
        this.weight = 1;
        this.updatedDate = new Date();
        return this;
    }

    public Answer update(String answer, Integer weight) throws Exception{
        if(answer==null || answer.isEmpty()) throw new NullPointerException("Не задано текстовое значение для ответа");
        this.answer = answer;
        this.weight = weight;
        this.updatedDate = new Date();
        return this;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Answer answer1 = (Answer) o;

        if (!answer.equals(answer1.answer)) {
            return false;
        }
        if (!idOfAnswer.equals(answer1.idOfAnswer)) {
            return false;
        }
        if (!weight.equals(answer1.weight)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (idOfAnswer==null?Long.valueOf(-1L).hashCode():idOfAnswer.hashCode());
        result = 31 * result + answer.hashCode();
        result = 31 * result + weight.hashCode();
        return result;
    }
}
