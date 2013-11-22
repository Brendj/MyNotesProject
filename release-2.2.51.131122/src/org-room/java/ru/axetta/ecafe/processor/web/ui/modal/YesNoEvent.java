/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 16.08.13
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */
public class YesNoEvent {
    private boolean yes;

    public YesNoEvent (boolean yes) {
        this.yes = yes;
    }

    public boolean isYes() {
        return yes;
    }
}
