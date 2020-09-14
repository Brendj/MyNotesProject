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
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class MeshCardServiceIml implements MeshCardService {
    private final Logger log = LoggerFactory.getLogger(MeshCardServiceIml.class);
    private final ObjectMapper ob = new ObjectMapper();
    private final SimpleDateFormat sdfForActualDates = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private final SimpleDateFormat sdfForIssueDate = new SimpleDateFormat("yyyy-MM-dd");
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
            log.error("Cant't create REST-Client", e);
            this.meshRestClient = null;
        }
    }

    @Override
    public void createReferenceBetweenClientAndCard(Client client, Card card) {
        Category category = buildCategory(client.getMeshGUID(), card);
        Session session = null;
        Transaction transaction = null;
        MeshClientCardRef refCardClient = null;
        try {
            String json = ob.writeValueAsString(category);
            byte[] response = meshRestClient.executeCreateCategory(client.getMeshGUID(), json);
            Category responseCategory = ob.readValue(response, Category.class);
            refCardClient = MeshClientCardRef.build(card, client, responseCategory.getId());
        } catch (Exception e){
            refCardClient = MeshClientCardRef.build(card, client, null);
            log.error("Exception, when send POST-request", e);
        }

        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            session.save(refCardClient);

            transaction.commit();
            transaction = null;
        } catch (Exception e){
            log.error("", e);
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }

    @Override
    public void updateCardForClient(Client client, Card card) {

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
