package ru.axetta.ecafe.processor.core.report.model.autoenterevent;

import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * User: shamil
 * Date: 23.09.14
 * Time: 12:40
 */
public class ReportItem {

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    private Integer id = null;
    private String fio = null;
    // события прохода через турникет
    private NavigableSet<Event> events = new TreeSet<Event>();


    private String date;
    private String groupName; // группа клиента (класс, сотрудники и т.д.)

    public ReportItem(Integer id, String fio, String date, String groupName) {
        this.id = id;
        this.fio = fio;
        this.date = date;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public ReportItem() {
    }

    public Integer getId() {
        return id;
    }

    public String getFio() {
        return fio;
    }

    public void addEvent(Event e){
        events.add(e);
    }
    //public NavigableSet<Event> getEvents() {
    //    return events;
    //}

    public String getDate() {
        return date;
    }

    // время прихода
    public String getTimeEnter() {
        if (events.isEmpty())
            return "";
        for (Event e : events) {
            if (EnterEvent.isEntryOrReEntryEvent(e.getPassdirection()))
                return timeFormat.format(new Date(e.getTime()));
        }
        return "-";
    }

    // время ухода
    public String getTimeExit() {
        if (events.isEmpty())
            return "";
        for (Event e : events.descendingSet()) {
            if (EnterEvent.isExitOrReExitEvent(e.getPassdirection()))
                return timeFormat.format(new Date(e.getTime()));
        }
        return "-";
    }

    // время отсутствия
    public Integer getAbsenceOfDay() {
        if (events.isEmpty())
            return null;
        Integer result = 0;
        Long exitTime = null;
        for (Event e : events) {
            if (EnterEvent.isEntryOrReEntryEvent(e.getPassdirection()) && exitTime!=null) {
                result += (int)((e.getTime() - exitTime) / (1000 * 60));
            }
            if (EnterEvent.isExitOrReExitEvent(e.getPassdirection()))
                exitTime = e.getTime();
        }
        return result;
    }

    // время присутствия в течении дня
    public String getPresenceOfDay() {
        if (events.isEmpty()) return ""; // событий прохода небыло
        boolean lastExit = false;
        long result = 0;
        long entryTime = 0L;
        for (Event e: events){
            if(EnterEvent.isEntryOrReEntryEvent(e.getPassdirection()) && entryTime<=0){
                entryTime = e.getTime();
                lastExit = false;
            }
            if(EnterEvent.isExitOrReExitEvent(e.getPassdirection()) && entryTime>0){
                double value = ((e.getTime() * 1.0 - entryTime * 1.0) / (1000.0 * 60.0));
                result += Math.round(value);
                //result += (int)((e.getTime() - entryTime) / (1000 * 60));
                lastExit = true;
                entryTime = 0L;
            }
        }

        //if (lastEntry == null)  return ""; // клиент только выходил
        if (!lastExit)  return ""; // клиент последний раз вошел но не вышел
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        //calendar.setTimeInMillis(presenceOfDay);
        //SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
        //ft.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        //return ft.format(calendar.getTime());
        //long h = (presenceOfDay / 60000L) / 60;
        //long m = (presenceOfDay / 60000L) % 60;
        if(result == 0L) return "00:00";
        long h = result / 60;
        long m = result % 60;
        return String.format("%02d:%02d", h,m);
    }

    // первый вход - последний выход
    public String getTimeEnterExit() {
        if (events.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        for (Event e : events) {
            if (EnterEvent.isEntryOrExitEvent(e.getPassdirection())) {
                if(StringUtils.isNotEmpty(e.getGuardianFIO())){
                    sb.append(e.getGuardianFIO()).append(" ");
                }
                sb.append(timeFormat.format(new Date(e.getTime())));
                sb.append((EnterEvent.isEntryOrReEntryEvent(e.getPassdirection())?" (+)":" (-)"));
                if(!e.equals(events.last())) sb.append(", ");
            }
        }
        if (sb.length()>0)
            return sb.toString();
        else
            return "-";

    }
}
