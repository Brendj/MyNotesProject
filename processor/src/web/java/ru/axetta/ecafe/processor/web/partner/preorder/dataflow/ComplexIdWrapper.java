/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * Created by i.semenov on 27.02.2018.
 */
public class ComplexIdWrapper {
    private Long complexId;
    private String bu;

    public static ComplexIdWrapper fromString(String jsonRepresentation) {
        ObjectMapper mapper = new ObjectMapper(); //Jackson's JSON marshaller
        ComplexIdWrapper o= null;
        try {
            o = mapper.readValue(jsonRepresentation, ComplexIdWrapper.class );
        } catch (IOException e) {
            throw new WebApplicationException();
        }
        return o;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public String getBu() {
        return bu;
    }

    public void setBu(String bu) {
        this.bu = bu;
    }
}
