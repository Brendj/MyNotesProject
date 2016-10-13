/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

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
public class VisitorDogmCreatePage extends BasicWorkspacePage{

    private final static Logger LOGGER = LoggerFactory.getLogger(VisitorDogmCreatePage.class);

    @Autowired
    private VisitorDogmServiceBean serviceBean;

    private VisitorItem visitorDogm;

    @Override
    public void onShow() throws Exception {
        visitorDogm = new VisitorItem();
    }

    public Object save(){
        try {
            Long id = serviceBean.saveVisitorDogm(visitorDogm);
            String info = visitorDogm.getFullName();
            visitorDogm = new VisitorItem();
            printMessage("Данные '"+info+"' успешно сохранены");
        } catch (Exception e) {
            printError("Ошибка при сохранении: "+e.getMessage());
            LOGGER.error("Error by update visitorDogm info:",e);
        }
        return null;
    }

    public VisitorItem getVisitorDogm() {
        return visitorDogm;
    }

    @Override
    public String getPageFilename() {
        return "visitorsdogm/visitordogm/create";
    }
}
