/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;

/**
 * Created by i.semenov on 27.10.2017.
 */
public class ConfigurationProviderItem {
    private Boolean selected;
    private Long idOfConfigurationProvider;
    private String name;

    public ConfigurationProviderItem() {

    }

    public ConfigurationProviderItem(ConfigurationProvider configurationProvider) {
        this.idOfConfigurationProvider = configurationProvider.getIdOfConfigurationProvider();
        this.name = configurationProvider.getName();
        this.selected = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigurationProviderItem)) {
            return false;
        }
        final ConfigurationProviderItem item = (ConfigurationProviderItem) o;
        return idOfConfigurationProvider.equals(item.getIdOfConfigurationProvider());
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
