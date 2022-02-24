/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh;

import org.hibernate.*;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Class;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nuc on 12.08.2020.
 */
@DependsOn("runtimeContext")
@Primary
@Component("meshPersonsSyncService")
public class MeshPersonsSyncService {
    public static final String MESH_REST_PERSONS_URL = "/persons?";
    public static final String MESH_REST_PERSONS_EXPAND = "education,categories";
    public static final String MESH_REST_PERSONS_TOP_PROPEERTY = "ecafe.processing.mesh.rest.persons.top";
    public static final String MESH_REST_ADDRESS_PROPERTY = "ecafe.processing.mesh.rest.address";
    public static final String MESH_REST_API_KEY_PROPERTY = "ecafe.processing.mesh.rest.api.key";

    public static final String TOP_DEFAULT = "50000";

    private static final String FILTER_VALUE_ORG = "education.organization_id";
    private static final String FILTER_VALUE_EQUALS = "equal";
    private static final String FILTER_VALUE_LASTNAME = "lastname";
    private static final String FILTER_VALUE_FIRSTNAME = "firstname";
    private static final String FILTER_VALUE_PATRONYMIC = "patronymic";

    private static final String OUT_ORG_GROUP_PREFIX = "Вне";

    private MeshRestClient meshRestClient;

    protected MeshPersonsSyncService() {
        String serviceAddress;
        String apiKey;
        try {
            serviceAddress = getServiceAddress();
            apiKey = getApiKey();
            this.meshRestClient = new MeshRestClient(serviceAddress, apiKey);
        } catch (Exception e) {
            this.meshRestClient = null;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(MeshPersonsSyncService.class);

    protected void processMeshResponse(List<ResponsePersons> meshResponses){
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            session.setFlushMode(FlushMode.COMMIT);
            Map<Integer, MeshTrainingForm> trainingForms = getTrainingForms(session);
            for (ResponsePersons person : meshResponses) {
                processPerson(session, person, trainingForms);
            }
            RuntimeContext.getAppContext().getBean(MeshPersonsSearchService.class).getMeshResponses().set(meshResponses);
        } catch (Exception e) {
            logger.error("Error in load persons from Mesh", e);
        } finally {
            HibernateUtils.close(session, logger);
        }
    }


    public void loadPersons(long idOfOrg, String meshId, String lastName, String firstName, String patronymic) throws
            Exception {
        logger.info("Start load persons from MESH");
        String parameters = String.format("filter=%s&expand=%s&top=%s", URLEncoder
                .encode(getFilter(idOfOrg, meshId, lastName, firstName, patronymic), "UTF-8"), getExpand(), getTop());
        byte[] response = meshRestClient.executeRequest(MESH_REST_PERSONS_URL, parameters);
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        CollectionType collectionType = typeFactory.constructCollectionType(
                List.class, ResponsePersons.class);
        List<ResponsePersons> meshResponses = objectMapper.readValue(response, collectionType);
        logger.info(String.format("Found %s persons in MESH", meshResponses.size()));
        processMeshResponse(meshResponses);
        logger.info("End load persons from MESH");
    }

    private Map<Integer, MeshTrainingForm> getTrainingForms(Session session) {
        Criteria criteria = session.createCriteria(MeshTrainingForm.class);
        criteria.add(Restrictions.eq("archive", false));
        criteria.add(Restrictions.eq("is_deleted", 0));
        List<MeshTrainingForm> list = criteria.list();
        Map<Integer, MeshTrainingForm> map = new HashMap<>();
        for (MeshTrainingForm trainingForm : list) {
            try {
                map.put(trainingForm.getId(), trainingForm);
            } catch (Exception ignore) {
            }
        }
        return map;
    }

    protected boolean isHomeStudy(Education education, Map<Integer, MeshTrainingForm> trainingForms) throws
            Exception {
        if (education.getActualFrom() == null && education.getEducationFormId() == null)
            throw new Exception("Arguments educationForm and educationFormId are NULL");
        if (Education.OUT_OF_ORG_EDUCATIONS.contains(education.getServiceTypeId())) {
            return true;
        }
        Integer id = education.getEducationForm() == null ? education.getEducationFormId() : education.getEducationForm().getId();
        MeshTrainingForm trainingForm = trainingForms.get(id);
        if (trainingForm == null) throw new Exception(String.format("TrainingForm by ID %d does exists", id));
        return trainingForm.getEducation_form().contains(OUT_ORG_GROUP_PREFIX);
    }

    protected void processPerson(Session session, ResponsePersons person, Map<Integer, MeshTrainingForm> trainingForms) throws Exception {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            String personguid = "";
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                personguid = person.getPersonId();
                Date birthdate = df.parse(person.getBirthdate());
                Education education = findEducation(person);
                if (education == null) {
                    Query query = session.createQuery("update MeshSyncPerson m" +
                            " set m.lastupdateRest = :lastupdateRest, m.deletestate = :deletestate" +
                            " where m.personguid = :personguid");
                    query.setParameter("lastupdateRest", new Date());
                    query.setParameter("deletestate", true);
                    query.setParameter("personguid", personguid);
                    query.executeUpdate();
                    return;
                }
                Date endTraining = df.parse(education.getTrainingEndAt());
                boolean deleted = false;
                if (endTraining.before(new Date())) deleted = true;
                String classname = null;
                Integer parallelid = null;
                Integer educationstageid = null;
                if (isHomeStudy(education, trainingForms)) {
                    classname = ClientGroup.Predefined.CLIENT_OUT_ORG.getNameOfGroup();
                } else {
                    classname = education.getClass_().getName();
                    parallelid = (Integer) (education.getClass_().getParallelId());
                    educationstageid = education.getClass_().getEducationStageId();
                }
                String classuid = education.getClassUid();
                String firstname = person.getFirstname();
                Integer genderid = person.getGenderId();
                String lastname = person.getLastname();
                Long organizationid = education.getOrganizationId().longValue();
                String patronymic = person.getPatronymic();
                String guidnsi = null;
                try {
                    Category category = findCategory(person);
                    guidnsi = category.getParameterValues().get(0).toString();
                } catch (Exception e) {
                    logger.info("Not found NSI guid for person with mesh guid " + personguid);
                }

                MeshClass meshClass = null;
                if (education.getClass_() != null) {
                    Class class_ = education.getClass_();
                    meshClass = (MeshClass) session.get(MeshClass.class, class_.getId().longValue());
                    if (meshClass == null) {
                        meshClass = DAOReadonlyService.getInstance().getMeshClassByUID(class_.getUid());
                    }
                    if (meshClass == null) {
                        meshClass = new MeshClass(class_.getId(), classuid);
                    }
                    meshClass.setLastUpdate(new Date());
                    meshClass.setName(classname);
                    meshClass.setParallelId(parallelid);
                    meshClass.setEducationStageId(educationstageid);
                    meshClass.setOrganizationId(organizationid);
                    session.saveOrUpdate(meshClass);
                }

                MeshSyncPerson meshSyncPerson = (MeshSyncPerson) session.get(MeshSyncPerson.class, personguid);
                if (meshSyncPerson == null) {
                    meshSyncPerson = new MeshSyncPerson(personguid);
                }
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
                meshSyncPerson.setInvaliddata(false);
                meshSyncPerson.setMeshClass(meshClass);
                session.saveOrUpdate(meshSyncPerson);
                session.flush();
                session.clear();
            } catch (Exception e) {
                logger.error(String.format("Error in process Mesh person with guid %s: ", personguid), e);
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
        }
    }

    protected String searchByMeshGuid(String guid) {
        try {
            Client client = DAOReadonlyService.getInstance().getClientByGuid(guid);
            return client.getIdOfClient().toString();
        } catch (NullPointerException e) {
            logger.error("idOfClient not found");
            return null;
        }
    }

    protected Education findEducation(ResponsePersons person) {
        try {
            Iterator<Education> iterator = person.getEducation().iterator();
            while (iterator.hasNext()) {
                Education education = iterator.next();
                if (education.getServiceTypeId() == null || !Education.ACCEPTABLE_EDUCATIONS.contains(education.getServiceTypeId())) {
                    iterator.remove();
                }
            }
            Collections.sort(person.getEducation());
            return person.getEducation().get(person.getEducation().size() - 1);
        } catch (Exception e) {
            logger.error("Can not find education from person with guid " + person.getPersonId());
            return null;
        }
    }

    protected Category findCategory(ResponsePersons person) {
        try {
            Collections.sort(person.getCategories());
            for (int i = person.getCategories().size() - 1; i > -1; i--) {
                if (person.getCategories().get(i).getCategoryId() == Category.PROPER_ID)
                    return person.getCategories().get(i);
            }
            return null;
        } catch (Exception e) {
            logger.error("Can not find education from person with guid " + person.getPersonId());
            return null;
        }
    }

    private String getExpand() {
        return MESH_REST_PERSONS_EXPAND;
    }

    private String getTop() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_PERSONS_TOP_PROPEERTY, TOP_DEFAULT);
    }

    private String getFilter(long idOfOrg, String meshIds, String lastName, String firstName, String patronymic) throws
            Exception {
        List<And> list = new ArrayList<>();
        MeshJsonFilter filter = new MeshJsonFilter();

        if (meshIds.equals("null")) {
            Long meshId = DAOReadonlyService.getInstance().getMeshIdByOrg(idOfOrg);
            if (meshId == null)
                throw new Exception("У организации не указан МЭШ ид.");
            And andOrg = new And();
            andOrg.setField(FILTER_VALUE_ORG);
            andOrg.setOp(FILTER_VALUE_EQUALS);
            andOrg.setValue(meshId.toString());
            list.add(andOrg);
        } else {
            if (!StringUtils.isEmpty(meshIds)) {
                And andOrg = new And();
                andOrg.setField("person_id");
                andOrg.setOp(FILTER_VALUE_EQUALS);
                andOrg.setValue(meshIds);
                list.add(andOrg);
            }
        }
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

    private String getServiceAddress() throws Exception {
        String address = RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_ADDRESS_PROPERTY, "");
        if (address.equals("")) throw new Exception("MESH REST address not specified");
        return address;
    }

    private String getApiKey() throws Exception {
        String key = RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_API_KEY_PROPERTY, "");
        if (key.equals("")) throw new Exception("MESH API key not specified");
        return key;
    }

}
