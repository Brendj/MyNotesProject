/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by anvarov on 26.02.2018.
 */
public class AcceptanceOfCompletedWorksActCrossTabData {

    /**
     * Наименование товара (Завтрак, Завтрак второй, Обед и т.д.)
     */
    public String goodName;

    /**
     * Наименование образовательного учреждения
     */
    private String education;

    /**
     * Сумма
     */
    private String sum;

    public AcceptanceOfCompletedWorksActCrossTabData() {
    }

    public AcceptanceOfCompletedWorksActCrossTabData(String goodName, String education, String sum) {
        this.goodName = goodName;
        this.education = education;
        this.sum = sum;
    }

    public AcceptanceOfCompletedWorksActCrossTabData(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
