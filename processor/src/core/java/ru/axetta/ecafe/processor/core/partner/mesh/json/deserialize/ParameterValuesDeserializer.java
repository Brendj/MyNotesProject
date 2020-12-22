/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.json.deserialize;

import ru.axetta.ecafe.processor.core.partner.mesh.json.Parameter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ParameterValuesDeserializer extends StdDeserializer<List<Object>> {
    private final ObjectMapper objectMapper;

    protected ParameterValuesDeserializer(Class<List<Object>> vc) {
        super(vc);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    protected ParameterValuesDeserializer(){
        this(null);
    }

    @Override
    public List<Object> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        List<Object> res = new LinkedList<>();

        for(JsonNode child : node){
            if(child.isObject() && child.has("name")){
                String name = child.get("name").asText();
                String value = child.get("value").asText();
                res.add(new Parameter(name, value));
            } else {
                res.add(child.asText());
            }
        }

        return res;
    }
}
