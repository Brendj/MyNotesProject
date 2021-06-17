/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 16.06.2021.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import java.util.ArrayList;
import java.util.List;

public class ResponseESPRequests extends Result{
    private List<ResponseESPRequestsPOJO> espRequests ;

    public List<ResponseESPRequestsPOJO> getEspRequests() {
        if (espRequests == null)
            espRequests = new ArrayList<>();
        return espRequests;
    }

    public void setEspRequests(List<ResponseESPRequestsPOJO> espRequests) {
        this.espRequests = espRequests;
    }
}
