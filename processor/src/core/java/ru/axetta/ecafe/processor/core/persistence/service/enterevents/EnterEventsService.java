/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.enterevents;

import ru.axetta.ecafe.processor.core.persistence.dao.enterevents.EnterEventsRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: shamil
 * Date: 19.09.14
 * Time: 12:28
 */
@Service
public class EnterEventsService {

    @Autowired
    EnterEventsRepository enterEventsRepository;

    public List<DAOEnterEventSummaryModel> getEnterEventsSummary(Long dateTime){
        return enterEventsRepository.getEnterEventsSummary(dateTime);
    }
}
