/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreOrderAutopaySettingValue extends AbstractParserBySettingValue{

    private boolean isActivePreorder;  // активность для предзаказов
    private Date processingTime_Preorder; // время срабатывания автооплаты
    private static SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("HH:mm");

    public PreOrderAutopaySettingValue(String[] values) throws ParseException {
        super(values);
    }

    @Override
    protected void parse(String[] values) throws ParseException {
        this.isActivePreorder = values[0].equals("1");
        this.processingTime_Preorder = dateOnlyFormat.parse(values[1]);
    }

    @Override
    public String build() {
        return (isActivePreorder?1:0)+";"+dateOnlyFormat.format(processingTime_Preorder)+";";
    }

    @Override
    public boolean check() {
        return true;
    }


    public boolean getIsActivePreorder() {
        return isActivePreorder;
    }

    public void setIsActivePreorder(boolean activePreorder) {
        isActivePreorder = activePreorder;
    }

    public Date getProcessingTime_Preorder() {
        return processingTime_Preorder;
    }

    public void setProcessingTime_Preorder(Date processingTime_Preorder) {
        this.processingTime_Preorder = processingTime_Preorder;
    }
}