/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.PreorderParam;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by i.semenov on 14.03.2018.
 */
public class PreorderSaveListParam {
    private Long contractId;
    private String date;
    private List<ComplexListParam> complexes;

    public PreorderSaveListParam() {

    }

    public PreorderSaveListParam(PreorderParam preorderParam) {
        this.contractId = preorderParam.getContractId();
        this.date = CalendarUtils.dateToString(preorderParam.getDate());
        this.complexes = preorderParam.getComplexes();
    }

    public static PreorderSaveListParam fromString(String jsonRepresentation) {
        ObjectMapper mapper = new ObjectMapper(); //Jackson's JSON marshaller
        PreorderSaveListParam o= null;
        try {
            o = mapper.readValue(jsonRepresentation, PreorderSaveListParam.class );
        } catch (IOException e) {
            throw new WebApplicationException();
        }
        return o;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Parse error";
        }
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ComplexListParam> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<ComplexListParam> complexes) {
        this.complexes = complexes;
    }
}
