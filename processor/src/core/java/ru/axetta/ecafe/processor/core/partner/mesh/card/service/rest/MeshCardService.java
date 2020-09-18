/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;

public interface MeshCardService {
    MeshClientCardRef updateCardForClient(MeshClientCardRef ref);

    void deleteReferenceBetweenClientAndCard(Client client, Card card);

    MeshClientCardRef createReferenceBetweenClientAndCard(Card card);

    void changeCardOwner(Client clientFrom, Client clientTo, Card card);
}
