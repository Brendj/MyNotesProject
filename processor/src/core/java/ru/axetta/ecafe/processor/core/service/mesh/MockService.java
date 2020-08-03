/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.mesh;

import ru.axetta.ecafe.processor.core.persistence.Card;

public class MockService implements MeshCardService {

    private static MockService instance;

    private MockService(){
    }

    public static MeshCardService getInstance(){
        if(instance == null){
            instance = new MockService();
        }
        return instance;
    }

    @Override
    public int updateCardForClient(String meshGUID, Card card) {
        // unsupported operation
        return OK;
    }

    @Override
    public int deleteReferenceBetweenClientAndCard(String meshGUID, Long idOfCard) {
        // unsupported operation
        return OK;
    }

    @Override
    public int createReferenceBetweenClientAndCard(String meshGUID, Card card) {
        // unsupported operation
        return OK;
    }

    @Override
    public int changeCardOwner(String meshGUIDFrom, String meshGUIDto, Long idOfCard) {
        // unsupported operation
        return OK;
    }
}
