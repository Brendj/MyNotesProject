/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 06.08.12
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class BanksData {
    public BanksList banksList;
    public Long resultCode;
    public String description;
    public BanksData(BanksList banksList, Long resultCode, String desc) {

        this.banksList=banksList;
        this.resultCode = resultCode;
        this.description = desc;

    }
    public BanksData() {}
}
