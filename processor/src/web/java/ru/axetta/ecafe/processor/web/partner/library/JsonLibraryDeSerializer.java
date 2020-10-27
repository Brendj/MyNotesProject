/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 27.10.2020.
 */

package ru.axetta.ecafe.processor.web.partner.library;

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

public class JsonLibraryDeSerializer extends JsonDeserializer<LibraryRequest> {

    @Override
    public LibraryRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        LibraryRequest libraryRequest = new LibraryRequest();
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
            if (elt.getKey().toLowerCase().equals("guid")) {
                libraryRequest.setGuid(val);
            }
            if (elt.getKey().toLowerCase().equals("librarycode")) {
                libraryRequest.setLibraryCode(val);
            }
            if (elt.getKey().toLowerCase().equals("libraryname")) {
                libraryRequest.setLibraryName(val);
            }
            if (elt.getKey().toLowerCase().equals("libraryadress")) {
                libraryRequest.setLibraryAdress(val);
            }
            if (elt.getKey().toLowerCase().equals("accesstime")) {
                String date = val;
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    libraryRequest.setAccessTime(format.parse(date));
                } catch (ParseException e) { }
            }
        }
        return libraryRequest;
    }
}
