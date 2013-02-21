/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.questionary;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 21.02.13
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
public class ClientAnswerByQuestionaryItem {

    /* количество ответивших на ответ */
    private Long count;
    /* Наименование анкетировния */
    private String questionary;
    /* Вариант ответа анкеты */
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getQuestionary() {
        return questionary;
    }

    public void setQuestionary(String questionary) {
        this.questionary = questionary;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ClientAnswerByQuestionaryItem");
        sb.append("{answer='").append(answer).append('\'');
        sb.append(", count=").append(count);
        sb.append(", questionary='").append(questionary).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
