/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 27.12.12
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public class QuestionaryResultByOrg {

    private Long idOfQuestionaryResultByOrg;
    private Integer count;
    private Questionary questionary;
    private Answer answer;
    private Org org;
    private Date updatedDate;

    protected QuestionaryResultByOrg() {}

    public QuestionaryResultByOrg(Answer answer, Questionary questionary, Org org) {
        this.answer = answer;
        this.questionary = questionary;
        this.org = org;
        this.updatedDate = new Date();
        this.count=1;
    }

    public void addAnswer(Answer answer){
        if(this.answer.equals(answer)){
            this.count++;
        }
    }

    public Long getIdOfQuestionaryResultByOrg() {
        return idOfQuestionaryResultByOrg;
    }

    public Org getOrg() {
        return org;
    }

    public Answer getAnswer() {
        return answer;
    }

    public Questionary getQuestionary() {
        return questionary;
    }

    public Integer getCount() {
        return count;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    protected void setIdOfQuestionaryResultByOrg(Long idOfQuestionaryResultByOrg) {
        this.idOfQuestionaryResultByOrg = idOfQuestionaryResultByOrg;
    }

    protected void setOrg(Org org) {
        this.org = org;
    }

    protected void setAnswer(Answer answer) {
        this.answer = answer;
    }

    protected void setQuestionary(Questionary questionary) {
        this.questionary = questionary;
    }

    protected void setCount(Integer count) {
        this.count = count;
    }

    protected void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
