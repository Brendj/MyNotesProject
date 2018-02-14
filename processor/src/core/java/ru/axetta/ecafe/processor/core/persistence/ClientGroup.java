/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class ClientGroup {
    public static final long PREDEFINED_ID_OF_GROUP_OTHER = 1200000000;
    public static final long PREDEFINED_ID_OF_GROUP_EMPLOYEES = 1100000000;
    public static final long TEMPORARY_GROUP_MAX_ID = -100000;

    private static List<String> names = new LinkedList<String>();

    public static List<String> predefinedGroupNames(){
        if(names.isEmpty()){
            for (Predefined p: Predefined.values()){
                if(p.getValue()>=1100000000L){
                    names.add(p.getNameOfGroup());
                }
            }
        }
        return names;
    }

    public enum Predefined{
        CLIENT_STUDENTS_CLASS_BEGIN(1000000000L,"Ученики"),
        CLIENT_EMPLOYEES (1100000000L,"Пед. состав"),
        CLIENT_EMPLOYEE (1100000001L,"Сотрудники"),
        CLIENT_ADMINISTRATION (1100000010L,"Администрация"),
        CLIENT_TECH_EMPLOYEES (1100000020L,"Тех. персонал"),
        CLIENT_PARENTS (1100000030L,"Родители"),
        CLIENT_VISITORS (1100000040L,"Посетители"),
        CLIENT_OTHERS ( 1100000050L,"Другое"),
        // все группы не-учащиеся и не-выбывшие должны быть до CLIENT_OTHERS
        CLIENT_LEAVING ( 1100000060L,"Выбывшие"),
        CLIENT_DELETED ( 1100000070L,"Удаленные"),
        CLIENT_DISPLACED ( 1100000080L,"Перемещенные"),
        CLIENT_OTHER_ORG (1100000090L, "Обучающиеся других ОО"),
        CLIENT_PARENT_OTHER_ORG (1100000100L, "Родители обучающихся других ОО"),
        CLIENT_EMPLOYEE_OTHER_ORG (1100000110L, "Сотрудники других ОО");

        private Long value;
        private String nameOfGroup;

        private Predefined(Long value, String nameOfGroup){
            this.value = value;
            this.nameOfGroup = nameOfGroup;
        }

        public static Predefined parse(Long value){
            Predefined currentPredefined = null;
            for (Predefined predefined: Predefined.values()){
                if(predefined.value.equals(value)){
                    currentPredefined = predefined;
                    break;
                }
            }
            return currentPredefined;
        }

        public static Predefined parse(String value){
            Predefined currentPredefined = null;
            for (Predefined predefined: Predefined.values()){
                if(predefined.nameOfGroup.equals(value)){
                    currentPredefined = predefined;
                    break;
                }
            }
            return currentPredefined;
        }

        public String getNameOfGroup() {
            return nameOfGroup;
        }

        public Long getValue() {
            return value;
        }
    }

    private CompositeIdOfClientGroup compositeIdOfClientGroup;
    private Org org;
    private String groupName;
    private Set<Client> clients = new HashSet<Client>();
    private Set<DiaryTimesheet> diaryTimesheets = new HashSet<DiaryTimesheet>();

    protected ClientGroup() {
        // For Hibernate only
    }

    public ClientGroup(CompositeIdOfClientGroup compositeIdOfClientGroup, String groupName) {
        this.compositeIdOfClientGroup = compositeIdOfClientGroup;
        this.groupName = groupName;
    }

    public CompositeIdOfClientGroup getCompositeIdOfClientGroup() {
        return compositeIdOfClientGroup;
    }

    private void setCompositeIdOfClientGroup(CompositeIdOfClientGroup compositeIdOfClientGroup) {
        // For Hibernate only
        this.compositeIdOfClientGroup = compositeIdOfClientGroup;
    }

    public Org getOrg() {
        return org;
    }

    private void setOrg(Org org) {
        // For Hibernate only
        this.org = org;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private Set<Client> getClientsInternal() {
        // For Hibernate only
        return clients;
    }

    private void setClientsInternal(Set<Client> clients) {
        // For Hibernate only
        this.clients = clients;
    }

    public Set<Client> getClients() {
        return Collections.unmodifiableSet(getClientsInternal());
    }

    private Set<DiaryTimesheet> getDiaryTimesheetsInternal() {
        // For Hibernate only
        return diaryTimesheets;
    }

    private void setDiaryTimesheetsInternal(Set<DiaryTimesheet> diaryTimesheets) {
        // For Hibernate only
        this.diaryTimesheets = diaryTimesheets;
    }

    public Set<DiaryTimesheet> getDiaryTimesheets() {
        return Collections.unmodifiableSet(getDiaryTimesheetsInternal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientGroup)) {
            return false;
        }
        final ClientGroup that = (ClientGroup) o;
        if (!compositeIdOfClientGroup.equals(that.getCompositeIdOfClientGroup())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return compositeIdOfClientGroup.hashCode();
    }

    @Override
    public String toString() {
        return "ClientGroup{" + "compositeIdOfClientGroup=" + compositeIdOfClientGroup + ", org=" + org
                + ", groupName='" + groupName + '\'' + '}';
    }

    public boolean isTemporaryGroup() {
        return (compositeIdOfClientGroup.getIdOfClientGroup()<=TEMPORARY_GROUP_MAX_ID);
    }
}