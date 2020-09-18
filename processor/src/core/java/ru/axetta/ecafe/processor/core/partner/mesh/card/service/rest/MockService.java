/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;

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
    public MeshClientCardRef updateCardForClient(MeshClientCardRef ref) {
        return ref;
    }

    @Override
    public void deleteReferenceBetweenClientAndCard(Client client, Card card) {
        // unsupported operation
    }

    @Override
    public MeshClientCardRef createReferenceBetweenClientAndCard(Card card) {
        return null;
    }

    @Override
    public void changeCardOwner(Client clientFrom, Client clientTo, Card card) {
        // unsupported operation
    }
}
