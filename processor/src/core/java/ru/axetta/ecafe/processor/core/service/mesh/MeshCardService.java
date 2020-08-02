/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.mesh;

public interface MeshCardService {
    int OK = 1;

    int updateCardForClient();

    int deleteReferenceBetweenClientAndCard();

    int createReferenceBetweenClientAndCard();

    int changeCardOwner();
}
