/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.mesh;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("meshRestService")
@DependsOn("runtimeContext")
public class MeshRestService {
    private final static Logger log = LoggerFactory.getLogger(MeshRestService.class);

    private final String TARGET_URL = "https://mes-api-test.mos.ru/contingent";
    private final String API_KEY = "ed046cb1-5ae9-4b97-9094-b078a6ecc503";

    private final HttpClient client;
    private final Header header = new Header("X-Api-Key", API_KEY);

    public MeshRestService() {
        this.client = new HttpClient();
    }


}
