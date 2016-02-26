/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgRegistryChange;
import ru.axetta.ecafe.processor.core.persistence.OrgRegistryChangeItem;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.02.12
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */

/**
 * Класс отчета по льготам ОУ
 */
public class NSIRegistryStatReport {

    private final List<Stat> items;

    public List<Stat> getItems() {
        return items;
    }

    public NSIRegistryStatReport() {
        this.items = new ArrayList<Stat>();
    }

    public void buildReport(Session session, Long selectedRevision) throws Exception {
        items.clear();
        List<OrgRegistryChange> dbItems = DAOService.getInstance().getOrgRegistryChangeByDate(selectedRevision);
        if (dbItems.size() == 0) {
            return;
        }
        Set<String> regions = new HashSet<String>();
        Map<String, Stat> stats = new TreeMap<String, Stat>();
        for (OrgRegistryChange item : dbItems) {
            String district = "";
            Integer operationType = item.getOperationType();
            if (operationType == OrgRegistryChange.SIMILAR) {
                continue;
            }
            if (operationType == OrgRegistryChange.DELETE_OPERATION) {
                if (item.getIdOfOrg() != null) {
                    Org org = (Org)session.load(Org.class, item.getIdOfOrg());
                    district = org.getDistrict();
                }
                if (district != null && !district.isEmpty()) {
                    regions.add(district);
                } else {
                    district = ".Регион не определен";
                    regions.add(district);
                }
                Stat stat;
                if (stats.containsKey(district)) {
                    stat = stats.get(district);
                } else {
                    stat = new Stat(district, 0, 0, 0);
                }
                stat.setRemoveOperation(stat.getRemoveOperation() + 1);
                stats.put(district, stat);
                continue;
            } else {
                for (OrgRegistryChangeItem subitem : item.getOrgs()) {
                    if (subitem.getOperationType() == OrgRegistryChange.SIMILAR) {
                        continue;
                    }
                    if (subitem.getIdOfOrg() == null) {
                        district = subitem.getRegion();
                    } else {
                        //Org org = (Org)session.load(Org.class, subitem.getIdOfOrg());
                        //district = org.getDistrict();
                        district = subitem.getRegionFrom();
                    }
                    if (district != null && !district.isEmpty()) {
                        regions.add(district);
                    } else {
                        district = ".Регион не определен";
                        regions.add(district);
                    }
                    Stat stat;
                    if (stats.containsKey(district)) {
                        stat = stats.get(district);
                    } else {
                        stat = new Stat(district, 0, 0, 0);
                    }
                    operationType = subitem.getOperationType();
                    switch (operationType) {
                        case OrgRegistryChange.CREATE_OPERATION :
                            stat.setCreateOperation(stat.getCreateOperation() + 1);
                            break;
                        case OrgRegistryChange.MODIFY_OPERATION :
                            stat.setChangeOperation(stat.getChangeOperation() + 1);
                            break;
                    }
                    stat.setTotalOperation(stat.getTotalOperation() + 1);
                    stats.put(district, stat);
                }
            }
        }
        Integer tCreate = 0;
        Integer tRemove = 0;
        Integer tChange = 0;
        for (Map.Entry<String, Stat> entry : stats.entrySet()) {
            Stat s = entry.getValue();
            tCreate += s.getCreateOperation();
            tChange += s.getChangeOperation();
            tRemove += s.getRemoveOperation();
            items.add(s);
        }
        Stat total = new Stat("ИТОГО", tCreate, tChange, tRemove);
        items.add(total);
    }

    public static class Stat {

        private String district;
        private Integer createOperation;
        private Integer changeOperation;
        private Integer removeOperation;
        private Integer totalOperation;

        public Stat(String district, Integer createOperation, Integer changeOperation, Integer removeOperation) {
            this.setDistrict(district);
            this.setCreateOperation(createOperation);
            this.setChangeOperation(changeOperation);
            this.setRemoveOperation(removeOperation);
            this.setTotalOperation(createOperation + changeOperation + removeOperation);
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public Integer getCreateOperation() {
            return createOperation;
        }

        public void setCreateOperation(Integer createOperation) {
            this.createOperation = createOperation;
        }

        public Integer getChangeOperation() {
            return changeOperation;
        }

        public void setChangeOperation(Integer changeOperation) {
            this.changeOperation = changeOperation;
        }

        public Integer getRemoveOperation() {
            return removeOperation;
        }

        public void setRemoveOperation(Integer removeOperation) {
            this.removeOperation = removeOperation;
        }

        public Integer getTotalOperation() {
            return totalOperation;
        }

        public void setTotalOperation(Integer totalOperation) {
            this.totalOperation = totalOperation;
        }
    }
}
