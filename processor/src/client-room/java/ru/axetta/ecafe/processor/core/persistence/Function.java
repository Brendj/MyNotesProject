/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.06.2009
 * Time: 15:56:56
 * To change this template use File | Settings | File Templates.
 */

@Table(name = "cf_functions")
@Entity
public class Function {

    public static final String FUNC_USER_VIEW= "viewUser";
    public static final String FUNCD_USER_VIEW = "Просмотр пользователей";
    public static final String FUNC_USER_EDIT= "editUser";
    public static final String FUNCD_USER_EDIT= "Редактирование пользователей";
    public static final String FUNC_USER_DELETE = "deleteUser";
    public static final String FUNCD_USER_DELETE= "Удаление пользователей";
    public static final String FUNC_PAY_PROCESS = "payProcess";
    public static final String FUNCD_PAY_PROCESS = "Проводка платежа";
    public static final String FUNC_ORG_VIEW = "orgView";
    public static final String FUNCD_ORG_VIEW = "Просмотр организаций";
    public static final String FUNC_ORG_EDIT = "orgEdit";
    public static final String FUNCD_ORG_EDIT = "Редактирование организаций";
    public static final String FUNC_CONTRAGENT_VIEW = "contraView";
    public static final String FUNCD_CONTRAGENT_VIEW = "Просмотр контрагентов";
    public static final String FUNC_CONTRAGENT_EDIT = "contraEdit";
    public static final String FUNCD_CONTRAGENT_EDIT = "Редактирование контрагентов";
    public static final String FUNC_CLIENT_VIEW = "clientView";
    public static final String FUNCD_CLIENT_VIEW = "Просмотр клиентов";
    public static final String FUNC_CLIENT_EDIT = "clientEdit";
    public static final String FUNCD_CLIENT_EDIT = "Редактирование клиентов";
    public static final String FUNC_CLIENT_REMOVE = "clientDel";
    public static final String FUNCD_CLIENT_REMOVE = "Удаление клиентов";
    public static final String FUNC_CARD_VIEW = "cardView";
    public static final String FUNCD_CARD_VIEW = "Просмотр карт";
    public static final String FUNC_CARD_EDIT = "cardEdit";
    public static final String FUNCD_CARD_EDIT = "Редактирование карт";
    public static final String FUNC_SERVICE_ADMIN = "servAdm";
    public static final String FUNCD_SERVICE_ADMIN = "Сервис - администратор";
    public static final String FUNC_SERVICE_SUPPORT = "servSupp";
    public static final String FUNCD_SERVICE_SUPPORT = "Сервис - поддержка";
    public static final String FUNC_SERVICE_CLIENTS = "servClnt";
    public static final String FUNCD_SERVICE_CLIENTS = "Сервис клиенты";
    public static final String FUNC_REPORT_VIEW = "reportView";
    public static final String FUNCD_REPORT_VIEW = "Отчеты просмотр";
    public static final String FUNC_REPORT_EDIT = "reportEdit";
    public static final String FUNCD_REPORT_EDIT = "Отчеты редактирование";
    public static final String FUNC_RESTICT_MESSAGE_IN_ARM_OO = "messageARMinOO";
    public static final String FUNCD_RESTICT_MESSAGE_IN_ARM_OO = "Закрыть 'Сообщения в АРМ администратора ОО'";
    // baybikov
    public static final String FUNC_WORK_OPTION = "workOption";
    public static final String FUNCD_WORK_OPTION = "Редактирование настроек";
    public static final String FUNC_WORK_ONLINE_REPORT = "onlineRprt";
    public static final String FUNCD_WORK_ONLINE_REPORT = "Онлайн отчеты";
    public static final String FUNC_COUNT_CURRENT_POSITIONS = "countCP";
    public static final String FUNCD_COUNT_CURRENT_POSITIONS = "Рассчитать текущие позиции";
    public static final String FUNC_POS_VIEW = "posView";
    public static final String FUNCD_POS_VIEW = "Просмотр точек продаж";
    public static final String FUNC_POS_EDIT = "posEdit";
    public static final String FUNCD_POS_EDIT = "Редактирование точек продаж";
    public static final String FUNC_PAYMENT_VIEW = "pmntView";
    public static final String FUNCD_PAYMENT_VIEW = "Просмотр платежей";
    public static final String FUNC_PAYMENT_EDIT = "pmntEdit";
    public static final String FUNCD_PAYMENT_EDIT = "Редактирование платежей";
    public static final String FUNC_CATEGORY_VIEW = "catView";
    public static final String FUNCD_CATEGORY_VIEW = "Просмотр категорий";
    public static final String FUNC_CATEGORY_EDIT = "catEdit";
    public static final String FUNCD_CATEGORY_EDIT = "Редактирование категорий";
    public static final String FUNC_RULE_VIEW = "ruleView";
    public static final String FUNCD_RULE_VIEW = "Просмотр правил";
    public static final String FUNC_RULE_EDIT = "ruleEdit";
    public static final String FUNCD_RULE_EDIT = "Редактирование правил";
    @Id
    @Column(name = "idoffunction")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idOfFunction;
    @Column(name = "functionname", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private String functionName;



   @JoinTable(name="cf_permissions",
           joinColumns=@JoinColumn(name="idoffunction"),
           inverseJoinColumns=@JoinColumn(name="idofuser") )
    @ManyToMany(targetEntity = User.class)
    private Set<User> users = new HashSet<User>();

    protected Function() {
        // For Hibernate
    }

    public Function(String functionName) {
        this.functionName = functionName;
    }

    public Long getIdOfFunction() {
        return idOfFunction;
    }

    private void setIdOfFunction(Long idOfFunction) {
        // For Hibernate
        this.idOfFunction = idOfFunction;
    }

    public String getFunctionName() {
        return functionName;
    }

    private void setFunctionName(String functionName) {
        // For Hibernate
        this.functionName = functionName;
    }



    private Set<User> getUsersInternal() {
        // For Hibernate
        return users;
    }



    private void setUsersInternal(Set<User> users) {
        // For Hibernate
        this.users = users;
    }

    public Set<User> getUsers() {
        return Collections.unmodifiableSet(getUsersInternal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Function)) {
            return false;
        }
        final Function function = (Function) o;
        return idOfFunction.equals(function.getIdOfFunction());
    }

    @Override
    public int hashCode() {
        return idOfFunction.hashCode();
    }

    @Override
    public String toString() {
        return "Function{" + "idOfFunction=" + idOfFunction + ", functionName='" + functionName + '\'' + '}';
    }

    static HashMap<String, String> functionDesc=null;

    static public String getFunctionDesc(String funcName) {
        return functionDesc.get(funcName);
    }
    static public List<Function> getFuncList() throws Exception {
        functionDesc=new HashMap<String, String>();
        LinkedList<Function> list=new LinkedList<Function>();
        long nFunc=1;
        for (Field f : Function.class.getFields()) {
            if (f.getName().startsWith("FUNC_")) {
                Function func=new Function();
                func.setFunctionName((String)f.get(func));
                func.setIdOfFunction(nFunc);
                list.addLast(func);
                String funcDesc=(String)Function.class.getField(f.getName().replace("FUNC_", "FUNCD_")).get(func);
                functionDesc.put(func.getFunctionName(), funcDesc);
                nFunc++;
            }
        }
        return list;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
