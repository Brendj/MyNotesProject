/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshRestClient;
import ru.axetta.ecafe.processor.core.partner.mesh.json.CardPropertiesEnum;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Category;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Parameter;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MeshCardServiceIml implements MeshCardService {
    private final Logger log = LoggerFactory.getLogger(MeshCardServiceIml.class);
    private final ObjectMapper ob = new ObjectMapper();
    private final SimpleDateFormat sdfForActualDates = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private final SimpleDateFormat sdfForIssueDate = new SimpleDateFormat("yyyy-MM-dd");
    private static final String MESH_REST_ADDRESS_PROPERTY = "ecafe.processing.mesh.card.rest.address";
    private static final String MESH_REST_API_KEY_PROPERTY = "ecafe.processing.mesh.card.rest.api.key";
    private static final Integer CARD_CATEGORY_ID = 1000;

    private MeshRestClient meshRestClient;

    public MeshCardServiceIml(){
        String serviceAddress;
        String apiKey;
        try {
            serviceAddress = getServiceAddress();
            apiKey = getApiKey();
            this.meshRestClient = new MeshRestClient(serviceAddress, apiKey);
        } catch (Exception e){
            log.error("Cant't create REST-Client", e);
            this.meshRestClient = null;
        }
    }

    @Override
    public MeshClientCardRef createReferenceBetweenClientAndCard(Card card) {
        Category category = buildCategory(card.getClient().getMeshGUID(), card);
        MeshClientCardRef refCardClient = null;
        try {
            String json = ob.writeValueAsString(category);
            byte[] response = meshRestClient.executeCreateCategory(card.getClient().getMeshGUID(), json);
            Category responseCategory = ob.readValue(response, Category.class);
            refCardClient = MeshClientCardRef.build(card, responseCategory.getId());
        } catch (Exception e){
            refCardClient = MeshClientCardRef.build(card, null);
            log.error("Exception, when send POST-request", e);
        }
        return refCardClient;
    }

    @Override
    public MeshClientCardRef updateCardForClient(MeshClientCardRef ref) {
        Category category = buildCategory(ref.getCard().getClient().getMeshGUID(), ref.getCard());
        category.setId(ref.getIdOfRefInExternalSystem());
        try {
            String json = ob.writeValueAsString(category);
            meshRestClient.executeUpdateCategory(ref.getClient().getMeshGUID(), ref.getIdOfRefInExternalSystem(), json);
            ref.setSend(true);
        } catch (Exception e){
            ref.setSend(false);
            log.error("Exception, when send PUT-request", e);
        } finally {
            ref.setLastUpdate(new Date());
        }
        return ref;
    }

    @Override
    public void deleteReferenceBetweenClientAndCard(Client client, Card card) {
    }

    @Override
    public void changeCardOwner(Client clientFrom, Client clientTo, Card card) {
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

    private Category buildCategory(String meshGUID, Card card) {
        Category category = new Category();
        category.setId(0);
        category.setCategoryId(CARD_CATEGORY_ID);
        category.setPersonId(meshGUID);
        category.setActualFrom(sdfForActualDates.format(card.getCreateTime()));
        category.setActualTo(sdfForActualDates.format(card.getValidTime()));

        category.getParameterValues().add(new Parameter(CardPropertiesEnum.CARD_UID, card.getCardNo()));
        category.getParameterValues().add(new Parameter(CardPropertiesEnum.CARD_TYPE, Card.TYPE_NAMES[card.getCardType()]));
        category.getParameterValues().add(new Parameter(CardPropertiesEnum.BOARD_CARD_NUMBER, card.getCardPrintedNo()));
        category.getParameterValues().add(new Parameter(CardPropertiesEnum.ISSUE_DATE, sdfForIssueDate.format(card.getIssueTime())));
        category.getParameterValues().add(new Parameter(CardPropertiesEnum.ACTION_PERIOD, sdfForIssueDate.format(card.getValidTime())));
        category.getParameterValues().add(new Parameter(CardPropertiesEnum.CARD_STATUS, CardState.fromInteger(card.getState())));

        return category;
    }
}
