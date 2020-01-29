/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.OrgSymmetricDAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ImportRegisterSymmetricService")
@Scope("singleton")
public class ImportRegisterSymmetricService extends ImportRegisterFileService {
    private static final Logger logger = LoggerFactory.getLogger(ImportRegisterSymmetricService.class);
    private final int LIMIT = 100000;

    protected String TRUNCATE_STATEMENT_GUARDIANS = "truncate table cf_registry_file_guardians";
    protected String DROP_INDEX_GUARDIANS = "drop index if exists cf_registry_file_guardians_guidofclient_idx";
    protected String CREATE_INDEX_GUARDIANS = "create index cf_registry_file_guardians_guidofclient_idx on cf_registry_file_guardians using btree (guidofclient)";
    protected final String INITIAL_INSERT_STATEMENT_GUARDIANS = "insert into cf_registry_file_guardians(guidofclient, "
            + "  rep_firstname, rep_secondname, rep_surname, rep_phone, rep_who, rep_legal_representative, rep_ssoid, rep_guid, rep_gender) values ";

    protected org.slf4j.Logger getLogger() {
        return logger;
    }

    public void loadClientsFromSymmetric() {
        Session session = null;
        Transaction transaction = null;
        try {
            DAOService.getInstance().setSverkaEnabled(false);
            session = RuntimeContext.getInstance().createPersistenceSession();
            session.setFlushMode(FlushMode.COMMIT);
            transaction = session.beginTransaction();

            Query query = session.createSQLQuery(getTruncateStatement());
            query.executeUpdate();
            query = session.createSQLQuery(getDropIndexStatement());
            query.executeUpdate();
            transaction.commit();

            String str_query = getInitialInsertStatement();
            int counter = 0;
            getLogger().info("Start fill cf_registry_file table");
            long begin = System.currentTimeMillis();
            int processed = 0;
            int errors = 0;
            String one_str = "";

            int offset = 0;
            while (true) {
                getLogger().info("Request to symmetric database with offset=" + offset);
                String symmetricQueryPeopleString = "select p.last_name, "   //0
                        + "p.first_name, "                                   //1
                        + "p.middle_name, "                                  //2
                        + "TO_CHAR(p.birthday, 'DD.MM.YYYY') as birthday, "  //3
                        + "cast(p.id as char(36)) as pid, "                  //4
                        + "dg.gender_name, "                                 //5
                        + "ig.parallel, "                                    //6
                        + "ig.letter, "                                      //7
                        + "ig.group_name, "                                  //8
                        + "ig.age_group, "                                   //9
                        + "upper(cast(i.id as char(36))) as iid, "           //10
                        + "TO_CHAR(pig.date_begin, 'DD.MM.YYYY') as date_begin " //11
                        + "from (" + "select distinct i.id from institutions i inner join institutions_buildings ib on ib.institution_id = i.id "
                        + "where ib.pp_status not in ('','Внедрение ИС ПП отложено','Подключение к ИС ПП не планируется') and ib.deleted_at is null and i.deleted_at is null "
                        + ") as i " + "inner join institution_groups ig on i.id=ig.institution_guid inner join person_institution_groups pig on ig.id=pig.institution_group_guid "
                        + "inner join students s on pig.student_guid=s.id inner join persons p on p.id=s.person_id "
                        + "left outer join dict_genders dg on p.gender_guid=dg.id where (not p.deleted or p.deleted is null) "
                        + "and s.created_by in ('ou','pt') and s.deleted_at is NULL and ig.deleted_at is null and pig.deleted_at is NULL "
                        + "limit " + LIMIT + " offset " + offset;

                List list = RuntimeContext.getAppContext().getBean(OrgSymmetricDAOService.class).getQueryResult(symmetricQueryPeopleString);

                String[] arr = new String[19];

                //0-Фамилия, 1-Имя, 2-Отчество, 3-Дата рождения, 4-Пол, 5-Льгота, 6-Параллель, 7-Буква, 8-Класс, 9-Текущий класс или группа
                //10-GUID, 11-GUID школы, 12-Статус записи, 13-Фамилия представителя, 14-Имя представителя, 15-Отчество представителя
                //16-Телефон представителя, 17-Представитель - кем приходится, 18-Тип возрастной группы
                for (Object obj : list) {
                    if (counter == 0 && !session.getTransaction().isActive())
                        transaction = session.beginTransaction();
                    try {
                        Object[] row = (Object[]) obj;
                        arr[0] = getValueNullSafe((String) row[0]); //фамилия
                        arr[1] = getValueNullSafe((String) row[1]); //имя
                        arr[2] = getValueNullSafe((String) row[2]); //отчество
                        arr[3] = getValueNullSafe((String) row[3]); //дата рождения
                        arr[4] = getValueNullSafe((String) row[5]); //пол
                        arr[5] = "";                                //льгота
                        arr[6] = getValueNullSafe((String) row[6]); //параллель
                        arr[7] = getValueNullSafe((String) row[7]); //буква
                        arr[8] = getValueNullSafe((String) row[8]); //класс
                        arr[9] = getValueNullSafe((String) row[8]); //текущий класс или группа
                        arr[10] = (String) row[4];                  //гуид
                        arr[11] = getValueNullSafe((String) row[10]); //гуид школы
                        arr[12] = "Новая запись"; //статус записи
                        arr[13] = ""; //фамилия представителя
                        arr[14] = ""; //имя представителя
                        arr[15] = ""; //отчество представителя
                        arr[16] = ""; //телефон представителя
                        arr[17] = ""; //представитель кем приходится
                        arr[18] = getValueNullSafe((String) row[9]); //тип возрастной группы
                        one_str = buildOneInsertValue(arr);

                        str_query += "(" + one_str + "), ";
                        counter++;

                        if (counter == 1000) {
                            executeQuery(str_query, session, transaction, processed);
                            counter = 0;
                            str_query = getInitialInsertStatement();
                        }
                        processed++;
                    } catch (Exception e) {
                        errors++;
                        processed++;
                        getLogger()
                                .error(String.format("Error in loading data from Symmetric. Record %s ", processed), e);
                        continue;
                    }
                }
                if (counter > 0) {
                    executeQuery(str_query, session, transaction, processed);
                }
                if (list.size() < LIMIT) break;
                offset += LIMIT;
            }

            transaction = session.beginTransaction();
            query = session.createSQLQuery(getCreateIndexStatement());
            query.executeUpdate();
            transaction.commit();
            getLogger().info(String.format("End fill cf_registry_file table. Time taken %s ms, processed %s lines, error lines: %s", System.currentTimeMillis() - begin, processed, errors));

            ///////////////////// Дальше прием представителей \\\\\\\\\\\\\\\\\\\\\\

            getLogger().info("Start fill cf_registry_file_guardians table");
            transaction = session.beginTransaction();

            query = session.createSQLQuery(getTruncateStatementGuardians());
            query.executeUpdate();
            query = session.createSQLQuery(getDropIndexStatementGuardians());
            query.executeUpdate();
            transaction.commit();

            String subquery = "SELECT cast(id as char(36)) FROM dict_relations_status WHERE relat_status != 'Аннулировано' AND created_by = 'ou' AND deleted_at IS NULL";
            List<String> listSubquery = RuntimeContext.getAppContext().getBean(OrgSymmetricDAOService.class).getQueryResult(subquery);
            String sub = "";
            for (String obj : listSubquery) {
                //Object[] row = (Object[])obj;
                sub += getQuotedStr(obj) + ",";
            }
            sub = sub.substring(0, sub.length()-1);

            String symmetricQueryGuardiansString = "select cast(pd.student_id as char(36)) as student_id, "  //0
                    + "cast(p.id as char(36)) as id, "                     //1
                    + "max(p.last_name) as last_name, "                    //2
                    + "max(p.first_name) as first_name, "                  //3
                    + "max(p.middle_name) as middle_name, "                //4
                    + "max(p.birthday) as birthday, "                      //5
                    + "max(g.gender_name) as gender_name, "                //6
                    + "cast(max(dr.local_id) as char(36)) as local_id, "   //7
                    + "max(dr.role_name) as role_name, "                   //8
                    + "max(dlg.delegation_type) as delegation_type, "      //9
                    + "max(cn.contact_value) as contact_value, "           //10
                    + "max(pf.sso_id) as sso_id "                          //11
                    + "from persons p "
                    + "inner join person_delegation pd on p.id=pd.delegate_id and pd.created_by ='ou' and pd.deleted_at is null "
                    + "inner join persons p1 on pd.student_id=p1.id and p1.deleted is not true and p1.created_by ='ou' "
                    + "inner join dict_roles dr on pd.role_id=dr.id and dr.created_by='ou' and dr.deleted_at is null "
                    + "INNER JOIN person_contacts cn ON cn.person_guid=p.id AND cn.deleted_at IS NULL AND cn.created_by='ou' AND cn.contact_value IS NOT NULL "
                    + "inner join (select person_guid, "
                    + "    max(coalesce(updated_at, created_at)) ctdt  "
                    + "    from person_contacts where  deleted_at is null and created_by='ou' and contact_value is not null "
                    + "    group by person_guid "
                    + ") cn2 on cn2.person_guid=cn.person_guid and cn2.ctdt = cn.updated_at "
                    + "inner join dict_contact_types dct on dct.id=cn.contact_type_guid and dct.contact_type in ('phone') "
                    + "left outer join dict_delegation_types dlg on pd.delegation_type_id=dlg.id and dlg.created_by='ou' and dlg.deleted_at is null "
                    + "left outer join person_profiles pf on pf.person_guid=p.id and pf.deleted_at is null "
                    + "left outer join dict_genders g on g.id=p.gender_guid "
                    + "where pd.status_id IN ("
                    + sub
                    + ") "
                    + "group by pd.student_id, p.id";
            List listGuardians = RuntimeContext.getAppContext().getBean(OrgSymmetricDAOService.class).getQueryResult(symmetricQueryGuardiansString);
            getLogger().info("List guardians records count: " + listGuardians.size());
            str_query = getInitialInsertStatementGuardians();
            counter = 0;
            processed = 0;
            errors = 0;
            String[] arr = new String[10];
            //0 - гуид клиента, 1 - имя представителя, 2 - отчество представителя, 3 - фамилия представителя, 4 - телефон представителя,
            //5 - кем является, 6 - законный предавитель, 7 - ssoid представителя, 8 - гуид клиента-представителя, 9 - пол представителя
            for (Object obj : listGuardians) {
                if (counter == 0 && !session.getTransaction().isActive()) transaction = session.beginTransaction();
                try {
                    Object[] row = (Object[]) obj;
                    arr[0] = (String) row[0];  //гуид клиента
                    arr[1] = getValueNullSafe((String) row[3]);  //имя
                    arr[2] = getValueNullSafe((String) row[4]);  //отчество
                    arr[3] = getValueNullSafe((String) row[2]);  //фамилия
                    arr[4] = getValueNullSafe((String) row[10]); //телефон
                    arr[5] = getValueNullSafe((String) row[8]);  //кем является
                    arr[6] = getValueNullSafe((String) row[9]);  //законный представитель
                    arr[7] = getValueNullSafe((String) row[11]); //ссоид
                    arr[8] = (String) row[1];  //гуид представителя
                    arr[9] = getValueNullSafe((String) row[6]);  //пол представителя
                    one_str = buildOneInsertValueGuardians(arr);
                    str_query += "(" + one_str + "), ";
                    counter++;

                    if (counter == 1000) {
                        executeQuery(str_query, session, transaction, processed);
                        counter = 0;
                        str_query = getInitialInsertStatementGuardians();
                    }
                    processed++;
                } catch (Exception e) {
                    errors++;
                    processed++;
                    getLogger().error(String.format("Error in loading data from Symmetric. Record %s ", processed), e);
                    continue;
                }
            }
            if (counter > 0) {
                executeQuery(str_query, session, transaction, processed);
            }

            transaction = session.beginTransaction();
            query = session.createSQLQuery(getCreateIndexStatementGuardians());
            query.executeUpdate();
            transaction.commit();
            getLogger().info(String.format("End fill cf_registry_file_guardians table. Time taken %s ms, processed %s lines, error lines: %s", System.currentTimeMillis() - begin, processed, errors));

            transaction = null;
        } catch (Exception e) {
            logger.error("Error in load clients from symmetric", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
            DAOService.getInstance().setSverkaEnabled(true);
        }
    }

    private String getValueNullSafe(String str) {
        return str == null ? "" : str;
    }

    protected String buildOneInsertValueGuardians(String[] arr) {
        //0 - гуид клиента, 1 - имя представителя, 2 - отчество представителя, 3 - фамилия представителя, 4 - телефон представителя,
        //5 - кем является, 6 - законный предавитель, 7 - ssoid представителя, 8 - гуид клиента-представителя, 9 - пол представителя

        StringBuilder sb = new StringBuilder();
        sb.append(getQuotedStr(arr[0])).append(", ");  //guidofclient
        sb.append(getQuotedStr(arr[1])).append(", ");  //rep_firstname
        sb.append(getQuotedStr(arr[2])).append(", ");  //rep_secondname
        sb.append(getQuotedStr(arr[3])).append(", ");  //rep_surname
        sb.append(getQuotedStr(arr[4])).append(", ");  //rep_phone
        sb.append(getQuotedStr(arr[5])).append(", ");  //rep_who
        sb.append(getQuotedStr(arr[6])).append(", ");  //rep_legal_representative
        sb.append(getQuotedStr(arr[7])).append(", ");  //rep_ssoid
        sb.append(getQuotedStr(arr[8])).append(", ");  //rep_guid
        sb.append(getQuotedStr(arr[9]));               //rep_gender
        return sb.toString();
    }

    protected String getTruncateStatementGuardians() {
        return TRUNCATE_STATEMENT_GUARDIANS;
    }

    protected String getDropIndexStatementGuardians() {
        return DROP_INDEX_GUARDIANS;
    }

    protected String getInitialInsertStatementGuardians() {
        return INITIAL_INSERT_STATEMENT_GUARDIANS;
    }

    protected String getCreateIndexStatementGuardians() {
        return CREATE_INDEX_GUARDIANS;
    }

    @Override
    protected String getQueryString() {
        return "SELECT r.guidofclient, "
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
                + "  array_to_string(array_agg(concat_ws('|', g.rep_firstname, g.rep_secondname, g.rep_surname, g.rep_phone, g.rep_who, rep_legal_representative, rep_ssoid, rep_guid)), '$') "
                + "from cf_registry_file r left outer join cf_registry_file_guardians g on r.guidofclient = g.guidofclient where r.guidoforg in :guids "
                + "group by r.guidofclient, "
                + "guidoforg, "
                + "firstname, "
                + "secondname, "
                + "surname, "
                + "birthdate, "
                + "gender, "
                + "benefit, "
                + "parallel, "
                + "letter, "
                + "clazz, "
                + "currentclassorgroup, "
                + "status, "
                + "agegrouptype";
    }
}
