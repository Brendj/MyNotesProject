/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.enterevents;

import ru.axetta.ecafe.processor.core.RuntimeContext;
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
import java.util.regex.Pattern;

@Component
@Scope("prototype")
public class EnterEventsMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(EnterEventsMonitoring.class);

    private static final long MINUTES10 = 10 * 60 * 1000;
    private static final long MINUTES30 = 30 * 60 * 1000;

    private static Map<Long, List<EnterEventItem>> enterEventMap = new HashMap<Long, List<EnterEventItem>>();
    private static Map<Long, String> electionAreaMap = new HashMap<Long, String>();
    private static EnterEventsMonitoringReport report = null;

    public static Map<Long, List<EnterEventItem>> getEnterEventMap() {
        return enterEventMap;
    }

    public static EnterEventsMonitoringReport getReport() {
        return report;
    }

    public static void setReport(EnterEventsMonitoringReport report) {
        EnterEventsMonitoring.report = report;
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
        if(isOn()) {
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

            Date date = new Date();

            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(date.getTime());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour < 7) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
            CalendarUtils.setHoursAndMinutes(calendar, 7, 0);
            Date morning = calendar.getTime();
            CalendarUtils.setHoursAndMinutes(calendar, 21, 0);
            Date evening = calendar.getTime();

            generateElectionAreaMap();
            runUpdateAccesories(persistenceSession);

            persistenceSession.flush();

            Map<Long, Map<Integer, EnterEventItem>> map = new HashMap<Long, Map<Integer, EnterEventItem>>();
            Map<Long, Map<String, AccessoryItem>> accMap = new HashMap<Long, Map<String, AccessoryItem>>();

            for(Object[] accessory : getAccesories(persistenceSession)) {
                long idOfOrg = (Long) accessory[0];
                String city = (String) accessory[3];
                if(StringUtils.isEmpty(city)) {
                    continue;
                }
                if(city.contains(" ")) {
                    String[] strings = city.split(" ");
                    if(strings.length > 1) {
                        city = strings[1];
                    }
                } else if (city.contains("г")) {
                    String[] strings = city.split(Pattern.quote("."));
                    if(strings.length > 1) {
                        city = strings[1];
                    }
                }
                String district = (String) accessory[4];
                if(StringUtils.isEmpty(district)) {
                    continue;
                }
                String accessoryNumber = (String) accessory[1];
                boolean usedSinceSeptember = (Boolean) accessory[2];
                String shortName = (String) accessory[5];
                String address = (String) accessory[6];

                if(accMap.get(idOfOrg) == null) {
                    accMap.put(idOfOrg, new HashMap<String, AccessoryItem>());
                }

                accMap.get(idOfOrg).put(accessoryNumber, new AccessoryItem(idOfOrg, accessoryNumber, usedSinceSeptember,
                        (accMap.get(idOfOrg).size() + 1), city.trim(), district.trim(), shortName, address));
            }

            for(Long idOfOrg : accMap.keySet()) {
                map.put(idOfOrg, new HashMap<Integer, EnterEventItem>());

                for(String accessoryNumber : accMap.get(idOfOrg).keySet()) {
                    AccessoryItem item = accMap.get(idOfOrg).get(accessoryNumber);
                    String electionArea = electionAreaMap.get(idOfOrg);
                    String orgNumStr = Org.extractOrgNumberFromName(item.getOrgShortName());
                    Integer orgNum = null;
                    if(!StringUtils.isEmpty(orgNumStr)) {
                        orgNum = Integer.parseInt(orgNumStr);
                    }
                    int colorType = item.isUsedSinceSeptember() ? EnterEventItem.COLOR_BLUE : EnterEventItem.COLOR_GRAY;
                    map.get(idOfOrg).put(item.getTurnStileNumber(),
                            new EnterEventItem(idOfOrg, item.getCity(), item.getDistrict(),
                                    orgNum, item.getOrgShortName(), item.getAddress(),
                            electionArea, item.getTurnStileNumber(), accessoryNumber, colorType));
                }
            }

            for(Object[] enterEvent : getEnterEvents(persistenceSession, morning, evening)) {
                String turnstileAddr = (String)enterEvent[2];
                if(turnstileAddr.length() < 12) {
                    continue;
                }
                int passDirection = (Integer) enterEvent[1];
                if(turnstileAddr.length() == 12 && passDirection == 1) {
                    continue;
                }
                long idOfOrg = (Long) enterEvent[0];
                int turnstile = accMap.get(idOfOrg).get(turnstileAddr).getTurnStileNumber();

                EnterEventItem item = map.get(idOfOrg).get(turnstile);

                if(item.getEventCount() == null) {
                    Date evtDateTime = (Date) enterEvent[3];
                    item.setEventCount(1);
                    item.setEvtDateTime(evtDateTime);
                } else {
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
                    if(item.getEventCount() != null && item.getEventCount() != 0) {
                        if(item.getTurnstileAddr().length() > 12) {
                            item.setEventCount((item.getEventCount() + 1) / 2);
                        }
                        long delay = date.getTime() - item.getEvtDateTime().getTime();
                        if(delay <= MINUTES10) {
                            item.setColorType(EnterEventItem.COLOR_GREEN);
                        } else if (delay <= MINUTES30) {
                            item.setColorType(EnterEventItem.COLOR_YELLOW);
                        } else {
                            item.setColorType(EnterEventItem.COLOR_RED);
                        }
                    }
                    result.get(idOfOrg).add(item);
                }
            }

            report = null;
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
        Query query = session.createQuery("SELECT ac.idOfSourceOrg, ac.accessoryNumber, ac.usedSinceSeptember, "
                + "o.city, o.district, o.shortNameInfoService, o.shortAddress from Accessory AS ac, Org AS o "
                + "WHERE ac.idOfSourceOrg = o.idOfOrg AND length(ac.accessoryNumber) > 11 "
                + "AND ac.accessoryType = 2 ORDER BY ac.idOfAccessory");
        List<Object[]> result = (List<Object[]>) query.list();
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getEnterEvents(Session session, Date start, Date end) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(new Date().getTime());
        CalendarUtils.setHoursAndMinutes(calendar, 7, 0);

        Criteria criteria = session.createCriteria(EnterEvent.class);
        criteria.add(Restrictions.between("evtDateTime", start, end));
        criteria.add(Restrictions.eq("eventCode", 112));
        //criteria.add(Restrictions.sqlRestriction("length({alias}.turnstileAddr) > 11"));
        criteria.add(Restrictions.or(
                Restrictions.eq("passDirection", 0),
                Restrictions.eq("passDirection", 1)));
        criteria.add(Restrictions.isNull("idOfCard"));
        criteria.add(Restrictions.isNull("client"));
        criteria.add(Restrictions.isNull("idOfVisitor"));
        criteria.addOrder(Order.desc("evtDateTime"));
        criteria.createAlias("org", "org");
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("org.idOfOrg"))
                .add(Projections.property("passDirection"))
                .add(Projections.property("turnstileAddr"))
                .add(Projections.property("evtDateTime")));
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

    public static class AccessoryItem {
        private long idOfSourceOrg;
        private String accessoryNumber;
        private boolean usedSinceSeptember;
        private int turnStileNumber;
        private String city;
        private String district;
        private String orgShortName;
        private String address;

        public AccessoryItem(long idOfSourceOrg, String accessoryNumber, boolean usedSinceSeptember,
                int turnStileNumber, String city, String district, String orgShortName, String address) {
            this.idOfSourceOrg = idOfSourceOrg;
            this.accessoryNumber = accessoryNumber;
            this.usedSinceSeptember = usedSinceSeptember;
            this.turnStileNumber = turnStileNumber;
            this.city = city;
            this.district = district;
            this.orgShortName = orgShortName;
            this.address = address;
        }

        public long getIdOfSourceOrg() {
            return idOfSourceOrg;
        }

        public void setIdOfSourceOrg(long idOfSourceOrg) {
            this.idOfSourceOrg = idOfSourceOrg;
        }

        public String getAccessoryNumber() {
            return accessoryNumber;
        }

        public void setAccessoryNumber(String accessoryNumber) {
            this.accessoryNumber = accessoryNumber;
        }

        public boolean isUsedSinceSeptember() {
            return usedSinceSeptember;
        }

        public void setUsedSinceSeptember(boolean usedSinceSeptember) {
            this.usedSinceSeptember = usedSinceSeptember;
        }

        public int getTurnStileNumber() {
            return turnStileNumber;
        }

        public void setTurnStileNumber(int turnStileNumber) {
            this.turnStileNumber = turnStileNumber;
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
    }

    public class EnterEventItem {
        private static final int COLOR_GREEN = 1;
        private static final int COLOR_YELLOW = 2;
        private static final int COLOR_RED = 3;
        private static final int COLOR_BLUE = 4;
        private static final int COLOR_GRAY = 5;

        private Long idOfOrg;
        private String city;
        private String district;
        private Integer orgNum;
        private String orgShortName;
        private String address;
        private String electionArea;
        private int turnstile;
        private String turnstileAddr;
        private Integer eventCount;
        private int colorType;
        private Date evtDateTime;

        public EnterEventItem(Long idOfOrg, String city, String district, Integer orgNum, String orgShortName,
                String address, String electionArea, int turnstile, String turnstileAddr, int colorType) {
            this.idOfOrg = idOfOrg;
            this.city = city;
            this.district = district;
            this.orgNum = orgNum;
            this.orgShortName = orgShortName;
            this.address = address;
            this.electionArea = electionArea;
            this.turnstile = turnstile;
            this.turnstileAddr = turnstileAddr;
            this.colorType = colorType;
            this.eventCount = null;
            this.evtDateTime = null;
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

        public Integer getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(Integer orgNum) {
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

        public Integer getEventCount() {
            return eventCount;
        }

        public void setEventCount(Integer eventCount) {
            this.eventCount = eventCount;
        }

        public int getColorType() {
            return colorType;
        }

        public void setColorType(int colorType) {
            this.colorType = colorType;
        }

        public Date getEvtDateTime() {
            return evtDateTime;
        }

        public void setEvtDateTime(Date evtDateTime) {
            this.evtDateTime = evtDateTime;
        }
    }
}
