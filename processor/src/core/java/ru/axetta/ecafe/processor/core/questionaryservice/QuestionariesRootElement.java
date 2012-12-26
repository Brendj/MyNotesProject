/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.questionaryservice;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "Questionaries")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuestionariesRootElement {

    public QuestionariesRootElement() {
        this.questionaryItemList = new ArrayList<QuestionaryItem>();
    }

    public QuestionariesRootElement(List<QuestionaryItem> questionaryItemList) {
        this.questionaryItemList = questionaryItemList;
    }

    @XmlElement(name="Questionary")
    protected List<QuestionaryItem> questionaryItemList;

    public List<QuestionaryItem> getQuestionaryItemList() {
        return questionaryItemList;
    }

}
