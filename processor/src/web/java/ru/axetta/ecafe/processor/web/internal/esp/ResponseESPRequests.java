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
    private List<ResponseESPRequestsPOJO> espRequestsPOJOS ;

    public List<ResponseESPRequestsPOJO> getEspRequestsPOJOS() {
        if (espRequestsPOJOS == null)
            espRequestsPOJOS = new ArrayList<>();
        return espRequestsPOJOS;
    }

    public void setEspRequestsPOJOS(List<ResponseESPRequestsPOJO> espRequestsPOJOS) {
        this.espRequestsPOJOS = espRequestsPOJOS;
    }
}
