/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.enterevents;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Accessory;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
@Scope("prototype")
public class EnterEventsMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(EnterEventsMonitoring.class);

    private static Map<Long, List<EnterEventItem>> enterEventMap = new HashMap<Long, List<EnterEventItem>>();
    private static Map<Long, String> electionAreaMap = new HashMap<Long, String>();

    public static Map<Long, List<EnterEventItem>> getEnterEventMap() {
        return enterEventMap;
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.entereventsmonitoring.node", "1");
        return !(StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim()));
    }

    /**
     * Обертка для запуска по расписанию
     */
    public void calculateEnterEvents() throws Exception {
        if(isOn()){
            if(!checkDateTime()){
                return;
            }
            logger.info("Starting calculate EnterEvent data for monitoring.");
            calculateEnterEventsDay();
            logger.info("EnterEvent data for monitoring calculated.");
        }
    }

    public void calculateEnterEventsDay() throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            generateElectionAreaMap();
            runUpdateAccesories(persistenceSession);

            Map<Long, Map<String, Integer>> accMap = new HashMap<Long, Map<String, Integer>>();
            for(Object[] accessory : getAccesories(persistenceSession)) {
                long idOfOrg = (Long) accessory[0];
                String accessoryNumber = (String) accessory[1];

                if(accMap.get(idOfOrg) == null) {
                    accMap.put(idOfOrg, new HashMap<String, Integer>());
                }

                accMap.get(idOfOrg).put(accessoryNumber, accMap.get(idOfOrg).size() + 1);
            }

            Map<Long, Map<Integer, EnterEventItem>> map = new HashMap<Long, Map<Integer, EnterEventItem>>();
            for(Object[] enterEvent : getEnterEvents(persistenceSession)) {
                long idOfOrg = (Long) enterEvent[0];
                int passDirection = (Integer) enterEvent[5];
                String turnstileAddr = (String)enterEvent[6];
                if(turnstileAddr.length() == 12 && passDirection == 1) {
                    continue;
                }
                int turnstile = accMap.get(idOfOrg).get(turnstileAddr);

                if(map.get(idOfOrg) == null) {
                    map.put(idOfOrg, new HashMap<Integer, EnterEventItem>());
                }

                if(map.get(idOfOrg).get(turnstile) == null) {
                    String city = (String) enterEvent[1];
                    String district = (String) enterEvent[2];
                    String shortName = (String) enterEvent[3];
                    String address = (String)enterEvent[4];
                    String electionArea = electionAreaMap.get(idOfOrg);
                    int orgNum = Integer.parseInt(Org.extractOrgNumberFromName(shortName));
                    if(electionArea == null) {
                        electionArea = "";
                    }
                    map.get(idOfOrg).put(turnstile, new EnterEventItem(idOfOrg, city, district, orgNum, shortName, address,
                            electionArea, turnstile, turnstileAddr, 1, 1));
                } else {
                    EnterEventItem item = map.get(idOfOrg).get(turnstile);
                    item.setEventCount(item.getEventCount() + 1);
                }
            }

            Map<Long, List<EnterEventItem>> result = new HashMap<Long, List<EnterEventItem>>();
            for(Long idOfOrg : map.keySet()) {
                for(Integer turnstile : map.get(idOfOrg).keySet()) {
                    if(result.get(idOfOrg) == null) {
                        result.put(idOfOrg, new ArrayList<EnterEventItem>());
                    }
                    EnterEventItem item = map.get(idOfOrg).get(turnstile);
                    if(item.getTurnstileAddr().length() > 12) {
                        item.setEventCount(item.getEventCount() / 2);
                    }
                    result.get(idOfOrg).add(map.get(idOfOrg).get(turnstile));
                }
            }

            enterEventMap = result;

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getAccesories(Session session) {
        Criteria criteria = session.createCriteria(Accessory.class);
        criteria.add(Restrictions.eq("accessoryType", 2));
        /*criteria.add(Restrictions.or(
                Restrictions.sqlRestriction("length({alias}.accessoryNumber) = 12"),
                Restrictions.like("accessoryNumber", "ERA%")));*/
        criteria.addOrder(Order.asc("idOfAccessory"));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("idOfSourceOrg"))
                .add(Projections.property("accessoryNumber")));
        return (List<Object[]>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getEnterEvents(Session session) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(new Date().getTime());
        CalendarUtils.setHoursAndMinutes(calendar, 7, 0);

        Criteria criteria = session.createCriteria(EnterEvent.class);
        criteria.add(Restrictions.ge("evtDateTime", calendar.getTime()));
        criteria.add(Restrictions.eq("eventCode", 112));
        /*criteria.add(Restrictions.or(
                Restrictions.sqlRestriction("length({alias}.turnstileAddr) = 12"),
                Restrictions.like("turnstileAddr", "ERA%")));*/
        criteria.add(Restrictions.or(
                Restrictions.eq("passDirection", 0),
                Restrictions.eq("passDirection", 1)));
        criteria.add(Restrictions.isNull("idOfCard"));
        criteria.add(Restrictions.isNull("client"));
        criteria.add(Restrictions.isNull("idOfVisitor"));

        criteria.createAlias("org", "org");
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("org.idOfOrg"))
                .add(Projections.property("org.city"))
                .add(Projections.property("org.district"))
                .add(Projections.property("org.shortName"))
                .add(Projections.property("org.address"))
                .add(Projections.property("passDirection"))
                .add(Projections.property("turnstileAddr")));
        return (List<Object[]>) criteria.list();
    }

    private void generateElectionAreaMap() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String filePath = runtimeContext.getConfigProperties().getProperty("ecafe.processor.entereventsmonitoring.csvpath", "");
        BufferedReader br = null;
        String line;
        Map<Long, String> map = new HashMap<Long, String>();
        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length < 1 || data[0].isEmpty() || data.length < 2 || data[1].isEmpty()) {
                    continue;
                }
                Long idOfOrg = Long.parseLong(data[0]);
                String electionArea = data[1];
                map.put(idOfOrg, electionArea);
            }
            electionAreaMap = map;
        } catch (FileNotFoundException ignore) {
            logger.info("ElectionArea File for EnterEvent monitoring not found!");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private void runUpdateAccesories(Session session) {
        Query q = session.createSQLQuery("UPDATE cf_org_accessories oa SET usedsinceseptember = temp.result "
                + " FROM (SELECT e.idoforg AS idoforg, e.turnstileAddr AS turnstileAddr, TRUE AS result"
                + " FROM cf_enterevents e WHERE e.passdirection IN (0, 1) "
                + " AND e.EvtDateTime > 1472677200000 AND length(e.turnstileaddr) > 11"
                + " GROUP BY e.idoforg, e.turnstileAddr) AS temp WHERE"
                + " temp.idoforg=oa.idofsourceorg AND temp.turnstileAddr=oa.accessorynumber"
                + " AND oa.usedsinceseptember = FALSE AND oa.accessorytype = 2");
        q.executeUpdate();
    }

    private boolean checkDateTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(new Date().getTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour > 6 && hour < 21;
    }

    public class EnterEventItem {
        private Long idOfOrg;
        private String city;
        private String district;
        private int orgNum;
        private String orgShortName;
        private String address;
        private String electionArea;
        private int turnstile;
        private String turnstileAddr;
        private int eventCount;
        private int colorType;

        public EnterEventItem(Long idOfOrg, String city, String district, int orgNum, String orgShortName,
                String address, String electionArea, int turnstile, String turnstileAddr, int eventCount,
                int colorType) {
            this.idOfOrg = idOfOrg;
            this.city = city;
            this.district = district;
            this.orgNum = orgNum;
            this.orgShortName = orgShortName;
            this.address = address;
            this.electionArea = electionArea;
            this.turnstile = turnstile;
            this.turnstileAddr = turnstileAddr;
            this.eventCount = eventCount;
            this.colorType = colorType;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public int getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(int orgNum) {
            this.orgNum = orgNum;
        }

        public String getOrgShortName() {
            return orgShortName;
        }

        public void setOrgShortName(String orgShortName) {
            this.orgShortName = orgShortName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getElectionArea() {
            return electionArea;
        }

        public void setElectionArea(String electionArea) {
            this.electionArea = electionArea;
        }

        public int getTurnstile() {
            return turnstile;
        }

        public void setTurnstile(int turnstile) {
            this.turnstile = turnstile;
        }

        public String getTurnstileAddr() {
            return turnstileAddr;
        }

        public void setTurnstileAddr(String turnstileAddr) {
            this.turnstileAddr = turnstileAddr;
        }

        public int getEventCount() {
            return eventCount;
        }

        public void setEventCount(int eventCount) {
            this.eventCount = eventCount;
        }

        public int getColorType() {
            return colorType;
        }

        public void setColorType(int colorType) {
            this.colorType = colorType;
        }
    }
}
