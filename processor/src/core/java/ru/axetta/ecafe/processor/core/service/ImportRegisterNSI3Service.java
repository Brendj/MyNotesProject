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
import java.util.Set;

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
    protected void fillOrgGuids(Query query, ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids) {
        query.setParameterList("guids", orgGuids.getOrgNSIIds());
    }

    private String extractDifferentIds(Set<String> set) {
        String result = "";
        for (String str : set) {
            result += str + ", ";
        }
        return result.substring(0, result.length()-2);
    }

    public String getBadGuids(ImportRegisterMSKClientsService.OrgRegistryGUIDInfo orgGuids) throws Exception {
        List<String> list = new ArrayList<String>();
        if (orgGuids.getOrgNSIIds().size() == 0) {
            return "У организации не задан НСИ-3 Id";
        }
        if (orgGuids.getOrgNSIIds().size() > 1) {
            return "У организации заданы несколько разных НСИ-3 Id: " + extractDifferentIds(orgGuids.getOrgNSIIds());
        }
        Boolean guidOK;
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            for (String nsiId : orgGuids.getOrgNSIIds()) {
                //Проверка на существование ЕКИС Ид ОО в выгрузке
                Query query = session.createSQLQuery("select personguid from cf_mh_persons where organizationid = :globalId limit 1");
                query.setParameter("globalId", Long.parseLong(nsiId));
                try {
                    Object res = query.uniqueResult();
                    guidOK = (res != null);
                } catch (Exception e) {
                    guidOK = false;
                }
                if (!guidOK) {
                    String badGuidString = "";
                    List<Org> orgs = DAOService.getInstance().findOrgsByNSIId(Long.parseLong(nsiId));
                    for (Org o : orgs) {
                        badGuidString += String.format("НСИ-3 Ид: %s, Ид. организации: %s, Название организации: %s;\n", nsiId, o.getIdOfOrg(), o.getShortNameInfoService());
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
