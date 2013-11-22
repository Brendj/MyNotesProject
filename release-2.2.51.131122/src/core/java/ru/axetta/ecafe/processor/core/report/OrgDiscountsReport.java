/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.02.12
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class OrgDiscountsReport extends BasicReport {

    private final List<ReportItem> itemList;
    private Long count = null;

    public OrgDiscountsReport() {
        super();
        itemList = Collections.emptyList();
    }

    public OrgDiscountsReport(Date generateTime, long generateDuration, List<ReportItem> itemList, Long count) {
        super(generateTime, generateDuration);
        this.itemList = itemList;
        this.count = count;
    }

    public List<ReportItem> getItemList() {
        return itemList;
    }

    public Long getCount() {
        return count;
    }

    public static class Builder {

        public OrgDiscountsReport build(Session session, Long idOfOrg) throws Exception {
            Date generateTime = new Date();
            List<ReportItem> reportItems = new ArrayList<ReportItem>();
            Long countResult = null;
            if (idOfOrg != null) {

                String q = "select c.idOfClient, c.idOfClientGroup, c.clientGroup.groupName, c.person.firstName, c.person.secondName, c.person.surname, cd.categoryName "
                    +  " from Client c, ClientsCategoryDiscount cc, CategoryDiscount cd"
                    + " where c.idOfClient=cc.idOfClient and "
                        + " cc.idOfCategoryDiscount >= 0 and "
                        + " cd.idOfCategoryDiscount=cc.idOfCategoryDiscount and "
                        + " c.org.idOfOrg = " + idOfOrg
                    + " order by c.idOfClientGroup, c.person.secondName";

                List clientList = session.createQuery(q).list();
                Long lastClientId = -1L;
                ReportItem.SubItem lastSubReportItem = null;
                Long lastIdGroup = -1L;
                ReportItem lastReportItem = null;
                for (Object record : clientList) {
                    Object[] client = (Object[])record;

                    ReportItem reportItem = null;
                    if (!lastIdGroup.equals((Long)client[1])) {
                        reportItem = new ReportItem((String)client[2]);
                        reportItems.add(reportItem);
                        lastReportItem = reportItem;
                        lastIdGroup = (Long)client[1];
                    } else
                        reportItem = lastReportItem;

                    String fio = String.format("%s %s %s", (String)client[3], (String)client[4], (String)client[5]);
                    if (!lastClientId.equals((Long)client[0])) {
                        lastSubReportItem = reportItem.addSubItem(fio, (String)client[6]);
                        lastClientId = (Long)client[0];
                    } else
                        lastSubReportItem.addCategory((String)client[6]);
                }

                // поиск общего количества льготников (включая тех, у кого пустая группа)
                q = "select count(distinct c.idOfClient) as fullCount"
                    + " from Client c, ClientsCategoryDiscount cc "
                    + " where c.idOfClient=cc.idOfClient and "
                        + " cc.idOfCategoryDiscount >= 0 and "
                        + " c.org.idOfOrg = " + idOfOrg;

                List result = session.createQuery(q).list();
                countResult = (Long) result.get(0);
            }

            return new OrgDiscountsReport(generateTime,  new Date().getTime() - generateTime.getTime(), reportItems, countResult);
        }



    }

    public static class ReportItem {

        private String name;
        private List<SubItem> subItemList;

        public ReportItem(String name) {
            this.name = name;
            subItemList = new ArrayList<SubItem>();
        }

        public Integer getSize() {
            return subItemList.size();
        }

        public SubItem addSubItem(String fio, String categories) {
            SubItem newSubItm = new SubItem(fio, categories);
            subItemList.add(newSubItm);
            return newSubItm;
        }

        public class SubItem {
            String fio;

            String categories;

            public SubItem(String fio, String categories) {
                this.fio = fio;
                this.categories = categories + ", ";
            }

            public String getFio() {
                return fio;
            }

            public void setFio(String fio) {
                this.fio = fio;
            }

            public String getCategories() {
                return categories.substring(0, categories.length()-2);
                //return categories;
            }

            public void addCategory(String categories) {
                this.categories += categories + ", ";
            }

        }

        public String getName() {
            return name;
        }

        public List<SubItem> getSubItemList() {
            return subItemList;
        }
    }
}
