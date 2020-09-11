package ru.iteco.nsisync.nsi.utils;

import ru.iteco.nsisync.nsi.dto.AttributeChangesDTO;
import ru.iteco.nsisync.nsi.dto.GroupValueDTO;

import java.util.List;

public class CatalogDTOUtil {
    public static String getAttributeByName(List<AttributeChangesDTO> changesAttributeDTOList, JsonFieldDescriptor descriptor){
        return changesAttributeDTOList
                .stream()
                .filter(attribute -> attribute.getName().equalsIgnoreCase(descriptor.getJsonFieldName()))
                .findFirst()
                .map(attribute -> attribute.getValues().getValue().get(0).getValue())
                .orElse("");
    }

    //for DICTIONARY
    public static Integer getAttributeDictionaryIdByName(List<AttributeChangesDTO> changesAttributeDTOList, JsonFieldDescriptor descriptor){
        String id = changesAttributeDTOList
                .stream()
                .filter(attribute -> attribute.getName().equalsIgnoreCase(descriptor.getJsonFieldName()))
                .findFirst()
                .map(attribute -> attribute.getValues().getValue().get(0).getId())
                .orElse("");
        return id.isEmpty() ? null : Integer.valueOf(id);
    }

    //for DICTIONARY
    public static String getAttributeDictionaryValueByName(List<AttributeChangesDTO> changesAttributeDTOList, JsonFieldDescriptor descriptor){
        return changesAttributeDTOList
                .stream()
                .filter(attribute -> attribute.getName().equalsIgnoreCase(descriptor.getJsonFieldName()))
                .findFirst()
                .map(attribute -> attribute.getValues().getValue().get(0).getValue())
                .orElse("");
    }

    public static GroupValueDTO getGroupValueByName(List<AttributeChangesDTO> changesAttributeDTOList, JsonFieldDescriptor descriptor){
        return changesAttributeDTOList
                .stream()
                .filter(attribute -> attribute.getName().equalsIgnoreCase(descriptor.getJsonFieldName()))
                .findFirst()
                .map(attribute -> attribute.getValues().getGroupvalue())
                .orElse(null);
    }

    public static Integer getAttributeAsIntegerByName(List<AttributeChangesDTO> changesAttributeDTOList, JsonFieldDescriptor descriptor) throws NumberFormatException {
        String result = getAttributeByName(changesAttributeDTOList, descriptor);
        return result.isEmpty() ? null : Integer.valueOf(result);
    }

    public static Long getAttributeAsLongByName(List<AttributeChangesDTO> changesAttributeDTOList, JsonFieldDescriptor descriptor) throws NumberFormatException {
        String result = getAttributeByName(changesAttributeDTOList, descriptor);
        return result.isEmpty() ? null : Long.valueOf(result);
    }

    public static Boolean getAttributeAsBoolByName(List<AttributeChangesDTO> changesAttributeDTOList, JsonFieldDescriptor descriptor) {
        String result = getAttributeByName(changesAttributeDTOList, descriptor);
        return result.isEmpty() ? null : result.equals("1");
    }
}
