/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.ArrayList;
import java.util.List;

/**
 * User: regal
 * Date: 03.02.15
 * Time: 15:53
 */
public class TotalBenefFeedItem {
    private long idOfOrg;
    private String orgNum ;
    private String name ;
    private String adress;
    private int students;
    private int benefStudents; //Число учащихся льготников
    private int orderedMeals ; //Количество заказанных порций
    private List<SubItem> orderedMealsList;
    private int enteredBenefStudents ; //Зафиксирован проход учащихся льготников
    private List<SubItem> enteredBenefStudentsList ;
    private int receiveMealBenefStudents ; //Предоставлено льготное питание учащимся льготникам
    private List<SubItem> receiveMealBenefStudentsList ;
    private int notReceiveMealEnteredBenefStudents ; //Не предоставлено льготное питание учащимся льготникам, присутствующим в ОО
    private List<SubItem> notReceiveMealEnteredBenefStudentsList ;
    private int receiveMealNotEnteredBenefStudents ; //Предоставлено льготное питание учащимся льготникам, не присутствующим в ОО
    private List<SubItem> receiveMealNotEnteredBenefStudentsList ;
    private int receiveMealReserveStudents ; //Педоставлено питание учащимся, числящимся в резервной группе


    public TotalBenefFeedItem() {
    }

    public TotalBenefFeedItem(Org org){
        this.idOfOrg = org.getIdOfOrg();
        this.orgNum = Org.extractOrgNumberFromName(org.getOfficialName());
        this.name = org.getShortName();
        this.adress = org.getAddress();

        orderedMealsList = new ArrayList<SubItem>();
        enteredBenefStudentsList = new ArrayList<SubItem>();
        receiveMealBenefStudentsList = new ArrayList<SubItem>();
        notReceiveMealEnteredBenefStudentsList = new ArrayList<SubItem>();
        receiveMealNotEnteredBenefStudentsList = new ArrayList<SubItem>();
    }

    public TotalBenefFeedItem(long idOfOrg, String name, String adress) {
        this.idOfOrg = idOfOrg;
        this.name = name;
        this.adress = adress;

        orderedMealsList = new ArrayList<SubItem>();
        enteredBenefStudentsList = new ArrayList<SubItem>();
        receiveMealBenefStudentsList = new ArrayList<SubItem>();
        notReceiveMealEnteredBenefStudentsList = new ArrayList<SubItem>();
        receiveMealNotEnteredBenefStudentsList = new ArrayList<SubItem>();

    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getStudents() {
        return students;
    }

    public void setStudents(int students) {
        this.students = students;
    }

    public int getBenefStudents() {
        return benefStudents;
    }

    public void setBenefStudents(int benefStudents) {
        this.benefStudents = benefStudents;
    }

    public int getOrderedMeals() {
        return orderedMeals;
    }

    public void setOrderedMeals(int orderedMeals) {
        this.orderedMeals = orderedMeals;
    }

    public int getEnteredBenefStudents() {
        return enteredBenefStudents;
    }

    public void setEnteredBenefStudents(int enteredBenefStudents) {
        this.enteredBenefStudents = enteredBenefStudents;
    }

    public int getReceiveMealBenefStudents() {
        return receiveMealBenefStudents;
    }

    public void setReceiveMealBenefStudents(int receiveMealBenefStudents) {
        this.receiveMealBenefStudents = receiveMealBenefStudents;
    }

    public int getNotReceiveMealEnteredBenefStudents() {
        return notReceiveMealEnteredBenefStudents;
    }

    public void setNotReceiveMealEnteredBenefStudents(int notReceiveMealEnteredBenefStudents) {
        this.notReceiveMealEnteredBenefStudents = notReceiveMealEnteredBenefStudents;
    }

    public int getReceiveMealNotEnteredBenefStudents() {
        return receiveMealNotEnteredBenefStudents;
    }

    public void setReceiveMealNotEnteredBenefStudents(int receiveMealNotEnteredBenefStudents) {
        this.receiveMealNotEnteredBenefStudents = receiveMealNotEnteredBenefStudents;
    }

    public int getReceiveMealReserveStudents() {
        return receiveMealReserveStudents;
    }

    public void setReceiveMealReserveStudents(int receiveMealReserveStudents) {
        this.receiveMealReserveStudents = receiveMealReserveStudents;
    }

    public List<SubItem> getOrderedMealsList() {
        return orderedMealsList;
    }

    public void setOrderedMealsList(List<SubItem> orderedMealsList) {
        this.orderedMealsList = orderedMealsList;
    }

    public List<SubItem> getEnteredBenefStudentsList() {
        return enteredBenefStudentsList;
    }

    public void setEnteredBenefStudentsList(List<SubItem> enteredBenefStudentsList) {
        this.enteredBenefStudentsList = enteredBenefStudentsList;
    }

    public List<SubItem> getReceiveMealBenefStudentsList() {
        return receiveMealBenefStudentsList;
    }

    public void setReceiveMealBenefStudentsList(List<SubItem> receiveMealBenefStudentsList) {
        this.receiveMealBenefStudentsList = receiveMealBenefStudentsList;
    }

    public List<SubItem> getNotReceiveMealEnteredBenefStudentsList() {
        return notReceiveMealEnteredBenefStudentsList;
    }

    public void setNotReceiveMealEnteredBenefStudentsList(List<SubItem> notReceiveMealEnteredBenefStudentsList) {
        this.notReceiveMealEnteredBenefStudentsList = notReceiveMealEnteredBenefStudentsList;
    }

    public List<SubItem> getReceiveMealNotEnteredBenefStudentsList() {
        return receiveMealNotEnteredBenefStudentsList;
    }

    public void setReceiveMealNotEnteredBenefStudentsList(List<SubItem> receiveMealNotEnteredBenefStudentsList) {
        this.receiveMealNotEnteredBenefStudentsList = receiveMealNotEnteredBenefStudentsList;
    }
}
