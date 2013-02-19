/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.Answer;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryStatus;
import ru.axetta.ecafe.processor.core.questionaryservice.AnswerItem;
import ru.axetta.ecafe.processor.core.questionaryservice.QuestionaryService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
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
    private String question;
    private String questionName;
    private String description;
    private Integer type;
    private Date viewDate;
    private final QuestionaryEnumTypeMenu questionaryEnumTypeMenu = new QuestionaryEnumTypeMenu();
    private List<OrgItem> orgItemList;
    private List<AnswerItem> answers;
    private AnswerItem removeAnswer;
    @Autowired
    private QuestionaryGroupPage questionaryGroupPage;
    @Autowired
    private QuestionaryService questionaryService;

    @Override
    public void onShow() throws Exception {
        questionary = questionaryGroupPage.getQuestionary();
        load();
    }

    public void load() {
        question = questionary.getQuestion();
        questionName = questionary.getQuestionName();
        description = questionary.getDescription();
        viewDate = questionary.getViewDate();
        type = questionary.getQuestionaryType().getValue();
        List<Answer> answerList = questionaryService.getAnswers(questionary);
        List<AnswerItem> answerItems = new ArrayList<AnswerItem>(answerList.size());
        for (Answer answer: answerList){
            answerItems.add(new AnswerItem(answer));
        }
        answers = answerItems;
        List<Org> orgList = questionaryService.getOrgs(questionary);
        if(!orgList.isEmpty()){
            orgItemList = new ArrayList<OrgItem>(orgList.size());
            StringBuilder sb=new StringBuilder();
            idOfOrgList = new ArrayList<Long>(orgList.size());
            for (Org org: orgList){
                orgItemList.add(new OrgItem(org));
                idOfOrgList.add(org.getIdOfOrg());
                sb.append(org.getShortName());
                sb.append("; ");
            }
            filter=sb.substring(0,sb.length()-2);
        }
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
            /* update answers */
            if (questionaryService.getStatus(questionary)){
                List<Answer> answerList = new ArrayList<Answer>(answers.size());
                for (AnswerItem answerItem: answers){
                    answerList.add(new Answer(answerItem.getAnswer(),answerItem.getDescription(),questionary,answerItem.getWeight()));
                }
                questionary = questionaryService.updateQuestionary(questionary.getIdOfQuestionary(),question,
                        questionName,description,idOfOrgList, type, answerList,viewDate);
                questionaryGroupPage.setQuestionary(questionary);
                load();
                printMessage("Изменения успешно сохранены");
            } else {
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
        answers.remove(getRemoveAnswer());
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public List<AnswerItem> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerItem> answers) {
        this.answers = answers;
    }

    public AnswerItem getRemoveAnswer() {
        return removeAnswer;
    }

    public void setRemoveAnswer(AnswerItem removeAnswer) {
        this.removeAnswer = removeAnswer;
    }

    public QuestionaryEnumTypeMenu getQuestionaryEnumTypeMenu() {
        return questionaryEnumTypeMenu;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public Date getViewDate() {
        return viewDate;
    }

    public void setViewDate(Date viewDate) {
        this.viewDate = viewDate;
    }
}
