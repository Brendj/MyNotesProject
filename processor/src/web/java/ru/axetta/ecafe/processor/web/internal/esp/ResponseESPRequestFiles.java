/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 16.06.2021.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import java.util.ArrayList;
import java.util.List;

public class ResponseESPRequestFiles extends Result{
    private List<ESPRequestAttachedFile> attached;

    public List<ESPRequestAttachedFile> getAttached() {
        if (attached == null)
            attached = new ArrayList<>();
        return attached;
    }

    public void setAttached(List<ESPRequestAttachedFile> attached) {
        this.attached = attached;
    }
}
