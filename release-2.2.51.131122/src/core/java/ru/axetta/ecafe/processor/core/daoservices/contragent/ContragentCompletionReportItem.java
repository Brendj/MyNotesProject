/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

import ru.axetta.ecafe.processor.core.persistence.Contragent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.01.13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ContragentCompletionReportItem {

    /* короткое наименование образовательного учереждения */
    private String educationalInstitutionName;

    private String contragentName;

    private Long paySum;

    public ContragentCompletionReportItem() {}

    public ContragentCompletionReportItem(String contragentName, String educationalInstitutionName, Long paySum) {
        this.contragentName = contragentName;
        this.educationalInstitutionName = educationalInstitutionName;
        this.paySum = paySum;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public String getEducationalInstitutionName() {
        return educationalInstitutionName;
    }

    public void setEducationalInstitutionName(String educationalInstitutionName) {
        this.educationalInstitutionName = educationalInstitutionName;
    }

    public Long getPaySum() {
        return paySum;
    }

    public void setPaySum(Long paySum) {
        this.paySum = paySum;
    }
}
