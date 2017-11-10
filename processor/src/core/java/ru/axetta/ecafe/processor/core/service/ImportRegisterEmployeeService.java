/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.nsi.ClientMskNSIService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by i.semenov on 08.11.2017.
 */
@Component("importRegisterEmployeeService")
@Scope("singleton")
public class ImportRegisterEmployeeService extends ImportRegisterClientsService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImportRegisterEmployeeService.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Transactional
    public StringBuffer syncEmployeesWithRegistry(long idOfOrg, StringBuffer logBuffer) throws Exception {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Org org = em.find(Org.class, idOfOrg);
        String synchDate = "[Синхронизация с Реестрами от " + date + " для " + org.getIdOfOrg() + "]: ";
        ImportRegisterClientsService.OrgRegistryGUIDInfo orgGuids = new ImportRegisterClientsService.OrgRegistryGUIDInfo(org);
        logger.info(synchDate + "Производится синхронизация сотрудников для " + org.getOfficialName() + " GUID [" + orgGuids.getGuidInfo()
                + "]", logBuffer);

        List<ImportRegisterClientsService.ExpandedPupilInfo> pupils = getPupilsByOrgGUID(orgGuids.orgGuids, null, null, null);
        logger.info(synchDate + "Получено " + pupils.size() + " записей", logBuffer);

        saveClients(synchDate, date, System.currentTimeMillis(), org, pupils, logBuffer);

        return logBuffer;
    }

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
            String str_query = "SELECT guidoforg, "
                    + "  firstname,"
                    + "  secondname, "
                    + "  surname, "
                    + "  birthdate, " //5
                    + "  gender, "
                    + "  snils "
                    + "from cf_registry_employee_file r where r.guidoforg in :guids" + fioCondition;
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
                pupil.firstName = (String) row[1];
                pupil.secondName = (String) row[2];
                pupil.familyName = (String) row[3];
                pupil.guid = (String) row[6]; //SNILS
                pupil.birthDate = (String) row[4];
                pupil.gender = (String) row[5];

                pupil.familyName = pupil.familyName == null ? null : pupil.familyName.trim();
                pupil.firstName = pupil.firstName == null ? null : pupil.firstName.trim();
                pupil.secondName = pupil.secondName == null ? null : pupil.secondName.trim();
                pupil.guid = pupil.guid == null ? null : pupil.guid.trim();
                pupil.guardiansCount = "0";

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

    protected ClientMskNSIService getNSIService() {
        return RuntimeContext.getAppContext().getBean("ImportRegisterEmployeeFileService", ImportRegisterEmployeeFileService.class);
    }

    @Override
    protected RegistryChange getRegistryChangeClassInstance() {
        return new RegistryChangeEmployee();
    }

    @Override
    protected List<RegistryChange> getRegistryChangeList(Session session, List<Long> changesList) {
        Criteria criteria = session.createCriteria(RegistryChangeEmployee.class);
        criteria.add(Restrictions.in("idOfRegistryChange", changesList));
        return criteria.list();
    }

    @Override
    protected void setGuidFromChange(FieldProcessor.Config createConfig, RegistryChange change) throws Exception {
        createConfig.setValue(ClientManager.FieldId.SAN, change.getClientGUID());
    }

    @Override
    public void setChangeError(long idOfRegistryChange, Exception e) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            RegistryChangeEmployee change = (RegistryChangeEmployee) session.load(RegistryChangeEmployee.class, idOfRegistryChange);
            String err = e.getMessage();
            if (err != null && err.length() > 255) {
                err = err.substring(0, 255).trim();
            }
            change.setError(err);
            session.update(change);
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public boolean isRegistryChangeExist(String notificationId, Client client, int operation, Session session) {
        Query q = session.createSQLQuery("SELECT 1 " + "FROM cf_registrychange_employee "
                + "where notificationId=:notificationId and operation=:operation");
        q.setParameter("notificationId", notificationId);
        q.setParameter("operation", operation);
        List res = q.list();
        if (res == null || res.size() < 1) {
            return false;
        } else {
            return true;
        }
    }

    public List <Client> findClientsWithoutPredefinedForOrgAndFriendly (Org organization) throws Exception {
        List <Org> orgs = DAOUtils.findFriendlyOrgs (em, organization);
        String orgsClause = " where (client.org = :org0 ";
        for (int i=0; i < orgs.size(); i++) {
            if (orgsClause.length() > 0) {
                orgsClause += " or ";
            }
            orgsClause += "client.org = :org" + (i + 1);
        }
        orgsClause += ") " + " and (client.idOfClientGroup >= " +
                ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and client.idOfClientGroup < " +
                ClientGroup.Predefined.CLIENT_DISPLACED.getValue() + " )";

        javax.persistence.Query query = em.createQuery(
                "from Client client " + orgsClause);
        query.setParameter("org0", organization);
        for (int i=0; i < orgs.size(); i++) {
            query.setParameter("org" + (i + 1), orgs.get(i));
        }
        if (query.getResultList().isEmpty()) return Collections.emptyList();
        List <Client> cls = (List <Client>)query.getResultList();
        return cls;
    }

    public List<Client> findClientsByGuids(List<String> guids) {
        if(guids.size() == 0){
            return new ArrayList<Client>();
        }
        javax.persistence.Query q = em.createQuery("from Client where san in :guids"); //search by snils
        q.setParameter("guids", guids);
        List<Client> result = (List<Client>) q.getResultList();
        return result != null ? result : new ArrayList<Client>();
    }

    @Override
    protected String getPupilGuid(String guid) {
        return guid.replaceAll(" ", "").replaceAll("-", "");
    }

    @Override
    protected String getClientGuid(Client client) {
        return client.getSan();
    }

    @Override
    protected Boolean belongToProperGroup(Client cl) {
        return !(cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().longValue() >= ClientGroup
                .Predefined.CLIENT_EMPLOYEES.getValue().longValue()
                && cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().longValue()
                < ClientGroup.Predefined.CLIENT_LEAVING.getValue().longValue());
    }

}
