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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component("ImportRegisterNSI3ServiceKafkaWrapper")
@DependsOn({"ImportRegisterNSI3Service", "runtimeContext"})
public class ImportRegisterNSI3ServiceKafkaWrapper extends ImportRegisterFileService {
    private static final Logger logger = LoggerFactory.getLogger(ImportRegisterNSI3ServiceKafkaWrapper.class);

    private final ImportRegisterNSI3Service innerServices = RuntimeContext.getAppContext().getBean("ImportRegisterNSI3Service", ImportRegisterNSI3Service.class);

    private final boolean workWithKafka = workWithKafka();
    protected final String DROP_INDEX = "drop index if exists cf_mh_persons_ekisid_idx";
    protected final String CREATE_INDEX = "create index cf_mh_persons_ekisid_idx on cf_mh_persons using btree (ekisId)";

    public static boolean workWithKafka(){
        String mode = RuntimeContext.getInstance().getPropertiesValue(ImportRegisterFileService.MODE_PROPERTY, null);
        return Objects.equals(mode, ImportRegisterFileService.MODE_KAFKA);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void fillOrgGuids(Query query, ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids) {
        if(!workWithKafka) {
            innerServices.fillOrgGuids(query, orgGuids);
        } else {
            query.setParameterList("guids", orgGuids.getOrgNSIIdsLong());
        }
    }

    @Override
    public String getBadGuids(ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids) throws Exception {
        return innerServices.getBadGuids(orgGuids);
    }

    @Override
    protected String getQueryString() {
        if(!workWithKafka){
            return innerServices.getQueryString();
        } else {
            return " SELECT DISTINCT p.guidnsi AS guid,\n"
                    + "              '' AS guidOfOrg,\n"
                    + "              p.firstname,\n"
                    + "              p.patronymic AS secondname,\n"
                    + "              p.lastname AS familyName,\n"
                    + "              to_char(p.birthdate, 'DD.MM.YYYY') AS birthdate,\n"
                    + "              g.title AS gender,\n"
                    + "              prll.title AS parallel,\n"
                    + "              case when cl.id is not null then cl.name\n"
                    + "                  else p.classname end AS group,\n"
                    + "              p.deletestate AS deleted,\n"
                    + "              el.title AS ageTypeGroup,\n"
                    + "              o.organizationidfromnsi,\n"
                    + "              p.personguid\n"
                    + "       FROM cf_mh_persons AS p\n"
                    + "                   JOIN cf_orgs AS o ON p.organizationid = o.organizationIdFromNSI\n"
                    + "                   LEFT JOIN cf_mh_classes cl ON p.idofclass = cl.id\n"
                    + "                   LEFT JOIN cf_kf_ct_educationlevel AS el ON cl.educationstageid = el.id\n"
                    + "                   LEFT JOIN cf_kf_ct_gender AS g ON p.genderid = g.id\n"
                    + "                   LEFT JOIN cf_kf_ct_parallel AS prll ON cl.parallelid = prll.id\n"
                    + "  WHERE p.invaliddata IS FALSE and o.organizationidfromnsi IN :guids";
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
    public List<ImportRegisterMSKClientsService.ExpandedPupilInfo> getPupilsByOrgGUID(ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids,
            String familyName, String firstName, String secondName) throws Exception {
        if(!workWithKafka){
            return innerServices.getPupilsByOrgGUID(orgGuids, familyName, firstName, secondName);
        }
        Session session = null;
        Transaction transaction = null;
        List<ImportRegisterMSKClientsService.ExpandedPupilInfo> pupils = new LinkedList<>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String fioCondition = (StringUtils.isNotBlank(familyName) ? " and pi.patronymic like :surname" : "") + (
                    StringUtils.isNotBlank(firstName) ? " and pi.firstname like :firstname" : "") + (StringUtils.isNotBlank(secondName) ? " and pi.lastname like :secondname" : "");
            String str_query = getQueryString() + fioCondition;

            Query query = session.createSQLQuery(str_query);
            fillOrgGuids(query, orgGuids);

            if (StringUtils.isNotBlank(familyName))
                query.setParameter("surname", familyName);
            if (StringUtils.isNotBlank(firstName))
                query.setParameter("firstname", firstName);
            if (StringUtils.isNotBlank(secondName))
                query.setParameter("secondname", secondName);

            List<Object[]> list = query.list();

            transaction.commit();
            transaction = null;

            for (Object[] row : list) {
                ImportRegisterMSKClientsService.ExpandedPupilInfo pupil = new ImportRegisterMSKClientsService.ExpandedPupilInfo();
                pupil.guid = HibernateUtils.getDbString(row[0]);
                pupil.guidOfOrg = HibernateUtils.getDbString(row[1]);
                pupil.firstName = HibernateUtils.getDbString(row[2]);
                pupil.secondName = HibernateUtils.getDbString(row[3]);
                pupil.familyName = HibernateUtils.getDbString(row[4]);
                pupil.birthDate = HibernateUtils.getDbString(row[5]);
                pupil.gender =  HibernateUtils.getDbString(row[6]);
                pupil.parallel = HibernateUtils.getDbString(row[7]);
                pupil.group = HibernateUtils.getDbString(row[8]);
                pupil.deleted = Boolean.parseBoolean(HibernateUtils.getDbString(row[9]));
                pupil.ageTypeGroup = HibernateUtils.getDbString(row[10]);
                pupil.orgId = HibernateUtils.getDbLong(row[11]);
                pupil.meshGUID = HibernateUtils.getDbString(row[12]);

                pupils.add(pupil);
            }

            return pupils;
        } catch (Exception e) {
            logger.error("Error when process data about client GUID from external source: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
    }
}
