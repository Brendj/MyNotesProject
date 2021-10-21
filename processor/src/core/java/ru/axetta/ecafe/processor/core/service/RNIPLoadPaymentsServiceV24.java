/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import generated.ru.gov.smev.artefacts.x.services.message_exchange._1.SMEVMessageExchangePortType_24;
import generated.ru.mos.rnip.xsd.catalog._2_1.ServiceCatalogType;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ImportCatalogRequest;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ejb.Init;

@Component("RNIPLoadPaymentsServiceV24")
@Scope("singleton")
public class RNIPLoadPaymentsServiceV24 extends RNIPLoadPaymentsServiceV22 {
    private static SMEVMessageExchangePortType_24 port24;

    @Override
    protected RNIPSecuritySOAPHandlerV22 getSecurityHandler(String alias, String pass, Contragent contragent) {
        return new RNIPSecuritySOAPHandlerV22(alias, pass, getPacketLogger(contragent));
    }

    @Override
    protected void setProperCatalogRequestSection(int requestType, ImportCatalogRequest importCatalogRequest,
            ServiceCatalogType serviceCatalogType) {
        importCatalogRequest.setServiceCatalog(serviceCatalogType);
    }

    @Override
    public String getRNIPUrl() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V22);
    }

}
