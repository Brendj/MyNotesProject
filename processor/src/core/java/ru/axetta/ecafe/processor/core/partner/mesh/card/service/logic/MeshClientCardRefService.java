/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.card.service.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MeshCardService;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MeshCardServiceIml;
import ru.axetta.ecafe.processor.core.partner.mesh.card.service.rest.MockService;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.MeshClientCardRef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;

@DependsOn("runtimeContext")
@Service
public class MeshClientCardRefService {
    private final Logger log = LoggerFactory.getLogger(MeshClientCardRefService.class);

    private MeshCardService meshCardService;

    @PostConstruct
    public void initService(){
        RuntimeContext.RegistryType currentType = RuntimeContext.getInstance().getRegistryType();
        if(currentType.equals(RuntimeContext.RegistryType.MSK)){
            this.meshCardService = new MeshCardServiceIml();
        } else {
            this.meshCardService = MockService.getInstance();
        }
    }

    public MeshClientCardRef createRef(Card c) {
        MeshClientCardRef ref = null;
        try {
            ref = meshCardService.createReferenceBetweenClientAndCard(c);
        } catch (Exception e){
            log.error("Can't create Ref", e);
        }
        return ref;
    }

    public MeshClientCardRef updateRef(MeshClientCardRef ref) {
        try {
            return meshCardService.updateCardForClient(ref);
        } catch (Exception e){
            log.error("Can't update Ref", e);
            return ref;
        }
    }

    public MeshClientCardRef deleteRef(MeshClientCardRef ref) {
        try {
            return meshCardService.deleteReferenceBetweenClientAndCard(ref);
        } catch (Exception e){
            log.error("Can't delete Ref", e);
            return ref;
        }
    }
}
