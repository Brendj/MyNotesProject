/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.03.14
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequestsNewReportService {

    final private long OVERALL ;
    final private long OVERALL_TOTAL;
    final private String OVERALL_TOTAL_TITLE;
    final private String OVERALL_TITLE;
    final private Session session;

    public GoodRequestsNewReportService(Session session, long OVERALL, long OVERALL_TOTAL, String OVERALL_TOTAL_TITLE,
            String OVERALL_TITLE) {
        this.OVERALL = OVERALL;
        this.OVERALL_TOTAL = OVERALL_TOTAL;
        this.OVERALL_TOTAL_TITLE = OVERALL_TOTAL_TITLE;
        this.OVERALL_TITLE = OVERALL_TITLE;
        this.session = session;
    }

    public List<Item> buildRepotItems(Date startTime, Date endTime, String nameFilter, int orgFilter, int hideDailySampleValue, Date generateBeginTime, Date generateEndTime,
            List<Long> idOfOrgList, List<Long> idOfMenuSourceOrgList, boolean hideMissedColumns,
            boolean hideGeneratePeriod, int hideLastValue) {
        Criteria orgCriteria = session.createCriteria(Org.class);
        if (!CollectionUtils.isEmpty(idOfOrgList)) {
            orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
        }
        orgCriteria.createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN);
        if (!CollectionUtils.isEmpty(idOfMenuSourceOrgList)) {
            orgCriteria.add(Restrictions.in("sm.idOfOrg", idOfMenuSourceOrgList));
        }
        orgCriteria.setProjection(
                Projections.projectionList().add(Projections.property("idOfOrg"))
                        .add(Projections.property("shortName")).add(Projections.property("officialName"))
                        .add(Projections.property("sm.idOfOrg")));
        List orgList = orgCriteria.list();


        HashMap<Long, BasicReportJob.OrgShortItem> orgMap = new HashMap<Long, BasicReportJob.OrgShortItem>(orgList.size());
        for (Object obj: orgList){
            Object[] row = (Object[]) obj;
            long idOfOrg = Long.parseLong(row[0].toString());
            BasicReportJob.OrgShortItem educationItem = new BasicReportJob.OrgShortItem(idOfOrg, row[1].toString(), row[2].toString());
            if(row[3]!=null) {
                Long sourceMenuOrg = Long.parseLong(row[3].toString());
                educationItem.setSourceMenuOrg(sourceMenuOrg);
                idOfMenuSourceOrgList.add(sourceMenuOrg);
            }
            orgMap.put(idOfOrg, educationItem);
        }

        List<Item> itemList = new LinkedList<Item>();

        Date  beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        Date  endDate = CalendarUtils.truncateToDayOfMonth(endTime);
        //TreeSet<Item> items = new TreeSet<Item>();
        TreeSet<Date> dates = new TreeSet<Date>();
        /* Скрыть пустые значения, если не надо скрывать то заполним их нулями*/
        if(!hideMissedColumns){
            while (beginDate.getTime()<=endDate.getTime()) {
                itemList.add(new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", beginDate, hideDailySampleValue, hideLastValue));
                dates.add(beginDate);
                beginDate = CalendarUtils.addOneDay(beginDate);
            }
        }

        Criteria goodRequestPositionCriteria = session.createCriteria(GoodRequestPosition.class);
        goodRequestPositionCriteria.createAlias("goodRequest", "gr");
        goodRequestPositionCriteria.add(Restrictions.between("gr.doneDate", startTime, endTime));
        goodRequestPositionCriteria.add(Restrictions.in("gr.orgOwner", orgMap.keySet()));
        goodRequestPositionCriteria.add(Restrictions.isNotNull("good"));
        goodRequestPositionCriteria.add(Restrictions.eq("deletedState", false));
        goodRequestPositionCriteria.add(Restrictions.eq("gr.deletedState", false));
        if(hideGeneratePeriod){
            Disjunction dateDisjunction = Restrictions.disjunction();
            dateDisjunction.add(Restrictions.le("createdDate", generateEndTime));
            dateDisjunction.add(Restrictions.le("lastUpdate", generateEndTime));
            goodRequestPositionCriteria.add(dateDisjunction);
        }
        if(StringUtils.isNotEmpty(nameFilter)){
            goodRequestPositionCriteria.createAlias("good", "g");
            goodRequestPositionCriteria.add(Restrictions.or(
                    Restrictions.ilike("g.fullName", nameFilter, MatchMode.ANYWHERE),
                    Restrictions.ilike("g.nameOfGood", nameFilter, MatchMode.ANYWHERE)
            ));
        }


        List goodRequestPositionList = goodRequestPositionCriteria.list();

        Date doneDate = CalendarUtils.truncateToDayOfMonth(new Date(endDate.getTime()));

        for (Object obj: goodRequestPositionList){
            GoodRequestPosition position = (GoodRequestPosition) obj;
            BasicReportJob.OrgShortItem org = orgMap.get(position.getOrgOwner());

            Long totalCount = position.getTotalCount() / 1000;
            Long dailySampleCount = position.getDailySampleCount();
            if(dailySampleCount!=null) dailySampleCount = dailySampleCount/1000;

            Long newTotalCount = 0L;
            Long newDailySample = 0L;

            if(hideGeneratePeriod){
                Date createDate = position.getCreatedDate();
                if(CalendarUtils.betweenDate(createDate, generateBeginTime, generateEndTime)){
                    newTotalCount = totalCount;
                    newDailySample = dailySampleCount;
                }

                Date lastDate = position.getLastUpdate();
                if(lastDate!=null){
                    if(CalendarUtils.betweenDate(lastDate, generateBeginTime, generateEndTime)){
                        newTotalCount = totalCount - position.getLastTotalCount();
                        newDailySample = dailySampleCount - position.getLastDailySampleCount();
                    }
                }
            }

            doneDate = CalendarUtils.truncateToDayOfMonth(position.getGoodRequest().getDoneDate());

            String name = position.getGood().getFullName();
            if(StringUtils.isEmpty(name)) name = position.getGood().getNameOfGood();

            addItemsFromList(itemList, org, doneDate, name, totalCount, dailySampleCount, newTotalCount, newDailySample,
                    hideDailySampleValue, hideLastValue);
            //items.add(new Item(org, name, doneDate, hideDailySampleValue));
            dates.add(doneDate);
        }

        //if(hideDailySampleValue>0){
        //    for (Item item: items){
        //        for (Date date: dates){
        //            itemList.add( new Item(item, date));
        //            itemList.add( new Item(OVERALL, OVERALL_TITLE, item.getGoodName(), date, hideDailySampleValue));
        //            itemList.add( new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", date, hideDailySampleValue));
        //        }
        //    }
        //}

        if(orgFilter==0 && !idOfMenuSourceOrgList.isEmpty()){
            for (BasicReportJob.OrgShortItem item: orgMap.values()){
                if(item.getSourceMenuOrg()!=null){
                    MultiValueMap fullNameProviderMap = new MultiValueMap();
                    Criteria goodCriteria = session.createCriteria(Good.class);
                    goodCriteria.add(Restrictions.in("orgOwner", idOfMenuSourceOrgList));
                    if(StringUtils.isNotEmpty(nameFilter)){
                        goodCriteria.add(Restrictions.or(
                                Restrictions.ilike("fullName", nameFilter, MatchMode.ANYWHERE),
                                Restrictions.ilike("nameOfGood", nameFilter, MatchMode.ANYWHERE)
                        )
                        );
                    }
                    goodCriteria.setProjection(Projections.projectionList()
                            .add(Projections.property("fullName"))
                            .add(Projections.property("nameOfGood"))
                            .add(Projections.property("orgOwner"))
                    );
                    List goodNames = goodCriteria.list();
                    for (Object obj: goodNames){
                        Object[] row = (Object[]) obj;
                        String nameOfGood = row[0].toString();
                        if(StringUtils.isEmpty(nameOfGood)) nameOfGood = row[1].toString();
                        fullNameProviderMap.put(Long.valueOf(row[2].toString()), nameOfGood);
                    }
                    for (Object name: fullNameProviderMap.getCollection(item.getSourceMenuOrg())){
                        if(hideMissedColumns){
                            for (Date date: dates){
                                addItemsFromList(itemList, item, date, name.toString(), hideDailySampleValue, hideLastValue);
                                //itemList.add( new Item(item, name.toString(), date, hideDailySampleValue));
                                //itemList.add( new Item(OVERALL, OVERALL_TITLE, name.toString(), date, hideDailySampleValue));
                                //itemList.add( new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", date, hideDailySampleValue));
                            }
                        } else {
                            beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
                            endDate = CalendarUtils.truncateToDayOfMonth(endTime);
                            while (beginDate.getTime()<=endDate.getTime()) {
                                addItemsFromList(itemList, item, beginDate, name.toString(), hideDailySampleValue, hideLastValue);
                                //itemList.add( new Item(item, name.toString(), beginDate, hideDailySampleValue));
                                //itemList.add( new Item(OVERALL, OVERALL_TITLE, name.toString(), beginDate, hideDailySampleValue));
                                //itemList.add( new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", beginDate, hideDailySampleValue));
                                beginDate = CalendarUtils.addOneDay(beginDate);
                            }
                        }
                    }
                }
            }
        }


        // функционал не работает
        if(orgFilter==2 && !idOfMenuSourceOrgList.isEmpty()){
            for (BasicReportJob.OrgShortItem item: orgMap.values()){
                if(item.getSourceMenuOrg()!=null){
                    MultiValueMap fullNameProviderMap = new MultiValueMap();
                    Criteria goodCriteria = session.createCriteria(Good.class);
                    goodCriteria.add(Restrictions.in("orgOwner", idOfMenuSourceOrgList));
                    if(StringUtils.isNotEmpty(nameFilter)){
                        goodCriteria.add(Restrictions.or(
                                Restrictions.ilike("fullName", nameFilter, MatchMode.ANYWHERE),
                                Restrictions.ilike("nameOfGood", nameFilter, MatchMode.ANYWHERE)
                        )
                        );
                    }
                    goodCriteria.setProjection(Projections.projectionList()
                            .add(Projections.property("fullName"))
                            .add(Projections.property("nameOfGood"))
                            .add(Projections.property("orgOwner"))
                    );
                    List goodNames = goodCriteria.list();
                    for (Object obj: goodNames){
                        Object[] row = (Object[]) obj;
                        String nameOfGood = row[0].toString();
                        if(StringUtils.isEmpty(nameOfGood)) nameOfGood = row[1].toString();
                        fullNameProviderMap.put(Long.valueOf(row[2].toString()), nameOfGood);
                    }
                    for (Object name: fullNameProviderMap.getCollection(item.getSourceMenuOrg())){
                        if(hideMissedColumns){
                            Date date = CalendarUtils.truncateToDayOfMonth(new Date(endDate.getTime()));
                            itemList.add( new Item(item, name.toString(), date, hideDailySampleValue, hideLastValue));
                            itemList.add( new Item(OVERALL, OVERALL_TITLE, name.toString(), date, hideDailySampleValue, hideLastValue));
                            itemList.add( new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", date, hideDailySampleValue, hideLastValue));
                        } else {
                            beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
                            endDate = CalendarUtils.truncateToDayOfMonth(endTime);
                            while (beginDate.getTime()<=endDate.getTime()) {
                                Item e = new Item(item, name.toString(), beginDate, hideDailySampleValue, hideLastValue);
                                boolean flag = true;
                                //Iterator<Item> itemIterator = itemList.listIterator();
                                List<Item> copyList = new ArrayList<Item>(itemList);
                                for (Item i: copyList){
                                    if(i.equals(e) && i.getTotalCount()>0) {
                                        itemList.remove(i);
                                        flag = false;
                                    }
                                }
                                if(flag){
                                    itemList.add(e);
                                    itemList.add( new Item(OVERALL, OVERALL_TITLE, name.toString(), beginDate, hideDailySampleValue, hideLastValue));
                                    itemList.add( new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", beginDate, hideDailySampleValue, hideLastValue));
                                    beginDate = CalendarUtils.addOneDay(beginDate);
                                } else {
                                    copyList = new ArrayList<Item>(itemList);
                                    for (Item i: copyList){
                                        if(i.equals(e)) {
                                            itemList.remove(i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if(itemList.isEmpty()){
            for (BasicReportJob.OrgShortItem item: orgMap.values()){
                itemList.add( new Item(item, "", CalendarUtils.truncateToDayOfMonth(startTime), hideDailySampleValue, hideLastValue));
            }
            itemList.add( new Item(OVERALL, OVERALL_TITLE, "", CalendarUtils.truncateToDayOfMonth(startTime), hideDailySampleValue, hideLastValue));
            itemList.add( new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", CalendarUtils.truncateToDayOfMonth(startTime), hideDailySampleValue, hideLastValue));
        }
        return itemList;
    }

    private void addItemsFromList(List<Item> itemList, BasicReportJob.OrgShortItem org, Date doneDate, String name,
            int hideDailySampleValue, int hideLastValue) {
        itemList.add( new Item(org, name, doneDate, hideDailySampleValue, hideLastValue));
        itemList.add( new Item(OVERALL, OVERALL_TITLE, name, doneDate, hideDailySampleValue, hideLastValue));
        itemList.add( new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", doneDate, hideDailySampleValue, hideLastValue));
    }


    private void addItemsFromList(List<Item> itemList, BasicReportJob.OrgShortItem org, Date doneDate, String name,
            Long totalCount, Long dailySampleCount, Long newTotalCount, Long newDailySample, int hideDailySampleValue,
            int hideLastValue) {
        itemList.add( new Item(org, name, doneDate, totalCount, dailySampleCount,
                newTotalCount, newDailySample, hideDailySampleValue, hideLastValue));
        itemList.add( new Item(OVERALL, OVERALL_TITLE, name, doneDate, totalCount, dailySampleCount,
                newTotalCount, newDailySample, hideDailySampleValue, hideLastValue));
        itemList.add( new Item(OVERALL_TOTAL, OVERALL_TOTAL_TITLE, "", doneDate, totalCount, dailySampleCount,
                newTotalCount, newDailySample, hideDailySampleValue, hideLastValue));
    }


    public static class Item implements Comparable{
        final private static String STR_YEAR_DATE_FORMAT = "EE dd.MM";
        final private static DateFormat YEAR_DATE_FORMAT = new SimpleDateFormat(STR_YEAR_DATE_FORMAT, new Locale("ru"));
        private Long orgNum;
        private String officialName;
        private String goodName;
        private Date doneDate;
        private String doneDateStr;
        private int hideDailySample=0;
        private int hideLastValue = 0;
        private Long totalCount;
        private Long dailySample;
        private Long newTotalCount;
        private Long newDailySample;

        protected Item(Item item, Date doneDate) {
            this(item.getOrgNum(), item.getOfficialName(), item.getGoodName(), doneDate, 0L, 0L, 0L, 0L,
                    item.getHideDailySample(), item.getHideLastValue());
        }

        @Override
        public int compareTo(Object o) {
            return Integer.valueOf(hashCode()).compareTo(o.hashCode());
        }

        public Item(Long orgNum, String officialName, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue) {
            this.orgNum = orgNum;
            this.officialName = officialName;
            this.goodName = goodName;
            this.doneDate = doneDate;
            doneDateStr = YEAR_DATE_FORMAT.format(doneDate);
            this.totalCount = totalCount;
            this.dailySample = dailySample;
            this.newTotalCount = newTotalCount;
            this.newDailySample = newDailySample;
            this.hideDailySample = hideDailySampleValue;
            this.hideLastValue = hideLastValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Item item = (Item) o;

            return doneDate.equals(item.doneDate) && goodName.equals(item.goodName) && officialName
                    .equals(item.officialName);

        }

        @Override
        public int hashCode() {
            int result = officialName.hashCode();
            result = 31 * result + goodName.hashCode();
            result = 31 * result + doneDate.hashCode();
            return result;
        }

        public Item(Long orgNum, String officialName, String goodName, Date doneDate, int hideDailySampleValue, int hideLastValue) {
            this(orgNum, officialName, goodName, doneDate, 0L, 0L, 0L, 0L, hideDailySampleValue, hideLastValue);
        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, Long totalCount, Long dailySample,
                Long newTotalCount, Long newDailySample, int hideDailySampleValue, int hideLastValue) {
            this(Long.parseLong(Org.extractOrgNumberFromName(item.getOfficialName())), item.getOfficialName(),
                    goodName, doneDate, totalCount, dailySample, newTotalCount, newDailySample,
                    hideDailySampleValue, hideLastValue);
        }

        public Item(BasicReportJob.OrgShortItem item, String goodName, Date doneDate, int hideDailySampleValue, int hideLastValue) {
            this(item, goodName, doneDate, 0L, 0L, 0L, 0L, hideDailySampleValue, hideLastValue);
        }

        public Long getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(Long orgNum) {
            this.orgNum = orgNum;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public Date getDoneDate() {
            return doneDate;
        }

        public void setDoneDate(Date doneDate) {
            this.doneDate = doneDate;
        }

        public String getDoneDateStr() {
            return doneDateStr;
        }

        public void setDoneDateStr(String doneDateStr) {
            this.doneDateStr = doneDateStr;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }

        public Long getDailySample() {
            return dailySample;
        }

        public void setDailySample(Long dailySample) {
            this.dailySample = dailySample;
        }

        public Long getNewTotalCount() {
            return newTotalCount;
        }

        public void setNewTotalCount(Long newTotalCount) {
            this.newTotalCount = newTotalCount;
        }

        public Long getNewDailySample() {
            return newDailySample;
        }

        public void setNewDailySample(Long newDailySample) {
            this.newDailySample = newDailySample;
        }

        public int getHideDailySample() {
            return hideDailySample;
        }

        public void setHideDailySample(int hideDailySample) {
            this.hideDailySample = hideDailySample;
        }

        public int getHideLastValue() {
            return hideLastValue;
        }

        public void setHideLastValue(int hideLastValue) {
            this.hideLastValue = hideLastValue;
        }
    }


}
