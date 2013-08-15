/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal.group;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 13.08.13
 * Time: 19:18
 * To change this template use File | Settings | File Templates.
 */
public class GroupCreateEvent {
    private String groupName;
    private boolean succeed;

    public GroupCreateEvent (String groupName, boolean succeed) {
        this.groupName = groupName;
        this.succeed = succeed;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isSucceed() {
        return succeed;
    }
}