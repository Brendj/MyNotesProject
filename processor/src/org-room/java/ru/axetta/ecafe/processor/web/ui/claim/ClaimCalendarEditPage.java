/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.claim;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.GoodRequestService;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequestPosition;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.modal.YesNoEvent;
import ru.axetta.ecafe.processor.web.ui.modal.YesNoListener;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 15.08.13
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ClaimCalendarEditPage extends BasicWorkspacePage implements YesNoListener{
    public static final long MILLIS_IN_MONTH = 2628000000L;
    private static final Logger logger = LoggerFactory.getLogger(ClaimCalendarEditPage.class);
    private final DateFormat comboboxDF = new SimpleDateFormat ("MMMMM yyyy", new Locale("ru"));
    private final DateFormat columnDF = new SimpleDateFormat ("dd MMM", new Locale("ru"));

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private GoodRequestService goodRequestService;

    private String errorMessages;
    private String infoMessages;
    private Org org;
    private List<Entry> entries = new ArrayList<Entry>();
    private List<DateColumn> columns = new ArrayList<DateColumn>();
    private Long month;
    private Long newMonth;
    private Long prevMonth;
    private boolean changesMade;




    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    @Transactional
    public Org getOrg() {
        if (org != null) {
            return org;
        }
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            return getOrg(session);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
        return null;
    }

    public Org getOrg(Session session) {
        if (org != null) {
            return org;
        }
        org = (Org) session.get(Org.class, 0L);
        return org;
    }

    public void fill() {
        RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).fill(true);
    }

    @Transactional
    public void fill(boolean buildData) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session, buildData);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session, boolean buildData) throws Exception {
        //  Устанавливаем даты, за которые следует отобразить таблицу
        Calendar start = new GregorianCalendar();
        start.setTimeInMillis(month);
        resetDate(start);
        Calendar end = new GregorianCalendar();
        end.setTimeInMillis(month);
        end.set(Calendar.MONTH, end.get(Calendar.MONTH) + 1);
        resetDate(end);

        //  Загружаем данные и обновляем колонки
        if (buildData) {
            buildData(session, start, end);
            changesMade = false;
        }
        buildColumns(start, end);
    }

    private void buildData(Session session, Calendar dateFrom, Calendar dateTo) {
        resetDate (dateFrom);
        resetDate (dateTo);

        
        Map <String, List<long []>> tmpData = new TreeMap <String, List <long []>> ();
        entries.clear();
        //  Загружам заявки из БД
        Integer deletedState = 2;
        List<DocumentState> stateList = new ArrayList<DocumentState>();
        for (DocumentState i: DocumentState.values()){
            stateList.add(i);
        }
        List<GoodRequest> goodRequestList = goodRequestService.findByFilter(getOrg().getIdOfOrg(),stateList,
                                                                            dateFrom.getTime(),dateTo.getTime(),
                                                                            deletedState);
        for (GoodRequest gr : goodRequestList) {
            List<GoodRequestPosition> positions = goodRequestService.getGoodRequestPositionByGoodRequest(gr);
            for (GoodRequestPosition pos : positions) {
                pos = entityManager.merge(pos);

                String good = pos.getGood().getFullName();
                Long count = pos.getTotalCount();
                long ts = gr.getDateOfGoodsRequest().getTime();
                Long idofgoodsrequestposition = pos.getGlobalId();

                List<long []> e = tmpData.get(good);
                if (e == null) {
                    e = new ArrayList<long[]>();
                    tmpData.put(good, e);
                }
                e.add(new long[] { ts, count, idofgoodsrequestposition });
            }
        }

        for (String good : tmpData.keySet()) {
            List <long []> e = tmpData.get(good);
            Entry entry = new Entry (good);
            for (long d [] : e) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(d[0]);
                resetDate(cal);
                entry.add(cal.getTimeInMillis(), d[1], d[2]);
            }
            entries.add(entry);
        }/*

        Entry e = new Entry ("Комплекс 1");
        e.add(1375387200000L, 120);
        e.add(1375560000000L, 140);
        entries.add(e);

        e = new Entry ("Комплекс 2");
        e.add(1375473600000L, 130);
        entries.add(e);*/
    }

    private void buildColumns (Calendar dateFrom, Calendar dateTo) {
        columns.clear();
        long start = dateFrom.getTimeInMillis();
        long end = dateTo.getTimeInMillis();
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(dateFrom.getTimeInMillis());
        Calendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(dateTo.getTimeInMillis());

        for (; start<end; ) {
            columns.add(new DateColumn(startDate.getTimeInMillis(), columnDF.format(startDate.getTime())));

            startDate.set(Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH) + 1);
            start = startDate.getTimeInMillis();
        }
    }

    @Transactional
    public void save() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            save(session);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void save(Session session) throws Exception {
        for (Entry e : entries) {
            for (long ts : e.data.keySet()) {
                Long v = e.data.get(ts);
                Long id = e.ids.get(ts);

                if (id != null) {
                    GoodRequestPosition pos = goodRequestService.findGoodRequestPositionById(id);
                    //goodRequestService.save();
                }
            }
        }
    }





    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    public void doChangeMonth (ActionEvent actionEvent) {
        prevMonth = month;
        RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).fill();
    }

    public void doValueChange (ActionEvent actionEvent) {
        changesMade = true;
    }

    public void doApply () {
        RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).save();
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<DateColumn> getColumns() {
        return columns;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public boolean getChangesMade() {
        return changesMade;
    }

    public List<SelectItem> getMonths() {
        List<SelectItem> result = new ArrayList<SelectItem>();
        //  Устанавливаем календарь
        Calendar startDate = new GregorianCalendar();
        startDate.set(Calendar.YEAR, 2012);
        startDate.set(Calendar.MONTH, Calendar.SEPTEMBER);
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        resetDate(startDate);
        long start = startDate.getTimeInMillis();
        long end = System.currentTimeMillis();

        for (; start<end; ) {
            String name = comboboxDF.format(startDate.getTime());
            result.add(new SelectItem(startDate.getTimeInMillis(), name));

            startDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH) + 1);
            start = startDate.getTimeInMillis();
        }
        return result;
    }





    /**
     * ****************************************************************************************************************
     * Работа с данными
     * ****************************************************************************************************************
     */
    public void onYesNoEvent(YesNoEvent event) {
        if (event.isYes()) {
            RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).fill();
        } else {
            month = prevMonth;
            RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).fill(false);
        }
    }








    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public String getPageFilename() {
        return "claim/claim_calendar";
    }

    public String getPageTitle() {
        return "Заявки на питание";
    }

    public void resetMessages() {
        errorMessages = "";
        infoMessages = "";
    }

    public void sendError(String message) {
        errorMessages = message;
    }

    public void sendInfo(String message) {
        infoMessages = message;
    }
    
    private void resetDate (Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    public static class Entry {
        private String food;
        private Map<Long, Long> data;
        private Map<Long, Long> ids;
        
        public Entry (String food) {
            this.food = food;
            data = new HashMap<Long, Long>();
            ids = new HashMap<Long, Long>();
        }

        public String getFood() {
            return food;
        }

        public Map<Long, Long> getData() {
            return data;
        }

        public void setData(Map<Long, Long> data) {
            this.data = data;
        }

        public void add(long date, long value, long idofgoodsrequestposition) {
            data.put(date, value);
            ids.put(date, idofgoodsrequestposition);
        }
    }

    public static class DateColumn {
        private long date;
        private String title;

        public DateColumn (long date, String title) {
            this.date = date;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }
    }
}