/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by i.semenov on 13.03.2018.
 */
public class ComplexListParam {

    private Long contractId;
    private Long idOfComplex;
    private Boolean selected;
    private Integer amount;
    private List<MenuItemParam> menuItems;

    public static ComplexListParam fromString(String jsonRepresentation) {
        ObjectMapper mapper = new ObjectMapper(); //Jackson's JSON marshaller
        ComplexListParam o= null;
        try {
            o = mapper.readValue(jsonRepresentation, ComplexListParam.class );
        } catch (IOException e) {
            throw new WebApplicationException();
        }
        return o;
    }
}
