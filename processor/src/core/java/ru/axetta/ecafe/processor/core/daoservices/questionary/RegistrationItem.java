/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.questionary;

import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.12.12
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
public class RegistrationItem {

    private QuestionaryItem questionaryItem;
    private Questionary questionary;
    private final String result;

    public RegistrationItem(String result) {
        this.result = result;
    }

    public RegistrationItem(QuestionaryItem questionaryItem, String result) {
        this.questionaryItem = questionaryItem;
        this.result = result;
    }

    public RegistrationItem(Questionary questionary, String result) {
        this.questionary = questionary;
        this.result = result;
    }

    public QuestionaryItem getQuestionaryItem() {
        return questionaryItem;
    }

    public Questionary getQuestionary() {
        return questionary;
    }

    public String getResult() {
        return result;
    }
}
