/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary;

import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryStatus;
import ru.axetta.ecafe.processor.core.questionaryservice.QuestionaryService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class QuestionaryListPage extends BasicWorkspacePage {

    private DataModel questionaries;
    private static Logger logger = LoggerFactory.getLogger(QuestionaryListPage.class);
    @Autowired
    private QuestionaryService questionaryService;
    @Autowired
    protected QuestionaryGroupPage questionaryGroupPage;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    @Override
    public String getPageFilename() {
        return "org/questionaries/questions_list";
    }

    /*TODO: вывод окна с вопросом об удалении*/
    public Object remove(){
        try {
            Questionary questionary = getEntityFromRequestParam();
            if(questionary.getStatus()== QuestionaryStatus.INACTIVE || questionary.getStatus()== QuestionaryStatus.STOP){
                questionaryService.extractQuestionary(questionary);
                reload();
                printMessage("Анкета успешно удалена");
            } else {
                printError("Нельзя удалть задествованный опросник");
            }
        } catch (Exception e){
            printError("Ошибка при удалении опросника");
            logger.error("Failed remove questionary: ",e);
        }
        return null;
    }

    /*TODO: вывод окнo с вопросом*/
    public Object start(){
        try {
            Questionary questionary = getEntityFromRequestParam();
            questionaryService.changeStatusQuestionary(questionary, QuestionaryStatus.START);
            questionaryGroupPage.setQuestionary(questionary);
            reload();
            printMessage("Анкета успешно активирована");
        } catch (Exception e) {
            printError("Ошибка при старте опросника");
            logger.error("Failed started questionary: ",e);
        }
        return null;
    }

    /*TODO: вывод окнo с вопросом*/
    public Object stop(){
        try {
            Questionary questionary = getEntityFromRequestParam();
            questionaryService.changeStatusQuestionary(questionary, QuestionaryStatus.STOP);
            questionaryGroupPage.setQuestionary(questionary);
            reload();
            printMessage("Анкета успешно остановлена");
        } catch (Exception e) {
            printError("Ошибка при остановке опросника");
            logger.error("Failed stop questionary: ",e);
        }
        return null;
    }

    public DataModel getQuestionary() {
        return questionaries;
    }

    public void setQuestionary(DataModel questionary) {
        this.questionaries = questionary;
    }

    private Questionary getEntityFromRequestParam() {
        if (questionaries == null) return null;
        return (Questionary) questionaries.getRowData();
    }

    private void reload(){
        List<Questionary> questionaryList = questionaryService.getQuestionaries();
        questionaries = new ListDataModel(questionaryList);
    }
}
