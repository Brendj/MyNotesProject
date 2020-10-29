/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.spb;

import generated.spb.register.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.service.spb.ClientSpbService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component
@Scope("singleton")
@DependsOn("runtimeContext")
public class SpbClientService {

    private static final Logger logger = LoggerFactory.getLogger(SpbClientService.class);

    private static ClientSpbService service = RuntimeContext.getAppContext().getBean(ClientSpbService.class);

    public List<Pupil> getPupilsByOrg(String guid, String registryUrl) throws Exception {
        QuerySchool querySchool = new QuerySchool();
        querySchool.setId(guid);
        Query query = new Query();
        query.setSchool(querySchool);

        Schools schools = service.sendEvent(query, registryUrl);

        List<Pupil> pupils = new ArrayList<Pupil>();

        boolean found = false;
        for(School school : schools.getSchool()) {
            if(school.getSchoolId().equals(guid)) {
                pupils = school.getPupils().getPupil();
                found = true;
            }
        }

        if(!found) {
            throw new Exception(String.format("Не найдены данные по организации guid = %s.", guid));
        }

        return pupils;
    }



}
