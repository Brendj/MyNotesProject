/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import ru.axetta.ecafe.processor.core.daoservices.questionary.QuestionaryItem;
import ru.axetta.ecafe.processor.core.persistence.DateType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;

import java.util.*;

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
    private QuestionaryType questionaryType;
    private Date createdDate;
    private Date updatedDate;
    private Set<Org> orgs = new HashSet<Org>();
    private String questionName;
    private String description;
    private Date viewDate;
    private Set<Answer> answers = new HashSet<Answer>();

    protected Questionary() {}

    public Questionary(String questionName, String question, String description) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.questionName = questionName;
        this.description = description;
        this.status = QuestionaryStatus.INACTIVE;
        this.questionaryType = QuestionaryType.DEFAULT;
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
        date = CalendarUtils.truncateToDayOfMonth(date);
        this.viewDate = date;
    }

    public Questionary(String questionName, String question, String description, QuestionaryType type) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.questionName = questionName;
        this.description = description;
        this.status = QuestionaryStatus.INACTIVE;
        this.questionaryType = type;
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
        date = CalendarUtils.truncateToDayOfMonth(date);
        this.viewDate = date;
    }

    public Questionary(QuestionaryItem questionaryItem){
        if(StringUtils.isEmpty(questionaryItem.getQuestion())) throw new NullPointerException("Отсутствует текст опросника");
        this.question = questionaryItem.getQuestion();
        this.questionName = questionaryItem.getQuestionName();
        this.description = questionaryItem.getDescription();
        this.status = QuestionaryStatus.INACTIVE;
        this.questionaryType = QuestionaryType.fromInteger(questionaryItem.getType());
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
        date = CalendarUtils.truncateToDayOfMonth(date);
        if(questionaryItem.getViewDate()!=null){
            this.viewDate = questionaryItem.getViewDate().getTime();
        }else {
            this.viewDate = date;
        }
    }

    public Questionary(String questionName, String question, String description, QuestionaryType type, QuestionaryStatus status) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.questionName = questionName;
        this.description = description;
        this.status = status;
        this.questionaryType = type;
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
        date = CalendarUtils.truncateToDayOfMonth(date);
        this.viewDate = date;
    }

    public Questionary update(String questionName, String question, String description) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.questionName = questionName;
        this.description = description;
        this.updatedDate = new Date();
        return this;
    }

    public Questionary update(String questionName, String question, String description, QuestionaryType type) throws Exception {
        if(question==null || question.isEmpty()) throw new NullPointerException("Отсутствует текст опросника");
        this.question = question;
        this.questionName = questionName;
        this.description = description;
        this.questionaryType = type;
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

    public Questionary deleted() throws Exception {
        if(!(this.status == QuestionaryStatus.INACTIVE || this.status == QuestionaryStatus.STOP)) {
            throw new Exception("Не возможно удалить анкетирование");
        }
        this.status = QuestionaryStatus.DELETED;
        this.updatedDate = new Date();
        return this;
    }

    public Boolean getInactiveStatus() {
        return this.status == QuestionaryStatus.INACTIVE;
    }

    void setInactiveStatus(Boolean stopStatus) {}

    public Boolean getStartStatus() {
        return this.status == QuestionaryStatus.START;
    }

    void setStartStatus(Boolean stopStatus) {}

    public Boolean getStopStatus() {
        return this.status == QuestionaryStatus.STOP;
    }

    void setStopStatus(Boolean stopStatus) {}

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

    public void addAnswer(Answer answer){
        if(!this.answers.contains(answer)){
            this.answers.add(answer);
        }
    }

    public void removeAnswer(Answer answer){
        if(!this.answers.contains(answer)){
            this.answers.remove(answer);
        }
    }

    public List<Answer> getAnswerList(){
        return new ArrayList<Answer>(answers);
    }

    public Date getViewDate() {
        return viewDate;
    }

    public void setViewDate(Date viewDate) {
        this.viewDate = viewDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }


    public Date getCreatedDate() {
        return createdDate;
    }

    public QuestionaryType getQuestionaryType() {
        return questionaryType;
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

    protected void setQuestionaryType(QuestionaryType questionaryType) {
        this.questionaryType = questionaryType;
    }

    public Integer getType() {
        return questionaryType.getValue();
    }

    public void setType(Integer questionaryType) {
        this.questionaryType = QuestionaryType.fromInteger(questionaryType);
    }

    public void setStatus(QuestionaryStatus status) {
        this.status = status;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setIdOfQuestionary(Long idOfQuestionary) {
        this.idOfQuestionary = idOfQuestionary;
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
}
