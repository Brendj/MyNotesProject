/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal.feed_plan;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 01.09.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */

public class ClientFeedActionEvent {
    public static final int ALL_CLIENTS    = 100000;
    private int actionType;

    public ClientFeedActionEvent (int actionType) {
        this.actionType = actionType;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }
}