/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.CryptoPro.JCP.JCP;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class CryptoInfoPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(CryptoInfoPage.class);

    @Override
    public String getPageFilename() {
        return "option/crypto_info_page";
    }

    public String getCryptoLibRelease() {
        return "" + JCP.getProductRelease();
    }

    public String getCryptoLibVersion() {
        return "" + JCP.getProductVersion();
    }

    //public String getCryptoLibURL() {
    //    return "" + JCP.getProviderURL();
    //}

    public String getCryptoProviderName() {
        return JCP.PROVIDER_NAME;
    }

}
