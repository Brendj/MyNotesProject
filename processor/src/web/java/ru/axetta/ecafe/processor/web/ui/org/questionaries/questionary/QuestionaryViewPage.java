/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary;

import ru.axetta.ecafe.processor.core.daoservices.questionary.QuestionaryDAOService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.daoservices.questionary.QuestionaryService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.12.12
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class QuestionaryViewPage extends BasicWorkspacePage {

    private Questionary questionary;
    private List<OrgItem> orgItemList;
    @Autowired
    private QuestionaryGroupPage questionaryGroupPage;
    @Autowired
    private QuestionaryService questionaryService;
    @Autowired
    private QuestionaryDAOService questionaryDAOService;

    @Override
    public void onShow() throws Exception {
        questionary = questionaryGroupPage.getQuestionary();
        loadOrg();
    }

    @Transactional
    protected void loadOrg(){
        List<Org> orgList = questionaryDAOService.getOrgs(questionary);
        orgItemList = new ArrayList<OrgItem>(orgList.size());
        for (Org org: orgList){
            orgItemList.add(new OrgItem(org));
        }
    }

    @Override
    public String getPageFilename() {
        return "org/questionaries/question_view";
    }

    public Questionary getQuestionary() {
        return questionary;
    }

    public List<OrgItem> getOrgItemList() {
        return orgItemList;
    }
}
