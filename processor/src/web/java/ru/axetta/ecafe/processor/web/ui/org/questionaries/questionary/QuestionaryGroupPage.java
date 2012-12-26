/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary;

import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class QuestionaryGroupPage extends BasicWorkspacePage {

    private Questionary questionary;

    @Override
    public String getPageTitle() {
        String title = getTitle();
        return super.getPageTitle() + title;
    }

    public String getTitle(){
        if(questionary!=null){
            if(questionary.getQuestion().length()>15){
                return questionary.getQuestion().substring(0,13)+"...";
            } else {
                return questionary.getQuestion();
            }
        }
        return "";
    }

    public Questionary getQuestionary() {
        return questionary;
    }

    public void setQuestionary(Questionary questionary) {
        this.questionary = questionary;
    }
}
