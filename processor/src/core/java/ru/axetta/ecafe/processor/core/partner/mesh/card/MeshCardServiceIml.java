/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card;

import ru.axetta.ecafe.processor.core.persistence.Card;

import org.springframework.stereotype.Component;

@Component
public class MeshCardServiceIml implements MeshCardService {

    @Override
    public int updateCardForClient(String meshGUID, Card card) {
        return 0;
    }

    @Override
    public int deleteReferenceBetweenClientAndCard(String meshGUID, Long idOfCard) {
        return 0;
    }

    @Override
    public int createReferenceBetweenClientAndCard(String meshGUID, Card card) {
        return 0;
    }

    @Override
    public int changeCardOwner(String meshGUIDFrom, String meshGUIDto, Long idOfCard) {
        return 0;
    }
}
