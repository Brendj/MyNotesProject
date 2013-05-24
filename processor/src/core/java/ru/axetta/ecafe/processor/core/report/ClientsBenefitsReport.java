/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 24.05.13
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class ClientsBenefitsReport extends BasicReport {
    private final List<Entry> items;
    private List<String> columnNames;
    public static final String COLUMN_TITLE = "Комплекс ";
    public static final String TOTAL_CAPTION = "Итого:";


    public static class Builder {

        public ClientsBenefitsReport build(Session session, Date startDate, Date endDate, Long idofOrg)
                throws Exception {
            Date generateTime = new Date();
            List<Entry> entries = new ArrayList<Entry>();
            Entry total = new TotalEntry(TOTAL_CAPTION);

            String orgCondition = "";
            if (idofOrg != null) {
                orgCondition = "where cf_orgs.idoforg=" + idofOrg + " ";
            }

            String preparedQuery =
                              "select cf_discountrules.description, cf_clientscomplexdiscounts.idofcomplex, count(distinct cf_clientscomplexdiscounts.idofclient) "
                            + "from cf_clientscomplexdiscounts "
                            + "left join cf_discountrules on cf_discountrules.idofrule=cf_clientscomplexdiscounts.idofrule "
                            + "join cf_clients on cf_clientscomplexdiscounts.idofclient=cf_clients.idofclient "
                            + "join cf_orgs on cf_clients.idoforg=cf_orgs.idoforg "
                            + orgCondition
                            + "group by cf_discountrules.description, cf_clientscomplexdiscounts.idofcomplex "
                            + "order by cf_discountrules.description, cf_clientscomplexdiscounts.idofcomplex";
            Query query = session.createSQLQuery(preparedQuery);
            List resultList = query.list();

            Entry entry = null;
            for (Object result : resultList) {
                Object e[]  = (Object[]) result;
                String rule = ((String) e[0]).trim();
                int complex = ((Integer) e[1]).intValue();
                long count  = ((BigInteger) e[2]).longValue();

                if (entry == null || !entry.getRule().equals(rule)) {
                    entry = new Entry(rule);
                    entries.add(entry);
                }

                entry.put(complex, count);
                total.put(complex, count);
            }
            entries.add(total);


            return new ClientsBenefitsReport(generateTime, new Date().getTime() - generateTime.getTime(), entries);
        }
    }


    public ClientsBenefitsReport() {
        super();
        this.items = Collections.emptyList();
    }


    public ClientsBenefitsReport(Date generateTime, long generateDuration, List<Entry> items) {
        super(generateTime, generateDuration);
        this.items = items;
    }

    public List <String> getColumnNames () {
        if (columnNames != null && !columnNames.isEmpty()) {
            return columnNames;
        }
        columnNames = new ArrayList <String> ();
        for (int i=0; i<50; i++) {
            columnNames.add(COLUMN_TITLE + i);
        }
        return columnNames;
    }


    public static class TotalEntry extends Entry {
        public TotalEntry (String rule) {
            super(rule);
        }


        @Override
        public void put(int complex, long count) {
            Long v = data.get(complex);
            if (v != null) {
                count = count + v;
            }
            data.put(complex, count);
        }
    }


    public static class Entry {

        protected String rule;
        protected Map <Integer, Long> data;


        public Map <Integer, Long> getData() {
            return data;
        }


        public void put(int complex, long count) {
            data.put(complex, count);
        }

        public long get (String col) {
            if (col == null || col.length() < 1) {
                return 0L;
            }

            int complex = -1;
            try {
                complex = Integer.parseInt(col.replaceAll(COLUMN_TITLE, ""));
            } catch (Exception e) {
                return 0L;
            }


            if (data != null) {
                Long v = data.get(complex);
                return v == null ? 0L : v;
            }
            return 0L;
        }

        public String getRule () {
            return rule;
        }

        public Entry(String rule) {
            this.rule = rule;
            data = new HashMap <Integer, Long>();
        }
    }

    public List<Entry> getItems() {
        return items;
    }
}