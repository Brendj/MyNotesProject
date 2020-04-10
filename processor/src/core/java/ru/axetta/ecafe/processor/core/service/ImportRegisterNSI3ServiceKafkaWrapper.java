/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component("ImportRegisterNSI3ServiceKafkaWrapper")
@DependsOn({"ImportRegisterNSI3Service", "runtimeContext"})
public class ImportRegisterNSI3ServiceKafkaWrapper extends ImportRegisterFileService {
    private static final Logger logger = LoggerFactory.getLogger(ImportRegisterNSI3ServiceKafkaWrapper.class);

    private final ImportRegisterFileService innerServices = RuntimeContext.getAppContext().getBean("ImportRegisterNSI3Service", ImportRegisterNSI3Service.class);

    private final boolean workWithKafka = workWithKafka();
    protected final String DROP_INDEX = "drop index if exists cf_mh_persons_ekisid_idx";
    protected final String CREATE_INDEX = "create index cf_mh_persons_ekisid_idx on cf_mh_persons using btree (ekisId)";

    private boolean workWithKafka(){
        String mode = RuntimeContext.getInstance().getPropertiesValue(ImportRegisterFileService.MODE_PROPERTY, null);
        return Objects.equals(mode, ImportRegisterFileService.MODE_KAFKA);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void fillOrgGuids(Query query, ImportRegisterClientsService.OrgRegistryGUIDInfo orgGuids) {
        innerServices.fillOrgGuids(query, orgGuids);
    }

    @Override
    public String getBadGuids(ImportRegisterClientsService.OrgRegistryGUIDInfo orgGuids) throws Exception {
        return innerServices.getBadGuids(orgGuids);
    }

    @Override
    protected String getQueryString() {
        if(!workWithKafka){
            return innerServices.getQueryString();
        } else {
            return "WITH pupils_info AS (\n"
                    + "       SELECT p.personguid,\n"
                    + "              o.guid,\n"
                    + "              p.firstname,\n"
                    + "              p.patronymic,\n"
                    + "              p.lastname,\n"
                    + "              extract(EPOCH FROM p.birthdate) * 1000 AS birthdate,\n"
                    + "              g.title AS gender,\n"
                    + "              prll.title AS parallel,\n"
                    + "              p.classname,\n"
                    + "              p.deletestate,\n"
                    + "              ag.title AS agegroup,"
                    + "              p.organizationid\n"
                    + "       FROM cf_mh_persons AS p\n"
                    + "                   JOIN cf_orgs AS o ON p.organizationid = o.organizationIdFromNSI\n"
                    + "                   JOIN cf_kf_ct_age_group AS ag ON p.agegroupid = ag.id\n"
                    + "                   LEFT JOIN cf_kf_ct_gender AS g ON p.genderid = g.id\n"
                    + "                   LEFT JOIN cf_kf_ct_parallel AS prll ON p.parallelid = prll.id\n"
                    + ") SELECT * FROM pupils_info as pi "
                    + " JOIN cf_orgs AS o ON pi.organizationid = o.organizationIdFromNSI "
                    + " WHERE o.ekisId in :guids ";
        }
    }

    @Override
    protected String getDropIndexStatement() {
        if(!workWithKafka) {
            return innerServices.getDropIndexStatement();
        }
        else return this.DROP_INDEX;
    }

    @Override
    protected String getCreateIndexStatement() {
        if(!workWithKafka) {
            return innerServices.getCreateIndexStatement();
        }
        else return this.CREATE_INDEX;
    }

    @Override
    public List<ImportRegisterClientsService.ExpandedPupilInfo> getPupilsByOrgGUID(ImportRegisterClientsService.OrgRegistryGUIDInfo orgGuids,
            String familyName, String firstName, String secondName) throws Exception {
        if(!workWithKafka){
            return innerServices.getPupilsByOrgGUID(orgGuids, familyName, firstName, secondName);
        }
        Session session = null;
        Transaction transaction = null;
        List<ImportRegisterClientsService.ExpandedPupilInfo> pupils = new LinkedList<>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String fioCondition = (StringUtils.isNotBlank(familyName) ? " and pi.patronymic like :surname" : "") +
                    (StringUtils.isNotBlank(firstName) ? " and pi.firstname like :firstname" : "") +
                    (StringUtils.isNotBlank(secondName) ? " and pi.lastname like :secondname" : "");
            String str_query = getQueryString() + fioCondition;

            Query query = session.createSQLQuery(str_query);
            fillOrgGuids(query, orgGuids);

            if (StringUtils.isNotBlank(familyName)) query.setParameter("surname", familyName);
            if (StringUtils.isNotBlank(firstName)) query.setParameter("firstname", firstName);
            if (StringUtils.isNotBlank(secondName)) query.setParameter("secondname", secondName);
            List<Object[]> list = query.list();

            transaction.commit();
            transaction = null;

            for (Object[] row : list) {
                ImportRegisterClientsService.ExpandedPupilInfo pupil = new ImportRegisterClientsService.ExpandedPupilInfo();
                pupil.guid = StringUtils.trim((String) row[0]);
                pupil.guidOfOrg = (String) row[1];
                pupil.firstName = StringUtils.trim((String) row[2]);
                pupil.secondName = StringUtils.trim((String) row[3]);
                pupil.familyName = StringUtils.trim((String) row[4]);
                pupil.birthDate = (String) row[5];
                pupil.gender = (String) row[6];
                pupil.parallel = (String) row[7];
                pupil.group = StringUtils.trim((String) row[8]);
                pupil.deleted = Boolean.parseBoolean((String) row[9]);
                pupil.ageTypeGroup = (String) row[10];

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
            HibernateUtils.rollback(transaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
    }
}
