/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 21.12.12
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */
public class Questionary {

    private Long idOfQuestionary;
    private String question;
    private Integer status;
    private Integer type;
    private Date createdDate;
    private Date updatedDate;
    private Set<Answer> answers;

    protected Questionary() {}

    public Questionary(String question) throws Exception{
        this.question = question;
        this.status = 0;
        this.type = 0;
        this.createdDate = new Date();
    }

    public Questionary(String question, Integer status) throws Exception {
        this.question = question;
        this.status = status;
        this.type = 0;
        this.createdDate = new Date();
    }

    public Questionary(String question, Integer status, Integer type) throws Exception {
        this.question = question;
        this.status = status;
        this.type = type;
        this.createdDate = new Date();
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }


    public Date getCreatedDate() {
        return createdDate;
    }

    public Integer getType() {
        return type;
    }

    public Integer getStatus() {
        return status;
    }

    public String getQuestion() {
        return question;
    }

    public Long getIdOfQuestionary() {
        return idOfQuestionary;
    }

    protected void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    protected void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    protected void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    protected void setType(Integer type) {
        this.type = type;
    }

    protected void setStatus(Integer status) {
        this.status = status;
    }

    protected void setQuestion(String question) {
        this.question = question;
    }

    protected void setIdOfQuestionary(Long idOfQuestionary) {
        this.idOfQuestionary = idOfQuestionary;
    }
}
