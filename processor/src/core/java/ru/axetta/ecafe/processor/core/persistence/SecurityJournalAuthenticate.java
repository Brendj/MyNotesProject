/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Akhmetov
 * Date: 27.04.16
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class SecurityJournalAuthenticate implements Serializable {

    private static final long serialVersionUID = 7521472271649776147L;

    private Long idOfJournalAuthenticate;
    private Integer eventType;
    private Date eventDate;
    private Boolean success;
    private String ipAddress;
    private Integer idOfArmType;
    private String login;
    private User user;
    private Integer denyCause;
    private String comment;

    public SecurityJournalAuthenticate() {
        //for Hibernate
    }

    public SecurityJournalAuthenticate(Builder builder) {
        this.eventType = builder.eventType;
        this.eventDate = builder.eventDate;
        this.success = builder.success;
        this.ipAddress = builder.ipAddress;
        this.idOfArmType = builder.idOfArmType;
        this.login = builder.login;
        this.user = builder.user;
        this.denyCause = builder.denyCause;
        this.comment = builder.comment;
    }

    public Long getIdOfJournalAuthenticate() {
        return idOfJournalAuthenticate;
    }

    public void setIdOfJournalAuthenticate(Long idOfJournalAuthenticate) {
        this.idOfJournalAuthenticate = idOfJournalAuthenticate;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getIdOfArmType() {
        return idOfArmType;
    }

    public void setIdOfArmType(Integer idOfArmType) {
        this.idOfArmType = idOfArmType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getDenyCause() {
        return denyCause;
    }

    public void setDenyCause(Integer denyCause) {
        this.denyCause = denyCause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityJournalAuthenticate)) {
            return false;
        }

        SecurityJournalAuthenticate that = (SecurityJournalAuthenticate) o;

        if (!idOfJournalAuthenticate.equals(that.idOfJournalAuthenticate)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfJournalAuthenticate.hashCode();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public enum EventType {
        LOGIN(0, "вход"),
        LOGOUT(1, "выход"),
        CREATE_USER(2, "создание пользователя"),
        MODIFY_USER(3, "редактирование пользователя"),
        DELETE_USER(4, "удаление пользователя"),
        CHANGE_GRANTS(5, "изменение прав доступа пользователя"),
        BLOCK_USER(6, "блокировка пользователя"),
        PASSWORD_CHANGE(7, "смена пароля"),
        GENERATE_SMS(8, "новый код активации по СМС");

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

    public enum ArmType {
        OPERATOR(0, "АРМ оператора"),
        ADMIN(1, "АРМ администратора"),
        SUPPLIER(2, "АРМ оператора поставщика питания"),
        MONITORING(3, "АРМ мониторинга"),
        ADMIN_SECURITY(4, "АРМ администратора ИБ"),
        WEB_ARM_ADMIN(5, "Веб АРМ администратора ОУ");

        private Integer identification;
        private String description;

        static Map<Integer, ArmType> armTypeMap = new HashMap<Integer, ArmType>();

        static {
            for(ArmType armType : ArmType.values()) {
                armTypeMap.put(armType.identification, armType);
            }
        }

        private ArmType(Integer identification, String description) {
            this.identification = identification;
            this.description = description;
        }

        public static ArmType parse(Integer identification) {
            return armTypeMap.get(identification);
        }

        public Integer getIdentification() {
            return identification;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public enum DenyCause {
        LOGIN_MISSING(0, "Логин отсутствует"),
        PASSWORD_MISSING(1, "Пароль отсутствует"),
        USER_NOT_FOUND(2, "Пользователь не найден"),
        USER_BLOCKED(3, "Пользователь заблокирован"),
        LONG_INACTIVITY(4, "Длительное неиспользование учетной записи"),
        WRONG_AUTH_URL(5, "Неверный адрес аутентификации"),
        WRONG_PASSWORD(6, "Неверный пароль"),
        MAX_FAULT_LOGIN_ATTEMPTS(7, "Превышено максимально возможное количество неудачных попыток входа"),
        USER_EDIT_BAD_PARAMETERS(8, "Некорректный ввод данных"),
        BAD_OPERATION(9, "Операция запрещена в текущем контексте");

        private Integer identification;
        private String description;

        private DenyCause(Integer identification, String description) {
            this.identification = identification;
            this.description = description;
        }

        static Map<Integer, DenyCause> denyCauseMap = new HashMap<Integer, DenyCause>();
        static {
            for(DenyCause denyCause : DenyCause.values()) {
                denyCauseMap.put(denyCause.identification, denyCause);
            }
        }
        public static DenyCause parse(Integer identification) {
            return denyCauseMap.get(identification);
        }

        public Integer getIdentification() {
            return identification;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public static class Builder {
        private final Integer eventType;
        private final Date eventDate;
        private final Boolean success;
        private String ipAddress;
        private Integer idOfArmType;
        private String login;
        private User user;
        private Integer denyCause;
        private String comment;

        public Builder(Integer eventType, Date eventDate, Boolean success) {
            this.eventType = eventType;
            this.eventDate = eventDate;
            this.success = success;
        }

        public Builder withIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder withIdOfArmType(Integer idOfArmType) {
            this.idOfArmType = idOfArmType;
            return this;
        }

        public Builder withLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withDenyCause(Integer denyCause) {
            this.denyCause = denyCause;
            return this;
        }

        public Builder withComment(String comment) {
            if (comment != null && comment.length() > 256) {
                comment = comment.substring(0, 255);
            }
            this.comment = comment;
            return this;
        }

        public SecurityJournalAuthenticate build() {
            return new SecurityJournalAuthenticate(this);
        }
    }

    public static Builder createLoginFaultBuilder() {
        return new Builder(EventType.LOGIN.getIdentification(), new Date(), false);
    }

    public static Builder createBuilder(EventType eventType, Boolean success) {
        return new Builder(eventType.getIdentification(), new Date(), success);
    }

    public static SecurityJournalAuthenticate createLoginFaultRecord(String ipAddress, String login, User user, Integer denyCause) {
        Builder builder = createLoginFaultBuilder();
        builder.withIpAddress(ipAddress).withLogin(login).withUser(user).withDenyCause(denyCause);
        if (user != null) builder.withIdOfArmType(user.getIdOfRole());
        return builder.build();
    }

    public static SecurityJournalAuthenticate createSuccessAuthRecord(String ipAddress, String login, User user) {
        Builder builder = new Builder(EventType.LOGIN.getIdentification(), new Date(), true).withIpAddress(ipAddress);
        builder.withLogin(login).withUser(user).withIdOfArmType(user.getIdOfRole());
        return builder.build();
    }

    public static SecurityJournalAuthenticate createSuccessLogout(String ipAddress, String login, User user) {
        Builder builder = new Builder(EventType.LOGOUT.getIdentification(), new Date(), true).withIpAddress(ipAddress);
        builder.withLogin(login).withUser(user).withIdOfArmType(user.getIdOfRole());
        return builder.build();
    }

    public static SecurityJournalAuthenticate createUserEditRecord(EventType eventType, String ipAddress, String login,
            User user, Boolean success, Integer denyCause, String comment) {
        Builder builder = createBuilder(eventType, success);
        builder.withIpAddress(ipAddress).withLogin(login).withUser(user).withDenyCause(denyCause).withComment(comment);
        if (user != null) builder.withIdOfArmType(user.getIdOfRole());
        return builder.build();
    }
}
