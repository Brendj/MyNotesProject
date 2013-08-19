/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class YesNoConfirmPanel extends BasicWorkspacePage {
    private List<YesNoListener> listeners;
    private static final Logger logger = LoggerFactory.getLogger(YesNoConfirmPanel.class);


    public void fill () {
        listeners = new ArrayList<YesNoListener>();
    }


    /**
     * ****************************************************************************************************************
     * Работа с UI
     * ****************************************************************************************************************
     */
    public void doYes () {
        if (listeners == null) {
            return;
        }
        for (YesNoListener listener : listeners) {
            listener.onYesNoEvent(new YesNoEvent(true));
        }
    }

    public void doNo () {
        if (listeners == null) {
            return;
        }
        for (YesNoListener listener : listeners) {
            listener.onYesNoEvent(new YesNoEvent(false));
        }
    }




    /**
     * ****************************************************************************************************************
     * Вспомогательные методы
     * ****************************************************************************************************************
     */
    public void addCallbackListener (BasicWorkspacePage page) {
        if (!(page instanceof YesNoListener)) {
            logger.error("Trying to add not listener for YesNoConfirmPanel");
            return;
        }
        listeners.add((YesNoListener) page);
    }
}
