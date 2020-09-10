/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshRestClient;
import ru.axetta.ecafe.processor.core.persistence.Card;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ejb.DependsOn;

@DependsOn("runtimeContext")
@Service
public class MeshCardServiceIml implements MeshCardService {
    private final Logger log = LoggerFactory.getLogger(MeshCardServiceIml.class);
    private static final String MESH_REST_ADDRESS_PROPERTY = "ecafe.processing.mesh.card.rest.address";
    private static final String MESH_REST_API_KEY_PROPERTY = "ecafe.processing.mesh.card.rest.api.key";

    private MeshRestClient meshRestClient;

    public MeshCardServiceIml(){
        String serviceAddress;
        String apiKey;
        try {
            serviceAddress = getServiceAddress();
            apiKey = getApiKey();
            this.meshRestClient = new MeshRestClient(serviceAddress, apiKey);
        } catch (Exception e){
            this.meshRestClient = null;
        }
    }

    @Override
    public int createReferenceBetweenClientAndCard(String meshGUID, Card card) {

        return 0;
    }

    @Override
    public int updateCardForClient(String meshGUID, Card card) {
        return 0;
    }

    @Override
    public int deleteReferenceBetweenClientAndCard(String meshGUID, Long idOfCard) {
        return 0;
    }

    @Override
    public int changeCardOwner(String meshGUIDFrom, String meshGUIDto, Long idOfCard) {
        return 0;
    }

    private String getServiceAddress() throws Exception{
        String address = RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_ADDRESS_PROPERTY, "");
        if (address.equals("")) throw new Exception("MESH REST address not specified");
        return address;
    }

    private String getApiKey() throws Exception{
        String key = RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_API_KEY_PROPERTY, "");
        if (key.equals("")) throw new Exception("MESH API key not specified");
        return key;
    }
}
