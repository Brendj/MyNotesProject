/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.ClientMskNSIService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgSync;
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
import java.util.Set;

/**
 * Created by baloun on 03.10.2017.
 */
@Component("ImportRegisterFileService")
@Scope("singleton")
public class ImportRegisterFileService extends ClientMskNSIService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterFileService.class);

    public static final String FILENAME_PROPERTY = "ecafe.processor.nsi.registry.filename";
    public static final String NODE_PROPERTY = "ecafe.processor.nsi.registry.node";
    public static final String MODE_PROPERTY = "ecafe.processor.nsi.registry.mode"; //допустимые значения: "file, service" или отсутствие настройки

    private static final String INITIAL_INSERT_STATEMENT = "insert into cf_registry_file(guidofclient, "
            + "  guidoforg, firstname, secondname, surname, birthdate, gender, benefit, parallel, "
            + "  letter, clazz, currentclassorgroup, status, rep_firstname, rep_secondname, rep_surname, rep_phone, "
            + "  rep_who, agegrouptype) values ";
    private static final String REGEXP = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public void run() throws Exception {
        if (isOn()) {
            loadNSIFile();
        }
    }

    private boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(NODE_PROPERTY);
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public static boolean isFileMode() {
        String mode = RuntimeContext.getInstance().getPropertiesValue(MODE_PROPERTY, null);
        return (mode != null && mode.equals("file"));
    }

    public void loadNSIFile() throws Exception {
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            String filename = RuntimeContext.getInstance().getPropertiesValue(FILENAME_PROPERTY, null);
            if (filename == null) {
                throw new Exception(String.format("Not found property %s in application config", FILENAME_PROPERTY));
            }
            File file = new File(filename);
            if (!file.exists()) {
                throw new Exception(String.format("Файл выгрузки от НСИ %s не найден", filename));
            }
            fileInputStream = new FileInputStream(filename); //"/home/jbosser/processor/Debugs/catalog-123.out");
            inputStreamReader = new InputStreamReader(fileInputStream, "windows-1251");
            bufferedReader = new BufferedReader(inputStreamReader);

            fillTable(bufferedReader);

        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    public void fillTable(BufferedReader bufferedReader) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            DAOService.getInstance().setSverkaEnabled(false);
            session = RuntimeContext.getInstance().createPersistenceSession();
            Query query = session.createSQLQuery("truncate table cf_registry_file");
            query.executeUpdate();
            query = session.createSQLQuery("drop index if exists cf_registry_file_guidoforg_idx");
            query.executeUpdate();

            String s;
            String str_query = INITIAL_INSERT_STATEMENT;
            int counter = 0;
            logger.info("Start fill temp table");
            long begin = System.currentTimeMillis();
            int processed = 0;
            int errors = 0;
            String one_str = "";
            while((s = bufferedReader.readLine()) != null) {
                if (counter == 0 && !session.getTransaction().isActive()) transaction = session.beginTransaction();
                try {
                    String[] arr = s.split(REGEXP, -1);
                    while (arr.length < 19) {
                        s += bufferedReader.readLine();
                        arr = s.split(REGEXP, -1);
                    }
                    one_str = buildOneInsertValue(arr);
                } catch (Exception e) {
                    errors++;
                    processed++;
                    logger.error(String.format("Error in process NSI file. Line %s ", processed), e);
                    continue;
                }
                str_query += "(" + one_str + "), ";
                counter++;
                if (counter == 1000) {
                    str_query = str_query.substring(0, str_query.length()-2);
                    query = session.createSQLQuery(str_query);
                    query.executeUpdate();
                    transaction.commit();
                    counter = 0;
                    str_query = INITIAL_INSERT_STATEMENT;
                    logger.info(String.format("Lines processed: %s", processed));
                }
                processed++;
            }
            if (counter > 0) {
                str_query = str_query.substring(0, str_query.length()-2);
                query = session.createSQLQuery(str_query);
                query.executeUpdate();
                transaction.commit();
                logger.info(String.format("Lines processed: %s", processed));
            }
            transaction = null;

            query = session.createSQLQuery("create index cf_registry_file_guidoforg_idx on cf_registry_file using btree (guidoforg)");
            query.executeUpdate();

            logger.info(String.format("End fill temp table. Time taken %s ms, processed %s lines, error lines: %s", System.currentTimeMillis() - begin, processed, errors));
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
            DAOService.getInstance().setSverkaEnabled(true);
        }
    }

    private String buildOneInsertValue(String[] arr) {
        //0-Фамилия, 1-Имя, 2-Отчество, 3-Дата рождения, 4-Пол, 5-Льгота, 6-Параллель, 7-Буква, 8-Класс, 9-Текущий класс или группа
        //10-GUID, 11-GUID школы, 12-Статус записи, 13-Фамилия представителя, 14-Имя представителя, 15-Отчество представителя
        //16-Телефон представителя, 17-Представитель - кем приходится, 18-Тип возрастной группы

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
        sb.append(getQuotedStr(arr[18]));               //agegrouptype
        return sb.toString();
    }

    private String getQuotedStr(String str) {
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
    public List<String> getBadGuids(Set<String> orgGuids) throws Exception {
        List<String> result = new ArrayList<String>();
        Boolean guidOK;
        ImportRegisterClientsService service = RuntimeContext.getAppContext().getBean(ImportRegisterClientsService.class);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            for (String guid : orgGuids) {
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
                    List<Org> orgs = DAOService.getInstance().findOrgsByGuidAddressINNOrNumber(guid, "", "", "");
                    for (Org o : orgs) {
                        badGuidString += String.format("Guid: %s, Ид. организации: %s, Название организации: %s;\n", guid, o.getIdOfOrg(), o.getShortNameInfoService());
                    }
                    result.add(badGuidString);
                }
            }
            transaction.commit();
            transaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public List<ImportRegisterClientsService.ExpandedPupilInfo> getPupilsByOrgGUID(Set<String> orgGuids,
            String familyName, String firstName, String secondName) throws Exception {
        List<ImportRegisterClientsService.ExpandedPupilInfo> pupils = new ArrayList<ImportRegisterClientsService.ExpandedPupilInfo>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            String fioCondition = (!StringUtils.isBlank(familyName) ? " and r.surname like :surname" : "") +
                    (!StringUtils.isBlank(firstName) ? " and r.firstname like :firstname" : "") +
                    (!StringUtils.isBlank(secondName) ? " and r.secondname like :secondname" : "");
            String str_query = "SELECT guidofclient, "
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
                    + "  rep_firstname,"
                    + "  rep_secondname, "
                    + "  rep_surname,"   //15
                    + "  rep_phone, "
                    + "  rep_who,"
                    + "  agegrouptype "  //19
                    + "from cf_registry_file r where r.guidoforg in :guids" + fioCondition;
            Query query = session.createSQLQuery(str_query);
            query.setParameterList("guids", orgGuids);
            if (!StringUtils.isBlank(familyName)) query.setParameter("surname", familyName);
            if (!StringUtils.isBlank(firstName)) query.setParameter("firstname", firstName);
            if (!StringUtils.isBlank(secondName)) query.setParameter("secondname", secondName);
            List list = query.list();
            transaction.commit();
            transaction = null;
            for (Object element : list) {
                ImportRegisterClientsService.ExpandedPupilInfo pupil = new ImportRegisterClientsService.ExpandedPupilInfo();
                Object[] row = (Object[])element;
                pupil.firstName = (String) row[2];
                pupil.secondName = (String) row[3];
                pupil.familyName = (String) row[4];
                pupil.guid = (String) row[0];
                pupil.birthDate = (String) row[5]; //(row[5] == null) ? "" : row[5].toString();
                pupil.groupDeprecated = (String) row[11];
                pupil.groupNewWay = (String) row[10];
                pupil.guidOfOrg = (String) row[1];
                pupil.gender = (String) row[6];//row[6] == null ? "" : getGenderString((Integer)row[6]);
                if (!StringUtils.isEmpty((String) row[15])) {
                    ImportRegisterClientsService.GuardianInfo guardianInfo = new ImportRegisterClientsService.GuardianInfo();
                    guardianInfo.setFamilyName((String) row[15]);
                    guardianInfo.setFirstName((String) row[13]);
                    guardianInfo.setSecondName((String) row[14]);
                    guardianInfo.setRelationship((String) row[17]);
                    guardianInfo.setPhoneNumber((String) row[16]);
                    pupil.getGuardianInfoList().add(guardianInfo);
                }
                pupil.benefitDSZN = (String) row[7];
                pupil.ageTypeGroup = (String) row[18];
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
            for (Iterator<ImportRegisterClientsService.ExpandedPupilInfo> i = pupils.iterator(); i.hasNext(); ) {
                ImportRegisterClientsService.ExpandedPupilInfo p = i.next();
                if (ImportRegisterClientsService.isPupilIgnoredFromImport(p.getGuid(), p.getGroup())) {
                    i.remove();
                }
            }

            return pupils;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}