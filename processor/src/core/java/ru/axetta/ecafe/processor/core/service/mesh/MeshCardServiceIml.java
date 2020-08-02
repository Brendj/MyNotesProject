/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.mesh;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("meshRestService")
public class MeshCardServiceIml {
    private final MeshRestService meshRestService;

    public MeshCardServiceIml(){
        this.meshRestService = RuntimeContext.getAppContext().getBean(MeshRestService.class);
    }
}
