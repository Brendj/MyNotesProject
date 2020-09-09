package ru.iteco.nsisync.nsi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.iteco.nsisync.error.UnknownActionTypeException;
import ru.iteco.nsisync.nsi.catalogs.models.*;
import ru.iteco.nsisync.nsi.catalogs.repo.*;
import ru.iteco.nsisync.nsi.dto.ChangesDTO;
import ru.iteco.nsisync.nsi.utils.CatalogDTOUtil;

import javax.transaction.Transactional;


@Service
public class CatalogService {
    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);

    private final AdminDistrictRepo adminDistrictRepo;
    private final ContactTypeRepo contactTypeRepo;
    private final GenderRepo genderRepo;
    private final LegalRepresentRepo legalRepresentRepo;
    private final ParallelRepo parallelRepo;
    private final CityAreasRepo cityAreasRepo;
    private final EducationLevelRepo educationLevelRepo;
    private final TrainingFormRepo trainingFormRepo;

    public CatalogService(AdminDistrictRepo adminDistrictRepo,
                          ContactTypeRepo contactTypeRepo,
                          GenderRepo genderRepo,
                          LegalRepresentRepo legalRepresentRepo,
                          ParallelRepo parallelRepo,
                          CityAreasRepo cityAreasRepo,
                          EducationLevelRepo educationLevelRepo,
                          TrainingFormRepo trainingFormRepo) {
        this.adminDistrictRepo = adminDistrictRepo;
        this.contactTypeRepo = contactTypeRepo;
        this.genderRepo = genderRepo;
        this.legalRepresentRepo = legalRepresentRepo;
        this.parallelRepo = parallelRepo;
        this.cityAreasRepo = cityAreasRepo;
        this.educationLevelRepo = educationLevelRepo;
        this.trainingFormRepo = trainingFormRepo;
    }

    @Transactional
    public void processLegalRepresent(ChangesDTO catalogChangeData) {
        LegalRepresent legalRepresent;
        switch (catalogChangeData.getAction()){
            case ADDED:
            case MODIFIED:
                legalRepresent = (LegalRepresent) getCatalogByJsonDTO(catalogChangeData, legalRepresentRepo);
                if(legalRepresent == null){
                    log.warn("LegalRepresent entity no exist, try create by JSON");
                    legalRepresent = buildAndCreateLegalRepresent(catalogChangeData);
                } else {
                    legalRepresent.setSystemObjectId(CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), LegalRepresent.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID));
                    legalRepresent.setTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), LegalRepresent.AbstractCatalogEnumJsonFields.TITLE));
                    legalRepresent.setId(CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), LegalRepresent.LegalRepresentJsonFields.ID));
                }
                break;
            case DELETED:
                legalRepresent = (LegalRepresent) getCatalogByJsonDTO(catalogChangeData, legalRepresentRepo);
                if(legalRepresent == null){
                    log.warn("LegalRepresent entity no exist, nothing delete");
                    return;
                }
                legalRepresent.setIsDelete(AbstractCatalog.DELETE);
                break;
            default:
                throw new UnknownActionTypeException();
        }
        legalRepresentRepo.save(legalRepresent);
    }

    @Transactional
    public void processContractType(ChangesDTO catalogChangeData) {
        ContactType contractType;
        switch (catalogChangeData.getAction()) {
            case ADDED:
            case MODIFIED:
                contractType = (ContactType) getCatalogByJsonDTO(catalogChangeData, contactTypeRepo);
                if (contractType == null) {
                    log.warn("ContractType entity no exist, try create by JSON");
                    contractType = buildAndCreateContactType(catalogChangeData);
                } else {
                    contractType.setSystemObjectId(CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), ContactType.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID));
                    contractType.setTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), ContactType.AbstractCatalogEnumJsonFields.TITLE));
                    contractType.setId(CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), ContactType.ContactTypeEnumJsonFields.ID));
                }
                break;
            case DELETED:
                contractType = (ContactType) getCatalogByJsonDTO(catalogChangeData, contactTypeRepo);
                if(contractType == null){
                    log.warn("ContractType entity no exist, nothing delete");
                    return;
                }
                contractType.setIsDelete(AbstractCatalog.DELETE);
                break;
            default:
                throw new UnknownActionTypeException();
        }
        contactTypeRepo.save(contractType);
    }

    @Transactional
    public void processGender(ChangesDTO catalogChangeData) {
        Gender gender;
        switch (catalogChangeData.getAction()){
            case ADDED:
            case MODIFIED:
                gender = (Gender) getCatalogByJsonDTO(catalogChangeData, genderRepo);
                if(gender == null){
                    log.warn("Gender entity no exist, try create by JSON");
                    gender = buildAndCreateGender(catalogChangeData);
                } else {
                    gender.setSystemObjectId(CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), Gender.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID));
                    gender.setTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), Gender.AbstractCatalogEnumJsonFields.TITLE));
                    gender.setId(CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), Gender.GenderJsonFields.ID));
                    gender.setCode(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), Gender.GenderJsonFields.CODE));
                }
                break;
            case DELETED:
                gender = (Gender) getCatalogByJsonDTO(catalogChangeData, genderRepo);
                if(gender == null){
                    log.warn("Gender entity no exist, nothing delete");
                    return;
                }
                gender.setIsDelete(AbstractCatalog.DELETE);
                break;
            default:
                throw new UnknownActionTypeException();
        }
        genderRepo.save(gender);
    }

    @Transactional
    public void processParallels(ChangesDTO catalogChangeData) {
        Parallel parallel;
        switch (catalogChangeData.getAction()){
            case ADDED:
            case MODIFIED:
                parallel = (Parallel) getCatalogByJsonDTO(catalogChangeData, parallelRepo);
                if(parallel == null){
                    log.warn("Parallel entity no exist, try create by JSON");
                    parallel = buildAndCreateParallel(catalogChangeData);
                } else {
                    parallel.setSystemObjectId(CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), Parallel.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID));
                    parallel.setTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), Parallel.AbstractCatalogEnumJsonFields.TITLE));
                    parallel.setId(CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), Parallel.ParallelJsonFields.ID));
                }
                break;
            case DELETED:
                parallel = (Parallel) getCatalogByJsonDTO(catalogChangeData, parallelRepo);
                if(parallel == null){
                    log.warn("Parallel entity no exist, nothing delete");
                    return;
                }
                parallel.setIsDelete(AbstractCatalog.DELETE);
                break;
            default:
                throw new UnknownActionTypeException();
        }
        parallelRepo.save(parallel);
    }

    @Transactional
    public void processAdminDistrict(ChangesDTO catalogChangeData) throws Exception {
        AdminDistrict adminDistrict;
        switch (catalogChangeData.getAction()){
            case ADDED:
            case MODIFIED:
                adminDistrict = (AdminDistrict) getCatalogByJsonDTO(catalogChangeData, adminDistrictRepo);
                if(adminDistrict == null){
                    log.warn("AdminDistrict entity no exist, try create by JSON");
                    adminDistrict = buildAndCreateAdminDistrict(catalogChangeData);
                }
                adminDistrict.setSystemObjectId(CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), AdminDistrict.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID));
                adminDistrict.setTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), AdminDistrict.AbstractCatalogEnumJsonFields.TITLE));
                break;
            case DELETED:
                adminDistrict = (AdminDistrict) getCatalogByJsonDTO(catalogChangeData, adminDistrictRepo);
                if(adminDistrict == null){
                    log.warn("AdminDistrict entity no exist, nothing delete");
                    return;
                }
                adminDistrict.setIsDelete(AbstractCatalog.DELETE);
                break;
            default:
                throw new UnknownActionTypeException();
        }
        adminDistrictRepo.save(adminDistrict);
    }

    @Transactional
    public void processCityAreas(ChangesDTO catalogChangeData) throws Exception {
        CityAreas cityAreas;
        switch (catalogChangeData.getAction()){
            case ADDED:
            case MODIFIED:
                cityAreas = (CityAreas) getCatalogByJsonDTO(catalogChangeData, cityAreasRepo);
                if(cityAreas == null){
                    log.warn("CityAreas entity no exist, try create by JSON");
                    cityAreas = buildAndCreateCityAreas(catalogChangeData);
                }
                cityAreas.setSystemObjectId(CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), CityAreas.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID));
                cityAreas.setTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), CityAreas.AbstractCatalogEnumJsonFields.TITLE));
                cityAreas.setId(CatalogDTOUtil.getAttributeDictionaryIdByName(catalogChangeData.getAttribute(), CityAreas.CityAreasJsonFields.ID));
                cityAreas.setParentId(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), CityAreas.CityAreasJsonFields.PARENT_ID));
                cityAreas.setBtiId(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), CityAreas.CityAreasJsonFields.BTI_ID));
                cityAreas.setBtiTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), CityAreas.CityAreasJsonFields.BTI_TITLE));
                break;
            case DELETED:
                cityAreas = (CityAreas) getCatalogByJsonDTO(catalogChangeData, cityAreasRepo);
                if(cityAreas == null){
                    log.warn("CityAreas entity no exist, nothing delete");
                    return;
                }
                cityAreas.setIsDelete(AbstractCatalog.DELETE);
                break;
            default:
                throw new UnknownActionTypeException();
        }
        cityAreasRepo.save(cityAreas);
    }

    @Transactional
    public void processEducationLevel(ChangesDTO catalogChangeData) throws Exception {
        EducationLevel educationLevel;
        switch (catalogChangeData.getAction()){
            case ADDED:
            case MODIFIED:
                educationLevel = (EducationLevel) getCatalogByJsonDTO(catalogChangeData, educationLevelRepo);
                if(educationLevel == null){
                    log.warn("EducationLevel entity no exist, try create by JSON");
                    educationLevel = buildAndCreateEducationLevel(catalogChangeData);
                }
                educationLevel.setSystemObjectId(CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), EducationLevel.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID));
                educationLevel.setTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), EducationLevel.AbstractCatalogEnumJsonFields.TITLE));
                educationLevel.setId(CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), EducationLevel.EducationLevelJsonFields.ID));
                educationLevel.setShortName(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), EducationLevel.EducationLevelJsonFields.SHORT_NAME));
                break;
            case DELETED:
                educationLevel = (EducationLevel) getCatalogByJsonDTO(catalogChangeData, educationLevelRepo);
                if(educationLevel == null){
                    log.warn("EducationLevel entity no exist, nothing delete");
                    return;
                }
                educationLevel.setIsDelete(AbstractCatalog.DELETE);
                break;
            default:
                throw new UnknownActionTypeException();
        }
        educationLevelRepo.save(educationLevel);
    }

    @Transactional
    public void processTrainingForm(ChangesDTO catalogChangeData) throws Exception {
        TrainingForm trainingForm;
        switch (catalogChangeData.getAction()){
            case ADDED:
            case MODIFIED:
                trainingForm = (TrainingForm) getCatalogByJsonDTO(catalogChangeData, trainingFormRepo);
                if(trainingForm == null){
                    log.warn("TrainingForm entity no exist, try create by JSON");
                    trainingForm = buildAndCreateTrainingForm(catalogChangeData);
                }
                trainingForm.setSystemObjectId(CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), TrainingForm.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID));
                trainingForm.setTitle(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), TrainingForm.AbstractCatalogEnumJsonFields.TITLE));
                trainingForm.setId(CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), TrainingForm.TrainingFormJsonFields.ID));
                trainingForm.setArchive(CatalogDTOUtil.getAttributeAsBoolByName(catalogChangeData.getAttribute(), TrainingForm.TrainingFormJsonFields.ARCHIVE));
                trainingForm.setCode(CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), TrainingForm.TrainingFormJsonFields.CODE));
                trainingForm.setEducationForm(CatalogDTOUtil.getAttributeDictionaryValueByName(catalogChangeData.getAttribute(), TrainingForm.TrainingFormJsonFields.EDUCATION_FORM));
                break;
            case DELETED:
                trainingForm = (TrainingForm) getCatalogByJsonDTO(catalogChangeData, trainingFormRepo);
                if(trainingForm == null){
                    log.warn("TrainingForm entity no exist, nothing delete");
                    return;
                }
                trainingForm.setIsDelete(AbstractCatalog.DELETE);
                break;
            default:
                throw new UnknownActionTypeException();
        }
        trainingFormRepo.save(trainingForm);
    }

    private LegalRepresent buildAndCreateLegalRepresent(ChangesDTO catalogChangeData) {
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), LegalRepresent.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        Long systemObjectId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), LegalRepresent.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID);
        String title = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), LegalRepresent.AbstractCatalogEnumJsonFields.TITLE);
        Integer id = CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), LegalRepresent.LegalRepresentJsonFields.ID);
        return new LegalRepresent(globalId, systemObjectId, title, LegalRepresent.ACTIVE, id);
    }


    private ContactType buildAndCreateContactType(ChangesDTO catalogChangeData) {
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), ContactType.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        Long systemObjectId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), ContactType.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID);
        String title = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), ContactType.AbstractCatalogEnumJsonFields.TITLE);
        Integer id = CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), ContactType.ContactTypeEnumJsonFields.ID);
        return new ContactType(globalId, systemObjectId, title, ContactType.ACTIVE, id);
    }

    private Gender buildAndCreateGender(ChangesDTO catalogChangeData) {
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), Gender.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        Long systemObjectId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), Gender.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID);
        String title = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), Gender.AbstractCatalogEnumJsonFields.TITLE);
        Integer id = CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), Gender.GenderJsonFields.ID);
        String code = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), Gender.GenderJsonFields.CODE);
        return new Gender(globalId, systemObjectId, title, Gender.ACTIVE, id, code);
    }

    private Parallel buildAndCreateParallel(ChangesDTO catalogChangeData) {
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), Parallel.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        Long systemObjectId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), Parallel.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID);
        String title = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), Parallel.AbstractCatalogEnumJsonFields.TITLE);
        Integer id = CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), Parallel.ParallelJsonFields.ID);
        return new Parallel(globalId, systemObjectId, title, Parallel.ACTIVE, id);
    }

    private AdminDistrict buildAndCreateAdminDistrict(ChangesDTO catalogChangeData) {
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), AdminDistrict.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        Long systemObjectId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), AdminDistrict.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID);
        String title = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), AdminDistrict.AbstractCatalogEnumJsonFields.TITLE);
        return new AdminDistrict(globalId, systemObjectId, title, AdminDistrict.ACTIVE);
    }

    private CityAreas buildAndCreateCityAreas(ChangesDTO catalogChangeData) {
        Long globalID = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), CityAreas.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        Long systemObjectId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), CityAreas.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID);
        String title = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), CityAreas.AbstractCatalogEnumJsonFields.TITLE);
        Integer id = CatalogDTOUtil.getAttributeDictionaryIdByName(catalogChangeData.getAttribute(), CityAreas.CityAreasJsonFields.ID);
        String parentId = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), CityAreas.CityAreasJsonFields.PARENT_ID);
        String btiId = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), CityAreas.CityAreasJsonFields.BTI_ID);
        String btiTitle = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), CityAreas.CityAreasJsonFields.BTI_TITLE);
        return new CityAreas(globalID, systemObjectId, title, CityAreas.ACTIVE, id, parentId, btiId, btiTitle);
    }

    private EducationLevel buildAndCreateEducationLevel(ChangesDTO catalogChangeData){
        Long globalID = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(),EducationLevel.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        Long systemObjectId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(),EducationLevel.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID);
        String title = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(),EducationLevel.AbstractCatalogEnumJsonFields.TITLE);
        Integer id = CatalogDTOUtil.getAttributeDictionaryIdByName(catalogChangeData.getAttribute(),EducationLevel.EducationLevelJsonFields.ID);
        String shortName = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), EducationLevel.EducationLevelJsonFields.SHORT_NAME);
        return new EducationLevel(globalID, systemObjectId, title,EducationLevel.ACTIVE, id, shortName);
    }

    private TrainingForm buildAndCreateTrainingForm(ChangesDTO catalogChangeData) {
        Long globalID = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(),TrainingForm.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        Long systemObjectId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), TrainingForm.AbstractCatalogEnumJsonFields.SYSTEM_OBJECT_ID);
        String title = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), TrainingForm.AbstractCatalogEnumJsonFields.TITLE);
        Integer id = CatalogDTOUtil.getAttributeAsIntegerByName(catalogChangeData.getAttribute(), TrainingForm.TrainingFormJsonFields.ID);
        Boolean archive = CatalogDTOUtil.getAttributeAsBoolByName(catalogChangeData.getAttribute(), TrainingForm.TrainingFormJsonFields.ARCHIVE);
        String code = CatalogDTOUtil.getAttributeByName(catalogChangeData.getAttribute(), TrainingForm.TrainingFormJsonFields.CODE);
        String educationForm = CatalogDTOUtil.getAttributeDictionaryValueByName(catalogChangeData.getAttribute(), TrainingForm.TrainingFormJsonFields.EDUCATION_FORM);
        return new TrainingForm(globalID, systemObjectId, title, id, archive, code, educationForm, AbstractCatalog.ACTIVE);
    }

    private AbstractCatalog getCatalogByJsonDTO(ChangesDTO catalogChangeData, AbstractCatalogRepository repository) {
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), AbstractCatalog.AbstractCatalogEnumJsonFields.GLOBAL_ID);
        AbstractCatalog ct = (AbstractCatalog) repository
                .findById(globalId)
                .orElse(null);
        if(ct == null){
            log.debug(String.format("Can't find Catalog entity by Global_id: %d",  globalId));
        }
        return ct;
    }
}
