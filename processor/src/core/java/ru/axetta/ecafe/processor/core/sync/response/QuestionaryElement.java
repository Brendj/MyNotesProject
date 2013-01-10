/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 10.01.13
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */
public class QuestionaryElement {

    private final Long idOfClient;
    private final String answer;

    public QuestionaryElement(String answer, Long idOfClient) {
        this.answer = answer;
        this.idOfClient = idOfClient;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ClientAnswers");
        element.setAttribute("IdOfClient", Long.toString(this.idOfClient));
        element.setAttribute("Answer", answer);
        return element;
    }

    public String getAnswer() {
        return answer;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    @Override
    public String toString() {
        return "QuestionaryElement{" +
                "answer='" + answer + '\'' +
                ", idOfClient=" + idOfClient +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuestionaryElement that = (QuestionaryElement) o;

        if (answer != null ? !answer.equals(that.answer) : that.answer != null) {
            return false;
        }
        if (idOfClient != null ? !idOfClient.equals(that.idOfClient) : that.idOfClient != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfClient != null ? idOfClient.hashCode() : 0;
        result = 31 * result + (answer != null ? answer.hashCode() : 0);
        return result;
    }
}
