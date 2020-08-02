package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleAttributeDTO implements Serializable {
    private List<AttributeChangesDTO> attribute;

    public SimpleAttributeDTO(List<AttributeChangesDTO> attribute) {
        this.attribute = attribute;
    }

    public SimpleAttributeDTO() {
    }

    public List<AttributeChangesDTO> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<AttributeChangesDTO> attribute) {
        this.attribute = attribute;
    }
}
