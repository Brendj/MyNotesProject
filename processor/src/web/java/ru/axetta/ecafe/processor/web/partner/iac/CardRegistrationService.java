/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.iac;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Scope("singleton")
public class CardRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(CardRegistrationService.class);

    public static final String COMMENT_ADDED_FROM_IAC = "{Добавлен из ИАЦ}";

    public Client registerNewClient(Session session, String firstName, String secondName, String surname, Date birthDate, String guid,
            String extId, String organizationGuid) throws Exception {
        ClientManager.ClientFieldConfig fieldConfig = new ClientManager.ClientFieldConfig();
        fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, guid);
        fieldConfig.setValue(ClientManager.FieldId.SURNAME, emptyIfNull(surname));
        fieldConfig.setValue(ClientManager.FieldId.NAME, emptyIfNull(firstName));
        fieldConfig.setValue(ClientManager.FieldId.SECONDNAME, emptyIfNull(secondName));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        fieldConfig.setValue(ClientManager.FieldId.BIRTH_DATE, dateFormat.format(birthDate));
        fieldConfig.setValue(ClientManager.FieldId.EXTERNAL_ID, extId);
        fieldConfig.setValue(ClientManager.FieldId.COMMENTS, COMMENT_ADDED_FROM_IAC);

        Org org = DAOUtils.findOrgByGuid(session, organizationGuid);
        if (null == org) {
            throw new OrganizationNotFoundException(String.format("Organization not found: guid = %s", organizationGuid));
        }

        Long idOfClient = ClientManager.registerClient(org.getIdOfOrg(), fieldConfig, true, true);

        return (Client) session.load(Client.class, idOfClient);
    }

    public void registerCard(Session session, Long cardId, Date validDate, Client client) throws Exception {

        if (null == cardId)
            throw new RequiredFieldsAreNotFilledException("Required fields are not filled: cardId = null");

        Card card = DAOUtils.findCardByCardNoAndOrg(session, cardId, client.getOrg().getIdOfOrg());

        if (null == card) {
            blockAllOtherClientCards(session, client);
            if (null == validDate)
                validDate = CalendarUtils.addYear(new Date(), 5);
            RuntimeContext.getInstance().getCardManager().createCard(session, session.getTransaction(), client.getIdOfClient(),
                    cardId, Arrays.asList(Card.TYPE_NAMES).indexOf("Mifare"), CardState.ISSUED.getValue(), validDate,
                    Card.ISSUED_LIFE_STATE, null, new Date(), cardId);
        } else {
            if (CardState.BLOCKED.getValue() != card.getState() && CardState.TEMPBLOCKED.getValue() != card.getState()) {
                throw new CardAlreadyUsedException(String.format("Card already used: cardId = %d, orgId = %d, clientId = %d",
                        cardId, client.getOrg().getIdOfOrg(), client.getIdOfClient()));
            } else {
                if (card.getClient().getIdOfClient().equals(client.getIdOfClient())) {
                    blockAllOtherClientCards(session, client);
                    card.setState(CardState.ISSUED.getValue());
                } else {
                    throw new CardAlreadyUsedException(String.format("Card already used: cardId = %d, orgId = %d, clientId = %d",
                            cardId, client.getOrg().getIdOfOrg(), client.getIdOfClient()));
                }
            }
        }
    }

    private void blockAllOtherClientCards(Session session, Client client) throws Exception {
        List<Card> cardList = DAOUtils.getAllCardByClient(session, client);
        CardManager cardManager = RuntimeContext.getInstance().getCardManager();

        for (Card card : cardList) {
            if (card.getState() != CardState.BLOCKED.getValue() && card.getState() != CardState.TEMPBLOCKED.getValue())
                cardManager.updateCard(card.getClient().getIdOfClient(), card.getIdOfCard(), card.getCardType(),
                        CardState.BLOCKED.getValue(), card.getValidTime(), card.getLifeState(), card.getLockReason(),
                        card.getIssueTime(), card.getExternalId());
        }
    }

    private String emptyIfNull(String str) {
        return str == null ? "" : str;
    }
}