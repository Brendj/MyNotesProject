/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 27.10.2020.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import ru.axetta.ecafe.processor.web.partner.library.LibraryRequest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

public class JsoneEspDeSerializer extends JsonDeserializer<ESPRequest> {

    @Override
    public ESPRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        ESPRequest espRequest = new ESPRequest();
        for (Iterator<Map.Entry<String, JsonNode>> it = node.getFields(); it.hasNext(); )
        {
            Map.Entry<String, JsonNode> elt = it.next();
            String val = elt.getValue().toString();
            if (val.startsWith("\""))
            {
                val = val.substring(1,val.length());
            }
            if (val.endsWith("\""))
            {
                val = val.substring(0,val.length()-1);
            }
            if (val.equals("null"))
                val = null;
            if (elt.getKey().toLowerCase().equals("daterequest")) {
                String date = val;
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    espRequest.setDateRequest(format.parse(date));
                } catch (ParseException e) { }
            }
            if (elt.getKey().toLowerCase().equals("meshguid")) {
                espRequest.setMeshGuid(val);
            }
            if (elt.getKey().toLowerCase().equals("email")) {
                espRequest.setEmail(val);
            }
            if (elt.getKey().toLowerCase().equals("idoforg")) {
                try {
                    espRequest.setIdOfOrg(Long.valueOf(val));
                }
                catch (Exception e) {
                    espRequest.setIdOfOrg(-1L);
                }
            }
            if (elt.getKey().toLowerCase().equals("topic")) {
                espRequest.setTopic(val);
            }
            if (elt.getKey().toLowerCase().equals("message")) {
                espRequest.setMessage(val);
            }

        }
        return espRequest;
    }
}
