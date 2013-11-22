/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 26.12.12
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class ClientAnswerByQuestionary {

    private Long idOfClientAnswerByQuestionary;
    private Client client;
    private Answer answer;
    private Date createdDate;
    private Date updatedDate;

    protected ClientAnswerByQuestionary() {}

    public ClientAnswerByQuestionary(Answer answer, Client client) {
        this.answer = answer;
        this.client = client;
        this.createdDate = new Date();
    }

    public Long getIdOfClientAnswerByQuestionary() {
        return idOfClientAnswerByQuestionary;
    }

    public Client getClient() {
        return client;
    }

    public Answer getAnswer() {
        return answer;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    protected void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    protected void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setAnswer(Answer answer) {
        this.updatedDate = new Date();
        this.answer = answer;
    }

    protected void setClient(Client client) {
        this.client = client;
    }

    protected void setIdOfClientAnswerByQuestionary(Long idOfClientAnswerByQuestionary) {
        this.idOfClientAnswerByQuestionary = idOfClientAnswerByQuestionary;
    }
}
