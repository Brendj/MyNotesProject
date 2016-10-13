/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardItem;
import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorDogmServiceBean;
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
public class VisitorDogmCardListPage extends BasicWorkspacePage{

    @Autowired
    private VisitorDogmServiceBean serviceBean;

    private List<CardItem> cards;

    @Override
    public void onShow() throws Exception {
        cards = serviceBean.findCardsByVisitorDogmTypes();
    }

    public List<CardItem> getCards() {
        return cards;
    }

    @Override
    public String getPageFilename() {
        return "visitorsdogm/card/list";
    }
}
