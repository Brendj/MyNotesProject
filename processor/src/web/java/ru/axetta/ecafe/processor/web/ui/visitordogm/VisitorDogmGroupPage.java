/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 12.10.16
 * Time: 10:37
 */
@Component
@Scope("session")
public class VisitorDogmGroupPage extends BasicWorkspacePage {

    private VisitorItem currentVisitorDogm;

    public VisitorItem getCurrentVisitorDogm() {
        return currentVisitorDogm;
    }

    public void setCurrentVisitorDogm(VisitorItem currentVisitorDogm) {
        this.currentVisitorDogm = currentVisitorDogm;
    }
}
