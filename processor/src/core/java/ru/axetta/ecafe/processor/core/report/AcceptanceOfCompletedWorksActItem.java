/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


/**
 * Created by anvarov on 20.02.2018.
 */
public class AcceptanceOfCompletedWorksActItem {

    /**
     * Дата заключения
     */
    public String dateOfContract;

    /**
     * Номер контракта
     */
    public String numberOfContract;
    /**
     * Текущая дата
     */
    public String startDate;

    /**
     * Краткое наименование ОО
     */
    public String shortNameInfoService;

    /**
     * Должность
     */
    public String position;

    /**
     * ФИО
     */
    public String fullName;

    /**
     * Физ лицо по договору
     */
    public String personUnderContract;

    /**
     * Берем исполнителя из контрагенты – контракты
     */
    public String executor;

    /**
     * Срок действия
     */
    public String dateOfClosing;

    /**
     * Конец периода
     */
    public String endDate;

    /**
     * Сумма
     */
    public String sum;

    public AcceptanceOfCompletedWorksActItem() {

    }

    public AcceptanceOfCompletedWorksActItem(String dateOfContract, String numberOfContract, String startDate,
            String shortNameInfoService, String position, String fullName, String personUnderContract,
            String executor, String dateOfClosing, String endDate, String sum) {
        this.dateOfContract = dateOfContract;
        this.numberOfContract = numberOfContract;
        this.startDate = startDate;
        this.shortNameInfoService = shortNameInfoService;
        this.position = position;
        this.fullName = fullName;
        this.personUnderContract = personUnderContract;
        this.executor = executor;
        this.dateOfClosing = dateOfClosing;
        this.endDate = endDate;
        this.sum = sum;
    }

    public String getDateOfContract() {
        return dateOfContract;
    }

    public void setDateOfContract(String dateOfContract) {
        this.dateOfContract = dateOfContract;
    }

    public String getNumberOfContract() {
        return numberOfContract;
    }

    public void setNumberOfContract(String numberOfContract) {
        this.numberOfContract = numberOfContract;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPersonUnderContract() {
        return personUnderContract;
    }

    public void setPersonUnderContract(String personUnderContract) {
        this.personUnderContract = personUnderContract;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getDateOfClosing() {
        return dateOfClosing;
    }

    public void setDateOfClosing(String dateOfClosing) {
        this.dateOfClosing = dateOfClosing;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
