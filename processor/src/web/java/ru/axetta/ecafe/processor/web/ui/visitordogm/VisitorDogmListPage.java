/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorDogmServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

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
public class VisitorDogmListPage extends BasicWorkspacePage{

    @Autowired
    private VisitorDogmServiceBean serviceBean;
    @Autowired
    private VisitorDogmGroupPage visitorDogmGroupPage;

    private List<VisitorItem> visitorsDogm;
    // 0 - показывать всех, 1 - актуальных, 2 - удаленных.
    private int showMode = 1;

    public int getShowMode() {
        return showMode;
    }

    public void setShowMode(int showMode) {
        this.showMode = showMode;
    }

    @Override
    public void onShow() throws Exception {
        if (showMode == 0) {
            visitorsDogm = serviceBean.findAllVisitorsDogm();
        } else {
            visitorsDogm = showMode == 1 ? serviceBean.findAllVisitorsDogm(false) : serviceBean.findAllVisitorsDogm(true);
        }
    }

    public List<VisitorItem> getVisitorsDogm() {
        return visitorsDogm;
    }

    @Override
    public String getPageFilename() {
        return "visitorsdogm/visitordogm/list";
    }

    public Object deleteVisitorDogm() {
        serviceBean.deleteVisitorDogm(visitorDogmGroupPage.getCurrentVisitorDogm().getIdOfVisitor());
        return null;
    }
}
