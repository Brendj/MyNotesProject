/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;

public interface MeshCardService {
    void updateCardForClient(Client client, Card card);

    void deleteReferenceBetweenClientAndCard(Client client, Card card);

    void createReferenceBetweenClientAndCard(Client client, Card card);

    void changeCardOwner(Client clientFrom, Client clientTo, Card card);
}
