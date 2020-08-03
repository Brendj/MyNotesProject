/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.mesh;

import ru.axetta.ecafe.processor.core.persistence.Card;

public interface MeshCardService {
    int OK = 1;

    int updateCardForClient(String meshGUID, Card card);

    int deleteReferenceBetweenClientAndCard(String meshGUID, Long idOfCard);

    int createReferenceBetweenClientAndCard(String meshGUID, Card card);

    int changeCardOwner(String meshGUIDFrom, String meshGUIDto, Long idOfCard);
}
