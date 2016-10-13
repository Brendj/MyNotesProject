/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorDogmServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 12.10.16
 * Time: 10:37
 */
@Component
@Scope("session")
public class VisitorDogmSelectPage extends BasicPage {

    @Autowired
    private VisitorDogmServiceBean serviceBean;

    private VisitorItem selectVisitorDogm;
    private List<VisitorItem> visitorsDogm;

    public List<VisitorItem> getVisitorsDogm() {
        return visitorsDogm;
    }

    public VisitorItem getSelectVisitorDogm() {
        return selectVisitorDogm;
    }

    public void setSelectVisitorDogm(VisitorItem selectVisitorDogm) {
        this.selectVisitorDogm = selectVisitorDogm;
    }

    public Object completeSelection(){
        BasicWorkspacePage basicWorkspacePage = MainPage.getSessionInstance().getCurrentWorkspacePage();
        SelectVisitorDogm selectVisitorDogmPanel = (SelectVisitorDogm) basicWorkspacePage;
        selectVisitorDogmPanel.completeSelection(selectVisitorDogm);
        return null;
    }

    public Object show() {
        visitorsDogm = serviceBean.findAllVisitorsDogm(false);
        MainPage.getSessionInstance().registerModalPageShow(this);
        return null;
    }

    public Object hide() {
        MainPage.getSessionInstance().registerModalPageHide(this);
        return null;
    }

}
