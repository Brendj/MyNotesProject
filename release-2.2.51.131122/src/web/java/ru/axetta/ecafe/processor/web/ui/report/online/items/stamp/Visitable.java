/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online.items.stamp;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.05.13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public interface Visitable<T> {

    void accept(Visitor<T> visitor);
}
