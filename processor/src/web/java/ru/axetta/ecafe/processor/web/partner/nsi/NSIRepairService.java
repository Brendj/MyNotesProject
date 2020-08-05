/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.nsi;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.ImportRegisterMSKClientsService;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 23.09.14
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class NSIRepairService {
    private final static Logger logger = LoggerFactory.getLogger(NSIRepairService.class);
    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    protected final String LOAD_REPAIR_QUERY =
            "select idofregistrychange, idofclient, idoforg, groupname "
            + "from cf_registrychange "
            + "where operation=:operation and createdate>=:date and groupname in (%GROUPS_LIST%) and applied=true";
    protected final String REMOVE_REPAIRED_QUERY =
            "delete from cf_registrychange where idofregistrychange=:idofregistrychange";


    public void run() {
        List<RepairEntry> toRepair = RuntimeContext.getAppContext().getBean(NSIRepairService.class).loadRepairEntries();
        long repaired = 0;
        logger.info(String.format("[Фикс записей из Реестров] К исправлению: %s", toRepair.size()));
        for(RepairEntry re : toRepair) {
            try {
                logger.info(String.format("[Фикс записей из Реестров] Попытка исправить: %s", re));
                RuntimeContext.getAppContext().getBean(NSIRepairService.class).repair(re);
                repaired++;
                logger.info(String.format("[Фикс записей из Реестров] %s исправлен", re));
                /*if(RuntimeContext.getAppContext().getBean(NSIRepairService.class).remove(re)) {
                }*/
            } catch (Exception e) {
                logger.error(String.format("[Фикс записей из Реестров] Не удалось исправить %s", re), e);
            }
        }
        logger.info(String.format("[Фикс записей из Реестров] К исправлению: %s, исправлено: %s", toRepair.size(), repaired));
    }

    @Transactional
    public List<RepairEntry> loadRepairEntries() {
        List<RepairEntry> result = new ArrayList<RepairEntry>();
        Session session = (Session) em.getDelegate();
        String q = replaceClientGroups(new String(LOAD_REPAIR_QUERY));
        org.hibernate.Query query = session.createSQLQuery(q);
        query.setParameter("operation", ImportRegisterMSKClientsService.DELETE_OPERATION);
        query.setParameter("date", 1411344000000L);
        List res = query.list();
        for (Object entry : res) {
            Object e[] = (Object[]) entry;
            long idofregistrychange = ((BigInteger) e[0]).longValue();
            long idofclient = ((BigInteger) e[1]).longValue();
            long idoforg = ((BigInteger) e[2]).longValue();
            String groupName = ((String) e[3]).trim();
            RepairEntry re = new RepairEntry(idofregistrychange, idoforg, idofclient, groupName);
            result.add(re);
        }
        return result;
    }

    public void repair(RepairEntry re) throws Exception {
        ClientGroup.Predefined predefined = ClientGroup.Predefined.parse(re.getGroupName());
        if(predefined == null) {
            return;
        }
        long idofclientgroup = predefined.getValue();
        DAOService.getInstance().bindClientToGroup(re.idofclient, idofclientgroup);
    }

    @Transactional
    public boolean remove(RepairEntry re) throws Exception {
        Session session = (Session) em.getDelegate();
        org.hibernate.Query query = session.createSQLQuery(REMOVE_REPAIRED_QUERY);
        query.setParameter("idofregistrychange", re.getIdofregistrychange());
        return query.executeUpdate() > 0;
    }

    protected String replaceClientGroups(String q) {
        String groups = "";
        for(ClientGroup.Predefined grp : ClientGroup.Predefined.values()) {
            if(grp.getValue() < ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()) {
                continue;
            }
            if(groups.length() > 0) {
                groups += ", ";
            }
            groups += String.format(" '%s'", grp.getNameOfGroup());
        }
        q = q.replaceAll("%GROUPS_LIST%", groups);
        return q;
    }

    public class RepairEntry {
        protected long idofregistrychange;
        protected long idoforg;
        protected long idofclient;
        protected String groupName;

        public RepairEntry(long idofregistrychange, long idoforg, long idofclient, String groupName) {
            this.idofregistrychange = idofregistrychange;
            this.idoforg = idoforg;
            this.idofclient = idofclient;
            this.groupName = groupName;
        }

        public long getIdofregistrychange() {
            return idofregistrychange;
        }

        public long getIdoforg() {
            return idoforg;
        }

        public long getIdofclient() {
            return idofclient;
        }

        public String getGroupName() {
            return groupName;
        }

        @Override
        public String toString() {
            return "RepairEntry{" +
                    "idoforg=" + idoforg +
                    ", idofclient=" + idofclient +
                    ", groupName='" + groupName + '\'' +
                    '}';
        }
    }
}