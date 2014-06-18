/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 09.06.14
 * Time: 17:29
 */

public class ProhibitionsResult extends Result {

    public Long prohibitionId = null;

    public ProhibitionsResult(Long resultCode, String desc) {
        super(resultCode, desc);
        this.prohibitionId = null;
    }

    public ProhibitionsResult(Long prohibitionId, Long resultCode, String desc) {
        super(resultCode, desc);
        this.prohibitionId = prohibitionId;
    }

    public ProhibitionsResult() {
    }
}
