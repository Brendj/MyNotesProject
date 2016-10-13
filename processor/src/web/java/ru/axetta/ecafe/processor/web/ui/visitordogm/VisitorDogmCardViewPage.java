/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardItem;
import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorDogmServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
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
public class VisitorDogmCardViewPage extends BasicWorkspacePage{

    @Autowired
    private VisitorDogmServiceBean serviceBean;
    @Autowired
    private VisitorDogmCardGroupPage visitorDogmCardGroupPage;

    private CardItem card;

    @Override
    public void onShow() throws Exception {
        card = visitorDogmCardGroupPage.getCurrentCard();
        if(card.getVisitorItem()==null){
            VisitorItem visitorItem = serviceBean.getVisitorDogmByCard(card.getId());
            card.setVisitorItem(visitorItem);
        }
    }

    public CardItem getCard() {
        return card;
    }

    @Override
    public String getPageFilename() {
        return "visitorsdogm/card/view";
    }
}
