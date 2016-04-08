/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp;

import generated.emp_storage.SelectEntriesRequest;
import generated.emp_storage.SelectEntriesResponse;

import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.test.synch.JUnit4ClassRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/context.xml" })
public class CatalogFindTest {

    EMPProcessor empProcessor;

    @Before
    public void init() throws Exception {
        /// "http://91.228.153.167:8090/ws/subscriptions/?wsdl", "http://91.228.153.167:8090/ws/storage/?wsdl",
        ///                "49aafdb8198311e48ee8416c74617269", "666255", "SYS666254CAT0000000SUBSCRIPTIONS", "true", "", "200"
        empProcessor = new EMPProcessor();
    }

    @Test
    public void searchInCatalogueByPhone() {
        SelectEntriesRequest request = empProcessor.buildSelectEntryParams("79162224001", null);
        searchInCatalogue(request);
    }

    @Test
    public void searchInCatalogueByContractId() {
        SelectEntriesRequest request = empProcessor.buildSelectEntryParams(null, 1L);
        searchInCatalogue(request);
    }

    public void searchInCatalogue(SelectEntriesRequest request) {
        SelectEntriesResponse response = empProcessor.createStorageController().selectEntries(request);
        if (response.getErrorCode() == EMPProcessor.EMP_ERROR_CODE_NOTHING_FOUND) {
            System.out.println("Client not found in catalog");
        }
        if (response.getErrorCode() != 0) {
            System.out.println("Error: " + response.getErrorCode() + ": " + response.getErrorMessage());
        }
    }

}
