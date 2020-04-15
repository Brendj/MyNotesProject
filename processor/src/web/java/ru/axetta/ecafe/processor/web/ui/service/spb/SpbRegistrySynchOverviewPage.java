/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.spb;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.RegistryChange;
import ru.axetta.ecafe.processor.core.service.ImportRegisterMSKClientsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
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
public class SpbRegistrySynchOverviewPage extends BasicWorkspacePage {
    protected List<Item> list;
    Logger logger = LoggerFactory.getLogger(SpbRegistrySyncPageBase.class);
    protected String orgFilter = "";
    protected boolean showOnlyUnsynch = false;
    protected static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    protected static final long OUT_OF_SYNCH_LIMIT = 86400000L * 2;
    protected int typeFilter;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    public void doGenerateXLS(ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(SpbRegistrySynchOverviewPage.class).doUpdate();
        RuntimeContext.getAppContext().getBean(SpbRegistrySynchOverviewPage.class).generateXLS();
    }

    public void generateXLS() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();


            response.setCharacterEncoding("winwdows-1251");
            response.setHeader("Content-Type", "text/csv");
            response.setHeader("Content-Disposition", "attachment;filename=\"synchHistory.csv\"");
            final ServletOutputStream responseOutputStream = response.getOutputStream();
            try {
                String str = "№;ID;Наименование;Всего;Добавленных;Измененных;Перемещенных;Удаленных;Дата сверки;Тип сверки;\n";
                responseOutputStream.write(str.getBytes("windows-1251"));
                for (int i=0; i<list.size(); i++) {
                    Item it = list.get(i);
                    str = i + ";" +
                          it.getIdoforg() + ";" +
                          it.getOrgName() + ";" +
                          it.getTotal() + ";" +
                          it.getCreated() + ";" +
                          it.getModified() + ";" +
                          it.getMoved() + ";" +
                          it.getDeleted() + ";" +
                          dateFormat.format(new Date(it.getTs())) + ";" +
                          it.getType() + ";\n";
                    responseOutputStream.write(str.getBytes("windows-1251"));
                }
                responseOutputStream.flush();
            } catch (Exception e1) {
                throw e1;
            } finally {
                responseOutputStream.close();
            }
            facesContext.responseComplete();
        } catch (Exception e) {
            logger.error("Failed to send comparison file", e);
        }
    }

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

    public int getTypeFilter() {
        return typeFilter;
    }

    public void setTypeFilter(int typeFilter) {
        this.typeFilter = typeFilter;
    }

    public List<SelectItem> getTypes() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(0, ""));
        items.add(new SelectItem(RegistryChange.FULL_COMPARISON, "Полная сверка"));
        items.add(new SelectItem(RegistryChange.CHANGES_UPDATE, "Загрузка обновлений"));
        return items;
    }

    @Override
    public void onShow() {
        RuntimeContext.getAppContext().getBean(SpbRegistrySynchOverviewPage.class).doUpdate();
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
                orgStatement = " cf_orgs.shortname like '%" + orgFilter + "%' ";
            }
            String typeStatement = "";
            if(typeFilter != 0) {
                if(orgStatement.length() > 0) {
                    typeStatement = " and ";
                }
                typeStatement += String.format(" cf_registrychange.type=%s ", typeFilter);
            }
            String whereClause = "";
            if(orgStatement.length() > 0 || typeStatement.length() > 0) {
                whereClause = " where " + orgStatement + typeStatement;
            }
            Query q = session.createSQLQuery(
                    "select cf_orgs.idoforg, cf_orgs.officialname, operation, cf_registrychange.createdate, count(cf_registrychange.operation), cf_registrychange.type "
                    + "from cf_registrychange "
                    + "left join cf_orgs on cf_orgs.idoforg=cf_registrychange.idoforg "
                    + whereClause
                    + "group by cf_orgs.idoforg, cf_orgs.officialname, cf_registrychange.createdate, operation, type "
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
                int type = ((Integer) o[5]).intValue();

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
                    case ImportRegisterMSKClientsService.CREATE_OPERATION:
                        i.setCreated(count);
                        i.addTotal(count);
                        break;
                    case ImportRegisterMSKClientsService.DELETE_OPERATION:
                        i.setDeleted(count);
                        i.addTotal(count);
                        break;
                    case ImportRegisterMSKClientsService.MODIFY_OPERATION:
                        i.setModified(count);
                        i.addTotal(count);
                        break;
                    case ImportRegisterMSKClientsService.MOVE_OPERATION:
                        i.setMoved(count);
                        i.addTotal(count);
                        break;
                }
                i.setType(type);
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
        protected int type;

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

        public String getType() {
            if(type == RegistryChange.FULL_COMPARISON) {
                return "Полная сверка";
            } else if (type == RegistryChange.CHANGES_UPDATE) {
                return "Загрузка изменений";
            }
            return "Неизвестный тип";
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
