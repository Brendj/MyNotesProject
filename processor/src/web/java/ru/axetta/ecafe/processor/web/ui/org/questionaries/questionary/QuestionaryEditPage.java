/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary;

import ru.axetta.ecafe.processor.core.daoservices.questionary.OrgItem;
import ru.axetta.ecafe.processor.core.daoservices.questionary.QuestionaryDAOService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.Answer;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.daoservices.questionary.AnswerItem;
import ru.axetta.ecafe.processor.core.daoservices.questionary.QuestionaryService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class QuestionaryEditPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private String filter = "Не выбрано";
    private List<Long> idOfOrgList = new ArrayList<Long>(0);
    private Questionary questionary;
    private final QuestionaryEnumTypeMenu questionaryEnumTypeMenu = new QuestionaryEnumTypeMenu();
    private List<OrgItem> orgItemList;
    private Answer removeAnswer;
    @Autowired
    private QuestionaryGroupPage questionaryGroupPage;
    @Autowired
    private QuestionaryDAOService questionaryDAOService;

    @Override
    public void onShow() throws Exception {
        questionary = questionaryGroupPage.getQuestionary();
        orgItemList = questionaryDAOService.getOrgs(questionary);
        if(!orgItemList.isEmpty()){
            StringBuilder sb=new StringBuilder();
            idOfOrgList = new ArrayList<Long>(orgItemList.size());
            for (OrgItem org: orgItemList){
                idOfOrgList.add(org.getIdOfOrg());
                sb.append(org.getShortName());
                sb.append("; ");
            }
            filter=sb.substring(0,sb.length()-2);
        }
        load();
    }

    public void load() {
        questionary = questionaryDAOService.getQuestionary(questionary);
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>(orgMap.size());
            if (orgMap.isEmpty())
                filter = "Не выбрано";
            else {
                filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 2);
            }
        }
    }

    public Object save(){
        try {
            if (questionaryDAOService.getStatus(questionary)){
                questionary.setOrgs(questionaryDAOService.getOrgs(idOfOrgList));
                questionary = questionaryDAOService.saveOrUpdate(questionary);
                questionaryGroupPage.setQuestionary(questionary);
                printMessage("Изменения успешно сохранены");
            }  else {
                printWarn("Необходимо остановить анкетирование перед редактированием");
            }
        } catch (Exception e) {
            getLogger().error("Filed to save questionary: ", e);
            printError(e.getMessage());
        }
        return null;
    }

    public Object reload(){
        load();
        return null;
    }

    public Object removeAnswer(){
        questionary.getAnswers().remove(getRemoveAnswer());
        return null;
    }

    @Override
    public String getPageFilename() {
        return "org/questionaries/question_edit";
    }

    public String getIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public String getFilter() {
        return filter;
    }

    public Questionary getQuestionary() {
        return questionary;
    }

    public List<OrgItem> getOrgItemList() {
        return orgItemList;
    }

    public Answer getRemoveAnswer() {
        return removeAnswer;
    }

    public void setRemoveAnswer(Answer removeAnswer) {
        this.removeAnswer = removeAnswer;
    }

    public QuestionaryEnumTypeMenu getQuestionaryEnumTypeMenu() {
        return questionaryEnumTypeMenu;
    }

}
