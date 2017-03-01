/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.nsi;

import generated.nsiws_delta.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.RegistryChange;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 07.07.14
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class NSIDeltaProcessor {
    private final static Logger logger = LoggerFactory.getLogger(NSIDeltaProcessor.class);

    private final static int DAYS_DELTA_ACTUAL = 7;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    public ContainerDelta unmarshal(DeltaType delta) {
        try {
            byte bytes [] = delta.getData().getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(delta.getData().getBytes("UTF-8"));

            JAXBContext jc = JAXBContext.newInstance(ContainerDelta.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            ContainerDelta containerDelta = (ContainerDelta) unmarshaller.unmarshal(bais);
            bais.close();
            return containerDelta;
        } catch(Exception e) {
            logger.error("Failed to parse XML node", e);
            return null;
        }
    }

    public ReceiveNSIDeltaResponseType process(ReceiveNSIDeltaRequestType receiveNSIDeltaRequest) {
        //  переменные
        DeltaType delta = receiveNSIDeltaRequest.getDelta();
        long ts = RuntimeContext.getAppContext().getBean(NSIDeltaProcessor.class).getUpdateTs();
        StringBuffer logBuffer = new StringBuffer("");
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ts));
        String synchDate = "[Синхронизация с Реестрами от " + date + " с UID " + delta.getUid() + "]: ";

        try {
            Date deltaDate = delta.getDate().toGregorianCalendar().getTime();
            if (CalendarUtils.getDifferenceInDays(deltaDate, new Date()) > DAYS_DELTA_ACTUAL) {
                ImportRegisterClientsService.log(synchDate + String.format("Дельта не принята по сроку давности %s", deltaDate), logBuffer);
                return buildOKResponse();
            }
        } catch (Exception e) {
            ImportRegisterClientsService.log(synchDate + "Не найдена секция date в дельте", logBuffer);
            return buildFailureResponse("Not found delta date");
        }

        //  парсинг
        ContainerDelta containerDelta = unmarshal(delta);
        if(containerDelta == null) {
            ImportRegisterClientsService.log(synchDate + "Не удалось провести парсинг", logBuffer);
            return buildFailureResponse("Invalid type");
        }
        //  анализ
        List<Item> items = containerDelta.getItem();
        if(items.size() < 1) {
            ImportRegisterClientsService.log(synchDate + "Элементов для изменений не найдено", logBuffer);
            return buildFailureResponse("Items are empty");
        }


        ImportRegisterClientsService.log(synchDate + "Всего элементов, подлежащих изменению " + items.size(), logBuffer);
        try {
            parseItems(synchDate, date, ts, items, logBuffer, false);
        } catch (NSIDeltaException nside) {
            logger.error("Failed to parse items", nside);
            return buildFailureResponse("Failed to proceed registry import " + nside.getMessage());
        }
        ImportRegisterClientsService.log(synchDate + "Загрузка изменений успешно завершена" + items.size(), logBuffer);
        return buildOKResponse();
    }

    protected ReceiveNSIDeltaResponseType buildFailureResponse(String error) {
        ReceiveNSIDeltaResponseType response = new ReceiveNSIDeltaResponseType();
        response.setResult(ResultType.FAILURE);
        List<String> errors = response.getErrorMessage();
        errors.add(error);
        return response;
    }

    protected ReceiveNSIDeltaResponseType buildOKResponse() {
        ReceiveNSIDeltaResponseType response = new ReceiveNSIDeltaResponseType();
        response.setResult(ResultType.SUCCESS);
        return response;
    }

    protected void parseItems(String synchDate, String date, long ts, List<Item> items,
            StringBuffer logBuffer, boolean useFullComparison) throws NSIDeltaException {
        if(useFullComparison) {
            ImportRegisterClientsService.log(synchDate + "Загрузка изменений с использованием старого метода полной сверки" + items.size(), logBuffer);
            Map<Org, List<ImportRegisterClientsService.ExpandedPupilInfo>> pupils = mergePupilByOrg(items);
            for(Org org : pupils.keySet()) {
                try {
                    RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class).saveClients
                            (synchDate, date, ts, org, pupils.get(org), logBuffer);
                } catch (Exception e) {
                    ImportRegisterClientsService.logError(synchDate + "При синхронизации произошла ошибка", e,
                            logBuffer);
                }
            }
        } else {
            ImportRegisterClientsService.log(synchDate + "Загрузка изменений с использованием нового метода доверительного к Реестрам" + items.size(), logBuffer);
            for(Item i : items) {
                DeltaItem deltaItem = new DeltaItem(i);
                if(deltaItem.isEmpty()) {
                    continue;
                }
                RuntimeContext.getAppContext().getBean(NSIDeltaProcessor.class).saveClientTrustedComparison(synchDate,
                        date, ts, logBuffer, deltaItem);
            }
        }
    }

    protected Map<Org, List<ImportRegisterClientsService.ExpandedPupilInfo>> mergePupilByOrg(List<Item> items) {
        Map<Org, List<ImportRegisterClientsService.ExpandedPupilInfo>> res = new HashMap<Org, List<ImportRegisterClientsService.ExpandedPupilInfo>>();
        for(Item i : items) {
            DeltaItem deltaItem = new DeltaItem(i);
            Org org = DAOService.getInstance().getOrgByGuid(deltaItem.getOrgGuid());

            List<ImportRegisterClientsService.ExpandedPupilInfo> orgItems = res.get(org);
            if(orgItems == null) {
                orgItems = new ArrayList<ImportRegisterClientsService.ExpandedPupilInfo>();
                res.put(org, orgItems);
            }

            ImportRegisterClientsService.ExpandedPupilInfo epi = new ImportRegisterClientsService.ExpandedPupilInfo();
            epi.firstName = deltaItem.getFirstName();
            epi.secondName = deltaItem.getSecondName();
            epi.familyName = deltaItem.getFamilyName();
            epi.group = deltaItem.getGroup();
            epi.guid = deltaItem.getGuid();
            epi.guidOfOrg = deltaItem.getOrgGuid();
            epi.deleted = i.getAction().ordinal() == Action.DELETED.ordinal();
            orgItems.add(epi);
        }
        return res;
    }

    @Transactional
    public long getUpdateTs() {
        return ImportRegisterClientsService.getLastUncommitedChange(em);
    }

    @Transactional
    public void saveClientTrustedComparison(String synchDate, String date, long ts,
                                               StringBuffer logBuffer, DeltaItem item) throws NSIDeltaException {
        if(item.getAction() == Action.ADDED.ordinal()) {
            Org org = DAOService.getInstance().getOrgByGuid(item.getOrgGuid());
            if(org == null) {
                ImportRegisterClientsService.log(synchDate + "Добавление " + item.getGuid() + ", " +
                        item.getFamilyName() + " " + item.getFirstName() + " " +
                        item.getSecondName() + ", " + item.getGroup() + " невозможено - школа " + item.getOrgGuid() + " не найдена", logBuffer);
                return;
            }
            try {
                //  log
                ImportRegisterClientsService.log(synchDate + "Добавление " + item.getGuid() + ", " +
                        item.getFamilyName() + " " + item.getFirstName() + " " +
                        item.getSecondName() + ", " + item.getGroup(), logBuffer);
                //  exec
                FieldProcessor.Config fieldConfig = buildFieldConfig(item, new ClientManager.ClientFieldConfig());
                ImportRegisterClientsService.addClientChange(em, ts, org.getIdOfOrg(), null, fieldConfig, null,
                        ImportRegisterClientsService.CREATE_OPERATION, RegistryChange.CHANGES_UPDATE, item.getNotificationId());
            } catch (Exception e) {
                throw new NSIDeltaException(String.format("Failed to create FieldConfig for client %s", item), e);
            }
        } else if(item.getAction() == Action.MODIFIED.ordinal()) {
            Client cl = DAOUtils.findClientByGuid(em, StringUtils.isBlank(item.getGuid()) ? "" : item.getGuid());
            if(cl == null) {
                ImportRegisterClientsService.log(synchDate + "Невозможно обработать изменение клиента " +
                        emptyIfNull(item.getGuid()) + ", " + emptyIfNull(item.getFamilyName()) + " " +
                        emptyIfNull(item.getFirstName()) + " - не найден", logBuffer);
                return;
            }
            //  проверка клиента
            if(cl.getClientGroup() != null &&
               cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().longValue() >=
                                                    ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue().longValue() &&
               cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().longValue() <
                                                    ClientGroup.Predefined.CLIENT_LEAVING.getValue().longValue()) {
                return;
            }


            try {
                FieldProcessor.Config fieldConfig = buildFieldConfig(item, new ClientManager.ClientFieldConfig(), cl);
                if(!StringUtils.isBlank(emptyIfNull(item.getOrgGuid())) && !cl.getOrg().getGuid().equals(emptyIfNull(item.getOrgGuid()))) {
                    Org newOrg = DAOService.getInstance().getOrgByGuid(item.getOrgGuid());
                    if(newOrg == null) {
                        ImportRegisterClientsService.log(synchDate + "Перевод " + emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(
                                cl.getPerson().getSurname()) + " " +
                                emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(cl.getPerson().getSecondName())
                                + ", " +
                                emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName()) + " из школы " + cl.getOrg().getIdOfOrg()
                                + " в школу " + item.getOrgGuid() + " невозможен - школа " + item.getOrgGuid() + " не найдена", logBuffer);
                        return;
                    }
                    //  log
                    ImportRegisterClientsService.log(synchDate + "Перевод " + emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(
                            cl.getPerson().getSurname()) + " " +
                            emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(cl.getPerson().getSecondName())
                            + ", " +
                            emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName()) + " из школы " + cl.getOrg().getIdOfOrg()
                            + " в школу " + newOrg.getIdOfOrg(), logBuffer);
                    //  exec
                    ImportRegisterClientsService.addClientChange(em, ts, cl.getOrg().getIdOfOrg(), newOrg.getIdOfOrg(), fieldConfig, cl,
                            ImportRegisterClientsService.MOVE_OPERATION, RegistryChange.CHANGES_UPDATE, item.getNotificationId());
                } else {
                    //  log
                    ImportRegisterClientsService.log(synchDate + "Изменение " +
                            emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(cl.getPerson().getSurname()) + " " +
                            emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(
                            cl.getPerson().getSecondName()) + ", " +
                            emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName()) + " на " +
                            emptyIfNull(item.getGuid()) + ", " + emptyIfNull(item.getFamilyName()) + " "
                            + emptyIfNull(item.getFirstName()) + " " +
                            emptyIfNull(item.getSecondName()) + ", " + emptyIfNull(item.getGroup()), logBuffer);
                    //  exec
                    ImportRegisterClientsService.addClientChange(em, ts, cl.getOrg().getIdOfOrg(), null, fieldConfig, cl,
                            ImportRegisterClientsService.MODIFY_OPERATION, RegistryChange.CHANGES_UPDATE, item.getNotificationId());
                }
            } catch (Exception e) {
                throw new NSIDeltaException(String.format("Failed to create FieldConfig for client %s", item), e);
            }
        } else if(item.getAction() == Action.DELETED.ordinal()) {
            Client cl = DAOUtils.findClientByGuid(em, StringUtils.isBlank(item.getGuid()) ? "" : item.getGuid());
            if(cl == null) {
                ImportRegisterClientsService.log(synchDate + "Невозможно обработать удаление клиента " +
                        emptyIfNull(item.getGuid()) + ", " + emptyIfNull(item.getFamilyName()) + " " +
                        emptyIfNull(item.getFirstName()) + " - не найден", logBuffer);
                return;
            }
            try {
                //  log
                ImportRegisterClientsService.log(synchDate + "Удаление " +
                        emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(cl.getPerson().getSurname()) + " " +
                        emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(cl.getPerson().getSecondName())
                        + ", " +
                        emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName()), logBuffer);
                //  exec
                ImportRegisterClientsService.addClientChange(em, ts, cl.getOrg().getIdOfOrg(), cl,
                        ImportRegisterClientsService.DELETE_OPERATION, RegistryChange.CHANGES_UPDATE, item.getNotificationId());
            } catch (Exception e) {
                throw new NSIDeltaException(String.format("Failed to create FieldConfig for client %s", item), e);
            }
        }
    }

    protected FieldProcessor.Config buildFieldConfig(DeltaItem item, FieldProcessor.Config fieldConfig) throws Exception {
        return buildFieldConfig(item, fieldConfig, null);
    }

    protected FieldProcessor.Config buildFieldConfig(DeltaItem item, FieldProcessor.Config fieldConfig, Client cl) throws Exception {
        fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, item.getGuid());
        fieldConfig.setValue(ClientManager.FieldId.SURNAME, solveField(item.getFamilyName(), cl != null ? cl.getPerson().getSurname() : ""));
        fieldConfig.setValue(ClientManager.FieldId.NAME, solveField(item.getFirstName(), cl != null ? cl.getPerson().getFirstName() : ""));
        fieldConfig.setValue(ClientManager.FieldId.SECONDNAME, solveField(item.getSecondName(), cl != null ? cl.getPerson().getSecondName() : ""));
        fieldConfig.setValue(ClientManager.FieldId.GROUP, solveField(item.getGroup(), cl != null && cl.getClientGroup() != null ? cl.getClientGroup().getGroupName() : ""));
        fieldConfig.setValue(ClientManager.FieldId.GENDER, solveField(item.getGender(), cl != null && cl.getGender() != null ? getGenderFromInteger(cl.getGender()) : ""));
        fieldConfig.setValue(ClientManager.FieldId.BIRTH_DATE, solveField(item.getBirthDate(), cl != null && cl.getBirthDate() != null ? getBirthDateFromDate(cl.getBirthDate()) : ""));
        fieldConfig.setValue(ClientManager.FieldId.BENEFIT_DSZN, solveField(item.getBenefitDSZN(), cl != null ? cl.getCategoriesDiscountsDSZN() : ""));
        fieldConfig.setValue(ClientManager.FieldId.AGE_TYPE_GROUP, solveField(item.getAgeTypeGroup(), cl != null ? cl.getAgeTypeGroup() : ""));
        fieldConfig.setValue(ClientManager.FieldId.GUARDIANS_COUNT, solveField(new Integer(item.getGuardians().size()).toString(), cl != null ? cl.getGuardiansCount() : ""));
        if (item.getGuardians() != null && item.getGuardians().size() > 0 ) {
            fieldConfig.setValueList(ClientManager.FieldId.GUARDIANS_COUNT_LIST, item.getGuardians());
        }
        return fieldConfig;
    }

    private String getGenderFromInteger(Integer gender) {
        if (gender.equals(0)) return "Женский";
            else if (gender.equals(1)) return "Мужской";
                else return null;
    }

    private String getBirthDateFromDate(Date birthDate) {
        if (birthDate != null) {
            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
            return timeFormat.format(birthDate);
        }
        return null;
    }

    protected String solveField(String value, String clientValue) {
        if(value == null) {
            if(clientValue != null) {
                return clientValue;
            }
            return "";
        }
        return value;
    }

    private static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    protected class DeltaItem {
        protected String familyName, firstName, secondName, guid, group, orgGuid;
        protected int action;
        protected String notificationId;
        protected String gender;
        protected String birthDate;
        protected String benefitDSZN;
        protected String ageTypeGroup;
        protected List<ImportRegisterClientsService.GuardianInfo> guardians;

        public DeltaItem(Item item) {
            this.notificationId = item.getNotificationId();
            this.guid = item.getGUID();
            action = item.getAction().ordinal();
            for(Attribute at : item.getAttribute()) {
                if(StringUtils.isBlank(at.getName())) {
                    continue;
                }
                String attributeName = at.getName().toLowerCase();
                if(attributeName.endsWith("имя")) {
                    firstName = getSingleValue(at);
                }
                else if(attributeName.endsWith("фамилия")) {
                    familyName = getSingleValue(at);
                }
                else if(attributeName.endsWith("отчество")) {
                    secondName = getSingleValue(at);
                }
                else if(attributeName.endsWith("пол")) {
                    gender = getSingleValue(at);
                }
                else if (attributeName.endsWith("дата рождения")) {
                    birthDate = getSingleValue(at);
                }
                else if (attributeName.endsWith("льготы учащегося")) {
                    List<Integer> benefits = new ArrayList<Integer>();
                    for (GroupValue groupValue : at.getGroupValue()) {
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
                    benefitDSZN = StringUtils.join(benefits, ",");

                }
                else if (attributeName.endsWith("представители")) {
                    guardians = new ArrayList<ImportRegisterClientsService.GuardianInfo>();
                    for (GroupValue groupValue : at.getGroupValue()) {
                        ImportRegisterClientsService.GuardianInfo guardianInfo = new ImportRegisterClientsService.GuardianInfo();

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
                        guardians.add(guardianInfo);
                    }
                }
                else if((group == null || StringUtils.isBlank(group)) &&
                        attributeName.endsWith("текущий класс или группа")) {
                    group = getSingleValue(at);
                } else  if ((group == null || StringUtils.isBlank(group)) &&
                        attributeName.endsWith("класс")) {
                    List<GroupValue> groupValues = at.getGroupValue();
                    for(GroupValue grpVal : groupValues) {
                        for(Attribute attr2 : grpVal.getAttribute()) {
                            if(attr2.getName().equals("Название")) {
                                group = attr2.getValue().get(0).getValue();
                            } else if (attr2.getName().equals("Тип возрастной группы")) {
                                ageTypeGroup = attr2.getValue().get(0).getValue();
                            }
                        }
                    }
                }
                else if(attributeName.endsWith("класс/название")) {
                    group = getSingleValue(at);
                }
                else if(attributeName.endsWith("guid образовательного учреждения")) {
                    orgGuid = getSingleValue(at);
                }
            }
        }

        public String getNotificationId() {
            return notificationId;
        }

        public int getAction() {
            return action;
        }

        public String getFamilyName() {
            return familyName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getGuid() {
            return guid;
        }

        public String getGroup() {
            return group;
        }

        protected String getSingleValue(Attribute at) {
            if(at.getValue().size() < 1) {
                return "";
            }
            return at.getValue().get(0).getValue();
        }

        public String getOrgGuid() {
            return orgGuid;
        }

        public boolean isEmpty() {
            if(StringUtils.isBlank(getFirstName()) &&
                StringUtils.isBlank(getFamilyName()) &&
                StringUtils.isBlank(getSecondName()) &&
                StringUtils.isBlank(getGroup()) &&
                StringUtils.isBlank(getOrgGuid())) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "DeltaItem{" +
                    "familyName='" + familyName + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", secondName='" + secondName + '\'' +
                    ", guid='" + guid + '\'' +
                    ", group='" + group + '\'' +
                    ", orgGuid='" + orgGuid + '\'' +
                    ", action=" + action +
                    '}';
        }

        public String getGender() {
            return gender;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public String getBenefitDSZN() {
            return benefitDSZN;
        }

        public String getAgeTypeGroup() {
            return ageTypeGroup;
        }

        public List<ImportRegisterClientsService.GuardianInfo> getGuardians() {
            return guardians;
        }
    }
}