/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;

public interface MeshCardService {
    MeshClientCardRef updateCardForClient(MeshClientCardRef ref);

    MeshClientCardRef deleteReferenceBetweenClientAndCard(MeshClientCardRef ref);

    MeshClientCardRef createReferenceBetweenClientAndCard(Card card);
}
