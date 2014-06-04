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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    protected String orgFilter = "";
    protected boolean showOnlyUnsynch = false;
    protected static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    protected static final long OUT_OF_SYNCH_LIMIT = 86400000L * 2;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    public String getOrgFilter() {
        return orgFilter;
    }

    public void setOrgFilter(String orgFilter) {
        this.orgFilter = orgFilter;
    }

    public boolean isShowOnlyUnsynch() {
        return showOnlyUnsynch;
    }

    public void setShowOnlyUnsynch(boolean showOnlyUnsynch) {
        this.showOnlyUnsynch = showOnlyUnsynch;
    }

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

    protected boolean isUnsynch(long idoforg, long ts, List<Long> excludeOrgs) {
        if(excludeOrgs.contains(idoforg)) {
            return true;
        }

        if(System.currentTimeMillis() - ts < OUT_OF_SYNCH_LIMIT) {
            excludeOrgs.add(idoforg);
            return true;
        }
        return false;
    }

    @Transactional
    public void doUpdate() {
        try {
            if (list == null) {
                list = new ArrayList<Item>();
            }
            list.clear();
            Map<Long, Item> res = new TreeMap<Long, Item>();
            Session session = (Session) entityManager.getDelegate();
            String orgStatement = "";
            if(orgFilter != null && orgFilter.length() > 0) {
                orgStatement = " where cf_orgs.shortname like '%" + orgFilter + "%' ";
            }
            Query q = session.createSQLQuery(
                    "select cf_orgs.idoforg, cf_orgs.officialname, operation, cf_registrychange.createdate, count(cf_registrychange.operation) "
                    + "from cf_registrychange "
                    + "left join cf_orgs on cf_orgs.idoforg=cf_registrychange.idoforg "
                    + orgStatement
                    + "group by cf_orgs.idoforg, cf_orgs.officialname, cf_registrychange.createdate, operation "
                    + "order by cf_orgs.idoforg, cf_orgs.officialname, cf_registrychange.createdate desc, operation");
            List result = q.list();

            long prevIdOfOrg = -1L;
            int prevOperation = -1;
            Long prevDate = null;
            List<Long> excludeOrgs = null;
            if(showOnlyUnsynch) {
                excludeOrgs = new ArrayList<Long>();
            }

            for (Object entry : result) {
                Object o[] = (Object[]) entry;
                long idoforg = ((BigInteger) o[0]).longValue();
                String orgName = ((String) o[1]).trim();
                int operation = ((Integer) o[2]).intValue();
                long ts = ((BigInteger) o[3]).longValue();
                long count = ((BigInteger) o[4]).longValue();

                if(showOnlyUnsynch && isUnsynch(idoforg, ts, excludeOrgs)) {
                    continue;
                }

                boolean passItem = false;
                if(prevIdOfOrg != idoforg) {
                    passItem = true;
                } else {
                    if(prevOperation != operation) {
                        if(prevDate == ts) {
                            passItem = true;
                        }
                    } else {
                        if(prevDate < ts) {
                            passItem = true;
                        }
                    }
                }
                if(!passItem) {
                    continue;
                }

                prevIdOfOrg = idoforg;
                prevOperation = operation;
                prevDate = ts;
                
                Item i = res.get(idoforg);
                if (i == null) {
                    i = new Item(idoforg, orgName, ts);
                    res.put(idoforg, i);
                }
                switch (operation) {
                    case ImportRegisterClientsService.CREATE_OPERATION:
                        i.setCreated(count);
                        i.addTotal(count);
                        break;
                    case ImportRegisterClientsService.DELETE_OPERATION:
                        i.setDeleted(count);
                        i.addTotal(count);
                        break;
                    case ImportRegisterClientsService.MODIFY_OPERATION:
                        i.setModified(count);
                        i.addTotal(count);
                        break;
                    case ImportRegisterClientsService.MOVE_OPERATION:
                        i.setMoved(count);
                        i.addTotal(count);
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
        protected long total;
        protected long ts;
        protected String date;

        public Item(long idoforg, String orgName, long ts) {
            this.idoforg = idoforg;
            this.orgName = orgName;
            modified = 0L;
            moved = 0L;
            created = 0L;
            deleted = 0L;
            total = 0L;
            this.ts = ts;
            date = dateFormat.format(new Date(ts));
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

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public void addTotal(long count) {
            total += count;
        }

        public long getTs() {
            return ts;
        }

        public String getDate() {
            return date;
        }

        public boolean isOutOfSynch() {
            return System.currentTimeMillis() - ts > OUT_OF_SYNCH_LIMIT;
        }
    }
}
