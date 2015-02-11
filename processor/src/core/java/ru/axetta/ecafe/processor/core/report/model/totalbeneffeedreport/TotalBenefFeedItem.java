/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport;

/**
 * User: regal
 * Date: 03.02.15
 * Time: 15:53
 */
public class TotalBenefFeedItem {
    private long idOfOrg;
    private String name ="d" ;
    private String adress = "dd";
    private int students;
    private int benefStudents; //Число учащихся льготников
    private int orderedMeals ; //Количество заказанных порций
    private int enteredBenefStudents ; //Зафиксирован проход учащихся льготников
    private int receiveMealBenefStudents ; //Предоставлено льготное питание учащимся льготникам
    private int notReceiveMealEnteredBenefStudents ; //Не предоставлено льготное питание учащимся льготникам, присутствующим в ОО
    private int receiveMealNotEnteredBenefStudents ; //Предоставлено льготное питание учащимся льготникам, не присутствующим в ОО
    private int receiveMealReserveStudents ; //Педоставлено питание учащимся, числящимся в резервной группе


    public TotalBenefFeedItem() {
    }

    public TotalBenefFeedItem(long idOfOrg, String name, String adress) {
        this.idOfOrg = idOfOrg;
        this.name = name;
        this.adress = adress;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
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
}
