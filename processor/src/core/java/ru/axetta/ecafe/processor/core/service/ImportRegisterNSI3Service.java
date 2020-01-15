/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 14.01.2020.
 */
@Component("ImportRegisterNSI3Service")
@Scope("singleton")
public class ImportRegisterNSI3Service extends ImportRegisterFileService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterNSI3Service.class);
    protected String DROP_INDEX = "drop index if exists cf_registry_file_ekisid_idx";
    protected String CREATE_INDEX = "create index cf_registry_file_ekisid_idx on cf_registry_file using btree (ekisId)";

    @Override
    protected void fillOrgGuids(Query query, ImportRegisterClientsService.OrgRegistryGUIDInfo orgGuids) {
        query.setParameterList("guids", orgGuids.getOrgEkisIds());
    }

    public List<String> getBadGuids(ImportRegisterClientsService.OrgRegistryGUIDInfo orgGuids) throws Exception {
        List<String> result = new ArrayList<String>();
        Boolean guidOK;
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            for (Long ekisId : orgGuids.getOrgEkisIds()) {
                //Проверка на существование ЕКИС Ид ОО в выгрузке
                Query query = session.createSQLQuery("select ekisId from cf_registry_file where ekisId = :ekisId limit 1");
                query.setParameter("ekisId", ekisId);
                try {
                    Object res = query.uniqueResult();
                    guidOK = (res != null);
                } catch (Exception e) {
                    guidOK = false;
                }
                if (!guidOK) {
                    String badGuidString = "";
                    List<Org> orgs = DAOService.getInstance().findOrgsByEkisId(ekisId);
                    for (Org o : orgs) {
                        badGuidString += String.format("ЕКИС Ид: %s, Ид. организации: %s, Название организации: %s;\n", ekisId, o.getIdOfOrg(), o.getShortNameInfoService());
                    }
                    result.add(badGuidString);
                }
            }
            transaction.commit();
            transaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(transaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
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
                + "  ekisId "
                + "from cf_registry_file r where r.ekisId in :guids";
    }

    protected String getDropIndexStatement() {
        return DROP_INDEX;
    }

    protected String getCreateIndexStatement() {
        return CREATE_INDEX;
    }
}
