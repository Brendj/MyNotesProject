/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh;

import ru.axetta.ecafe.processor.core.partner.mesh.json.Category;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Education;
import ru.axetta.ecafe.processor.core.partner.mesh.json.ResponsePersons;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.MeshTrainingForm;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@DependsOn("runtimeContext")
@Component("meshPersonsSearchService")
public class MeshPersonsSearchService extends MeshPersonsSyncService {

    private static final Logger logger = LoggerFactory.getLogger(MeshPersonsSearchService.class);
    private ThreadLocal<List<ResponsePersons>> meshResponses =  new ThreadLocal<List<ResponsePersons>>(){
        @Override
        protected ArrayList<ResponsePersons> initialValue() {
            return new ArrayList<ResponsePersons>();
        }
    };
    public ThreadLocal<List<ResponsePersons>> getMeshResponses() {
        return meshResponses;
    }

    @Override
    protected void processPerson(Session session, ResponsePersons person, Map<Integer, MeshTrainingForm> trainingForms) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Education education = findEducation(person);
            String classname = null;
            String guidNsi = null;
            try {
                Date endTraining = df.parse(education.getTrainingEndAt());
                person.setTraining_end_at(df.format(endTraining));
            } catch (NullPointerException e){
                logger.info("endTraining not found");
            }
            try {
                Category category = findCategory(person);
                guidNsi = category.getParameterValues().get(0).toString();
                person.setGuidNsi(guidNsi);
                person.setIdIsPp(searchByMeshGuid(guidNsi));
            } catch (Exception e){
                logger.info("Not found NSI guid for person with mesh guid " + person.getPersonId());
            }
            try {
                if (isHomeStudy(education, trainingForms))
                    classname = ClientGroup.Predefined.CLIENT_OUT_ORG.getNameOfGroup();
                else
                    classname = education.getClass_().getName();
                person.setClassName(classname);
            } catch (NullPointerException e){
                logger.info("Group not found");
            }
            try {
                person.setOoId(Long.toString(education.getOrganizationId().longValue()));
            } catch (NullPointerException e) {
                logger.info("organization not found");
            }
        } catch (Exception e) {
            logger.error(String.format("Error in process Mesh person with guid %s: ", person.getPersonId()), e);
        }
    }
}
