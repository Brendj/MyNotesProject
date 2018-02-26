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

    public AcceptanceOfCompletedWorksActCrossTabData() {
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

}
