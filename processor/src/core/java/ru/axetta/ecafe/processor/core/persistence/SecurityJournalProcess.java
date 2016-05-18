/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 10.05.16
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class SecurityJournalProcess {
    private Long idOfJournalProcess;
    private Integer eventType;
    private Integer eventClass;
    private Date eventDate;
    private Boolean success;
    private User user;
    private String serverAddress;

    public Long getIdOfJournalProcess() {
        return idOfJournalProcess;
    }

    public void setIdOfJournalProcess(Long idOfJournalProcess) {
        this.idOfJournalProcess = idOfJournalProcess;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SecurityJournalProcess() {
        //for Hibernate
    }

    public static SecurityJournalProcess createJournalRecordStart(EventType eventType, Date eventDate) {
        SecurityJournalProcess process = new SecurityJournalProcess();
        process.setEventType(eventType.getIdentification());
        process.setEventDate(eventDate);
        process.setEventClass(EventClass.START.getIdentification());
        setUserFromSession(process);
        writeServerAddress(process);
        return process;
    }

    public static SecurityJournalProcess createJournalRecordEnd(EventType eventType, Date eventDate) {
        SecurityJournalProcess process = new SecurityJournalProcess();
        process.setEventType(eventType.getIdentification());
        process.setEventDate(eventDate);
        process.setEventClass(EventClass.END.getIdentification());
        setUserFromSession(process);
        writeServerAddress(process);
        return process;
    }

    private static void setUserFromSession(SecurityJournalProcess process) {
        process.setUser(DAOReadonlyService.getInstance().getUserFromSession());
    }

    private static void writeServerAddress(SecurityJournalProcess process) {
        try {
            process.setServerAddress(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception ignore) { }
    }

    public void saveWithSuccess(Boolean success) {
        setSuccess(success);
        DAOService.getInstance().writeProcessJournalRecord(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityJournalProcess)) {
            return false;
        }

        SecurityJournalProcess that = (SecurityJournalProcess) o;

        if (!idOfJournalProcess.equals(that.idOfJournalProcess)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfJournalProcess.hashCode();
    }

    public Integer getEventClass() {
        return eventClass;
    }

    public void setEventClass(Integer eventClass) {
        this.eventClass = eventClass;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public enum EventClass {
        START(0, "Старт"),
        END(1, "Стоп");

        private Integer identification;
        private String description;

        static Map<Integer, EventClass> eventClassMap = new HashMap<Integer, EventClass>();

        static {
            for (EventClass event : EventClass.values()) {
                eventClassMap.put(event.identification, event);
            }
        }

        private EventClass(Integer identification, String description) {
            this.description = description;
            this.identification = identification;
        }

        public static EventClass parse(Integer identification) {
            return eventClassMap.get(identification);
        }

        public Integer getIdentification() {
            return identification;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public enum EventType {
        RNIP(0, "Импорт платежей из РНИП"),
        EMP_BIND_CLIENTS(1, "Привязка клиентов ИСПП к ЕМП"),
        EMP_RECEIVE_UPDATES(2, "Получение изменений из ЕМП"),
        SMS_RESENDING(3, "Повторная отправка СМС"),
        NSI_CLIENTS(4, "Сверка контингента"),
        NSI_ORGS(5, "Сверка организаций"),
        SMS_SUBSCRIPTION_FEE(6, "Напоминание о необходимости пополнения баланса"),
        REGULAR_PAYMENTS(7, "Сервис регулярных платежей"),
        ORDER_STATE_CHANGE(8, "Оповещение контрагента о сторнировании заказов"),
        XML_REPORT_GENERATOR(9, "Формирование реестров в формате XML");

        private Integer identification;
        private String description;

        static Map<Integer, EventType> eventTypeMap = new HashMap<Integer, EventType>();

        static {
            for (EventType event : EventType.values()) {
                eventTypeMap.put(event.identification, event);
            }
        }

        private EventType(Integer identification, String description) {
            this.description = description;
            this.identification = identification;
        }

        public static EventType parse(Integer identification) {
            return eventTypeMap.get(identification);
        }

        public Integer getIdentification() {
            return identification;
        }

        @Override
        public String toString() {
            return description;
        }
    }

}
