/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 13.02.2020.
 */
public class PreorderStatus {
    private Date date;
    private String guid;
    private PreorderStatusType status;
    private Integer storno;
    private Long version;
    private Boolean deletedState;
}
