/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.claim;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.GoodRequestService;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
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
import javax.persistence.TypedQuery;
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
    public static final long DEFAULT_EDITABLE_DAYS = 2L;
    public static final long MILLIS_IN_MONTH = 2628000000L;
    private static final Logger logger = LoggerFactory.getLogger(ClaimCalendarEditPage.class);
    private final DateFormat comboboxDF = new SimpleDateFormat ("MMMMM yyyy", new Locale("ru"));
    private final DateFormat columnDF = new SimpleDateFormat ("dd MMM", new Locale("ru"));
    private static final String NEW_CLAIM_COMMENT = "- Добавлено в тонком клиенте -";
    public static final String OVERALL_TITLE = "ИТОГО";
    public static final long OVERALL_GLOBAL_ID = Long.MIN_VALUE;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private GoodRequestService goodRequestService;

    private String errorMessages;
    private String infoMessages;
    private Org org;
    private List<Entry> entries = new ArrayList<Entry>();
    private List<DateColumn> columns = new ArrayList<DateColumn>();
    private Map<Long, String> goodsGroups;
    private Long goodGroup;
    private Long month;
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
            logger.error("Failed to load claims data", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session, boolean buildData) throws Exception {
        //  Устанавливаем даты, за которые следует отобразить таблицу
        Calendar start = new GregorianCalendar();
        start.setTimeInMillis(getMonth());
        resetDate(start);
        Calendar end = new GregorianCalendar();
        end.setTimeInMillis(getMonth());
        end.set(Calendar.MONTH, end.get(Calendar.MONTH) + 1);
        resetDate(end);
        resetDate(start);
        resetDate (end);

        //  Загружаем данные и обновляем колонки
        if (buildData) {
            buildData(session, start, end);
            changesMade = false;
        }
        buildColumns(start, end);
        buildGoodsGroups(session);
    }

    private void buildData(Session session, Calendar start, Calendar end) {
        Calendar dateFrom = new GregorianCalendar();
        dateFrom.setTimeInMillis(start.getTimeInMillis() - getCalendarPadding());
        Calendar dateTo = new GregorianCalendar();
        dateTo.setTimeInMillis(end.getTimeInMillis() + getCalendarPadding());

        //  Загружаем все товары, которые есть
        List<Good> goods = DAOUtils.getAllGoods(session, goodGroup);
        Map <Long, Long> overall = new HashMap<Long, Long>();


        Map <String, List<long []>> tmpData = new TreeMap <String, List <long []>> ();
        entries.clear();
        //  Загружам заявки из БД через методы core модуля
        Integer deletedState = 2;
        List<DocumentState> stateList = new ArrayList<DocumentState>();
        for (DocumentState i: DocumentState.values()){
            stateList.add(i);
        }
        List<GoodRequest> goodRequestList = goodRequestService.findByFilter(getOrg().getIdOfOrg(),stateList,
                                                                            dateFrom.getTime(),dateTo.getTime(),
                                                                            deletedState, goodGroup);
        for (GoodRequest gr : goodRequestList) {
            List<GoodRequestPosition> positions = goodRequestService.getGoodRequestPositionByGoodRequest(gr);
            for (GoodRequestPosition pos : positions) {
                pos = entityManager.merge(pos);

                String good = pos.getGood().getFullName();
                Long count = pos.getTotalCount() / 1000;
                long ts = gr.getDateOfGoodsRequest().getTime();
                Long idofgoodsrequestposition = pos.getGlobalId();

                List<long []> e = tmpData.get(good);
                if (e == null) {
                    e = new ArrayList<long[]>();
                    tmpData.put(good, e);
                }
                e.add(new long[] { ts, count, idofgoodsrequestposition });

                Long total = overall.get(ts);
                if (total == null) {
                    total = 0L;
                }
                overall.put(ts, total + count);
            }
        }


        //  Листаем ВСЕ товары и соотносим их с заявками
        for (Good g : goods) {
            String goodName = g.getFullName();
            List <long []> e = tmpData.get(goodName);   //  Ищем по полному имени
            Entry entry = new Entry (goodName, g.getGlobalId());
            if (e != null && e.size() > 0) {
                for (long d [] : e) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(d[0]);
                    resetDate(cal);
                    entry.add(cal.getTimeInMillis(), d[1], d[2]);
                }
            }
            entries.add(entry);
        }
        Entry overallEntry = new Entry(OVERALL_TITLE, OVERALL_GLOBAL_ID);
        for (Long ts : overall.keySet()) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ts);
            resetDate(cal);
            overallEntry.add(cal.getTimeInMillis(), overall.get(ts), OVERALL_GLOBAL_ID);
        }
        entries.add(overallEntry);
    }


    private void buildColumns (Calendar dateFrom, Calendar dateTo) {
        columns.clear();
        long start = dateFrom.getTimeInMillis() - getCalendarPadding();
        long end = dateTo.getTimeInMillis() + getCalendarPadding();
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(start);
        Calendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(dateTo.getTimeInMillis());

        for (; start<end; ) {
            columns.add(new DateColumn(startDate.getTimeInMillis(), columnDF.format(startDate.getTime())));
            startDate.set(Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH) + 1);
            start = startDate.getTimeInMillis();
        }
    }

    private void buildGoodsGroups(Session session) {
        if (goodsGroups != null) {
            return;
        }
        goodsGroups = ru.axetta.ecafe.processor.core.dao.DAOServices.getInstance().loadGoodsGroups(session);
    }

    @Transactional
    public void save() {
        Session session = null;
        try {
            resetMessages();
            session = (Session) entityManager.getDelegate();
            save(session);
            fill(session, true);
            sendInfo("Изменения в заявки успешно внесены");
            changesMade = false;
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
                Long v = e.getDataForDate(ts);
                if (v == null) {
                    continue;
                }
                v = v * 1000;
                List<Long> ids = e.ids.get(ts);

                //  Если списка id не существует, это обозначает что значения добавлены, необходимо создавать заявку
                if (ids == null) {
                    goodRequestService.createGoodRequestWithPosition(getOrg().getIdOfOrg(),
                                                                     e.getIdofgood(), ts, v, NEW_CLAIM_COMMENT);
                }
                //  Иначе - скидываем у всех заявок значения в 0, кроме первой
                else {
                    //  Получаем каждую
                    for (int i=0; i<ids.size(); i++) {
                        if (ids.get(i).longValue() == Long.MIN_VALUE) {
                            continue;
                        }
                        GoodRequestPosition pos = goodRequestService.findGoodRequestPositionById(ids.get(i));
                        if (i == 0) {
                            pos.setTotalCount(v);
                        } else {
                            pos.setTotalCount(0L);
                        }
                        pos.setLastUpdate(new Date(System.currentTimeMillis()));
                        goodRequestService.save(pos);
                    }
                }
            }
        }
    }





    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).fill();
    }

    public void doChangeMonth (ActionEvent actionEvent) {
        prevMonth = month;
        RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).fill();
    }

    public void doChangeGoodsGroup (ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).fill();
    }

    public void doValueChange (ActionEvent actionEvent) {
        changesMade = true;
    }

    public void doApply () {
        RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).save();
    }

    public void doCancel () {
        RuntimeContext.getAppContext().getBean(ClaimCalendarEditPage.class).fill();
    }

    public void doValidateValue (javax.faces.event.ValueChangeEvent event) {
        resetMessages();
        try {
            String val = event.getNewValue().toString();
            Long.parseLong(val);
        } catch (Exception e) {
            sendError("Значение не может быть сохранено, допускаются только цифровые значения");
        }
    }

    public boolean isEditable (long idofgood, long ts) {
        //  Если это ИТОГО, то делаем его не редактируемым
        if (idofgood == OVERALL_GLOBAL_ID) {
            return false;
        }

        //  Сравниваем даты
        Calendar mon = new GregorianCalendar();
        mon.setTimeInMillis(getMonth());
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(ts);
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis()+ getEditableDateIncrement ());
        resetDate(cal);
        resetDate(now);
        if (cal.getTimeInMillis() > now.getTimeInMillis()) {
            if (mon.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
                return true;
            }
        }
        return false;
    }

    public String getColumnColor (long ts) {
        //  Готовим даты
        Calendar mon = new GregorianCalendar();
        mon.setTimeInMillis(getMonth());
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(ts);
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());
        Calendar edit = new GregorianCalendar();
        edit.setTimeInMillis(System.currentTimeMillis()+ getEditableDateIncrement ());
        resetDate(now);
        resetDate(cal);
        resetDate(mon);
        resetDate(edit);


        //  Если выходит за границы месяца, т.е. являются добавочными датами с других месяцов, то СЕРЫЙ
        if (mon.get(Calendar.MONTH) != cal.get(Calendar.MONTH)) {
            return "background-color: #DEDEDE;";
        //  Если это не редактируемые следующие дни, то ЖЕЛТЫЙ
        } else if (cal.getTimeInMillis() > now.getTimeInMillis() &&
                   cal.getTimeInMillis() <= edit.getTimeInMillis()) {
            return "background-color: #FDFFE0";
        //  Если текущий день, то ЗЕЛЕНЫЙ
        } else if (now.getTimeInMillis() == ts) {
            return "background-color: #E0FFE7";
        } else {
            return "";
        }
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
        if (month == null) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            resetDate(cal);
            return cal.getTimeInMillis();
        }
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public boolean getChangesMade() {
        return changesMade;
    }
    
    public List<SelectItem> getGoodsGroups () {
        List<SelectItem> result = new ArrayList<SelectItem>();
        result.add(new SelectItem(Long.MIN_VALUE, "Все"));
        for (Long idofgoodgroup : goodsGroups.keySet()) {
            result.add(new SelectItem(idofgoodgroup, goodsGroups.get(idofgoodgroup)));
        }
        return result;
    }

    public List<SelectItem> getMonths() {
        List<SelectItem> result = new ArrayList<SelectItem>();
        //  Устанавливаем календарь
        Calendar startDate = new GregorianCalendar();
        startDate.set(Calendar.YEAR, 2012);
        startDate.set(Calendar.MONTH, Calendar.SEPTEMBER);
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        resetDate(startDate);
        Calendar endDate = new GregorianCalendar();
        endDate.set(Calendar.YEAR, 2014);
        endDate.set(Calendar.MONTH, Calendar.SEPTEMBER);
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        resetDate(endDate);
        long start = startDate.getTimeInMillis();
        long end = endDate.getTimeInMillis();

        for (; start<end; ) {
            String name = comboboxDF.format(startDate.getTime());
            result.add(new SelectItem(startDate.getTimeInMillis(), name));

            startDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH) + 1);
            start = startDate.getTimeInMillis();
        }
        return result;
    }

    public Long getGoodGroup() {
        return goodGroup;
    }

    public void setGoodGroup(Long goodGroup) {
        this.goodGroup = goodGroup;
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

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    private void resetDate (Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    public long getEditableDateIncrement() {
        int v = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_THIN_CLIENT_MIN__CLAIMS_EDITABLE_DAYS);
        return v * 86400000L;
    }

    public long getCalendarPadding () {
        return RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_THIN_CLIENT_PRE_POST_DATE) * 86400000L;
    }

    public static class Entry {
        private long idofgood;
        private String food;
        private Map<Long, Long> data;
        private Map<Long, List<Long>> ids;

        public Entry (String food, long idofgood) {
            this.food = food;
            this.idofgood = idofgood;
            data = new HashMap<Long, Long>();
            ids = new HashMap<Long, List<Long>>();
        }

        public long getIdofgood() {
            return idofgood;
        }

        public String getFood() {
            return food;
        }

        public Long getDataForDate(long ts) {
            Object o = data.get(ts);
            if (o == null || o.toString().length() < 1) {
                return null;
            }
            try {
                return Long.parseLong(o.toString());
            } catch (Exception e) {
                return null;
            }
        }

        public Map<Long, Long> getData() {
            return data;
        }

        public void setData(Map<Long, Long> data) {
            this.data = data;
        }

        public void add(long date, long value, long idofgoodsrequestposition) {
            //  Суммируем значения за одну дату
            if (data.get(date) != null) {
                value = data.get(date) + value;
            }
            data.put(date, value);

            //  Сохраняем несколько idofgoodsrequestposition в общем массиве за одну дату
            List<Long> localIds = ids.get(date);
            if (localIds == null) {
                localIds = new ArrayList<Long> ();
                ids.put(date, localIds);
            }
            localIds.add(idofgoodsrequestposition);
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