/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.json;

import java.util.List;

/**
 * Created by nuc on 11.08.2020.
 */
public class MeshResponse {
    private List<ResponsePersons> responsePersonses;

    public List<ResponsePersons> getResponsePersonses() {
        return responsePersonses;
    }

    public void setResponsePersonses(List<ResponsePersons> responsePersonses) {
        this.responsePersonses = responsePersonses;
    }
}
