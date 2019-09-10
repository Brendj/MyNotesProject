/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku.dataflow;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.util.StdDateFormat;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class JsonDateSerializer extends JsonSerializer<Date> {

    private static final DateFormat iso8601Format = StdDateFormat.getBlueprintISO8601Format();

    @Override
    public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        // clone because DateFormat is not thread-safe
        DateFormat myformat = (DateFormat) iso8601Format.clone();
        String formattedDate = myformat.format(date);
        jgen.writeString(formattedDate);
    }
}
