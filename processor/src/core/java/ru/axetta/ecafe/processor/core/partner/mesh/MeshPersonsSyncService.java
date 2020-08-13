/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.json.And;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Education;
import ru.axetta.ecafe.processor.core.partner.mesh.json.MeshJsonFilter;
import ru.axetta.ecafe.processor.core.partner.mesh.json.ResponsePersons;
import ru.axetta.ecafe.processor.core.persistence.MeshSyncPerson;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by nuc on 12.08.2020.
 */
@Component
public class MeshPersonsSyncService {
    public static final String MESH_REST_PERSONS_URL = "/persons?";
    public static final String MESH_REST_PERSONS_EXPAND = "education,categories";

    private static final String FILTER_VALUE_ORG = "education.organization_id";
    private static final String FILTER_VALUE_EQUALS = "equal";
    private static final String FILTER_VALUE_LASTNAME = "lastname";
    private static final String FILTER_VALUE_FIRSTNAME = "firstname";
    private static final String FILTER_VALUE_PATRONYMIC = "patronymic";

    @Autowired
    MeshRestClient meshRestClient;

    private static final Logger logger = LoggerFactory.getLogger(MeshPersonsSyncService.class);

    public void loadPersons(long idOfOrg, String lastName, String firstName, String patronymic) throws Exception {
        String parameters = String.format("filter=%s&expand=%s", URLEncoder
                .encode(getFilter(idOfOrg, lastName, firstName, patronymic), "UTF-8"), getExpand());
        try {
            byte[] response = meshRestClient.executeRequest(MESH_REST_PERSONS_URL, parameters);
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(
                    List.class, ResponsePersons.class);
            List<ResponsePersons> meshResponses = objectMapper.readValue(response, collectionType);
            Session session = null;
            Transaction transaction = null;
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                session.setFlushMode(FlushMode.COMMIT);
                transaction = session.beginTransaction();
                for (ResponsePersons person : meshResponses) {
                    processPerson(session, person);
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        } catch (Exception e) {
            logger.error("Error in load persons from Mesh", e);
        }
    }

    private void processPerson(Session session, ResponsePersons person) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String personguid = person.getPersonId();
            Date birthdate = df.parse(person.getBirthdate());
            Education education = findEducation(person);
            if (education == null) return;
            Date endTraining = df.parse(education.getTrainingEndAt());
            boolean deleted = false;
            if (endTraining.before(new Date())) deleted = true;
            String classname = education.getClass_().getName();
            String classuid = education.getClassUid();
            String firstname = person.getFirstname();
            Integer genderid = person.getGenderId();
            String lastname = person.getLastname();
            Long organizationid = education.getOrganizationId().longValue();
            Integer parallelid = (Integer)(education.getClass_().getParallelId());
            String patronymic = person.getPatronymic();
            Integer educationstageid = education.getClass_().getEducationStageId();
            String guidnsi = null;
            try {
                guidnsi = person.getCategories().get(0).getParameterValues().get(0);
            } catch (Exception e) {
                logger.info("Not found NSI guid for person with mesh guid " + personguid);
            }
            MeshSyncPerson meshSyncPerson = (MeshSyncPerson)session.get(MeshSyncPerson.class, personguid);
            if (meshSyncPerson == null) meshSyncPerson = new MeshSyncPerson(personguid);
            meshSyncPerson.setBirthdate(birthdate);
            meshSyncPerson.setClassname(classname);
            meshSyncPerson.setClassuid(classuid);
            meshSyncPerson.setFirstname(firstname);
            meshSyncPerson.setGenderid(genderid);
            meshSyncPerson.setLastname(lastname);
            meshSyncPerson.setOrganizationid(organizationid);
            meshSyncPerson.setParallelid(parallelid);
            meshSyncPerson.setPatronymic(patronymic);
            meshSyncPerson.setEducationstageid(educationstageid);
            meshSyncPerson.setGuidnsi(guidnsi);
            meshSyncPerson.setLastupdateRest(new Date());
            meshSyncPerson.setDeletestate(deleted);
            session.saveOrUpdate(meshSyncPerson);

        } catch (Exception e) {
            logger.error("Error in process Mesh person: ", e);
        }
    }

    private Education findEducation(ResponsePersons person) {
        try {
            Collections.sort(person.getEducation());
            return person.getEducation().get(person.getEducation().size() - 1);
        } catch (Exception e) {
            logger.error("Can not find education from person with guid " + person.getPersonId());
            return null;
        }
    }

    private String getExpand() {
        return MESH_REST_PERSONS_EXPAND;
    }

    private String getFilter(long idOfOrg, String lastName, String firstName, String patronymic) throws Exception {
        Long meshId = DAOService.getInstance().getMeshIdByOrg(idOfOrg);
        if (meshId == null) throw new Exception("У организации не указан МЭШ ид.");
        MeshJsonFilter filter = new MeshJsonFilter();
        List<And> list = new ArrayList<>();
        And andOrg = new And();
        andOrg.setField(FILTER_VALUE_ORG);
        andOrg.setOp(FILTER_VALUE_EQUALS);
        andOrg.setValue(meshId.toString());
        list.add(andOrg);
        if (!StringUtils.isEmpty(lastName)) {
            And andLastname = new And();
            andLastname.setField(FILTER_VALUE_LASTNAME);
            andLastname.setOp(FILTER_VALUE_EQUALS);
            andLastname.setValue(lastName);
            list.add(andLastname);
        }
        if (!StringUtils.isEmpty(firstName)) {
            And andFirstname = new And();
            andFirstname.setField(FILTER_VALUE_FIRSTNAME);
            andFirstname.setOp(FILTER_VALUE_EQUALS);
            andFirstname.setValue(firstName);
            list.add(andFirstname);
        }
        if (!StringUtils.isEmpty(patronymic)) {
            And andPatronymic = new And();
            andPatronymic.setField(FILTER_VALUE_PATRONYMIC);
            andPatronymic.setOp(FILTER_VALUE_EQUALS);
            andPatronymic.setValue(patronymic);
            list.add(andPatronymic);
        }
        filter.setAnd(list);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(filter);
    }
}
