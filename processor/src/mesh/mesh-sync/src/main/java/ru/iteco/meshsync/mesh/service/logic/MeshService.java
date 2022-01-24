package ru.iteco.meshsync.mesh.service.logic;

import org.threeten.bp.LocalDate;
import ru.iteco.client.ApiException;
import ru.iteco.client.model.ModelClass;
import ru.iteco.client.model.PersonCategory;
import ru.iteco.client.model.PersonEducation;
import ru.iteco.client.model.PersonInfo;
import ru.iteco.meshsync.enums.ActionType;
import ru.iteco.meshsync.enums.EntityType;
import ru.iteco.meshsync.enums.ServiceType;
import ru.iteco.meshsync.error.EducationNotFoundException;
import ru.iteco.meshsync.error.NoRequiredDataException;
import ru.iteco.meshsync.error.UnknownActionTypeException;
import ru.iteco.meshsync.mesh.service.DAO.CatalogService;
import ru.iteco.meshsync.mesh.service.DAO.ClassService;
import ru.iteco.meshsync.mesh.service.DAO.EntityChangesService;
import ru.iteco.meshsync.mesh.service.DAO.ServiceJournalService;
import ru.iteco.meshsync.models.ClassEntity;
import ru.iteco.meshsync.models.EntityChanges;
import ru.iteco.meshsync.models.Person;
import ru.iteco.meshsync.repo.PersonRepo;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeshService {
    private static final Logger log = LoggerFactory.getLogger(MeshService.class);
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final List<String> INNER_OBJ_FOR_INIT = Arrays.asList(
            EntityType.PERSON_EDUCATION.getApiField(),
            EntityType.CATEGORY.getApiField()
    );
    private static final String EXPAND = StringUtils.join(INNER_OBJ_FOR_INIT, ",");

    private static final List<Integer> notInOrganization = Arrays.asList(
            ServiceType.ATTESTATION.getCode(),
            ServiceType.ACADEMIC_LEAVE.getCode()
    );

    private static final List<Integer> enabledServiceTypeIds = Arrays.asList(
            ServiceType.EDUCATION.getCode(),
            ServiceType.ATTESTATION.getCode(),
            ServiceType.ACADEMIC_LEAVE.getCode()
    );

    private final PersonRepo personRepo;
    private final RestService restService;
    private final EntityChangesService entityChangesService;
    private final CatalogService catalogService;
    private final ServiceJournalService serviceJournalService;
    private final ClassService classService;

    public MeshService(PersonRepo personRepo,
                       RestService restService,
                       EntityChangesService entityChangesService,
                       CatalogService catalogService,
                       ServiceJournalService serviceJournalService,
                       ClassService classService) {
        this.personRepo = personRepo;
        this.restService = restService;
        this.entityChangesService = entityChangesService;
        this.catalogService = catalogService;
        this.serviceJournalService = serviceJournalService;
        this.classService = classService;
    }

    @Transactional
    public boolean processClassChanges(EntityChanges entityChanges) {
        if (entityChanges == null) {
            log.warn("Get entityChanges param as NULL");
            return false;
        }
        ClassEntity classEntity = classService.getByUid(entityChanges.getUid());
        try {
            switch (entityChanges.getAction()) {
                case create:
                case update:
                case merge:
                    ModelClass modelClass = restService.getClassById(UUID.fromString(entityChanges.getUid()));
                    if (modelClass == null) {
                        throw new NoRequiredDataException("MESH-REST return NULL");
                    }
                    classEntity = changeEntityClass(classEntity, modelClass);
                    classService.save(classEntity);
                    break;
                case delete:
                    if (classEntity != null) {
                        classService.remove(classEntity);
                    }
                    break;
                default:
                    throw new UnknownActionTypeException();
            }
        } catch (Exception e) {
            log.error("Cant process ModelClass change", e);
            return false;
        }
        return true;
    }

    @Transactional
    public boolean processEntityChanges(EntityChanges entityChanges) {
        if (entityChanges == null) {
            log.warn("Get entityChanges param as NULL");
            return false;
        }

        boolean inSupportedOrg = true;
        boolean invalidData = false;
        boolean homeStudy = false;
        PersonEducation actualEdu = null;
        Person person = personRepo.findById(entityChanges.getPersonGUID()).orElse(null);

        try {
            if (entityChanges.getAction() == null) {
                throw new UnknownActionTypeException();
            } else if (entityChanges.getEntity().equals(EntityType.PERSON) && entityChanges.getAction().equals(ActionType.delete)) {
                if (person == null) {
                    log.warn("Get action DELETE from Kafka for person GUID: " + entityChanges.getPersonGUID()
                            + ", but in our DB not data about this person");
                    entityChangesService.deleteChangesForPersonGUID(entityChanges.getPersonGUID());
                } else {
                    person.setDeleteState(true);
                    serviceJournalService.writeMessage("Из Apache Kafka получен пакет с меткой \"Удален\"",
                            entityChanges.getPersonGUID());
                }
            } else {
                PersonInfo info = restService.getPersonInfoByGUIDAndExpand(entityChanges.getPersonGUID(), EXPAND);
                if (info == null) {
                    throw new NoRequiredDataException("MESH-REST return NULL");
                }

                if (person == null && CollectionUtils.isEmpty(info.getEducation())) {
                    throw new EducationNotFoundException(String
                            .format("Person %s have no info about Education and not exists in our DB",
                                    entityChanges.getPersonGUID()));
                }

                actualEdu = getLastEducation(info.getEducation());
                if (actualEdu != null) {
                    if (actualEdu.getOrganizationId() == null) {
                        throw new NoRequiredDataException("OrganizationID in Education is NULL");
                    }
                    if (actualEdu.getPropertyClass() == null) {
                        if (actualEdu.getEducationForm() == null && actualEdu.getEducationFormId() == null) {
                            throw new NoRequiredDataException(String.format("Person %s have no info about Class and EducationForm",
                                    entityChanges.getPersonGUID()));
                        }
                    }

                    homeStudy = isHomeStudy(info.getEducation(), actualEdu);
                    inSupportedOrg = personRepo.personFromSupportedOrg(actualEdu.getOrganizationId());

                    if (person == null && !inSupportedOrg) {
                        log.info(String.format(
                                "Person %s in the organization %d, this person no in DB and this OO not support ISPP " +
                                        "or no data about OrganizationID from NSI",
                                entityChanges.getPersonGUID(), actualEdu.getOrganizationId()));
                        return true;
                    }

                    String lastGuid = getLastGuid(info);
                    person = changePerson(person, info, inSupportedOrg, actualEdu, homeStudy, lastGuid);
                } else if (person != null) {
                    log.warn(String.format("Get Person %s without Education, but he exists in DB, mark as delete",
                            entityChanges.getPersonGUID()));
                    person.setDeleteState(true);
                }
                info = null;
            }

            invalidData = false;
            serviceJournalService.decideAllRowsForPerson(entityChanges.getPersonGUID());
        } catch (EducationNotFoundException e) {
            // Возможно сотрудник ОО
            log.warn(String
                    .format("Catch EducationNotFoundException for Person ID: %s ", entityChanges.getPersonGUID()));
            invalidData = false;
        } catch (ApiException e) {
            log.error(String.format("Catch error from MESH-Server when process Person ID: %s :\n Code: %d \n Body: %s",
                    entityChanges.getPersonGUID(), e.getCode(), e.getResponseBody()));
            serviceJournalService.writeErrorWithUserMsg(e, e.getResponseBody(), entityChanges.getPersonGUID());
            if (e.getResponseBody().contains("удален")) { // Нет точного признака удаления
                if (person != null) {
                    person.setDeleteState(true);
                }
            } else {
                invalidData = true;
            }
        } catch (NoRequiredDataException e) {
            log.warn("Catch NoRequiredDataException, person marks as with invalid data Except: " + e.getMessage());
            serviceJournalService.writeError(e, entityChanges.getPersonGUID());
            invalidData = true;
        } catch (Exception e) {
            log.error(String.format("Can't process entityChanges for Person ID: %s",
                    entityChanges.getPersonGUID()), e);
            serviceJournalService.writeError(e, entityChanges.getPersonGUID());
            invalidData = true;
        } finally {
            if (person != null) {
                person.setInvalidData(invalidData);
                personRepo.save(person);
                person = null;
            }
        }
        return !invalidData;
    }

    private boolean isHomeStudy(List<PersonEducation> education, PersonEducation actualEdu) throws Exception {
        LocalDate now = LocalDate.now();
        boolean isHomeStudy = notInOrganization.contains(actualEdu.getServiceTypeId()) || catalogService.educationFormIsHomeStudy(actualEdu);
        List<PersonEducation> allActualEducation = education.stream().filter(
                e -> (enabledServiceTypeIds.contains(e.getServiceTypeId()) || e.getServiceTypeId() == null)
                && actualEdu.getTrainingEndAt().isAfter(now)).collect(Collectors.toList());
        boolean toManyActualEducations = CollectionUtils.isNotEmpty(allActualEducation) && allActualEducation.size() > 1;
        return isHomeStudy || toManyActualEducations;
    }

    private ClassEntity changeEntityClass(ClassEntity classEntity, ModelClass modelClass) {
        if (classEntity == null) {
            classEntity = new ClassEntity();
            classEntity.setId(modelClass.getId());
            classEntity.setUid(modelClass.getUid().toString());
        }
        classEntity.setName(modelClass.getName());
        classEntity.setOrganizationId(modelClass.getOrganizationId());
        classEntity.setParallelId(modelClass.getParallelId());
        classEntity.setEducationStageId(modelClass.getEducationStageId());

        return classEntity;
    }

    private Person changePerson(Person person, PersonInfo info, Boolean inSupportedOrg, PersonEducation actualEdu,
                                boolean homeStudy, String lastGuid) throws Exception {
        if (person == null) {
            person = new Person();
            person.setPersonGUID(info.getPersonId().toString());
        }
        Date endAt = format.parse(actualEdu.getTrainingEndAt().toString());
        boolean isGraduated = endAt.before(new Date());

        person.setBirthDate(format.parse(info.getBirthdate().toString()));
        person.setGenderId(info.getGenderId());
        person.setLastName(info.getLastname());
        person.setFirstName(info.getFirstname());
        person.setPatronymic(info.getPatronymic());

        if (StringUtils.isNoneEmpty(lastGuid)) {
            person.setGuidNSI(lastGuid);
        }

        if (!homeStudy) {
            ClassEntity classEntity = classService.getAndChange(actualEdu.getPropertyClass());
            person.setClassEntity(classEntity);
            person.setClassName(actualEdu.getPropertyClass().getName());
            person.setParallelID(actualEdu.getPropertyClass().getParallelId());
            person.setEducationStageId(actualEdu.getPropertyClass().getEducationStageId());
            person.setClassUID(actualEdu.getClassUid().toString());
        } else {
            person.setClassName("Вне ОУ");
            person.setParallelID(null);
            person.setEducationStageId(null);
            person.setClassUID(null);
            person.setClassEntity(null);
        }

        if (!inSupportedOrg) {
            serviceJournalService.writeMessage(
                    String.format("Клиент %s переведен в OO organizationID %d, данная OO не подключена к ISPP," +
                                    " клиент помечен как \"Удален\"",
                            person.getPersonGUID(), actualEdu.getOrganizationId()), person.getPersonGUID());
        } else {
            person.setOrganizationId(actualEdu.getOrganizationId());
        }

        if (isGraduated) {
            serviceJournalService.writeMessage("Person " + person.getPersonGUID() + " end training "
                    + format.format(endAt) + " ,person marked as deleted", person.getPersonGUID());
        }

        person.setDeleteState(!inSupportedOrg || isGraduated);
        return person;
    }

    private String getLastGuid(PersonInfo info) {
        if (info.getCategories() == null) {
            return null;
        }
        List<PersonCategory> personCategories = new LinkedList<>();
        for (PersonCategory category : info.getCategories()) {
            if (category.getCategoryId().equals(1)) { // GUID NSI
                personCategories.add(category);
            }
        }
        if (CollectionUtils.isEmpty(personCategories)) {
            return null;
        }

        personCategories.sort(Comparator.comparing(PersonCategory::getCreatedAt));
        PersonCategory category = personCategories.get(personCategories.size() - 1);
        return category.getParameterValues().toString().replace("[", "").replace("]", "");
    }

    private PersonEducation getLastEducation(List<PersonEducation> allEdu) {
        if (CollectionUtils.isEmpty(allEdu)) {
            return null;
        }

        allEdu = allEdu
                .stream()
                .filter(e -> (enabledServiceTypeIds.contains(e.getServiceTypeId()) || e.getServiceTypeId() == null))
                .collect(Collectors.toList());
        allEdu.sort(Comparator.comparing(PersonEducation::getTrainingEndAt));

        if (allEdu.size() > 1) {
            LocalDate now = LocalDate.now();

            List<PersonEducation> educationsOnBudget = new LinkedList<>();
            for (PersonEducation e : allEdu) {
                LocalDate trainingEndAt = e.getTrainingEndAt();


                if (trainingEndAt.isAfter(now) && e.getFinancingType().getName().equals("Бюджет")) {
                    educationsOnBudget.add(e);
                }
            }
            if (!educationsOnBudget.isEmpty()) {
                return educationsOnBudget.get(educationsOnBudget.size() - 1);
            }
        }

        return allEdu.get(allEdu.size() - 1);
    }
}
