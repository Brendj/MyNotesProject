/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.json.Class;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by nuc on 12.08.2020.
 */
@DependsOn("runtimeContext")
@Primary
@Component("meshPersonsSyncService")
public class MeshPersonsSyncService {
    private static final Logger logger = LoggerFactory.getLogger(MeshPersonsSyncService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String MESH_REST_PERSONS_URL = "/persons?";
    public static final String MESH_REST_PERSONS_EXPAND = "education,categories";
    public static final String MESH_REST_PERSONS_TOP_PROPEERTY = "ecafe.processing.mesh.rest.persons.top";
    public static final String MESH_REST_ADDRESS_PROPERTY = "ecafe.processing.mesh.rest.address";
    public static final String MESH_REST_API_KEY_PROPERTY = "ecafe.processing.mesh.rest.api.key";
    public static final String MESH_REST_CONNECTION_TIMEOUT_PROPERTY = "ecafe.processing.mesh.rest.connection.timeout";

    public static final String TOP_DEFAULT = "50000";
    public static final String CONNECTION_TIMEOUT_DEFAULT = "30000"; //ms

    private static final String FILTER_VALUE_ORG = "education.organization_id";
    private static final String FILTER_VALUE_EQUALS = "equal";
    private static final String FILTER_VALUE_LASTNAME = "lastname";
    private static final String FILTER_VALUE_FIRSTNAME = "firstname";
    private static final String FILTER_VALUE_PATRONYMIC = "patronymic";

    private static final String OUT_ORG_GROUP_PREFIX = "Вне";

    private static final ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("yyyy-MM-dd"); }
    };

    protected MeshRestClient meshRestClient;

    protected MeshPersonsSyncService() {
        String serviceAddress;
        String apiKey;
        Integer connectionTimeout;
        try {
            serviceAddress = getServiceAddress();
            apiKey = getApiKey();
            connectionTimeout = getConnectionTimeout();
            this.meshRestClient = new MeshRestClient(serviceAddress, apiKey, connectionTimeout);
        } catch (Exception e) {
            this.meshRestClient = null;
        }
    }
    protected void processMeshResponse(List<ResponsePersons> meshResponses) {
        Session session = null;
        Transaction transaction = null;
        Map<Integer, MeshTrainingForm> trainingForms = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            trainingForms = getTrainingForms(session);
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in load MeshTrainingForm", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        for (ResponsePersons person : meshResponses) {
            processPerson(null, person, trainingForms);
        }
        RuntimeContext.getAppContext().getBean(MeshPersonsSearchService.class).getMeshResponses().set(meshResponses);
    }

    public void loadPersons(long idOfOrg, String meshId, String lastName, String firstName, String patronymic) throws
            Exception {
        logger.info("Start load persons from MESH");
        String parameters = String.format("filter=%s&expand=%s&top=%s", URLEncoder
                .encode(getFilter(idOfOrg, meshId, lastName, firstName, patronymic), "UTF-8"), getExpand(), getTop());
        byte[] response = meshRestClient.executeRequest(MESH_REST_PERSONS_URL, parameters);
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

    protected boolean isHomeStudy(Education education, Map<Integer, MeshTrainingForm> trainingForms) throws Exception {
        if(education.getSetHomeStudy()){
            return true;
        }
        if (education.getActualFrom() == null && education.getEducationFormId() == null)
            throw new Exception("Arguments educationForm and educationFormId are NULL");
        if (Education.OUT_OF_ORG_EDUCATIONS.contains(education.getServiceTypeId())) {
            return true;
        }
        Integer id = education.getEducationForm() == null ? education.getEducationFormId() : education.getEducationForm().getId();
        MeshTrainingForm trainingForm = trainingForms.get(id);
        if (trainingForm == null){
            return false;
        }
        return trainingForm.getEducation_form().contains(OUT_ORG_GROUP_PREFIX);
    }

    protected void processPerson(Session session, ResponsePersons person, Map<Integer, MeshTrainingForm> trainingForms){
        Transaction transaction = null;
        Date now = new Date();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            session.setFlushMode(FlushMode.COMMIT);
            transaction = session.beginTransaction();
            String personguid = "";
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                personguid = person.getPersonId();
                Date birthdate = df.parse(person.getBirthdate());
                Education education = findEducation(person, trainingForms);
                if (education == null) {
                    Query query = session.createQuery("update MeshSyncPerson m" +
                            " set m.lastupdateRest = :lastupdateRest, m.deletestate = :deletestate, m.invaliddata = :invaliddata" +
                            " where m.personguid = :personguid");
                    query.setParameter("lastupdateRest", now);
                    query.setParameter("deletestate", true);
                    query.setParameter("invaliddata", false);
                    query.setParameter("personguid", personguid);
                    query.executeUpdate();

                    transaction.commit();
                    transaction = null;
                    return;
                }
                Date endTraining = df.parse(education.getTrainingEndAt());
                boolean deleted = endTraining.before(now);
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
            if(education.getClass_() != null){
                Class class_ = education.getClass_();
                meshClass = (MeshClass) session.get(MeshClass.class, class_.getId().longValue());
                if(meshClass == null){
                    meshClass = DAOReadonlyService.getInstance().getMeshClassByUID(class_.getUid());
                }
                if(meshClass == null){
                    meshClass = new MeshClass(class_.getId(), classuid);
                }
                meshClass.setLastUpdate(new Date());
                meshClass.setName(class_.getName());
                meshClass.setParallelId(parallelid);
                meshClass.setEducationStageId(class_.getEducationStageId());
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
                meshSyncPerson.setLastupdateRest(now);
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
        } catch (Exception e) {
            logger.error("Error in load persons from Mesh", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void deleteIrrelevantPersons() throws Exception {
        String top = getTop();

        Transaction transaction = null;
        Session session = null;

        Date now = new Date();
        try{
            List<MeshSyncPerson> forDelete = new LinkedList<>();
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            List<Long> allOrganizationIds = DAOUtils.getAllDistinctOrganizationId(session);

            logger.info("Process \"falsely alive\" persons for " + allOrganizationIds.size() + " Orgs");

            for(Long orgIdFromNsi : allOrganizationIds){
                if (orgIdFromNsi == null) {
                    continue;
                }
                List<MeshSyncPerson> personList = DAOUtils.getActiveMeshPersonsByOrg(session, orgIdFromNsi);
                String parameters = String.format("filter=%stop=%s",URLEncoder
                        .encode(getFilter(orgIdFromNsi), "UTF-8"), top);
                byte[] response = meshRestClient.executeRequest(MESH_REST_PERSONS_URL, parameters);
                TypeFactory typeFactory = objectMapper.getTypeFactory();
                CollectionType collectionType = typeFactory.constructCollectionType(
                        List.class, ResponsePersons.class);
                List<ResponsePersons> meshResponses = objectMapper.readValue(response, collectionType);

                for(MeshSyncPerson person : personList){
                    if(meshResponses.stream().noneMatch(mh -> mh.getPersonId().equals(person.getPersonguid()))){
                        forDelete.add(person);
                    }
                }
            }

            logger.info(forDelete.size() + " will be processed for deletion");

            for(MeshSyncPerson d : forDelete){
                d.setLastupdate(now);
                d.setLastupdateRest(now);
                d.setDeletestate(true);
                session.update(d);
            }

            transaction.commit();
            transaction = null;
            session.close();

            logger.info("Completed processing of \"falsely alive\" persons");
        } catch (Exception e){
            logger.error("Error in deleteIrrelevantPersons: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
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

    protected Education findEducation(ResponsePersons person, Map<Integer, MeshTrainingForm> trainingForms) {
        try {
            Education result = null;
            Date now = CalendarUtils.startOfDay(new Date());
            List<Education> educations = person.getEducation();
            educations.removeIf(education -> {
                try {
                    return format.get().parse(education.getTrainingEndAt()).before(now);
                } catch (ParseException e) {
                    return true;
                }
            });
            if(educations.isEmpty()){
                return null;
            }

            Collections.sort(educations);
            if (educations.size() > 1) {
               for(Education e : educations){
                   if(!Education.ACCEPTABLE_EDUCATIONS.contains(e.getServiceTypeId())){
                       continue;
                   } else if(Education.NOT_PROCESS_SERVICE_TYPES.contains(e.getServiceTypeId())){
                       continue;
                   }

                   if(e.getServiceTypeId().equals(2)){ //"Образование"
                       MeshTrainingForm form = trainingForms.get(e.getEducationFormId());
                       if(form == null){
                           result = e;
                           break;
                       } else if(Education.OUT_OF_ORG_TRAINING_FORM.contains(form.getId())){
                           e.setSetHomeStudy(true);
                           result = e;
                           break;
                       } else {
                           result = e;
                           break;
                       }
                   } else if(Education.OUT_OF_ORG_EDUCATIONS.contains(e.getServiceTypeId())){
                       result = e;
                   }
               }
            } else {
                result = educations.get(0);
            }

            if(result != null) {
                if (Education.NOT_PROCESS_SERVICE_TYPES.contains(result.getServiceTypeId())) {
                    return null;
                } else if (DAOService.getInstance().orgNotExistsByNsiId(result.getOrganizationId())) {
                    return null;
                }
            }

            return result;
        } catch (Exception e) {
            logger.error("Can not find education from person with guid " + person.getPersonId());
            return null;
        }
    }

    protected Category findCategory(ResponsePersons person) {
        try {
            if(CollectionUtils.isEmpty(person.getCategories())){
                return null;
            }
            Collections.sort(person.getCategories());
            for (int i = person.getCategories().size() - 1; i > -1; i--) {
                if (person.getCategories().get(i).getCategoryId() == Category.PROPER_ID)
                    return person.getCategories().get(i);
            }
            return null;
        } catch (Exception e) {
            logger.error("Can not find category from person with guid " + person.getPersonId());
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
        List<OpContainer> list = new ArrayList<>();
        MeshJsonFilter filter = new MeshJsonFilter();

        if (meshIds.equals("null")) {
            Long meshId = DAOReadonlyService.getInstance().getMeshIdByOrg(idOfOrg);
            if (meshId == null)
                throw new Exception("У организации не указан МЭШ ид.");
            OpContainer opContainerOrg = new OpContainer();
            opContainerOrg.setField(FILTER_VALUE_ORG);
            opContainerOrg.setOp(FILTER_VALUE_EQUALS);
            opContainerOrg.setValue(meshId.toString());
            list.add(opContainerOrg);
        } else {
            if (!StringUtils.isEmpty(meshIds)) {
                OpContainer opContainerOrg = new OpContainer();
                opContainerOrg.setField("person_id");
                opContainerOrg.setOp(FILTER_VALUE_EQUALS);
                opContainerOrg.setValue(meshIds);
                list.add(opContainerOrg);
            }
        }
        if (!StringUtils.isEmpty(lastName)) {
            OpContainer opContainerLastname = new OpContainer();
            opContainerLastname.setField(FILTER_VALUE_LASTNAME);
            opContainerLastname.setOp(FILTER_VALUE_EQUALS);
            opContainerLastname.setValue(lastName);
            list.add(opContainerLastname);
        }
        if (!StringUtils.isEmpty(firstName)) {
            OpContainer opContainerFirstname = new OpContainer();
            opContainerFirstname.setField(FILTER_VALUE_FIRSTNAME);
            opContainerFirstname.setOp(FILTER_VALUE_EQUALS);
            opContainerFirstname.setValue(firstName);
            list.add(opContainerFirstname);
        }
        if (!StringUtils.isEmpty(patronymic)) {
            OpContainer opContainerPatronymic = new OpContainer();
            opContainerPatronymic.setField(FILTER_VALUE_PATRONYMIC);
            opContainerPatronymic.setOp(FILTER_VALUE_EQUALS);
            opContainerPatronymic.setValue(patronymic);
            list.add(opContainerPatronymic);
        }
        filter.setAnd(list);
        return objectMapper.writeValueAsString(filter);
    }

    private String getFilter(Long orgIdFromNSI) throws Exception {
        List<OpContainer> list;
        MeshJsonFilter filter = new MeshJsonFilter();

        OpContainer opContainerOrg = new OpContainer();
        opContainerOrg.setField(FILTER_VALUE_ORG);
        opContainerOrg.setOp(FILTER_VALUE_EQUALS);
        opContainerOrg.setValue(orgIdFromNSI.toString());
        list = Collections.singletonList(opContainerOrg);

        filter.setAnd(list);
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

    private Integer getConnectionTimeout() throws Exception {
        String timeout = RuntimeContext.getInstance().getConfigProperties().getProperty(MESH_REST_CONNECTION_TIMEOUT_PROPERTY, CONNECTION_TIMEOUT_DEFAULT);
        return Integer.parseInt(timeout);
    }

}
