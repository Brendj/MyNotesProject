/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.enterevents;

import ru.axetta.ecafe.processor.core.persistence.dao.enterevents.EnterEventsRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * User: shamil
 * Date: 19.09.14
 * Time: 12:28
 */
@Service
public class EnterEventsService {

    @Autowired
    EnterEventsRepository enterEventsRepository;

    public List<DAOEnterEventSummaryModel> getEnterEventsSummary(Long idOfOrg, Long startTime, Long endTime){
        return enterEventsRepository.getEnterEventsSummary(idOfOrg,startTime,endTime);
    }
    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryNotEmptyClient(Long dateTime){
        return enterEventsRepository.getEnterEventsSummaryNotEmptyClient(dateTime);
    }

    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryVisitors(Long dateTime){
        return enterEventsRepository.getEnterEventsSummaryVisitors(dateTime);
    }

    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryEmptyClient(Long dateTime){
        return enterEventsRepository.getEnterEventsSummaryEmptyClient(dateTime);
    }

    public Map<Long, Map<Long, List<DAOEnterEventSummaryModel>>> getEnterEventsSummaryNotEmptyClientFull(Long dateTime){
        return enterEventsRepository.getEnterEventsSummaryNotEmptyClientFull(dateTime);
    }
}
