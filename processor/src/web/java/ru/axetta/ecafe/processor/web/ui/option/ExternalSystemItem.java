/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

/**
 * Created with IntelliJ IDEA.
 * User: liya
 * Date: 18.07.17
 * Time: 10:37
 */

public class ExternalSystemItem {

    private Integer type;
    private String name;
    private boolean enabled;

    public ExternalSystemItem(Integer type, String name, boolean enabled) {
        this.type = type;
        this.name = name;
        this.enabled = enabled;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
