/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.nsi;

import generated.nsiws2.com.rstyle.nsi.beans.Attribute;
import generated.nsiws2.com.rstyle.nsi.beans.GroupValue;
import generated.nsiws2.com.rstyle.nsi.beans.Item;
import generated.nsiws2.com.rstyle.nsi.beans.SearchPredicate;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgSync;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterMSKClientsService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.util.*;

/**
 * User: shamil
 * Date: 10.07.15
 * Time: 10:25
 */
@Primary
@Component("ClientMskNSIService")
@Scope("singleton")
public class ClientMskNSIService extends MskNSIService {
    private static final Logger logger = LoggerFactory.getLogger(ClientMskNSIService.class);

    public void fillTable(BufferedReader bufferedReader) throws Exception {
        //stub
    }

    public String getBadGuids(ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids) throws Exception {
        List<String> list = new ArrayList<String>();
        SearchPredicateInfo searchPredicateInfo = new SearchPredicateInfo();
        searchPredicateInfo.setCatalogName("Реестр образовательных учреждений");

        SearchPredicate searchByStatus = new SearchPredicate();
        searchByStatus.setAttributeName("Статус записи");
        searchByStatus.setAttributeType(TYPE_STRING);
        searchByStatus.setAttributeValue("Удаленный");
        searchByStatus.setAttributeOp("not like");
        searchPredicateInfo.addSearchPredicate(searchByStatus);

        boolean guidOK;
        for (String guid : orgGuids.getOrgGuids()) {
            guidOK = false;
            if (searchPredicateInfo.getSearchPredicates().size() > 1) {
                searchPredicateInfo.getSearchPredicates().remove(1);
            }

            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("GUID Образовательного учреждения");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue(guid);
            search.setAttributeOp("in");
            searchPredicateInfo.addSearchPredicate(search);

            List<Item> queryResults = executeQuery(searchPredicateInfo, -1);
            ImportRegisterMSKClientsService service = RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class);
            for (Item i : queryResults) {
                for(Attribute attr : i.getAttribute()) {
                    if (attr.getName().equals("GUID Образовательного учреждения")) {
                        String guidFrom = attr.getValue().get(0).getValue();
                        if (guid.equals(guidFrom)) {
                            guidOK = true;
                            service.setOrgSyncErrorCode(guid, OrgSync.ERROR_STATE_OK_CODE);
                            break;
                        }
                    }
                }
                if (guidOK) break;
            }
            if (!guidOK) {
                service.setOrgSyncErrorCode(guid, OrgSync.ERROR_STATE_BAD_GUID_CODE);
                String badGuidString = "";
                List<Org> orgs = DAOReadonlyService.getInstance().findOrgsByGuidAddressINNOrNumber(guid, "", "", "");
                for (Org o : orgs) {
                    badGuidString += String.format("Guid: %s, Ид. организации: %s, Название организации: %s;\n", guid, o.getIdOfOrg(), o.getShortNameInfoService());
                }
                list.add(badGuidString);
            }
        }
        String badGuids = "Найдены следующие неактуальные идентификаторы организаций в НСИ:\n";
        for (String g : list) {
            badGuids += g;
        }
        return badGuids;
    }

    public List<ImportRegisterMSKClientsService.ExpandedPupilInfo> getPupilsByOrgGUID(ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids,
            String familyName, String firstName, String secondName) throws Exception {
        List<ImportRegisterMSKClientsService.ExpandedPupilInfo> pupils = new ArrayList<ImportRegisterMSKClientsService.ExpandedPupilInfo>();
        int importIteration = 1;
        while (true) {
            List<ImportRegisterMSKClientsService.ExpandedPupilInfo> iterationPupils = null;
            iterationPupils = getClientsForOrgs(orgGuids.getOrgGuids(), familyName, firstName, secondName, importIteration);
            if (iterationPupils == null) continue;
            if (iterationPupils.size() > 0) {
                pupils.addAll(iterationPupils);
            } else {
                break;
            }
            importIteration++;
        }
        /// удалить неимпортируемые группы
        for (Iterator<ImportRegisterMSKClientsService.ExpandedPupilInfo> i = pupils.iterator(); i.hasNext(); ) {
            ImportRegisterMSKClientsService.ExpandedPupilInfo p = i.next();
            if (ImportRegisterMSKClientsService.isPupilIgnoredFromImport(p.getGuid(), p.getGroup())) {
                i.remove();
            }
        }
        return pupils;
    }

    public List<ImportRegisterMSKClientsService.ExpandedPupilInfo> getClientsForOrgs(Set<String> guids, String familyName,
            String firstName, String secondName, int importIteration) throws Exception {
        if(guids == null || guids.size() < 1) {
            throw new Exception("Запрос конитингенту без указания организации запрещен. Необходимо указывать организацию!");
        }
        //  Ограничение по guid'ам
        SearchPredicateInfo searchPredicateInfo = new SearchPredicateInfo();
        searchPredicateInfo.setCatalogName("Реестр обучаемых");
        String guidCase = "";
        if (CollectionUtils.isNotEmpty(guids)) {
            for (String guid : guids) {
                if(guidCase.length() > 0) {
                    guidCase += ", ";
                }
                guidCase += guid;
            }
        }
        if(guidCase.length() > 0) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("GUID образовательного учреждения");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue(guidCase);
            search.setAttributeOp("in");
            searchPredicateInfo.addSearchPredicate(search);
        }

        //  ФИО ограничения
        if(!StringUtils.isBlank(familyName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Фамилия");
            search.setAttributeType(TYPE_STRING);
            if(guids != null && guids.size() > 0) {
                search.setAttributeValue("%" + familyName + "%");
                search.setAttributeOp("like");
            } else {
                search.setAttributeValue(familyName);
                search.setAttributeOp("=");
            }
            searchPredicateInfo.addSearchPredicate(search);
        }
        if(!StringUtils.isBlank(firstName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Имя");
            search.setAttributeType(TYPE_STRING);
            if(guids != null && guids.size() > 0) {
                search.setAttributeValue("%" + firstName + "%");
                search.setAttributeOp("like");
            } else {
                search.setAttributeValue(firstName);
                search.setAttributeOp("=");
            }
            searchPredicateInfo.addSearchPredicate(search);
        }
        if(!StringUtils.isBlank(secondName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Отчество");
            search.setAttributeType(TYPE_STRING);
            if(guids != null && guids.size() > 0) {
                search.setAttributeValue("%" + secondName + "%");
                search.setAttributeOp("like");
            } else {
                search.setAttributeValue(secondName);
                search.setAttributeOp("=");
            }
            searchPredicateInfo.addSearchPredicate(search);
        }

        //  Запрет на удаленных
        SearchPredicate search1 = new SearchPredicate();
        search1.setAttributeName("Статус записи");
        search1.setAttributeType(TYPE_STRING);
        search1.setAttributeValue("Удален%");
        search1.setAttributeOp("not like");
        searchPredicateInfo.addSearchPredicate(search1);
        SearchPredicate search2 = new SearchPredicate();
        search2.setAttributeName("Статус записи");
        search2.setAttributeType(TYPE_STRING);
        search2.setAttributeValue("%Отчислен%");
        search2.setAttributeOp("not like");
        searchPredicateInfo.addSearchPredicate(search2);
        SearchPredicate search3 = new SearchPredicate();
        search3.setAttributeName("Статус записи");
        search3.setAttributeType(TYPE_STRING);
        search3.setAttributeValue("%Выпущен%");
        search3.setAttributeOp("not like");
        searchPredicateInfo.addSearchPredicate(search3);

        List<Item> queryResults = executeQuery(searchPredicateInfo, importIteration);
        if (queryResults == null) return null;
        LinkedList<ImportRegisterMSKClientsService.ExpandedPupilInfo> list = new LinkedList<ImportRegisterMSKClientsService.ExpandedPupilInfo>();
        for(Item i : queryResults) {
            ImportRegisterMSKClientsService.ExpandedPupilInfo pupilInfo = new ImportRegisterMSKClientsService.ExpandedPupilInfo();
            for(Attribute attr : i.getAttribute()) {
                if (attr.getName().equals("Фамилия")) {
                    pupilInfo.familyName = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Имя")) {
                    pupilInfo.firstName = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Отчество")) {
                    pupilInfo.secondName = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("GUID")) {
                    pupilInfo.guid = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Дата рождения")) {
                    pupilInfo.birthDate = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Текущий класс или группа")) {
                    pupilInfo.groupDeprecated = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Класс")) {
                    pupilInfo.groupNewWay = getGroupFromClassAttribute(attr);
                }
                if (attr.getName().equals("GUID образовательного учреждения")) {
                    pupilInfo.guidOfOrg = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Льготы учащегося")) {
                    List<Integer> benefits = new ArrayList<Integer>();
                    for (GroupValue groupValue : attr.getGroupValue()) {
                        for (Attribute attr1 : groupValue.getAttribute()) {
                            if (attr1.getName().equals("Льгота")) {
                                String benefit = attr1.getValue().get(0).getValue();
                                if(StringUtils.isNotEmpty(benefit)) {
                                    benefits.add(Integer.valueOf(benefit));
                                }
                            }
                        }
                    }
                    Collections.sort(benefits);
                    pupilInfo.benefitDSZN = StringUtils.join(benefits, ",");
                }

                if (attr.getName().equals("Пол")) {
                    pupilInfo.gender = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Представители")) {
                    for (GroupValue groupValue : attr.getGroupValue()) {
                        ImportRegisterMSKClientsService.GuardianInfo guardianInfo = new ImportRegisterMSKClientsService.GuardianInfo();

                        for (Attribute attr1 : groupValue.getAttribute()) {
                            if (attr1.getName().equals("Фамилия представителя")) {

                                String famName = attr1.getValue().get(0).getValue();
                                guardianInfo.setFamilyName(famName == null ? null : famName.trim());
                            }
                            if (attr1.getName().equals("Имя представителя")) {

                                String fName = attr1.getValue().get(0).getValue();
                                guardianInfo.setFirstName(fName == null ? null : fName.trim());
                            }
                            if (attr1.getName().equals("Отчество представителя")) {

                                String sName = attr1.getValue().get(0).getValue();
                                guardianInfo.setSecondName(sName == null ? null : sName.trim());
                            }
                            if (attr1.getName().equals("Кем приходится")) {
                                guardianInfo.setRelationship(attr1.getValue().get(0).getValue());
                            }
                            if (attr1.getName().equals("Телефон представителя")) {
                                guardianInfo.setPhoneNumber(attr1.getValue().get(0).getValue());
                            }
                            if (attr1.getName().equals("Адрес электронной почты")) {
                                guardianInfo.setEmailAddress(attr1.getValue().get(0).getValue());
                            }
                        }
                        pupilInfo.getGuardianInfoList().add(guardianInfo);
                    }
                }

                if (attr.getName().equals("Класс")) {
                    for (GroupValue groupValue : attr.getGroupValue()) {
                        for (Attribute attr1 : groupValue.getAttribute()) {
                            if (attr1.getName().equals("Тип возрастной группы")) {
                                pupilInfo.ageTypeGroup = attr1.getValue().get(0).getValue();
                            }
                        }
                    }
                }
                pupilInfo.guardiansCount = String.valueOf(pupilInfo.getGuardianInfoList().size());
            }

            pupilInfo.familyName = pupilInfo.familyName == null ? null : pupilInfo.familyName.trim();
            pupilInfo.firstName = pupilInfo.firstName == null ? null : pupilInfo.firstName.trim();
            pupilInfo.secondName = pupilInfo.secondName == null ? null : pupilInfo.secondName.trim();
            pupilInfo.guid = pupilInfo.guid == null ? null : pupilInfo.guid.trim();
            pupilInfo.group = !StringUtils.isEmpty(pupilInfo.groupNewWay) ? pupilInfo.groupNewWay : pupilInfo.groupDeprecated;
            pupilInfo.group = pupilInfo.group == null ? null : pupilInfo.group.trim();

            list.add(pupilInfo);
        }
        return list;
    }

    private String getGroupFromClassAttribute(Attribute attr) {
        //При изменениях нужно править аналогичный метод получения группы в NSIDeltaProcessor
        try {
            String group = null;
            List<GroupValue> groupValues = attr.getGroupValue();
            GroupValue lastYearGroupValue = null;
            if (groupValues.size() > 1) {
                Integer year = null;
                //Пробегаемся по всем блокам <groupValue occurrence="0 .. N"> с целью найти блок с последним годом обучения
                for(GroupValue grpVal : groupValues) {
                    for(Attribute attr2 : grpVal.getAttribute()) {
                        if(attr2.getName().equals("Год обучения")) {
                            String[] years = attr2.getValue().get(0).getValue().split("/");
                            if (years.length > 0) {
                                String sYear = years[years.length-1];
                                if ((lastYearGroupValue == null) || (Integer.parseInt(sYear) > year)) {
                                    lastYearGroupValue = grpVal;
                                    year = Integer.parseInt(sYear);
                                }
                            }
                        }
                    }
                }
            } else {
                lastYearGroupValue = groupValues.get(0);
            }
            if (lastYearGroupValue != null) {
                for(Attribute attr2 : lastYearGroupValue.getAttribute()) {
                    if(attr2.getName().equals("Название")) {
                        group = attr2.getValue().get(0).getValue();
                    }
                }
            }
            return group;
        } catch (Exception e) {
            logger.info("Error finding group from group attribute");
            return null;
        }
    }
}
