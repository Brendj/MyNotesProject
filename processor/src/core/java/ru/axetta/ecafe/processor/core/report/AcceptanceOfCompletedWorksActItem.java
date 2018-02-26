/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import java.util.List;

/**
 * Created by anvarov on 20.02.2018.
 */
public class AcceptanceOfCompletedWorksActItem {

    /**
     * Дата заключения
     */
    public String dateOfConclusion;

    /**
     * Номер контракта
     */
    public String numberOfContract;

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
     * Сумма
     */
    public String sum;

    /**
     * Должность
     */
    public String officialPosition;

    /**
     * Данные для кростаба
     */
    public List<AcceptanceOfCompletedWorksActCrossTabData> actCrossTabDatas;

    public AcceptanceOfCompletedWorksActItem() {

    }

    public AcceptanceOfCompletedWorksActItem(String dateOfConclusion, String numberOfContract,
            String shortNameInfoService, String position, String fullName, String personUnderContract, String executor,
            String dateOfClosing, String sum, String officialPosition) {
        this.dateOfConclusion = dateOfConclusion;
        this.numberOfContract = numberOfContract;
        this.shortNameInfoService = shortNameInfoService;
        this.position = position;
        this.fullName = fullName;
        this.personUnderContract = personUnderContract;
        this.executor = executor;
        this.dateOfClosing = dateOfClosing;
        this.sum = sum;
        this.officialPosition = officialPosition;
    }

    public String getDateOfConclusion() {
        return dateOfConclusion;
    }

    public void setDateOfConclusion(String dateOfConclusion) {
        this.dateOfConclusion = dateOfConclusion;
    }

    public String getNumberOfContract() {
        return numberOfContract;
    }

    public void setNumberOfContract(String numberOfContract) {
        this.numberOfContract = numberOfContract;
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

    public String getOfficialPosition() {
        return officialPosition;
    }

    public void setOfficialPosition(String officialPosition) {
        this.officialPosition = officialPosition;
    }

    public List<AcceptanceOfCompletedWorksActCrossTabData> getActCrossTabDatas() {
        return actCrossTabDatas;
    }

    public void setActCrossTabDatas(List<AcceptanceOfCompletedWorksActCrossTabData> actCrossTabDatas) {
        this.actCrossTabDatas = actCrossTabDatas;
    }
}
