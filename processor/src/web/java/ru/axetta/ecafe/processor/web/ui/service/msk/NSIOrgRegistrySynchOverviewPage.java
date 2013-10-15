/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Query;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 15.10.13
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class NSIOrgRegistrySynchOverviewPage extends BasicWorkspacePage {
    protected List<Item> list;
    Logger logger = LoggerFactory.getLogger(NSIOrgRegistrySynchPageBase.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    @Override
    public void onShow() {
        RuntimeContext.getAppContext().getBean(NSIOrgRegistrySynchOverviewPage.class).doUpdate();
    }

    public String getPageFilename() {
        return "service/msk/nsi_synch_table";
    }

    public String getPageTitle() {
        return "История синхронизации с Реестрами";
    }

    @Transactional
    public void doUpdate() {
        try {
            if (list == null) {
                list = new ArrayList<Item>();
            }
            list.clear();
            Map<Long, Item> res = new HashMap<Long, Item>();
            Session session = (Session) entityManager.getDelegate();
            Query q = session.createSQLQuery(
                    "select cf_orgs.idoforg, cf_orgs.officialname, operation, count(cf_registrychange.operation) "
                    + "from cf_registrychange "
                    + "left join cf_orgs on cf_orgs.idoforg=cf_registrychange.idoforg "
                    + "group by cf_orgs.idoforg, cf_orgs.officialname, operation "
                    + "order by cf_orgs.idoforg, cf_orgs.officialname, operation");
            List result = q.list();
            for (Object entry : result) {
                Object o[] = (Object[]) entry;
                long idoforg = ((BigInteger) o[0]).longValue();
                String orgName = ((String) o[1]).trim();
                int operation = ((Integer) o[2]).intValue();
                long count = ((BigInteger) o[3]).longValue();
                
                Item i = res.get(idoforg);
                if (i == null) {
                    i = new Item(idoforg, orgName);
                    res.put(idoforg, i);
                }
                switch (operation) {
                    case ImportRegisterClientsService.CREATE_OPERATION:
                        i.setCreated(count);
                        break;
                    case ImportRegisterClientsService.DELETE_OPERATION:
                        i.setDeleted(count);
                        break;
                    case ImportRegisterClientsService.MODIFY_OPERATION:
                        i.setModified(count);
                        break;
                    case ImportRegisterClientsService.MOVE_OPERATION:
                        i.setMoved(count);
                        break;
                }
            }

            for (Long id : res.keySet()) {
                list.add(res.get(id));
            }
        } catch (Exception e) {
            logger.error("Failed to update list of updates", e);
        }
    }

    public List<Item> getList() {
        return list;
    }




    public class Item {
        protected long idoforg;
        protected String orgName;
        protected long modified;
        protected long moved;
        protected long created;
        protected long deleted;

        public Item(long idoforg, String orgName) {
            this.idoforg = idoforg;
            this.orgName = orgName;
            modified = 0L;
            moved = 0L;
            created = 0L;
            deleted = 0L;
        }

        public long getIdoforg() {
            return idoforg;
        }

        public void setIdoforg(long idoforg) {
            this.idoforg = idoforg;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public long getModified() {
            return modified;
        }

        public void setModified(long modified) {
            this.modified = modified;
        }

        public long getMoved() {
            return moved;
        }

        public void setMoved(long moved) {
            this.moved = moved;
        }

        public long getCreated() {
            return created;
        }

        public void setCreated(long created) {
            this.created = created;
        }

        public long getDeleted() {
            return deleted;
        }

        public void setDeleted(long deleted) {
            this.deleted = deleted;
        }
    }
}
