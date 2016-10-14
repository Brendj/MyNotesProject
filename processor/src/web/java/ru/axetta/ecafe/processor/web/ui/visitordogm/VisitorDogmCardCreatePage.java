/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardItem;
import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorDogmServiceBean;
import ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class VisitorDogmCardCreatePage extends BasicWorkspacePage implements SelectVisitorDogm {

    private final static Logger LOGGER = LoggerFactory.getLogger(VisitorDogmCardCreatePage.class);

    @Autowired
    private VisitorDogmServiceBean serviceBean;

    private CardItem card;
    private final CardOperationStationMenu cardOperationStationMenu = new CardOperationStationMenu();

    @Override
    public void onShow() throws Exception {
        card = new CardItem();
    }

    @Override
    public void completeSelection(VisitorItem visitorDogm) {
        if(visitorDogm!=null){
            card.setVisitorItem(visitorDogm);
        }
    }

    public Object save(){
        try {
            Long id = serviceBean.saveVisitorDogmCard(card, card.getVisitorItem().getIdOfVisitor());
            printMessage("Данные успешно сохранены");
        } catch (Exception e) {
            printError("Ошибка при сохранении: "+e.getMessage());
            LOGGER.error("Error by update visitorDogm info:",e);
        }
        return null;
    }

    public CardItem getCard() {
        return card;
    }

    public CardOperationStationMenu getCardOperationStationMenu() {
        return cardOperationStationMenu;
    }

    @Override
    public String getPageFilename() {
        return "visitorsdogm/card/create";
    }
}
