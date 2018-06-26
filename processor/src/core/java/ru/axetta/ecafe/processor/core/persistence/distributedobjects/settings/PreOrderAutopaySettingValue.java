/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import java.text.ParseException;

public class PreOrderAutopaySettingValue extends AbstractParserBySettingValue{

    private boolean isActivePreorder;  // активность для предзаказов
    private String processingTime_Preorder; // время срабатывания автооплаты

    public PreOrderAutopaySettingValue(String[] values) throws ParseException {
        super(values);
    }

    @Override
    protected void parse(String[] values) throws ParseException {
        this.isActivePreorder = Boolean.parseBoolean(values[0]);
        this.processingTime_Preorder = values[1];
    }

    @Override
    public String build() {
        return isActivePreorder+";"+processingTime_Preorder+";";
    }

    @Override
    public boolean check() {
        return true;
    }


    public boolean isActivePreorder() {
        return isActivePreorder;
    }

    public void setActivePreorder(boolean activePreorder) {
        isActivePreorder = activePreorder;
    }

    public String getProcessingTime_Preorder() {
        return processingTime_Preorder;
    }

    public void setProcessingTime_Preorder(String processingTime_Preorder) {
        this.processingTime_Preorder = processingTime_Preorder;
    }
}
