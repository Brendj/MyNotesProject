/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * Created by i.semenov on 06.03.2018.
 */
public class SudirBaseData {
    //protected final String token;

    public SudirBaseData() {
        //this.token = token;
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String s = null;
        try {
            s = mapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new WebApplicationException();
        }
        return wrapResponse(s);
    }

    private String wrapResponse(String s) {
        return "{\"" + this.getClass().getSimpleName() + "\":" + s + "}";
    }

    /*public String getToken() {
        return token;
    }*/

}
