/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.mesh;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("meshRestService")
public class MeshCardServiceIml implements MeshCardService {
    private final MeshRestService meshRestService;

    public MeshCardServiceIml(){
        this.meshRestService = RuntimeContext.getAppContext().getBean(MeshRestService.class);
    }

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
