/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.security;

/**
 * Created by nuc on 10.12.2020.
 */
public class SecurityClientAuthorizationItem {
    private Boolean enabled;
    private Integer option;
    private String optionName;

    public SecurityClientAuthorizationItem(Integer option, Boolean enabled, String optionName) {
        this.option = option;
        this.enabled = enabled;
        this.optionName = optionName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
