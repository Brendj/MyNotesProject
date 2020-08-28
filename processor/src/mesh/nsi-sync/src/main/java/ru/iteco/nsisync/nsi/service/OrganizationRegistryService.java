package ru.iteco.nsisync.nsi.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.iteco.nsisync.error.NotServedOrganizationException;
import ru.iteco.nsisync.error.UnknownActionTypeException;
import ru.iteco.nsisync.nsi.dto.AttributeChangesDTO;
import ru.iteco.nsisync.nsi.dto.ChangesDTO;
import ru.iteco.nsisync.nsi.dto.GroupValueDTO;
import ru.iteco.nsisync.nsi.dto.SimpleAttributeDTO;
import ru.iteco.nsisync.nsi.regisry.models.EOAddress;
import ru.iteco.nsisync.nsi.regisry.models.EOAddress.*;
import ru.iteco.nsisync.nsi.regisry.models.OrganizationRegistry;
import ru.iteco.nsisync.nsi.regisry.models.OrganizationRegistry.*;
import ru.iteco.nsisync.nsi.regisry.repo.EOAddressRepo;
import ru.iteco.nsisync.nsi.regisry.repo.OrganizationRegistryRepo;
import ru.iteco.nsisync.nsi.utils.CatalogDTOUtil;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OrganizationRegistryService {
    private static final Logger log = LoggerFactory.getLogger(OrganizationRegistryService.class);

    private final EOAddressRepo eoAddressRepo;
    private final OrganizationRegistryRepo organizationRegistryRepo;

    public OrganizationRegistryService(EOAddressRepo eoAddressRepo,
                                       OrganizationRegistryRepo organizationRegistryRepo){
        this.eoAddressRepo = eoAddressRepo;
        this.organizationRegistryRepo = organizationRegistryRepo;
    }

    @Transactional
    public void processOrganizationRegistry(ChangesDTO catalogChangeData) throws Exception {
        OrganizationRegistry organizationRegistry = getOrganizationRegistryByJsonDTO(catalogChangeData);
        if(organizationRegistry == null && notServedByISPP(catalogChangeData)){
            Long eo_id = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(), OrganizationRegistryEnumJsonFields.EO_ID);
            log.warn(String.format("OO ID:%d not served by IS PP", eo_id));
            return;
        }

        switch (catalogChangeData.getAction()){
            case ADDED:
            case MODIFIED:
                if(organizationRegistry == null){
                    log.debug("Try create new OrganizationRegistry entity by JSON");
                    buildOrganizationRegistry(catalogChangeData);
                } else {
                    modifyOrganizationRegistry(organizationRegistry, catalogChangeData);
                }
                break;
            case DELETED:
                log.warn("For ORGANIZATION_REGISTRY not support operation DELETED");
                break;
            default:
                throw new UnknownActionTypeException();
        }
    }

    private void modifyOrganizationRegistry(OrganizationRegistry organizationRegistry, ChangesDTO catalogChangeData) {
        List<AttributeChangesDTO> attributes = catalogChangeData.getAttribute();
        GroupValueDTO dataAboutEOAddress = CatalogDTOUtil.getGroupValueByName(catalogChangeData.getAttribute(),
                OrganizationRegistryEnumJsonFields.EO_ADDRESS_ID);

        processEoAddress(dataAboutEOAddress, organizationRegistry);
        if(eoAddressRepo.countByOrganizationRegistry(organizationRegistry) == 0){
            log.info(
                    String.format("All building of OO EKIS_ID %d no integration queue, record remove from DB",
                            organizationRegistry.getEoId())
            );
            organizationRegistryRepo.delete(organizationRegistry);
        } else {
            organizationRegistry.setSystemObjectId(CatalogDTOUtil.getAttributeByName(attributes,
                    OrganizationRegistry.AbstractRegistryJsonFieldsEnum.SYSTEM_OBJECT_ID));
            organizationRegistry.setEoId(CatalogDTOUtil.getAttributeAsLongByName(attributes, OrganizationRegistryEnumJsonFields.EO_ID));
            organizationRegistry.setFullName(CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.FULL_NAME));
            organizationRegistry.setShortName(CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.SHORT_NAME));
            organizationRegistry.setType2Id(CatalogDTOUtil.getAttributeDictionaryIdByName(attributes, OrganizationRegistryEnumJsonFields.TYPE_2));
            organizationRegistry.setInn(CatalogDTOUtil.getAttributeAsLongByName(attributes, OrganizationRegistryEnumJsonFields.INN));
            organizationRegistry.setOgrn(CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.OGRN));
            organizationRegistry.setSubordinationValue(CatalogDTOUtil.getAttributeDictionaryValueByName(attributes, OrganizationRegistryEnumJsonFields.SUBORDINATION_ID));
            organizationRegistry.setDirector(CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.DIRECTOR));
            organizationRegistry.setArhiv(CatalogDTOUtil.getAttributeAsBoolByName(attributes, OrganizationRegistryEnumJsonFields.ARHIV));
            organizationRegistry.setEgissoId(CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.EGISSO_ID));
            organizationRegistry.setXaIsActive(CatalogDTOUtil.getAttributeAsIntegerByName(attributes, OrganizationRegistryEnumJsonFields.XA_IS_ACTIVE));
            organizationRegistry.setFounder(CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.FOUNDER));

            organizationRegistryRepo.save(organizationRegistry);
        }
    }

    private void modifyEOAddress(EOAddress address, OrganizationRegistry organizationRegistry, List<AttributeChangesDTO> attribute){
        address.setOrganizationRegistry(organizationRegistry);
        address.setSystemObjectId(CatalogDTOUtil.getAttributeByName(attribute,
                EOAddress.AbstractRegistryJsonFieldsEnum.SYSTEM_OBJECT_ID));
        address.setEoId(CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddressEnumJsonFields.EO_ID));
        address.setUniqueAddressId(CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddressEnumJsonFields.UNIQUE_ADDRESS_ID));
        address.setUnom(CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddressEnumJsonFields.UNOM));
        address.setUnad(CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddressEnumJsonFields.UNAD));
        address.setAddress(CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.ADDRESS));
        address.setAddressAsur(CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.ADDRESS_ASUR));
        address.setArea(CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.AREA));
        address.setDistrict(CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.DISTRICT));
        address.setIsBti(CatalogDTOUtil.getAttributeAsBoolByName(attribute, EOAddressEnumJsonFields.IS_BTI));
        address.setAddressDescription(CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.ADDRESS_DESCRIPTION));

        address.setQueueFromBTI(getFirstFindingQueue(attribute, EOAddressEnumJsonFields.DATA_IN_BTI, EOAddressEnumJsonFields.QUEUE_VALUE_IN_BTI));
        address.setQueueNotFromBTI(getFirstFindingQueue(attribute, EOAddressEnumJsonFields.DATA_NOT_IN_BTI, EOAddressEnumJsonFields.QUEUE_VALUE_NOT_IN_BTI));
    }

    private OrganizationRegistry buildOrganizationRegistry(ChangesDTO catalogChangeData) {
        List<AttributeChangesDTO> attributes = catalogChangeData.getAttribute();

        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(attributes, OrganizationRegistry.AbstractRegistryJsonFieldsEnum.GLOBAL_ID);
        String systemObjectId = CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistry.AbstractRegistryJsonFieldsEnum.SYSTEM_OBJECT_ID);
        Long eoId = CatalogDTOUtil.getAttributeAsLongByName(attributes, OrganizationRegistryEnumJsonFields.EO_ID);
        String fullName = CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.FULL_NAME);
        String shortName = CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.SHORT_NAME);
        Integer type2Id = CatalogDTOUtil.getAttributeDictionaryIdByName(attributes, OrganizationRegistryEnumJsonFields.TYPE_2);
        Long inn = CatalogDTOUtil.getAttributeAsLongByName(attributes, OrganizationRegistryEnumJsonFields.INN);
        String ogrn = CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.OGRN);
        String subordinationValue = CatalogDTOUtil.getAttributeDictionaryValueByName(attributes, OrganizationRegistryEnumJsonFields.SUBORDINATION_ID);
        String director = CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.DIRECTOR);
        Boolean arhiv = CatalogDTOUtil.getAttributeAsBoolByName(attributes, OrganizationRegistryEnumJsonFields.ARHIV);
        String egissoId = CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.EGISSO_ID);
        Integer xaIsActive = CatalogDTOUtil.getAttributeAsIntegerByName(attributes, OrganizationRegistryEnumJsonFields.XA_IS_ACTIVE);
        String founder = CatalogDTOUtil.getAttributeByName(attributes, OrganizationRegistryEnumJsonFields.FOUNDER);

        OrganizationRegistry orgRegistry = new OrganizationRegistry(globalId, systemObjectId, eoId, fullName, shortName,
                type2Id, inn, ogrn, subordinationValue, director, arhiv, egissoId, xaIsActive, founder);
        orgRegistry = organizationRegistryRepo.save(orgRegistry);

        GroupValueDTO dataAboutEOAddress = CatalogDTOUtil.getGroupValueByName(catalogChangeData.getAttribute(), OrganizationRegistryEnumJsonFields.EO_ADDRESS_ID);
        processEoAddress(dataAboutEOAddress, orgRegistry);

        return orgRegistry;
    }

    private void processEoAddress(GroupValueDTO dataAboutEOAddress, OrganizationRegistry organizationRegistry) {
        if(CollectionUtils.isEmpty(dataAboutEOAddress.getItem())) {
            return;
        }

        for (SimpleAttributeDTO simpleDTO : dataAboutEOAddress.getItem()) {
            EOAddress eoAddress = getEOAddressBySimpleDTO(simpleDTO);
            if (notHaveDataAboutQueue(simpleDTO)) {
                if (eoAddress != null) {
                    log.info(String.format("Building by address %s of OO EKIS_ID %d no integration queue, record remove from DB",
                            eoAddress.getAddressAsur(), organizationRegistry.getEoId())
                    );
                    eoAddressRepo.delete(eoAddress);
                }
                continue;
            } else if (eoAddress == null) {
                log.info("Create new EOAddress from JSON");
                eoAddress = buildEOAddress(simpleDTO.getAttribute(), organizationRegistry);
            } else {
                modifyEOAddress(eoAddress, organizationRegistry, simpleDTO.getAttribute());
            }
            eoAddressRepo.save(eoAddress);
        }
    }

    private EOAddress buildEOAddress(List<AttributeChangesDTO> attribute, OrganizationRegistry organizationRegistry) {
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddress.AbstractRegistryJsonFieldsEnum.GLOBAL_ID);
        String systemObjectId = CatalogDTOUtil.getAttributeByName(attribute, EOAddress.AbstractRegistryJsonFieldsEnum.SYSTEM_OBJECT_ID);
        Long eoId = CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddressEnumJsonFields.EO_ID);
        Long uniqueAddressId = CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddressEnumJsonFields.UNIQUE_ADDRESS_ID);
        Long unom = CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddressEnumJsonFields.UNOM);
        Long unad = CatalogDTOUtil.getAttributeAsLongByName(attribute, EOAddressEnumJsonFields.UNAD);
        String address = CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.ADDRESS);
        String addressAsur = CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.ADDRESS_ASUR);
        String area = CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.AREA);
        String district = CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.DISTRICT);
        Boolean isBti = CatalogDTOUtil.getAttributeAsBoolByName(attribute, EOAddressEnumJsonFields.IS_BTI);
        String addressDescription = CatalogDTOUtil.getAttributeByName(attribute, EOAddressEnumJsonFields.ADDRESS_DESCRIPTION);

        String queueFromBTI = getFirstFindingQueue(attribute, EOAddressEnumJsonFields.DATA_IN_BTI, EOAddressEnumJsonFields.QUEUE_VALUE_IN_BTI);
        String queueNotFromBTI = getFirstFindingQueue(attribute, EOAddressEnumJsonFields.DATA_NOT_IN_BTI, EOAddressEnumJsonFields.QUEUE_VALUE_NOT_IN_BTI);

        return new EOAddress(globalId, systemObjectId, eoId, uniqueAddressId, unom, unad, address, addressAsur, area,
                district, isBti, addressDescription, organizationRegistry, queueFromBTI, queueNotFromBTI);
    }

    private String getFirstFindingQueue(List<AttributeChangesDTO> attribute, EOAddressEnumJsonFields dataJsonField,
                                        EOAddressEnumJsonFields queueJsonField) {
        GroupValueDTO dataGroupValue = CatalogDTOUtil.getGroupValueByName(attribute, dataJsonField);
        if(dataGroupValue == null || CollectionUtils.isEmpty(dataGroupValue.getItem())){
            return null;
        }
        for (SimpleAttributeDTO s : dataGroupValue.getItem()) {
            String dataAboutQueue = CatalogDTOUtil.getAttributeByName(s.getAttribute(), queueJsonField);
            if(StringUtils.isNotBlank(dataAboutQueue)){
                return dataAboutQueue;
            }
        }
        return null;
    }

    private boolean notHaveDataAboutQueue(SimpleAttributeDTO simpleDTO) {
        GroupValueDTO dataFromBTI = CatalogDTOUtil.getGroupValueByName(simpleDTO.getAttribute(), EOAddressEnumJsonFields.DATA_IN_BTI);
        GroupValueDTO dataNotFromBTI = CatalogDTOUtil.getGroupValueByName(simpleDTO.getAttribute(), EOAddressEnumJsonFields.DATA_NOT_IN_BTI);
        if(dataFromBTI != null && !CollectionUtils.isEmpty(dataFromBTI.getItem())){
            for (SimpleAttributeDTO s : dataFromBTI.getItem()) {
                if(StringUtils.isNotBlank(CatalogDTOUtil.getAttributeByName(s.getAttribute(), EOAddressEnumJsonFields.QUEUE_VALUE_IN_BTI))){
                    return false;
                }
            }
        } else if(dataNotFromBTI != null && !CollectionUtils.isEmpty(dataNotFromBTI.getItem())){
            for (SimpleAttributeDTO s : dataNotFromBTI.getItem()) {
                if(StringUtils.isNotBlank(CatalogDTOUtil.getAttributeByName(s.getAttribute(), EOAddressEnumJsonFields.QUEUE_VALUE_IN_BTI))){
                    return false;
                }
            }
        }
        return true;
    }

    private OrganizationRegistry getOrganizationRegistryByJsonDTO(ChangesDTO catalogChangeData) {
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(),
                OrganizationRegistry.AbstractRegistryJsonFieldsEnum.GLOBAL_ID);
        return organizationRegistryRepo.findById(globalId).orElse(null);
    }

    private EOAddress getEOAddressBySimpleDTO(SimpleAttributeDTO simpleDTO){
        Long globalId = CatalogDTOUtil.getAttributeAsLongByName(simpleDTO.getAttribute(), EOAddress.AbstractRegistryJsonFieldsEnum.GLOBAL_ID);
        EOAddress address = eoAddressRepo.findById(globalId).orElse(null);
        if(address  == null){
            log.debug(String.format("Can't find EOAddress entity by Global_id: %d",  globalId));
        }
        return address;
    }

    private boolean notServedByISPP(ChangesDTO catalogChangeData) {
        try {
            Long globalId = CatalogDTOUtil.getAttributeAsLongByName(catalogChangeData.getAttribute(),
                    EOAddress.AbstractRegistryJsonFieldsEnum.GLOBAL_ID);
            if(organizationRegistryRepo.existsById(globalId)){
                return false;
            }
            GroupValueDTO addresses = CatalogDTOUtil.getGroupValueByName(catalogChangeData.getAttribute(),
                    OrganizationRegistryEnumJsonFields.EO_ADDRESS_ID);
            if (addresses == null || CollectionUtils.isEmpty(addresses.getItem())) {
                throw new NotServedOrganizationException("No data about addresses");
            } else {
                boolean isServed = false;
                for(SimpleAttributeDTO simpleObj : addresses.getItem()){
                    isServed = !notHaveDataAboutQueue(simpleObj);
                    if(isServed){
                        break;
                    }
                }
                if (!isServed) {
                    throw new NotServedOrganizationException("Invalid data from all sources (BTI and not in BTI)");
                }
            }
        } catch (NotServedOrganizationException e) {
            log.warn("Could not determine service attribute: " + e.getMessage());
            return true;
        } catch (Exception e) {
            log.error("Internal error: ", e);
            return true;
        }
        return false;
    }
}
