/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 10.07.14
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public class RegistryChangeRevisionItem {
    protected long date;
    protected int type;

    public RegistryChangeRevisionItem() {
    }

    public RegistryChangeRevisionItem(long date, int type) {
        this.date = date;
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
