package ru.iteco.meshsync.mesh.service.logic;

import ru.iteco.client.ApiException;
import ru.iteco.client.model.PersonCategory;
import ru.iteco.client.model.PersonEducation;
import ru.iteco.client.model.PersonInfo;
import ru.iteco.meshsync.EntityType;
import ru.iteco.meshsync.error.EducationNotFoundException;
import ru.iteco.meshsync.error.NoRequiredDataException;
import ru.iteco.meshsync.error.UnknownActionTypeException;
import ru.iteco.meshsync.mesh.service.DAO.CatalogService;
import ru.iteco.meshsync.mesh.service.DAO.EntityChangesService;
import ru.iteco.meshsync.mesh.service.DAO.ServiceJournalService;
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

@Service
public class MeshService {
    private static final Logger log = LoggerFactory.getLogger(MeshService.class);
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final List<String> INNER_OBJ_FOR_INIT = Arrays.asList(
            EntityType.person_education.getApiField(),
            EntityType.category.getApiField()
    );
    private static final String EXPAND = StringUtils.join(INNER_OBJ_FOR_INIT, ",");

    private final PersonRepo personRepo;
    private final RestService restService;
    private final EntityChangesService entityChangesService;
    private final CatalogService catalogService;
    private final ServiceJournalService serviceJournalService;

    public MeshService(PersonRepo personRepo,
                       RestService restService,
                       EntityChangesService entityChangesService,
                       CatalogService catalogService,
                       ServiceJournalService serviceJournalService){
        this.personRepo = personRepo;
        this.restService = restService;
        this.entityChangesService = entityChangesService;
        this.catalogService = catalogService;
        this.serviceJournalService = serviceJournalService;
    }

    @Transactional
    public boolean processEntityChanges(EntityChanges entityChanges){
        if(entityChanges == null){
            log.warn("Get entityChanges param as NULL");
            return false;
        }

        boolean inSupportedOrg = true;
        boolean invalidData = false;
        boolean homeStudy = false;
        PersonEducation actualEdu = null;
        Person person = personRepo.findById(entityChanges.getPersonGUID()).orElse(null);

        try{
            switch (entityChanges.getAction()) {
                case create:
                case update:
                case merge:
                    PersonInfo info = restService.getPersonInfoByGUIDAndExpand(entityChanges.getPersonGUID(), EXPAND);
                    if (info == null) {
                        throw new NoRequiredDataException("MESH-REST return NULL");
                    }

                    if (person == null && CollectionUtils.isEmpty(info.getEducation())) {
                        throw new EducationNotFoundException(String
                                .format("Person %s have no info about Education and not exists in our DB", entityChanges.getPersonGUID()));
                    }

                    actualEdu = getLastEducation(info.getEducation());
                    if(actualEdu != null) {
                        if (actualEdu.getOrganizationId() == null) {
                            throw new NoRequiredDataException("OrganizationID in Education is NULL");
                        }
                        if (actualEdu.getPropertyClass() == null) {
                            if (actualEdu.getEducationForm() == null && actualEdu.getEducationFormId() == null) {
                                throw new NoRequiredDataException(String.format("Person %s have no info about Class and EducationForm",
                                        entityChanges.getPersonGUID()));
                            } else {
                                homeStudy = catalogService.isHomeStudy(actualEdu.getEducationForm(), actualEdu.getEducationFormId());
                            }
                        }
                        inSupportedOrg = personRepo.personFromSupportedOrg(actualEdu.getOrganizationId());

                        if (person == null && !inSupportedOrg) {
                            log.info(String.format(
                                    "Person %s in the organization %d, this person no in DB and this OO not support ISPP or no data about OrganizationID from NSI",
                                    entityChanges.getPersonGUID(), actualEdu.getOrganizationId()));
                            return true;
                        }

                        String lastGuid = getLastGuid(info);
                        person = changePerson(person, info, inSupportedOrg, actualEdu, homeStudy, lastGuid);
                    } else if(person != null){
                        log.warn(String.format("Get Person %s without Education, but he exists in DB, mark as delete",
                                entityChanges.getPersonGUID()));
                        person.setDeleteState(true);
                    }

                    info = null;
                    break;
                case delete:
                    if (person == null) {
                        log.warn("Get action DELETE from Kafka for person GUID: " + entityChanges.getPersonGUID()
                                + ", but in our DB not data about this person");
                        entityChangesService.deleteChangesForPersonGUID(entityChanges.getPersonGUID());
                    } else {
                        person.setDeleteState(true);
                        serviceJournalService.writeMessage("Из Apache Kafka получен пакет с меткой \"Удален\"",
                                entityChanges.getPersonGUID());
                    }
                    break;
                default:
                    throw new UnknownActionTypeException();
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
            if(e.getResponseBody().contains("удален")) { // Нет точного признака удаления
                if (person != null){
                    person.setDeleteState(true);
                }
            } else {
                invalidData = true;
            }
        } catch (NoRequiredDataException e){
            log.warn("Catch NoRequiredDataException, person marks as with invalid data Except: " + e.getMessage());
            serviceJournalService.writeError(e, entityChanges.getPersonGUID());
            invalidData = true;
        } catch (Exception e){
            log.error(String.format("Can't process entityChanges for Person ID: %s",
                    entityChanges.getPersonGUID()), e);
            serviceJournalService.writeError(e, entityChanges.getPersonGUID());
            invalidData = true;
        } finally {
            if(person != null){
                person.setInvalidData(invalidData);
                personRepo.save(person);
                person = null;
            }
        }
        return !invalidData;
    }

    private Person changePerson(Person person, PersonInfo info, Boolean inSupportedOrg, PersonEducation actualEdu,
                                boolean homeStudy, String lastGuid) throws Exception {
        if(person == null) {
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

        if(StringUtils.isNoneEmpty(lastGuid)){
            person.setGuidNSI(lastGuid);
        }

        if(!homeStudy) {
            person.setClassName(actualEdu.getPropertyClass().getName());
            person.setParallelID(actualEdu.getPropertyClass().getParallelId());
            person.setEducationStageId(actualEdu.getPropertyClass().getEducationStageId());
            person.setClassUID(actualEdu.getClassUid().toString());
        } else {
            person.setClassName("Вне ОУ");
            person.setParallelID(null);
            person.setEducationStageId(null);
            person.setClassUID(null);
        }

        if(!inSupportedOrg) {
            serviceJournalService.writeMessage(
                    String.format("Клиент %s переведен в OO organizationID %d, данная OO не подключена к ISPP, клиент помечен как \"Удален\"",
                    person.getPersonGUID(), actualEdu.getOrganizationId()), person.getPersonGUID());
        } else {
            person.setOrganizationId(actualEdu.getOrganizationId());
        }

        if(isGraduated){
            serviceJournalService.writeMessage("Person " + person.getPersonGUID() + " end training "
                            + format.format(endAt) + " ,person marked as deleted", person.getPersonGUID());
        }

        person.setDeleteState(!inSupportedOrg || isGraduated);
        return person;
    }

    private String getLastGuid(PersonInfo info) {
        if(info.getCategories() == null){
            return null;
        }
        List<PersonCategory> personCategories = new LinkedList<>();
        for(PersonCategory category : info.getCategories()){
            if(category.getCategoryId().equals(1)){ // GUID NSI
                personCategories.add(category);
            }
        }
        if(CollectionUtils.isEmpty(personCategories)){
            return null;
        }

        personCategories.sort(Comparator.comparing(PersonCategory::getCreatedAt));
        PersonCategory category = personCategories.get(personCategories.size() - 1);
        return category.getParameterValues().toString().replace("[","").replace("]", "");
    }

    private PersonEducation getLastEducation(List<PersonEducation> allEdu){
        if(CollectionUtils.isEmpty(allEdu)){
            return null;
        }
        allEdu.sort(Comparator.comparing(PersonEducation::getTrainingEndAt));
        return allEdu.get(allEdu.size() - 1);
    }
}
