/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.persistence.PreorderState;
import ru.axetta.ecafe.processor.core.persistence.RegularPreorder;

import org.hibernate.Session;

/**
 * Created by i.semenov on 01.08.2018.
 */
public interface IPreorderDAOOperations {
    void deleteRegularPreorder(Session session, RegularPreorder regularPreorder, PreorderState preorderState) throws Exception;
    void generatePreordersBySchedule();
}
