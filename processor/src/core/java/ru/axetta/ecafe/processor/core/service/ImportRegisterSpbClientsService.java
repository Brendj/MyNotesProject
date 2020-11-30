/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import generated.spb.register.Pupil;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.CardManagerProcessor;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.partner.spb.SpbClientService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component
@Scope("singleton")
public class ImportRegisterSpbClientsService implements ImportClientRegisterService {

    private static final Logger logger = LoggerFactory.getLogger(ImportRegisterSpbClientsService.class);

    public static final int CREATE_OPERATION = 1;
    public static final int DELETE_OPERATION = 2;
    public static final int MODIFY_OPERATION = 3;
    public static final int MOVE_OPERATION = 4;

    private static final String DATA = "DATA";
    private static final String IN = "IN";
    private static final String OUT = "OUT";

    public static final String COMMENT_AUTO_IMPORT = "{Импорт из Параграфа %s}";
    public static final String COMMENT_AUTO_MODIFY = "{Изменено из Параграфа %s}";
    public static final String COMMENT_AUTO_CREATE = "{Создано из Параграфа %s}";
    public static final String COMMENT_AUTO_DELETED = "{Исключен по Параграфу %s}";
    public static final String REPLACEMENT_REGEXP = "\\{[^}]* Параграф[^}]*\\}";

    private static final String EMPLOYEE = "СОТР";

    private static final long MILLISECONDS_IN_DAY = 86400000L;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Autowired
    SpbClientService spbService;

    public void run() throws IOException {
        if (!RuntimeContext.getInstance().isMainNode() || !RuntimeContext.RegistryType.isSpb()) {
            return;
        }
        RuntimeContext.getAppContext().getBean(ImportRegisterSpbClientsService.class).checkRegistryChangesValidity();
    }

    @Transactional
    public void checkRegistryChangesValidity() {
        long minCreateDate =
                RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_MSK_NSI_REGISTRY_CHANGE_DAYS_TIMEOUT)
                        * MILLISECONDS_IN_DAY;
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis() - minCreateDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Session session = (Session) em.getDelegate();
        Query q = session.createSQLQuery(
                  "BEGIN WORK; "
                + "LOCK TABLE cf_registrychange_guardians, cf_registrychange, cf_registrychange_errors IN SHARE MODE; "
                + "CREATE TEMP TABLE cf_registrychange_guardians_backup ON COMMIT DROP AS "
                + "      SELECT * FROM cf_registrychange_guardians WHERE createddate >=:minCreateDate ; "
                + "CREATE TEMP TABLE cf_registrychange_backup ON COMMIT DROP AS "
                + "      SELECT * FROM cf_registrychange WHERE createdate >=:minCreateDate ; "
                + "CREATE TEMP TABLE cf_registrychange_errors_backup ON COMMIT DROP AS "
                + "      SELECT * FROM cf_registrychange_errors WHERE createdate >=:minCreateDate ; "
                + "TRUNCATE cf_registrychange_guardians, cf_registrychange, cf_registrychange_errors; "
                + "INSERT INTO cf_registrychange SELECT * FROM cf_registrychange_backup; "
                + "INSERT INTO cf_registrychange_guardians SELECT * FROM cf_registrychange_guardians_backup; "
                + "INSERT INTO cf_registrychange_errors SELECT * FROM cf_registrychange_errors_backup; "
                + "COMMIT WORK;");
        q.setLong("minCreateDate", cal.getTimeInMillis());
        q.executeUpdate();
    }

    @Override
    @Transactional
    public StringBuffer syncClientsWithRegistry(long idOfOrg, boolean performChanges, StringBuffer logBuffer,
            boolean manualCheckout) throws Exception {
        logBuffer = new StringBuffer();
        if (!DAOService.getInstance().isSverkaEnabled()) {
            throw new Exception("Service temporary unavailable");
        }
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Org org = em.find(Org.class, idOfOrg);
        String synchDate = "[Синхронизация с Реестрами от " + date + " для " + org.getIdOfOrg() + "]: ";
        log(synchDate + "Производится синхронизация для " + org.getOfficialName() + " GUID [" + org.getGuid()
                + "]", logBuffer);

        SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                SecurityJournalProcess.EventType.NSI_CLIENTS, new Date());
        process.saveWithSuccess(true);
        boolean isSuccessEnd = true;

        try {
            //  Итеративно загружаем клиентов, используя ограничения
            List<Pupil> result = spbService.getPupilsByOrg(org.getGuid(), org.getRegistryUrl());
            List<Pupil> pupils = new ArrayList<Pupil>();

            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyyy");

            for(Pupil pupil : result) {
                if(StringUtils.isNotEmpty(pupil.getDob())) {
                    Date bDate = oldFormat.parse(pupil.getDob());
                    pupil.setDob(newFormat.format(bDate));
                }
                if(pupil.getClazz().equals(EMPLOYEE)) {
                    pupil.setClazz(ClientGroup.Predefined.CLIENT_EMPLOYEES.getNameOfGroup());
                }
                if(StringUtils.isNotEmpty(pupil.getClazz())) {
                    pupils.add(pupil);
                }
            }

            log(synchDate + "Получено " + pupils.size() + " записей", logBuffer);
            saveClients(synchDate, System.currentTimeMillis(), org, pupils, logBuffer);
            return logBuffer;
        } catch (Exception e) {
            isSuccessEnd = false;
            throw e;
        } finally {
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(
                    SecurityJournalProcess.EventType.NSI_CLIENTS, new Date());
            processEnd.saveWithSuccess(isSuccessEnd);
        }
    }

    @Override
    public List<RegistryChangeCallback> applyRegistryChangeBatch(List<Long> changesList, boolean fullNameValidation,
            String groupName) throws Exception {
        List<RegistryChangeCallback> result = new LinkedList<>();
        for (Long idOfRegistryChange : changesList) {
            try {
                applyRegistryChange(idOfRegistryChange, fullNameValidation);
                result.add(new RegistryChangeCallback(idOfRegistryChange, ""));
            } catch (Exception e1) {
                logger.error("Error when apply RegistryChange: ", e1);
                setChangeError(idOfRegistryChange, e1);
                result.add(new RegistryChangeCallback(idOfRegistryChange, e1.getMessage()));
            }
        }
        return result;
    }

    public void applyRegistryChange(long idOfRegistryChange, boolean fullNameValidation) throws Exception {
        Session session = null;
        Transaction transaction = null;

        Client afterSaveClient = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            RegistryChange change = (RegistryChange)session.load(RegistryChange.class, idOfRegistryChange);

            Client dbClient = null;
            if (change.getIdOfClient() != null) {
                dbClient = (Client)session.load(Client.class, change.getIdOfClient());
            }
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

            Boolean migration = false;

            Date issueTime = new Date();
            Date validTime = CalendarUtils.endOfDay(CalendarUtils.addYear(issueTime, 5));
            Long cardNo;
            ClientGroup beforeMigrationGroup = null;
            switch (change.getOperation()) {
                case CREATE_OPERATION:
                    //  добавление нового клиента
                    String dateCreate = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));

                    String notifyByPush = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1" : "0";
                    String notifyByEmail = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS) ? "1" : "0";
                    FieldProcessor.Config createConfig = new ClientManager.ClientFieldConfig();
                    createConfig.setValue(ClientManager.FieldId.CLIENT_GUID, change.getClientGUID());
                    createConfig.setValue(ClientManager.FieldId.SURNAME, change.getSurname());
                    createConfig.setValue(ClientManager.FieldId.NAME, change.getFirstName());
                    createConfig.setValue(ClientManager.FieldId.SECONDNAME, change.getSecondName());
                    createConfig.setValue(ClientManager.FieldId.GROUP, change.getGroupName());
                    createConfig.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
                    createConfig.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, notifyByEmail);
                    Date createDateBirth = new Date(change.getBirthDate());
                    createConfig.setValue(ClientManager.FieldId.BIRTH_DATE, format.format(createDateBirth));
                    createConfig.setValue(ClientManager.FieldId.CHECKBENEFITS, change.getCheckBenefitsSafe());
                    if(change.getCheckBenefitsSafe()) {
                        createConfig.setValue(ClientManager.FieldId.BENEFIT_DSZN, change.getBenefitDSZN());
                        createConfig.setValue(ClientManager.FieldId.BENEFIT, change.getNewDiscounts());
                    }
                    afterSaveClient = ClientManager.registerClientTransactionFree(change.getIdOfOrg(),
                            (ClientManager.ClientFieldConfig) createConfig, fullNameValidation, session, String.format(MskNSIService.COMMENT_AUTO_CREATE, dateCreate));
                    try {
                        cardNo = Long.parseLong(afterSaveClient.getClientGUID());
                    } catch (Exception e) {
                        cardNo = null;
                    }
                    if (cardNo != null) {
                        Org org = DAOUtils.findOrg(session, change.getIdOfOrg());
                        if (org.getAutoCreateCards()) {
                            RuntimeContext.getInstance().getCardManager()
                                    .createCardTransactionFree(session, afterSaveClient.getIdOfClient(), cardNo, Card.parseCardType(Card.TYPE_NAMES[1]),
                                            CardState.ISSUED.getValue(), validTime, Card.ISSUED_LIFE_STATE, "", issueTime, cardNo);
                        }
                    }
                    change.setIdOfClient(afterSaveClient.getIdOfClient());
                    change.setIdOfOrg(afterSaveClient.getOrg().getIdOfOrg());

                    break;
                case DELETE_OPERATION:
                    ClientGroup deletedClientGroup = DAOUtils
                            .findClientGroupByGroupNameAndIdOfOrg(session, change.getIdOfOrg(),
                                    ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                    if (deletedClientGroup == null) {
                        deletedClientGroup = DAOUtils.createClientGroup(session, change.getIdOfOrg(),
                                ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
                    }
                    dbClient.setIdOfClientGroup(deletedClientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());

                    String dateDelete = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
                    String deleteCommentsAdds = String.format(MskNSIService.COMMENT_AUTO_DELETED, dateDelete);
                    commentsAddsDelete(dbClient, deleteCommentsAdds);
                    session.save(dbClient);
                    break;
                case MOVE_OPERATION:
                    migration = true;
                    Org newOrg = (Org) session.load(Org.class, change.getIdOfMigrateOrgTo());

                    Org beforeMigrateOrg = dbClient.getOrg();
                    beforeMigrationGroup = dbClient.getClientGroup();

                    GroupNamesToOrgs groupNamesToOrgs = DAOUtils
                            .getAllGroupnamesToOrgsByIdOfMainOrgAndGroupName(session, newOrg.getIdOfOrg(),
                                    change.getGroupName());

                    if (groupNamesToOrgs != null && groupNamesToOrgs.getIdOfOrg() != null) {
                        clientGroupProcess(session, dbClient, groupNamesToOrgs);
                    } else {
                        ClientGroup clientGroup = DAOUtils
                                .findClientGroupByGroupNameAndIdOfOrgNotIgnoreCase(session, newOrg.getIdOfOrg(),
                                        change.getGroupName());
                        if (clientGroup == null) {
                            clientGroup = DAOUtils
                                    .createClientGroup(session, newOrg.getIdOfOrg(), change.getGroupName());
                        }
                        dbClient.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                        dbClient.setOrg(newOrg);
                    }
                    addClientMigrationEntry(session, beforeMigrateOrg, dbClient.getOrg(), beforeMigrationGroup, dbClient, change);
                    change.setIdOfOrg(dbClient.getOrg().getIdOfOrg());
                case MODIFY_OPERATION:
                    Org newOrg1 = (Org)session.load(Org.class, change.getIdOfOrg());
                    Org beforeModifyOrg = dbClient.getOrg();
                    if (beforeMigrationGroup == null)
                    {
                        beforeMigrationGroup = dbClient.getClientGroup();
                    }

                    String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
                    FieldProcessor.Config modifyConfig = new ClientManager.ClientFieldConfigForUpdate();
                    modifyConfig.setValue(ClientManager.FieldId.CLIENT_GUID, change.getClientGUID());
                    modifyConfig.setValue(ClientManager.FieldId.MESH_GUID, change.getMeshGUID());
                    modifyConfig.setValue(ClientManager.FieldId.SURNAME, change.getSurname());
                    modifyConfig.setValue(ClientManager.FieldId.NAME, change.getFirstName());
                    modifyConfig.setValue(ClientManager.FieldId.SECONDNAME, change.getSecondName());
                    modifyConfig.setValue(ClientManager.FieldId.GROUP, change.getGroupName());
                    Date modifyDateBirth = new Date(change.getBirthDate());
                    modifyConfig.setValue(ClientManager.FieldId.BIRTH_DATE, format.format(modifyDateBirth));
                    modifyConfig.setValue(ClientManager.FieldId.CHECKBENEFITS, change.getCheckBenefitsSafe());
                    if(change.getCheckBenefitsSafe()) {
                        modifyConfig.setValue(ClientManager.FieldId.BENEFIT_DSZN, change.getBenefitDSZN());
                        modifyConfig.setValue(ClientManager.FieldId.BENEFIT, change.getNewDiscounts());
                    }
                    ClientManager.modifyClientTransactionFree((ClientManager.ClientFieldConfigForUpdate) modifyConfig,
                            newOrg1, String.format(MskNSIService.COMMENT_AUTO_MODIFY, date),
                            dbClient, session, true);

                    if (!migration) {
                        if (!dbClient.getOrg().getIdOfOrg().equals(beforeModifyOrg.getIdOfOrg())) {
                            addClientMigrationEntry(session, beforeModifyOrg, dbClient.getOrg(), beforeMigrationGroup, dbClient,
                                    change); //орг. меняется - история миграции между ОО
                        } else {
                            if((change.getGroupName() == null && change.getGroupNameFrom() == null) ||
                                    (change.getGroupName() != null && change.getGroupNameFrom() != null &&
                                            !change.getGroupName().equals(change.getGroupNameFrom()))) {
                                addClientGroupMigrationEntry(session, dbClient.getOrg(), dbClient, change);
                                //если орг. не меняется, добавляем историю миграции внутри ОО
                            }
                        }
                    }
                    change.setIdOfOrg(dbClient.getOrg().getIdOfOrg());
                    break;
                default:
                    logger.error("Unknown update registry change operation " + change.getOperation());
            }
            change.setApplied(true);
            session.update(change);
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Transactional
    public void saveClients(String synchDate, long ts, Org org, List<Pupil> pupils,
            StringBuffer logBuffer) throws Exception {
        log(synchDate + "Начато сохранение списка клиентов для " + org.getOfficialName() + " в БД", logBuffer);

        //  Открываем сессию и загружаем клиентов, которые сейчас находятся в БД
        Session session = (Session) em.getDelegate();
        List<Client> currentClients = DAOUtils.findClientsWithoutPredefinedForOrg(em, org);

        List<String> pupilsGuidList = new ArrayList<String>();
        for (Pupil pupil : pupils) {
            if(pupil.getSuid() != null && !pupil.getSuid().isEmpty()) {
                pupilsGuidList.add(pupil.getSuid());
            }
        }

        List<Client> findByGuidsList = DAOUtils.findClientsByGuids(em, pupilsGuidList);
        Map<String, Client> guidMap = new HashMap<String, Client>();
        for(Client client : findByGuidsList){
            guidMap.put(client.getClientGUID(), client);
        }

        for (Client dbClient : currentClients) {
            boolean found = false;
            for (Pupil pupil : pupils) {
                if (pupil.getSuid() != null && dbClient.getClientGUID() != null && pupil.getSuid()
                        .equals(dbClient.getClientGUID())) {
                    if(!isDirectionTypeOut(pupil)) {
                        found = true;
                        break;
                    }
                }
            }
            try {
                ClientGroup currGroup = dbClient.getClientGroup();
                if (currGroup != null &&
                        currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup() >= ClientGroup
                                .Predefined.CLIENT_EMPLOYEES.getValue() &&
                        currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup() < ClientGroup
                                .Predefined.CLIENT_LEAVING.getValue()) {
                    break;
                }
                Long currGroupId =
                        currGroup == null ? null : currGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
                if(currGroupId != null &&
                        (currGroupId.equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue()) ||
                                currGroupId.equals(ClientGroup.Predefined.CLIENT_DELETED.getValue()))) {
                    continue;
                }
                if (emptyIfNull(dbClient.getClientGUID()).equals("") || !found) {
                    log(synchDate + "Удаление " +
                            emptyIfNull(dbClient.getClientGUID()) + ", " + emptyIfNull(
                            dbClient.getPerson().getSurname()) + " " +
                            emptyIfNull(dbClient.getPerson().getFirstName()) + " " + emptyIfNull(
                            dbClient.getPerson().getSecondName()) + ", " +
                            emptyIfNull(dbClient.getClientGroup() == null ? ""
                                    : dbClient.getClientGroup().getGroupName()), logBuffer);
                    addClientChange(em, ts, org.getIdOfOrg(), dbClient, DELETE_OPERATION,
                            RegistryChange.FULL_COMPARISON, org.getChangesDSZN());
                }
            } catch (Exception e) {
                logError("Failed to delete client " + dbClient, e, logBuffer);
            }
        }

        Map<Long, CategoryDiscount> categoryMap = getCategoriesMap(session);
        Map<Integer, CategoryDiscountDSZN> categoryDSZNMap = getCategoriesDSZNMap(session);

        //  Проходим по ответу от Реестров и анализируем надо ли обновлять его или нет
        for (Pupil pupil : pupils) {
            FieldProcessor.Config fieldConfig;
            boolean updateClient = false;
            Client cl = guidMap.get(emptyIfNull(pupil.getSuid()));
            if (cl == null) {
                fieldConfig = new ClientManager.ClientFieldConfig();
            } else {
                if (cl.getClientGroup() != null &&
                    cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup() >= ClientGroup
                        .Predefined.CLIENT_EMPLOYEES.getValue()
                        && cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                        < ClientGroup.Predefined.CLIENT_LEAVING.getValue()) {
                    continue;
                }
                fieldConfig = new ClientManager.ClientFieldConfigForUpdate();
            }
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.CLIENT_GUID, pupil.getSuid(),
                    cl == null ? null : cl.getClientGUID(), updateClient);
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.SURNAME, pupil.getSurname(),
                    cl == null ? null : cl.getPerson().getSurname(), updateClient);
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.NAME, pupil.getName(),
                    cl == null ? null : cl.getPerson().getFirstName(), updateClient);
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.SECONDNAME, pupil.getPatronymic(),
                    cl == null ? null : cl.getPerson().getSecondName(), updateClient);

            if(org.getChangesDSZN()) {
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.BENEFIT_DSZN, pupil.getBenefit() == null ? null :
                        pupil.getBenefit().getCode(), cl == null ? null : cl.getCategoriesDSZN().size() == 0 ? null : DiscountManager.getClientDiscountsDSZNAsString(cl),
                        updateClient);
                if(!updateClient) {
                    updateClient = doCategoriesUpdate(getCategoriesString(pupil.getBenefit().getCode(), cl == null ? null : DiscountManager.getClientDiscountsAsString(cl),
                                    categoryMap, categoryDSZNMap), cl == null ? null : DiscountManager.getClientDiscountsAsString(cl));
                }
            }

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
            updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.BIRTH_DATE, pupil.getDob(),
                    cl == null ? null : cl.getBirthDate() == null ? null : timeFormat.format(cl.getBirthDate()), updateClient);

            if (pupil.getClazz() != null) {
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.GROUP, pupil.getClazz(),
                        cl == null || cl.getClientGroup() == null ? null : cl.getClientGroup().getGroupName(),
                        updateClient);
            } else {
                //  Если группа у клиента не указана, то перемещаем его в Другие
                updateClient = doClientUpdate(fieldConfig, ClientManager.FieldId.GROUP,
                        ClientGroup.Predefined.CLIENT_OTHERS.getNameOfGroup(),
                        cl == null || cl.getClientGroup() == null ? null : cl.getClientGroup().getGroupName(),
                        updateClient);
            }

            if (cl != null && !cl.getOrg().getGuid().equals(org.getGuid())) {
                log(synchDate + "Перевод " + emptyIfNull(cl.getClientGUID()) + ", " +
                        emptyIfNull(cl.getPerson() == null ? "" : cl.getPerson().getSurname()) + " " +
                        emptyIfNull(cl.getPerson() == null ? "" : cl.getPerson().getFirstName()) + " " +
                        emptyIfNull(cl.getPerson() == null ? "" : cl.getPerson().getSecondName()) + ", " +
                        emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName())
                        + " из школы " + cl.getOrg().getIdOfOrg() + " в школу " + org.getIdOfOrg(), logBuffer);
                addClientChange(ts, org.getIdOfOrg(), org.getIdOfOrg(), fieldConfig, cl, MOVE_OPERATION,
                        RegistryChange.FULL_COMPARISON, org.getChangesDSZN());
                continue;
            }

            if (!updateClient) {
                continue;
            }

            try {
                //  Если клиента по GUID найти не удалось, это значит что он новый - добавляем его
                if (cl == null) {
                    try {
                        log(synchDate + "Добавление " + pupil.getSuid() + ", " +
                                pupil.getSurname() + " " + pupil.getName() + " " +
                                pupil.getPatronymic() + ", " + pupil.getClazz(), logBuffer);
                        addClientChange(ts, org.getIdOfOrg(), null, fieldConfig, null, CREATE_OPERATION,
                                RegistryChange.FULL_COMPARISON, org.getChangesDSZN());
                    } catch (Exception e) {
                        logError("Ошибка добавления клиента", e, logBuffer);
                    }
                } else {
                    log(synchDate + "Изменение " +
                            emptyIfNull(cl.getClientGUID()) + ", " + emptyIfNull(cl.getPerson().getSurname()) + " " +
                            emptyIfNull(cl.getPerson().getFirstName()) + " " + emptyIfNull(
                            cl.getPerson().getSecondName()) + ", " +
                            emptyIfNull(cl.getClientGroup() == null ? "" : cl.getClientGroup().getGroupName()) + " на "
                            +
                            emptyIfNull(pupil.getSuid()) + ", " + emptyIfNull(pupil.getSurname()) + " "
                            + emptyIfNull(pupil.getName()) + " " +
                            emptyIfNull(pupil.getPatronymic()) + ", " + emptyIfNull(pupil.getClazz()), logBuffer);
                    addClientChange(ts, org.getIdOfOrg(), null, fieldConfig, cl, MODIFY_OPERATION,
                            RegistryChange.FULL_COMPARISON, org.getChangesDSZN());
                }
            } catch (Exception e) {
                logError("Failed to add client for " + org.getIdOfOrg() + " org", e, logBuffer);
            }
        }
        log(synchDate + "Синхронизация завершена для " + org.getOfficialName(), logBuffer);
    }

    private boolean isDirectionTypeOut(Pupil pupil) {
        boolean result = false;
        for(String event : pupil.getEvent().getDirectionType()) {
            if(event.equals(OUT)) {
                result = true;
            }
        }
        return result;
    }

    public void addClientChange(long ts, long idOfOrg, Long idOfMigrateOrg,
            FieldProcessor.Config fieldConfig, Client currentClient, int operation, int type, Boolean checkBenefits)
            throws Exception {
        //  ДОБАВИТЬ ЗАПИСЬ ОБ ИЗМЕНЕНИИ ПОЛЬЗОВАТЕЛЯ И УКАЗАТЬ СООТВЕТСТВУЮЩУЮ ОПЕРАЦИЮ
        Session sess = (Session) em.getDelegate();
        if (currentClient != null) {
            currentClient = em.merge(currentClient);
        }

        String clientGuid = emptyIfNull(fieldConfig.getValue(ClientManager.FieldId.CLIENT_GUID));
        String name = trim(fieldConfig.getValue(ClientManager.FieldId.NAME), 64, clientGuid, "Имя ученика");
        String secondname = trim(fieldConfig.getValue(ClientManager.FieldId.SECONDNAME), 128, clientGuid, "Отчество ученика");
        String surname = trim(fieldConfig.getValue(ClientManager.FieldId.SURNAME), 128, clientGuid, "Фамилия ученика");
        String registryGroupName = trim(fieldConfig.getValue(ClientManager.FieldId.GROUP), 64, clientGuid, "Наименование группы");
        String clientBenefitDSZN = trim(fieldConfig.getValue(ClientManager.FieldId.BENEFIT_DSZN), 128, clientGuid, "Льгота учащегося");
        String clientBirthDate = trim(fieldConfig.getValue(ClientManager.FieldId.BIRTH_DATE), 64, clientGuid, "Дата рождения");

        RegistryChange ch = new RegistryChange();
        ch.setClientGUID(clientGuid);
        ch.setFirstName(name);
        ch.setSecondName(secondname);
        ch.setSurname(surname);
        ch.setGroupName(registryGroupName);
        ch.setIdOfClient(currentClient == null ? null : currentClient.getIdOfClient());
        ch.setIdOfOrg(idOfOrg);
        ch.setOperation(operation);
        ch.setCreateDate(ts);
        ch.setApplied(false);
        ch.setType(type);

        ch.setCheckBenefits(checkBenefits);
        if(checkBenefits) {
            ch.setBenefitDSZN(clientBenefitDSZN);
            ch.setNewDiscounts(StringUtils.join(getCategoriesByDSZNCodes(sess, clientBenefitDSZN,
                    currentClient != null ? DiscountManager.getClientDiscountsAsString(currentClient) : ""), ","));
            if(currentClient != null) {
                ch.setBenefitDSZNFrom(DiscountManager.getClientDiscountsDSZNAsString(currentClient));
                ch.setOldDiscounts(StringUtils.isEmpty(DiscountManager.getClientDiscountsAsString(currentClient)) ? "" :
                        StringUtils.join(new TreeSet<String>(Arrays.asList(DiscountManager.getClientDiscountsAsString(currentClient).split(","))), ","));
            }
        }


        if (clientBirthDate != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            if(StringUtils.isNotEmpty(clientBirthDate)) {
                Date date = format.parse(clientBirthDate);
                ch.setBirthDate(date.getTime());
            }
        }

        if (operation == MOVE_OPERATION) {
            ch.setIdOfMigrateOrgFrom(currentClient.getOrg().getIdOfOrg());
            ch.setIdOfMigrateOrgTo(idOfMigrateOrg);
            ClientGroup currentGroup = currentClient.getClientGroup();
            if (currentGroup != null) {
                currentGroup = em.merge(currentGroup);
                ch.setGroupNameFrom(currentGroup.getGroupName());
            } else {
                ch.setGroupNameFrom("");
            }

            ch.setGenderFrom(currentClient.getGender());
            if (currentClient.getBirthDate() != null) {
                ch.setBirthDateFrom(currentClient.getBirthDate().getTime());
            }
            ch.setAgeTypeGroupFrom(currentClient.getAgeTypeGroup());
        }
        if (operation == MODIFY_OPERATION) {
            ClientGroup currentGroup = currentClient.getClientGroup();
            if (currentGroup != null) {
                currentGroup = em.merge(currentGroup);
                ch.setGroupNameFrom(currentGroup.getGroupName());
            } else {
                ch.setGroupNameFrom("");
            }
            ch.setFirstNameFrom(currentClient.getPerson().getFirstName());
            ch.setSecondNameFrom(currentClient.getPerson().getSecondName());
            ch.setSurnameFrom(currentClient.getPerson().getSurname());
            if (currentClient.getBirthDate() != null) {
                ch.setBirthDateFrom(currentClient.getBirthDate().getTime());
            }
            ch.setBenefitDSZNFrom(DiscountManager.getClientDiscountsDSZNAsString(currentClient));
            ch.setOldDiscounts(currentClient.getCategories().size() == 0 ? "" :
                    StringUtils.join(new TreeSet<String>(Arrays.asList(DiscountManager.getClientDiscountsAsString(currentClient).split(","))), ","));
        }
        sess.save(ch);
    }

    public static void addClientChange(EntityManager em, long ts, long idOfOrg, Client currentClient, int operation,
            int type, Boolean checkBenefits) throws Exception {
        Session sess = (Session) em.getDelegate();
        if (currentClient != null) {
            currentClient = em.merge(currentClient);
        }

        RegistryChange ch = new RegistryChange();
        ch.setClientGUID(emptyIfNull(currentClient.getClientGUID()));
        ch.setFirstName(currentClient.getPerson().getFirstName());
        ch.setSecondName(currentClient.getPerson().getSecondName());
        ch.setSurname(currentClient.getPerson().getSurname());
        ch.setGroupName(currentClient.getClientGroup() == null ? "" : currentClient.getClientGroup().getGroupName());
        ch.setIdOfClient(currentClient.getIdOfClient());
        ch.setIdOfOrg(idOfOrg);
        ch.setOperation(operation);
        ch.setType(type);
        ch.setCreateDate(ts);
        ch.setApplied(false);
        ch.setGender(currentClient.getGender());
        if (currentClient.getBirthDate() != null) {
            ch.setBirthDate(currentClient.getBirthDate().getTime());
        }
        ch.setCheckBenefits(checkBenefits);
        if(checkBenefits) {
            ch.setBenefitDSZN(DiscountManager.getClientDiscountsDSZNAsString(currentClient));
            ch.setNewDiscounts(DiscountManager.getClientDiscountsAsString(currentClient));
        }
        sess.save(ch);
    }

    private static Set<Long> getCategoriesByDSZNCodes(Session session, String clientBenefitDSZN, String oldDiscounts) {
        Set<Long> newDiscountsIds = new TreeSet<Long>();
        List<Long> oldDiscountsIds = new ArrayList<Long>();
        for(String o : oldDiscounts.split(",")) {
            if(StringUtils.isNotEmpty(o)) {
                oldDiscountsIds.add(Long.parseLong(o));
            }
        }
        if(oldDiscountsIds.size() > 0) {
            Collections.sort(oldDiscountsIds);
            Criteria criteria = session.createCriteria(CategoryDiscount.class);
            criteria.add(Restrictions.in("idOfCategoryDiscount", oldDiscountsIds));
            List<CategoryDiscount> list = criteria.list();
            for (CategoryDiscount categoryDiscount : list) {
                if (!(categoryDiscount.getCategoriesDiscountDSZN().size() > 0)) {
                    newDiscountsIds.add(categoryDiscount.getIdOfCategoryDiscount());
                }
            }
        }

        if(StringUtils.isNotEmpty(clientBenefitDSZN)) {
            List<Integer> benefitsList = new ArrayList<Integer>();
            for(String s : clientBenefitDSZN.split(",")) {
                if(StringUtils.isNotEmpty(s)) {
                    benefitsList.add(Integer.valueOf(s));
                }
            }
            Criteria criteria1 = session.createCriteria(CategoryDiscountDSZN.class);
            criteria1.add(Restrictions.in("code", benefitsList));
            criteria1.add(Restrictions.eq("deleted", false));
            List<CategoryDiscountDSZN> cdDSZN = criteria1.list();
            for(CategoryDiscountDSZN discountDSZN : cdDSZN) {
                if(discountDSZN.getCategoryDiscount() != null) {
                    newDiscountsIds.add(discountDSZN.getCategoryDiscount().getIdOfCategoryDiscount());
                }
            }
        }
        return newDiscountsIds;
    }

    public static String getCategoriesString(String categoriesDSZN, String clientCategories,
            Map<Long, CategoryDiscount> categoryMap, Map<Integer, CategoryDiscountDSZN> categoryDSZNMap) {
        List<Long> categoriesList = new ArrayList<Long>();
        for(String c : clientCategories.split(",")) {
            if(StringUtils.isNotEmpty(c)) {
                categoriesList.add(Long.valueOf(c));
            }
        }
        List<Integer> categoriesDSZNList = new ArrayList<Integer>();
        for(String c : categoriesDSZN.split(",")) {
            if(StringUtils.isNotEmpty(c)) {
                categoriesDSZNList.add(Integer.valueOf(c));
            }
        }

        Set<Long> resultCategories = new TreeSet<Long>();
        for(Long c : categoriesList) {
            if(categoryMap.get(c) != null && !(categoryMap.get(c).getCategoriesDiscountDSZN().size() > 0)) {
                resultCategories.add(c);
            }
        }

        for(Integer c : categoriesDSZNList) {
            if(categoryDSZNMap.get(c) != null && categoryDSZNMap.get(c).getCategoryDiscount() != null) {
                resultCategories.add(categoryDSZNMap.get(c).getCategoryDiscount().getIdOfCategoryDiscount());
            }
        }

        return StringUtils.join(resultCategories, ",");
    }

    public static boolean doCategoriesUpdate(String newCategories, String oldCategories) {
        if(StringUtils.isEmpty(newCategories) && StringUtils.isEmpty(oldCategories)) {
            return false;
        }
        Set<String> newCategoriesSet = new HashSet<String>(Arrays.asList(newCategories.split(",")));
        Set<String> oldCategoriesSet = new HashSet<String>(Arrays.asList(oldCategories.split(",")));
        return !newCategoriesSet.equals(oldCategoriesSet);
    }

    @SuppressWarnings("unchecked")
    public static Map<Long, CategoryDiscount> getCategoriesMap(Session session) {
        Map<Long, CategoryDiscount> result = new HashMap<Long, CategoryDiscount>();
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        List<CategoryDiscount> list = criteria.list();
        for(CategoryDiscount categoryDiscount : list) {
            result.put(categoryDiscount.getIdOfCategoryDiscount(), categoryDiscount);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, CategoryDiscountDSZN> getCategoriesDSZNMap(Session session) {
        Map<Integer, CategoryDiscountDSZN> result = new HashMap<Integer, CategoryDiscountDSZN>();
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        List<CategoryDiscountDSZN> list = criteria.list();
        for(CategoryDiscountDSZN categoryDiscountDSZN : list) {
            result.put(categoryDiscountDSZN.getCode(), categoryDiscountDSZN);
        }
        return result;
    }

    protected static String trim(String source, int maxLen, String clientGuid, String fieldName) {
        if(source != null && !StringUtils.isBlank(source) && source.length() > maxLen) {
            String replace = source.substring(0, maxLen - 1);
            logger.error(String.format("ВНИМАНИЕ! %s (%s) ученика (%s) в Реестрах "
                    + "слишком длинное, будет применено ограничение на длинну поля "
                    + "%s (%s)", fieldName, source, clientGuid, fieldName, replace));
            return replace;
        }
        return source;
    }

    public RegistryChange getRegistryChange(Long idOfRegistryChange) {
        if (idOfRegistryChange == null) {
            return null;
        }
        RegistryChange change = em.find(RegistryChange.class, idOfRegistryChange);
        return change;
    }

    @Override
    public RegistryChangeError getRegistryChangeError(Long idOfRegistryChangeError) {
        if (idOfRegistryChangeError == null) {
            return null;
        }
        RegistryChangeError e = em.find(RegistryChangeError.class, idOfRegistryChangeError);
        return e;
    }

    public static void clientGroupProcess(Session session, Client dbClient, GroupNamesToOrgs groupNamesToOrgs)
            throws Exception {
        ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrgNotIgnoreCase(session,
                groupNamesToOrgs.getIdOfOrg(), groupNamesToOrgs.getGroupName());
        if (clientGroup == null) {
            clientGroup = DAOUtils.createClientGroup(session, groupNamesToOrgs.getIdOfOrg(),
                    groupNamesToOrgs.getGroupName());
        }
        dbClient.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        Org org = (Org) session.load(Org.class, groupNamesToOrgs.getIdOfOrg());
        dbClient.setOrg(org);
    }

    public static void commentsAddsDelete(Client dbClient, String deleteCommentsAdds) {
        if (deleteCommentsAdds != null && deleteCommentsAdds.length() > 0) {
            String comments = dbClient.getRemarks();
            if (comments==null) comments="";
            if (comments.indexOf("{%") > -1) {
                comments = comments.substring(0, comments.indexOf("{%")) + comments
                        .substring(comments.indexOf("%}") + 1);
            }
            comments += deleteCommentsAdds;
            if (comments.length() >= 1024) {
                comments = comments.replaceAll(MskNSIService.REPLACEMENT_REGEXP, "");
            }

            dbClient.setRemarks(comments);
        }
    }

    //@Transactional
    private void addClientMigrationEntry(Session session,Org oldOrg, Org newOrg,  ClientGroup beforeMigrationGroup,
            Client client, RegistryChange change){
        ClientManager.checkUserOPFlag(session, oldOrg, newOrg, client.getIdOfClientGroup(), client);
        ClientMigration migration = new ClientMigration(client, newOrg, oldOrg);
        migration.setComment(ClientMigration.MODIFY_IN_REGISTRY.concat(String.format(" (ид. ОО=%s)", change.getIdOfOrg())));
        if(beforeMigrationGroup != null) {
            migration.setOldGroupName(beforeMigrationGroup.getGroupName());
        }
        migration.setNewGroupName(change.getGroupName());
        session.save(migration);
    }

    //@Transactional
    private void addClientGroupMigrationEntry(Session session,Org org, Client client, RegistryChange change){
        ClientManager.createClientGroupMigrationHistory(session, client, org, client.getIdOfClientGroup(),
                change.getGroupName(), ClientGroupMigrationHistory.MODIFY_IN_REGISTRY.concat(String.format(" (ид. ОО=%s)", change.getIdOfOrg())));
    }

    @Transactional
    public void setChangeError(long idOfRegistryChange, Exception e) throws Exception {
        RegistryChange change = em.find(RegistryChange.class, idOfRegistryChange);
        Session session = null;
        try {
            session = (Session) em.getDelegate();
        } catch (Exception ex) {
            logger.error("Failed to craete session", e);
            throw ex;
        }
        String err = e.getMessage();
        if (err != null && err.length() > 255) {
            err = err.substring(0, 255).trim();
        }
        change.setError(err);
        session.update(change);
    }

    public static boolean doClientUpdate(FieldProcessor.Config fieldConfig, Object fieldID, String reesterValue,
            String currentValue, boolean doClientUpdate) throws Exception {
        reesterValue = emptyIfNull(reesterValue);
        currentValue = emptyIfNull(currentValue);
        fieldConfig.setValue(fieldID, reesterValue);
        return doClientUpdate || !currentValue.trim().equals(reesterValue.trim());
    }

    private static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    public static void log(String str, StringBuffer logBuffer) {
        if (logBuffer != null) {
            logBuffer.append(str).append('\n');
        }
        if (RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_MSK_NSI_LOG)) {
            logger.info(str);
        }
    }

    public static void logError(String str, Exception e, StringBuffer logBuffer) {
        if (logBuffer != null) {
            logBuffer.append(str).append(": ").append(e.getMessage());
        }
        if (e != null) {
            logger.error(str, e);
        } else {
            logger.error(str);
        }
    }

    public static int createSpbCards(Long idOfOrg) throws Exception {
        Session session = null;
        Transaction transaction = null;
        int counter = 0;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            List<Long> orgs = DAOUtils.findFriendlyOrgIds(session, idOfOrg);
            for (Long id : orgs) {
                Org org = DAOUtils.findOrg(session, id);
                if (!org.getAutoCreateCards()) {
                    throw new Exception(String.format("У организации c идентификатором = %s не включен флаг автогенерации карт!", id));
                }
            }

            Criteria criteria = session.createCriteria(Client.class);
            criteria.add(Restrictions.in("org.idOfOrg", orgs));
            Disjunction groupDisjunction = Restrictions.disjunction();
            groupDisjunction.add(Restrictions.lt("idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()));
            groupDisjunction.add(Restrictions.gt("idOfClientGroup", ClientGroup.Predefined.CLIENT_DISPLACED.getValue()));
            criteria.add(groupDisjunction);
            List<Client> clients = criteria.list();

            Date issueTime = new Date();
            Date validTime = CalendarUtils.endOfDay(CalendarUtils.addYear(issueTime, 5));
            Long cardNo;
            boolean doCreate;

            for(Client client : clients) {
                if (StringUtils.isEmpty(client.getClientGUID())) continue;
                try {
                    cardNo = Long.parseLong(client.getClientGUID());
                } catch (Exception e) {
                    cardNo = null;
                }
                if (cardNo == null) continue;
                doCreate = true;
                for (Card card : client.getCards()) {
                    if (card.getState().equals(CardState.ISSUED.getValue())) {
                        if (card.getCardNo().equals(cardNo)) {
                            doCreate = false;
                            break;
                        }
                    }
                }
                if (doCreate) {
                    try {
                        CardManagerProcessor.lockActiveCards(session, client.getCards());
                        RuntimeContext.getInstance().getCardManager()
                                .createCardTransactionFree(session, client.getIdOfClient(), cardNo, Card.parseCardType(Card.TYPE_NAMES[1]),
                                        CardState.ISSUED.getValue(), validTime, Card.ISSUED_LIFE_STATE, "", issueTime, cardNo);
                    } catch (Exception e) {
                        logger.error("Error SPb cards creation for client " + client.getContractId(), e);
                        continue;
                    }
                    counter++;
                }
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error SPb cards creation: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return counter;
    }


}