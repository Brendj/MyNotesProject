/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.nsi;

import generated.nsiws2.com.rstyle.nsi.beans.Attribute;
import generated.nsiws2.com.rstyle.nsi.beans.GroupValue;
import generated.nsiws2.com.rstyle.nsi.beans.Item;
import generated.nsiws2.com.rstyle.nsi.beans.SearchPredicate;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgRegistryChange;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterOrgsService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * User: shamil
 */

@Primary
@Component("OrgMskNSIService")
@Scope("singleton")
public class OrgMskNSIService extends MskNSIService {
    private static final Logger logger = LoggerFactory.getLogger(OrgMskNSIService.class);

    public String getFounderFromOptions() {
        String s = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_FOUNDER_FROM_NSI);
        try {
            return s.split("\\|")[0];
        } catch (Exception e) {
            return "";
        }
    }

    public List<ImportRegisterOrgsService.OrgInfo> getOrgs(String orgName, String region, String founder, String industry, int importIteration) throws Exception {
        SearchPredicateInfo searchPredicateInfo = new SearchPredicateInfo();
        searchPredicateInfo.setCatalogName("Реестр образовательных учреждений");

        //  Название ОУ ограничения
        if(!StringUtils.isBlank(orgName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Полное название учреждения");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue("%" + orgName + "%");
            search.setAttributeOp("like");
            searchPredicateInfo.addSearchPredicate(search);
        }

        //  ограничение на округ
        if(!StringUtils.isBlank(region)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Округ");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue(region);
            search.setAttributeOp("=");
            searchPredicateInfo.addSearchPredicate(search);
        }

        //  ограничение по учредителю
        if(!StringUtils.isBlank(founder)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Учредитель");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue(founder);
            search.setAttributeOp("=");
            searchPredicateInfo.addSearchPredicate(search);
        }

        //  ограничение по отраслевому подчинению
        if(!StringUtils.isBlank(industry)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Отраслевое подчинение");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue(industry);
            search.setAttributeOp("=");
            searchPredicateInfo.addSearchPredicate(search);
        }

        //  Запрет на удаленных
        SearchPredicate search1 = new SearchPredicate();
        search1.setAttributeName("Статус записи");
        search1.setAttributeType(TYPE_STRING);
        search1.setAttributeValue("Удаленный");
        search1.setAttributeOp("not like");
        searchPredicateInfo.addSearchPredicate(search1);


        List<Item> queryResults = executeQuery(searchPredicateInfo, importIteration);
        LinkedList<ImportRegisterOrgsService.OrgInfo> list = new LinkedList<ImportRegisterOrgsService.OrgInfo>();
        for(Item i : queryResults) {
            ImportRegisterOrgsService.OrgInfo info = new ImportRegisterOrgsService.OrgInfo();
            try {
                for(Attribute attr : i.getAttribute()) {
                    if (attr.getName().equals("Типы образовательных учреждений")) {
                        for(Attribute.Value val : attr.getValue()) {
                            if(val.getValue().equals("Общеобразовательное учреждение")) {
                                info.setOrganizationType(OrganizationType.SCHOOL);
                            } else {
                                info.setOrganizationType(OrganizationType.PROFESSIONAL);
                                // TODO: добавить детский сад
                            }
                        }
                    }
                    if (attr.getName().equals("Краткое наименование учреждения")) {
                        info.setShortName(attr.getValue().get(0).getValue());
                    }
                    if (attr.getName().equals("Полное название учреждения")) {
                        info.setOfficialName(attr.getValue().get(0).getValue());
                    }
                    if (attr.getName().equals("ИНН образовательного учреждения")) {
                        info.setInn(attr.getValue().get(0).getValue());
                    }

                    if (attr.getName().equals("Официальный адрес")) {
                        info.setAddress(attr.getValue().get(0).getValue());
                    }
                    info.setCity("Москва");
                    if (attr.getName().equals("Округ")) {
                        info.setRegion(attr.getValue().get(0).getValue());
                    }

                    if (attr.getName().equals("Сведения о БТИ")) {
                        info.setOrgInfos(parseBTIInfo(attr, info.getGuid()));
                        if(attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
                            info.setGuid(attr.getValue().get(0).getValue());
                        }

                        for (GroupValue groupValue : attr.getGroupValue()) {
                            for (Attribute attribute : groupValue.getAttribute()) {
                                if("БТИ.unom".equals(attribute.getName())){
                                    info.setUnom(stringToLongNullSafe(attribute.getValue().get(0).getValue()));
                                }
                                if ("unique_address_id".equals(attribute.getName())){
                                    info.setUniqueAddressId(stringToLongNullSafe(attribute.getValue().get(0).getValue()));
                                }
                            }
                        }
                    }
                    if (attr.getName().equals("Сведения о КЛАДР")) {
                        if(attr.getValue() != null && attr.getValue().size() > 0 && attr.getValue().get(0) != null) {
                            info.setOfficialName(attr.getValue().get(0).getValue());
                        }
                    }

                    if (attr.getName().equals("GUID Образовательного учреждения")) {
                        info.setGuid(attr.getValue().get(0).getValue());
                    }
                    if (attr.getName().equals("Первичный ключ")) {
                        String v = attr.getValue().get(0).getValue();
                        Long registryPrimaryId = null;
                        if(NumberUtils.isNumber(v)) {
                            registryPrimaryId = NumberUtils.toLong(v);
                        }
                        if(registryPrimaryId == null) {
                            break;
                        }
                        info.setRegisteryPrimaryId(registryPrimaryId);
                    }
                    if (attr.getName().equals("ФИО директора")) {
                        info.setDirectorFullName(attr.getValue().get(0).getValue());
                    }

                    if (attr.getName().equals("Код ОГРН")) {
                        info.setOGRN(attr.getValue().get(0).getValue());
                    }
                    if (attr.getName().equals("Признак активности")) {
                        info.setState(attr.getValue().get(0).getValue());
                    }
                }

                if (info.getOrgInfos().size() == 0) {
                    info.setOrgInfos(addInfosWithoutBTIData(info.getAddress()));
                }

                //Здесь - переписать значение полей OrgRegistryChange в каждую запись OrgRegistryChangeItem
                boolean modify = false;
                for (ImportRegisterOrgsService.OrgInfo item : info.getOrgInfos()) {
                    item.setShortName(info.getShortName());
                    item.setOfficialName(info.getOfficialName());
                    item.setCity(info.getCity());
                    item.setRegion(info.getRegion());
                    item.setGuid(info.getGuid());
                    item.setInn(info.getInn());

                    Org fOrg = DAOReadonlyService.getInstance().findOrgByRegistryData(item.getUniqueAddressId(), item.getGuid(),
                            item.getInn(), item.getUnom(), item.getUnad(), false);
                    if (fOrg != null) {
                        fillInfOWithOrg(item, fOrg);
                        item.setOperationType(OrgRegistryChange.MODIFY_OPERATION);
                        //item.setShortNameFrom(fOrg.getShortName());
                        //item.setOfficialNameFrom(fOrg.getOfficialName());
                        modify = true;
                    }
                    else {
                        item.setOperationType(OrgRegistryChange.CREATE_OPERATION);
                    }

                }
                //теперь добавим в таблицу _items организации, не найденные по точным критериям (гуид и уник_аддресс_ид).
                // Совпадение м.б. по номеру организации, адресу, гуиду или ИНН по отдельности
                //addSimilarOrgs(info);

                if (modify) {
                    info.setOperationType(OrgRegistryChange.MODIFY_OPERATION);
                } else {
                    info.setOperationType(OrgRegistryChange.CREATE_OPERATION);
                }

                if(info.getOrganizationType() == null) {
                    logger.error(String.format("При сверке с Реестрами, организация '%s' [%s] "
                            + "не имееет тип организации в Реестрах, или "
                            + "он указан не корректно.", info.getShortName(), info.getGuid()));
                }
                else if(info.getRegisteryPrimaryId() == null) {
                    logger.error(String.format("При сверке с Реестрами, организация '%s' [%s] "
                            + "не имееет первичного ключа в Реестрах, или "
                            + "он указан не корректно.", info.getShortName(), info.getGuid()));
                } else {
                    info.setCreateDate(System.currentTimeMillis());
                    info.setAdditionalId(info.getRegisteryPrimaryId());
                    list.add(info);
                }
            } catch (Exception e) {
                String str = info.getShortName() == null ? "" : info.getShortName();
                logger.error(String.format("Error parsing ORGs info from registry. Org: %s", str), e);
            }
        }
        return list;
    }

    private void addSimilarOrgs(ImportRegisterOrgsService.OrgInfo infoMain) {
        List<Org> orgs = DAOReadonlyService.getInstance().findOrgsByGuidAddressINNOrNumber(infoMain.getGuid(), infoMain.getAddress(),
                infoMain.getInn(), Org.extractOrgNumberFromName(infoMain.getShortName()));
        if (orgs == null || orgs.size() == 0) {
            return;
        }
        boolean found;
        for (Org org : orgs) {
            found = false;
            for (ImportRegisterOrgsService.OrgInfo ii : infoMain.getOrgInfos()) {
                if (org.getIdOfOrg() == ii.getIdOfOrg()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                ImportRegisterOrgsService.OrgInfo newItem = new ImportRegisterOrgsService.OrgInfo();
                fillInfOWithOrg(newItem, org);
                newItem.setOperationType(OrgRegistryChange.SIMILAR);
                infoMain.getOrgInfos().add(newItem);
            }
        }
    }

    protected void fillInfOWithOrg(ImportRegisterOrgsService.OrgInfo info, Org existingOrg) {
        info.setIdOfOrg(existingOrg.getIdOfOrg());
        info.setOrgState(existingOrg.getState());
        info.setOrganizationTypeFrom(existingOrg.getType());

        info.setShortNameFrom(existingOrg.getShortNameInfoService());
        info.setOfficialNameFrom(existingOrg.getOfficialName());
        info.setShortNameSupplierFrom(existingOrg.getShortName());

        info.setAddressFrom(existingOrg.getAddress());
        info.setCityFrom(existingOrg.getCity());
        info.setRegionFrom(existingOrg.getDistrict());

        info.setUnomFrom(existingOrg.getBtiUnom());
        info.setUnadFrom(existingOrg.getBtiUnad());
        info.setUniqueAddressIdFrom(existingOrg.getUniqueAddressId());

        info.setGuidFrom(existingOrg.getGuid());
        info.setMainBuilding(existingOrg.isMainBuilding());
        info.setInnFrom(existingOrg.getINN());
        info.setDirectorFrom(existingOrg.getOfficialPerson().getFullName());
        info.setEkisIdFrom(existingOrg.getEkisId());
        info.setEgissoIdFrom(existingOrg.getEgissoId());
        info.setShortAddressFrom(existingOrg.getShortAddress());
        info.setMunicipalDistrictFrom(existingOrg.getMunicipalDistrict());

        info.setFounderFrom(existingOrg.getFounder());
        info.setSubordinationFrom(existingOrg.getSubordination());
        info.setGlobalIdFrom(existingOrg.getOrgIdFromNsi());

        info.setOperationType(OrgRegistryChange.MODIFY_OPERATION);
    }

    private Org findOrgByUniqueAddressId(List<Org> existingOrgList, Long additionalId) {
        for (Org org : existingOrgList) {
            if(org.getUniqueAddressId() != null && org.getUniqueAddressId().equals(additionalId)){
                return      org;
            }
        }
        return null;
    }

    protected List<ImportRegisterOrgsService.OrgInfo> addInfosWithoutBTIData(String address) {
        List<ImportRegisterOrgsService.OrgInfo> result = new LinkedList<ImportRegisterOrgsService.OrgInfo>();
        ImportRegisterOrgsService.OrgInfo info = new ImportRegisterOrgsService.OrgInfo();
        info.setAddress(address);
        result.add(info);
        return result;
    }

    private List<ImportRegisterOrgsService.OrgInfo> parseBTIInfo(Attribute attr, String guid) {
        List<ImportRegisterOrgsService.OrgInfo> result = new LinkedList<ImportRegisterOrgsService.OrgInfo>();
        if (attr.getGroupValue().size()> 0){
            for (GroupValue groupValue : attr.getGroupValue()) {
                ImportRegisterOrgsService.OrgInfo info = new ImportRegisterOrgsService.OrgInfo();
                info.setGuid(guid);
                for (Attribute attribute : groupValue.getAttribute()) {

                    if("БТИ.eo_address".equals(attribute.getName())){
                        info.setAddress(attribute.getValue().get(0).getValue());
                    }
                    if("unique_address_id".equals(attribute.getName())){
                        Long value = stringToLongNullSafe(attribute.getValue().get(0).getValue());
                        info.setAdditionalId(value);
                        info.setUniqueAddressId(value); //todo почему 2 одинаковых значения?
                    }
                    if("is_main_building".equals(attribute.getName())){
                        if (attribute.getValue()!= null
                                && attribute.getValue().get(0) != null
                                && attribute.getValue().get(0).getValue() != null
                                &&!attribute.getValue().get(0).getValue().isEmpty()){
                            info.setMainBuilding(Integer.valueOf(attribute.getValue().get(0).getValue()) == 1);
                        }else{
                            info.setMainBuilding(false);
                        }
                    }
                    //====begin сюда добавляю заполнение полей для __Item
                    if("БТИ.unom".equals(attribute.getName())){
                        info.setUnom(stringToLongNullSafe(attribute.getValue().get(0).getValue()));
                    }
                    if("БТИ.unad".equals(attribute.getName())){
                        info.setUnad(stringToLongNullSafe(attribute.getValue().get(0).getValue()));
                    }
                    /*if("unique_address_id".equals(attribute.getName())){
                        info.setUniqueAddressId(Long.valueOf(attribute.getValue().get(0).getValue()));
                    }*/
                    //====end of сюда добавляю заполнение полей для __Item
                }
                result.add(info);
            }
        }
        return result;
    }

    private Long stringToLongNullSafe(String value) {
        Long result = null;
        try {
            result = Long.valueOf(value);
        } catch (Exception ignore) { }
        return result;
    }

    public OrgMskNSIService getNSIService() {
        switch (RuntimeContext.getInstance().getOptionValueString(Option.OPTION_NSI_VERSION)) {
            case Option.NSI2 : return RuntimeContext.getAppContext().getBean("OrgSymmetricDAOService", OrgSymmetricDAOService.class);
            case Option.NSI3 : return RuntimeContext.getAppContext().getBean(OrgNSI3DAOService.class);
        }
        return null;
    }

    //Получения списка изменений из реестров с учетом имени, и ограничения на кол-во оргзаписей в ответе
    public List<ImportRegisterOrgsService.OrgInfo> getOrgs(String orgName, String region) throws Exception {
        return getNSIService().getOrgs(orgName, region);
        /*List<ImportRegisterOrgsService.OrgInfo> orgs = new ArrayList<ImportRegisterOrgsService.OrgInfo>();
        int importIteration = 1;
        while (true) {
            List<ImportRegisterOrgsService.OrgInfo> iterationOrgs = null;
            iterationOrgs = getOrgs(orgName, region, founder, industry, importIteration);
            if (iterationOrgs.size() > 0) {
                orgs.addAll(iterationOrgs);
            } else {
                break;
            }
            importIteration++;
            //break; /////////////////////////////Здесь можно ставить break для тестовых целей;
        }
        return orgs;*/
    }

    private boolean testTwoStrings(String byOrg, String byReestrOrgInfo) {
        boolean result = false;
        if (byOrg != null && byReestrOrgInfo != null && byOrg.equals(byReestrOrgInfo)) {
            result = true;
        }
        return result;
    }

    private boolean testTwoLongs(Long byOrg, Long byRestrOrgInfo) {
        if (byOrg == null || byRestrOrgInfo == null) {
            return false;
        } else {
            return testTwoStrings(byOrg.toString(), byRestrOrgInfo.toString());
        }
    }

    protected void addDeletedOrgs(List<ImportRegisterOrgsService.OrgInfo> list) {
        List<Org> dbOrgs = DAOService.getInstance().getActiveOrgsList();
        for(Org o : dbOrgs) {
            boolean found = false;
            for(ImportRegisterOrgsService.OrgInfo oi : list) {
                for(ImportRegisterOrgsService.OrgInfo oi_item : oi.getOrgInfos()) {
                    if(
                            (testTwoStrings(o.getGuid(), oi_item.getGuid()) && testTwoLongs(o.getUniqueAddressId(), oi_item.getUniqueAddressId()))
                                    ||
                                    (testTwoStrings(o.getINN(), oi_item.getInn()) && testTwoLongs(o.getUniqueAddressId(), oi_item.getUniqueAddressId()))
                                    ||
                                    (testTwoLongs(o.getBtiUnom(), oi_item.getUnom()) && testTwoLongs(o.getBtiUnad(), oi_item.getUnad()))
                            ) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            if(found && o.getState() != Org.ACTIVE_STATE) {  //если организация не обслуживается - ее не включаем в список
                continue;
            }

            ImportRegisterOrgsService.OrgInfo info = new ImportRegisterOrgsService.OrgInfo();
            info.setIdOfOrg(o.getIdOfOrg());
            info.setOrgState(o.getState());
            info.setOrganizationType(o.getType());
            info.setShortNameFrom(o.getShortName());
            info.setOfficialNameFrom(o.getOfficialName());
            info.setAddressFrom(o.getAddress());
            info.setCityFrom(o.getCity());
            info.setRegionFrom(o.getDistrict());
            info.setUnomFrom(o.getBtiUnom());
            info.setUnadFrom(o.getBtiUnad());
            info.setUniqueAddressIdFrom(o.getUniqueAddressId());
            info.setGuidFrom(o.getGuid());
            info.setAdditionalId(o.getAdditionalIdBuilding());
            info.setCreateDate(System.currentTimeMillis());
            info.setOperationType(OrgRegistryChange.DELETE_OPERATION);
            list.add(info);
        }
    }


    public List<OrgInfo> getOrgByNameAndGuid(String orgName, String orgGuid) throws Exception {
        if (StringUtils.isEmpty(orgName) && StringUtils.isEmpty(orgGuid)) {
            throw new Exception("Не указано название организации и GUID");
        }
        /*
       От Козлова
       "select item['РОУ XML/GUID Образовательного учреждения'], "+
       "item['РОУ XML/Номер учреждения'], "+
       "item['РОУ XML/Краткое наименование учреждения'], "+
       "item['РОУ XML/Официальный адрес'], "+
       "item['РОУ XML/Дата изменения (число)'] "+
       "from catalog('Реестр образовательных учреждений') "+
       "where "+
       "item['РОУ XML/Статус записи']!='Удаленный' and "+
       "item['РОУ XML/Краткое наименование учреждения'] like '"+orgGuid+"'
        */
        /*String query = "select \n" + "item['РОУ XML/GUID Образовательного учреждения'],\n"
                + "item['РОУ XML/Краткое наименование учреждения'],\n" + "item['РОУ XML/Официальный адрес'],\n"
                + "item['РОУ XML/Дата изменения (число)']\n"
                + "from catalog('Реестр образовательных учреждений') where \n"
                + "item['РОУ XML/Статус записи'] not like 'Удален%'";
        if (StringUtils.isNotEmpty(orgName)) {
            query += " and item['РОУ XML/Краткое наименование учреждения'] like '%" + orgName + "%'";
        }
        if (StringUtils.isNotEmpty(orgGuid)) {
            query += " and item['РОУ XML/GUID Образовательного учреждения']='" + orgGuid + "'";
        }*/


        SearchPredicateInfo searchPredicateInfo = new SearchPredicateInfo();
        searchPredicateInfo.setCatalogName("Реестр образовательных учреждений");
        if (!StringUtils.isBlank(orgName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Краткое наименование учреждения");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue("%" + orgName + "%");
            search.setAttributeOp("like");
            searchPredicateInfo.addSearchPredicate(search);
        }
        if(!StringUtils.isBlank(orgGuid)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("GUID Образовательного учреждения");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue(orgGuid);
            search.setAttributeOp("=");
            searchPredicateInfo.addSearchPredicate(search);
        }


        List<Item> queryResults = executeQuery(searchPredicateInfo);
        LinkedList<OrgInfo> list = new LinkedList<OrgInfo>();
        for (Item i : queryResults) {
            OrgInfo orgInfo = new OrgInfo();
            for(Attribute attr : i.getAttribute()) {
                if(attr.getName().equals("Краткое наименование учреждения")) {
                    orgInfo.shortName = attr.getValue().get(0).getValue();
                    orgInfo.number = Org.extractOrgNumberFromName(orgInfo.shortName);
                }
                if (attr.getName().equals("GUID Образовательного учреждения")) {
                    orgInfo.guid = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Адрес")) {
                    orgInfo.address = attr.getValue().get(0).getValue();
                }
            }

            orgInfo.guid = orgInfo.guid == null ? null : orgInfo.guid.trim();
            orgInfo.shortName = orgInfo.shortName == null ? null : orgInfo.shortName.trim();
            orgInfo.address = orgInfo.address == null ? null : orgInfo.address.trim();

            list.add(orgInfo);
        }
        return list;
    }


    public static class OrgInfo {

        public String guid, number, shortName, address;

        public String getGuid() {
            return guid;
        }

        public String getNumber() {
            return number;
        }

        public String getShortName() {
            return shortName;
        }

        public String getAddress() {
            return address;
        }
    }
}
