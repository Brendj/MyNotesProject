/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.questionaryservice.QuestionariesRootElement;
import ru.axetta.ecafe.processor.core.questionaryservice.QuestionaryService;
import ru.axetta.ecafe.processor.core.questionaryservice.RegistrationItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class QuestionaryLoadPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList{

    private static Logger logger = LoggerFactory.getLogger(QuestionaryLoadPage.class);

    private String filter = "Не выбрано";
    private List<Long> idOfOrgList = new ArrayList<Long>(0);
    private List<RegistrationItem> registrationItems = new ArrayList<RegistrationItem>();
    @Autowired
    private QuestionaryService questionaryService;

    public void questionaryLoadFileListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        QuestionariesRootElement questionariesRootElement = null;
        try {
            File file = item.getFile();
            questionariesRootElement = questionaryService.parseQuestionaryByXML(file);
        } catch (Exception e){
            registrationItems.add(new RegistrationItem("Ошибка при загрузке данных: " + e));
            logger.error("Failed to load from file", e);
            return;
        }
        try {
            registrationItems = questionaryService.registrationQuestionariesFromXML(questionariesRootElement, idOfOrgList);
            printMessage("Данные загружены и зарегистрированы успешно");
        } catch (Exception e) {
            registrationItems.add(new RegistrationItem("Ошибка при регистрации данных: " + e));
            logger.error("Failed to registration questionaries from file", e);
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

    @Override
    public void onShow() throws Exception {
    }

    @Override
    public String getPageFilename() {
        return "org/questionaries/questions_load";
    }

    public String getIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public String getFilter() {
        return filter;
    }

    public List<RegistrationItem> getRegistrationItems() {
        return registrationItems;
    }
}
