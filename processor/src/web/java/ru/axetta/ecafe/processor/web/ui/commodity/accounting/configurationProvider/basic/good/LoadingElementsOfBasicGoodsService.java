/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;


/**
 * Created by anvarov on 17.02.2017.
 */
public class LoadingElementsOfBasicGoodsService {

    public ConfigurationProvider findConfigurationProviderByName(Session session, String nameOfGood) {
        ConfigurationProvider configurationProvider = DAOUtils.findConfigurationProviderByName(session, nameOfGood);
        return configurationProvider;
    }
}
