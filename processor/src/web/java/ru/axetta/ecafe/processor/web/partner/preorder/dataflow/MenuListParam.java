/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by i.semenov on 26.02.2018.
 */
public class MenuListParam {
    private Long contractId;
    private String startDate;
    private String endDate;
    private static final String formatString = "yyyy-MM-dd";

    public static MenuListParam fromString(String jsonRepresentation) {
        ObjectMapper mapper = new ObjectMapper(); //Jackson's JSON marshaller
        MenuListParam o= null;
        try {
            o = mapper.readValue(jsonRepresentation, MenuListParam.class );
        } catch (IOException e) {
            throw new WebApplicationException();
        }
        return o;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Date getStartDate() throws ParseException {
        return new SimpleDateFormat(formatString).parse(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() throws ParseException {
        return new SimpleDateFormat(formatString).parse(endDate);
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /*public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }*/
}
