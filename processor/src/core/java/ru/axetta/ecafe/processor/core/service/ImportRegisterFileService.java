/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.ClientMskNSIService;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgSync;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by baloun on 03.10.2017.
 */
@Component("ImportRegisterFileService")
@Scope("singleton")
public class ImportRegisterFileService extends ClientMskNSIService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterFileService.class);

    public final String FILENAME_PROPERTY = "ecafe.processor.nsi.registry.filename";
    public final String NODE_PROPERTY = "ecafe.processor.nsi.registry.node";
    public static final String MODE_PROPERTY = "ecafe.processor.nsi.registry.mode"; //допустимые значения: "file, service, symmetric, kafka" или отсутствие настройки
    public static final String MODE_FILE = "file";
    public static final String MODE_SYMMETRIC = "symmetric";
    public static final String MODE_KAFKA = "kafka";
    public static final String LEGAL_REPRESENTATIVE = "Законный представитель";

    protected final String INITIAL_INSERT_STATEMENT = "insert into cf_registry_file(guidofclient, "
            + "  guidoforg, firstname, secondname, surname, birthdate, gender, benefit, parallel, "
            + "  letter, clazz, currentclassorgroup, status, rep_firstname, rep_secondname, rep_surname, rep_phone, "
            + "  rep_who, agegrouptype, ekisId) values ";
    protected static final String REGEXP = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    protected Integer LINE_SIZE = 19;
    protected String TRUNCATE_STATEMENT = "truncate table cf_registry_file";
    protected String DROP_INDEX = "drop index if exists cf_registry_file_guidoforg_idx";
    protected String CREATE_INDEX = "create index cf_registry_file_guidoforg_idx on cf_registry_file using btree (guidoforg)";

    public void run() throws Exception {
        if (isOn()) {
            String mode = RuntimeContext.getInstance().getPropertiesValue(MODE_PROPERTY, null);
            if (mode.equals(MODE_FILE)) {
                loadNSIFile();
            }
            if (mode.equals(MODE_SYMMETRIC)) {
                RuntimeContext.getAppContext().getBean("ImportRegisterSymmetricService", ImportRegisterSymmetricService.class).loadClientsFromSymmetric();
            } else {
                logger.error("Не определен тип сверки по контингенту");
            }
        }
    }

    protected boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(getNodeProperty());
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void loadNSIFile() throws Exception {
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            String filename = RuntimeContext.getInstance().getPropertiesValue(getFilenameProperty(), null);
            if (filename == null) {
                throw new Exception(String.format("Not found property %s in application config", getFilenameProperty()));
            }
            File file = new File(filename);
            if (!file.exists()) {
                throw new Exception(String.format("Файл выгрузки от НСИ %s не найден", filename));
            }
            fileInputStream = new FileInputStream(filename); //"/home/jbosser/processor/Debugs/catalog-123.out");
            inputStreamReader = new InputStreamReader(fileInputStream, "windows-1251");
            bufferedReader = new BufferedReader(inputStreamReader);

            ClientMskNSIService service = RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class).getNSIService();
            service.fillTable(bufferedReader);

        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    protected String getInitialInsertStatement() {
        return INITIAL_INSERT_STATEMENT;
    }

    protected String getTruncateStatement() {
        return TRUNCATE_STATEMENT;
    }

    protected String getDropIndexStatement() {
        return DROP_INDEX;
    }

    protected String getCreateIndexStatement() {
        return CREATE_INDEX;
    }

    protected Integer getLineSizeValue() {
        return LINE_SIZE;
    }

    protected String getNodeProperty() {
        return NODE_PROPERTY;
    }

    protected String getFilenameProperty() {
        return FILENAME_PROPERTY;
    }

    protected org.slf4j.Logger getLogger() {
        return logger;
    }

    public void fillTable(BufferedReader bufferedReader) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            DAOService.getInstance().setSverkaEnabled(false);
            session = RuntimeContext.getInstance().createPersistenceSession();
            Query query = session.createSQLQuery(getTruncateStatement());
            query.executeUpdate();
            query = session.createSQLQuery(getDropIndexStatement());
            query.executeUpdate();

            String s;
            String str_query = getInitialInsertStatement();
            int counter = 0;
            getLogger().info("Start fill temp table");
            long begin = System.currentTimeMillis();
            int processed = 0;
            int errors = 0;
            String one_str = "";
            while((s = bufferedReader.readLine()) != null) {
                if (counter == 0 && !session.getTransaction().isActive()) transaction = session.beginTransaction();
                try {
                    String[] arr = s.split(REGEXP, -1);
                    while (arr.length < getLineSizeValue()) {
                        s += bufferedReader.readLine();
                        arr = s.split(REGEXP, -1);
                    }
                    one_str = buildOneInsertValue(arr);
                } catch (Exception e) {
                    errors++;
                    processed++;
                    getLogger().error(String.format("Error in process NSI file. Line %s ", processed), e);
                    continue;
                }
                str_query += "(" + one_str + "), ";
                counter++;
                if (counter == 1000) {
                    executeQuery(str_query, session, transaction, processed);
                    counter = 0;
                    str_query = getInitialInsertStatement();
                }
                processed++;
            }
            if (counter > 0) {
                executeQuery(str_query, session, transaction, processed);
            }
            transaction = null;

            query = session.createSQLQuery(getCreateIndexStatement());
            query.executeUpdate();

            getLogger().info(String.format("End fill temp table. Time taken %s ms, processed %s lines, error lines: %s", System.currentTimeMillis() - begin, processed, errors));
        } finally {
            HibernateUtils.rollback(transaction, getLogger());
            HibernateUtils.close(session, getLogger());
            DAOService.getInstance().setSverkaEnabled(true);
        }
    }

    protected void executeQuery(String str_query, Session session, Transaction transaction, int processed) {
        str_query = str_query.substring(0, str_query.length()-2);
        Query query = session.createSQLQuery(str_query);
        query.executeUpdate();
        transaction.commit();
        getLogger().info(String.format("Lines processed: %s", processed));
    }

    protected String buildOneInsertValue(String[] arr) {
        //0-Фамилия, 1-Имя, 2-Отчество, 3-Дата рождения, 4-Пол, 5-Льгота, 6-Параллель, 7-Буква, 8-Класс, 9-Текущий класс или группа
        //10-GUID, 11-GUID школы, 12-Статус записи, 13-Фамилия представителя, 14-Имя представителя, 15-Отчество представителя
        //16-Телефон представителя, 17-Представитель - кем приходится, 18-Тип возрастной группы, 19-ЕКИС ид здания

        StringBuilder sb = new StringBuilder();
        sb.append(getQuotedStr(arr[10])).append(", "); //guidofclient
        sb.append(getQuotedStr(arr[11])).append(", "); //guidoforg
        sb.append(getQuotedStr(arr[1])).append(", ");  //firstname
        sb.append(getQuotedStr(arr[2])).append(", ");  //secondname
        sb.append(getQuotedStr(arr[0])).append(", ");  //surname
        sb.append(getQuotedStr(arr[3])).append(", ");  //birthdate
        sb.append(getQuotedStr(arr[4])).append(", ");   //gender
        sb.append(getQuotedStr(arr[5])).append(", ");   //benefit
        sb.append(getQuotedStr(arr[6])).append(", ");   //parallel
        sb.append(getQuotedStr(arr[7])).append(", ");   //letter
        sb.append(getQuotedStr(arr[8])).append(", ");   //clazz
        sb.append(getQuotedStr(arr[9])).append(", ");   //currentclassorgroup
        sb.append(getQuotedStr(arr[12])).append(", ");  //status
        sb.append(getQuotedStr(arr[14])).append(", ");  //rep_firstname
        sb.append(getQuotedStr(arr[15])).append(", ");  //rep_secondname
        sb.append(getQuotedStr(arr[13])).append(", ");  //rep_surname
        sb.append(getQuotedStr(arr[16])).append(", ");  //rep_phone
        sb.append(getQuotedStr(arr[17])).append(", ");  //rep_who
        sb.append(getQuotedStr(arr[18])).append(", ");  //agegrouptype
        sb.append(getQuotedStr(arr[19]));               //ekisId
        return sb.toString();
    }

    protected String getQuotedStr(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length()-1);
        }
        return "'" + str.replaceAll("\"\"", "\"").replaceAll("'", "''") + "'";
    }

    /*private Integer getGender(String str) {
        if (StringUtils.isEmpty(str)) return 0;
        if (str.equals("Женский")) return 0;
        else return 1;
    }

    private String getGenderString(Integer value) {
        if (value == null) return "";
        if (value.equals(0)) return "Женский"; else return "Мужской";
    }*/

    @Override
    public String getBadGuids(ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids) throws Exception {
        List<String> list = new ArrayList<String>();
        Boolean guidOK;
        ImportRegisterMSKClientsService service = RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            for (String guid : orgGuids.getOrgGuids()) {
                //Проверка на существование гуида ОО в выгрузке
                Query query = session.createSQLQuery("select guidoforg from cf_registry_file where guidoforg = :guid limit 1");
                query.setParameter("guid", guid);
                try {
                    Object res = query.uniqueResult();
                    guidOK = (res != null);
                } catch (Exception e) {
                    guidOK = false;
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
            transaction.commit();
            transaction = null;
            if (list.size() == 0) return "";
            String badGuids = "Найдены следующие неактуальные идентификаторы организаций в НСИ:\n";
            for (String g : list) {
                badGuids += g;
            }
            return badGuids;
        } finally {
            HibernateUtils.rollback(transaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
    }

    protected void fillOrgGuids(Query query, ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids) {
        query.setParameterList("guids", orgGuids.getOrgGuids());
    }

    @Override
    public List<ImportRegisterMSKClientsService.ExpandedPupilInfo> getPupilsByOrgGUID(ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids,
            String familyName, String firstName, String secondName) throws Exception {
        List<ImportRegisterMSKClientsService.ExpandedPupilInfo> pupils = new ArrayList<ImportRegisterMSKClientsService.ExpandedPupilInfo>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            String fioCondition = (!StringUtils.isBlank(familyName) ? " and r.surname like :surname" : "") +
                    (!StringUtils.isBlank(firstName) ? " and r.firstname like :firstname" : "") +
                    (!StringUtils.isBlank(secondName) ? " and r.secondname like :secondname" : "");
            String str_query = getQueryString() + fioCondition;
            Query query = session.createSQLQuery(str_query);
            fillOrgGuids(query, orgGuids);
            if (!StringUtils.isBlank(familyName)) query.setParameter("surname", familyName);
            if (!StringUtils.isBlank(firstName)) query.setParameter("firstname", firstName);
            if (!StringUtils.isBlank(secondName)) query.setParameter("secondname", secondName);
            List list = query.list();
            transaction.commit();
            transaction = null;
            for (Object element : list) {
                ImportRegisterMSKClientsService.ExpandedPupilInfo pupil = new ImportRegisterMSKClientsService.ExpandedPupilInfo();
                Object[] row = (Object[])element;
                pupil.firstName = (String) row[2];
                pupil.secondName = (String) row[3];
                pupil.familyName = (String) row[4];
                pupil.guid = (String) row[0];
                pupil.birthDate = (String) row[5];
                pupil.parallel = (String) row[8];
                pupil.groupDeprecated = (String) row[11];
                pupil.groupNewWay = (String) row[10];
                pupil.guidOfOrg = (String) row[1];
                pupil.gender = (String) row[6];
                pupil.benefitDSZN = (String) row[7];
                pupil.ageTypeGroup = (String) row[13];
                try {
                    String[] guardians = ((String) row[14]).split("\\$");
                    for (String guardian : guardians) {
                        String[] arr = guardian.split("\\|");
                        if (!StringUtils.isEmpty(arr[2])) {
                            String validPhone = getValidPhone(arr[3]);
                            if (validPhone == null) continue;
                            ImportRegisterMSKClientsService.GuardianInfo guardianInfo = new ImportRegisterMSKClientsService.GuardianInfo();
                            guardianInfo.setFamilyName(arr[2]);
                            guardianInfo.setFirstName(arr[0]);
                            guardianInfo.setSecondName(arr[1]);
                            guardianInfo.setRelationship(arr[4]);
                            guardianInfo.setPhoneNumber(validPhone);
                            if (arr.length > 5 ) {
                                guardianInfo.setLegalRepresentative(arr[5].equals(LEGAL_REPRESENTATIVE));
                                guardianInfo.setSsoid(arr[6]);
                                guardianInfo.setGuid(arr[7]);
                            }
                            pupil.getGuardianInfoList().add(guardianInfo);
                        }
                    }
                } catch (Exception e) {
                    logger.info("Error when parse guardian info for people guid " + pupil.guid);
                }

                pupil.guardiansCount = String.valueOf(pupil.getGuardianInfoList().size());

                pupil.familyName = pupil.familyName == null ? null : pupil.familyName.trim();
                pupil.firstName = pupil.firstName == null ? null : pupil.firstName.trim();
                pupil.secondName = pupil.secondName == null ? null : pupil.secondName.trim();
                pupil.guid = pupil.guid == null ? null : pupil.guid.trim();
                pupil.group = !StringUtils.isEmpty(pupil.groupNewWay) ? pupil.groupNewWay : pupil.groupDeprecated;
                pupil.group = pupil.group == null ? null : pupil.group.trim();

                pupils.add(pupil);
            }
            /// удалить неимпортируемые группы
            for (Iterator<ImportRegisterMSKClientsService.ExpandedPupilInfo> i = pupils.iterator(); i.hasNext(); ) {
                ImportRegisterMSKClientsService.ExpandedPupilInfo p = i.next();
                if (ImportRegisterMSKClientsService.isPupilIgnoredFromImport(p.getGuid(), p.getGroup())) {
                    i.remove();
                }
            }

            return pupils;
        } finally {
            HibernateUtils.rollback(transaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
    }

    public String getValidPhone(String phones) {
        String result = "";
        if (phones == null) return null;
        String[] arr = phones.split(",");
        for (String phone : arr) {
            String ph = Client.checkAndConvertMobile(phone);
            if (ph == null) continue;
            if (ph.startsWith("7495") || ph.startsWith("7499")) continue;
            if (result.length() > 0) return null; //нашли второй моб. телефон - отбрасываем представителя
            result = ph;
        }
        return result.length() > 0 ? result : null;
    }

    protected String getQueryString() {
        return "SELECT guidofclient, "
                + "  guidoforg, "
                + "  firstname,"
                + "  secondname, "
                + "  surname, "
                + "  birthdate, " //5
                + "  gender, "
                + "  benefit, "
                + "  parallel, "
                + "  letter,"
                + "  clazz, "    //10
                + "  currentclassorgroup,"
                + "  status, "
                + "  agegrouptype, "
                + "  concat_ws('|', rep_firstname,  rep_secondname, rep_surname, rep_phone, rep_who, '', '', ''), "  //последние 3 поля - законный представитель, ссоид, гуид
                + "  cast(null as bigint) as ekisId "
                + "from cf_registry_file r where r.guidoforg in :guids";
    }
}
