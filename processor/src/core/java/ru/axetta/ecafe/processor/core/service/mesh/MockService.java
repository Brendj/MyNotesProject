/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.mesh;

public class MockService implements MeshCardService {

    @Override
    public int updateCardForClient() {
        // unsupported operation
        return OK;
    }

    @Override
    public int deleteReferenceBetweenClientAndCard() {
        // unsupported operation
        return OK;
    }

    @Override
    public int createReferenceBetweenClientAndCard() {
        // unsupported operation
        return OK;
    }

    @Override
    public int changeCardOwner() {
        // unsupported operation
        return OK;
    }
}
