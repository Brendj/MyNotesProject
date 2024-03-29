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
    private String message;
    private String nodePanel;
    private YesNoListener yesActionListener;


    public void fill () {
        listeners = new ArrayList<YesNoListener>();
    }


    /**
     * ****************************************************************************************************************
     * Работа с UI
     * ****************************************************************************************************************
     */
    public void doYes () {
        //  Если указан метод, который необходимо выполнить до
        if (yesActionListener != null) {
            if (!(yesActionListener instanceof YesNoListener)) {
                logger.error("Trying to add not listener for YesNoConfirmPanel");
            } else {
                yesActionListener.onYesNoEvent(new YesNoEvent(true));
                return;
            }
        }


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

    public YesNoListener getYesActionListener() {
        return yesActionListener;
    }

    public void setYesActionListener(YesNoListener yesActionListener) {
        this.yesActionListener = yesActionListener;
    }

    public String getNodePanel() {
        return nodePanel;
    }

    public void setNodePanel(String nodePanel) {
        this.nodePanel = nodePanel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
