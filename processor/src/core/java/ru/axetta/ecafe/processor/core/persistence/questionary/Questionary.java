/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import ru.axetta.ecafe.processor.core.persistence.Org;

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
    private QuestionaryStatus status;
    private Integer type;
    private Date createdDate;
    private Date updatedDate;
    private Set<Answer> answers = new HashSet<Answer>();
    private Set<Org> orgs = new HashSet<Org>();

    protected Questionary() {}

    public Questionary(String question) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.status = QuestionaryStatus.INACTIVE;
        this.type = 0;
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
    }

    public Questionary(String question, Integer type) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.status = QuestionaryStatus.INACTIVE;
        this.type = type;
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
    }

    public Questionary(String question, Integer type, Integer status) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.status = QuestionaryStatus.INACTIVE;
        this.type = type;
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
    }

    public Questionary update(String question) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.updatedDate = new Date();
        return this;
    }

    public Questionary update(String question, Integer type) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.type = type;
        this.updatedDate = new Date();
        return this;
    }

    public Questionary start() throws Exception {
        if(status == QuestionaryStatus.START) throw new Exception("Ошибка при старте анкетирования, возможно анкетирование уже идет");
        this.status = QuestionaryStatus.START;
        this.updatedDate = new Date();
        return this;
    }

    public Questionary stop() throws Exception {
        if(this.status != QuestionaryStatus.START) throw new Exception("Не возможно остановить анкетирование");
        this.status = QuestionaryStatus.STOP;
        this.updatedDate = new Date();
        return this;
    }

    public boolean getInactiveStatus() throws Exception {
        return this.status == QuestionaryStatus.INACTIVE;
    }

    public boolean getStartStatus() throws Exception {
        return this.status == QuestionaryStatus.START;
    }

    public boolean getStopStatus() {
        return  this.status == QuestionaryStatus.STOP;
    }

    public Set<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        this.orgs = orgs;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
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

    public QuestionaryStatus getStatus() {
        return status;
    }

    public String getQuestion() {
        return question;
    }

    public Long getIdOfQuestionary() {
        return idOfQuestionary;
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

    protected void setStatus(QuestionaryStatus status) {
        this.status = status;
    }

    protected void setQuestion(String question) {
        this.question = question;
    }

    protected void setIdOfQuestionary(Long idOfQuestionary) {
        this.idOfQuestionary = idOfQuestionary;
    }
}
