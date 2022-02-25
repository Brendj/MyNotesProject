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

import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 12.10.16
 * Time: 10:37
 */
@Component
@Scope("session")
public class VisitorDogmEditPage extends BasicWorkspacePage {

    private final static Logger LOGGER = LoggerFactory.getLogger(VisitorDogmEditPage.class);

    @Autowired
    private VisitorDogmServiceBean serviceBean;
    @Autowired
    private VisitorDogmGroupPage visitorDogmGroupPage;

    private VisitorItem visitorDogm;

    @Override
    public void onShow() throws Exception {
        visitorDogm = visitorDogmGroupPage.getCurrentVisitorDogm();
        List<CardItem> cardItems = serviceBean.findCardsByVisitorDogm(visitorDogm.getIdOfVisitor());
        visitorDogm.clearCardItems();
        visitorDogm.addCard(cardItems);
    }

    public Object clear() {
        visitorDogm = serviceBean.findVisitorsDogmByIdOfVisitor(visitorDogm.getIdOfVisitor());
        visitorDogmGroupPage.setCurrentVisitorDogm(visitorDogm);
        super.show();
        return null;
    }

    public Object save() {
        try {
            Long id = serviceBean.saveVisitorDogm(visitorDogm);
            printMessage("Данные успешно сохранены");
        } catch (Exception e) {
            printError("Ошибка при сохранении: " + e.getMessage());
            LOGGER.error("Error by update visitorDogm info:", e);
        }
        return null;
    }

    public Object addCard() {
        return null;
    }

    public VisitorItem getVisitorDogm() {
        return visitorDogm;
    }

    @Override
    public String getPageFilename() {
        return "visitorsdogm/visitordogm/edit";
    }
}
