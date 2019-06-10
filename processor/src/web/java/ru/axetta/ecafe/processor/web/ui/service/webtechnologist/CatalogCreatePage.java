/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("webTechnologistCatalogCreatePage")
@Scope("session")
public class CatalogCreatePage extends BasicWorkspacePage {
    private static Logger logger = LoggerFactory.getLogger(CatalogCreatePage.class);


}
