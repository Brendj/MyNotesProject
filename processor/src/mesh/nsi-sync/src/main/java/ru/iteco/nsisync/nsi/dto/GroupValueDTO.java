package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupValueDTO implements Serializable {
    private List<SimpleAttributeDTO> item;

    public GroupValueDTO(List<SimpleAttributeDTO> item) {
        this.item = item;
    }

    public GroupValueDTO() {
    }

    public List<SimpleAttributeDTO> getItem() {
        return item;
    }

    public void setItem(List<SimpleAttributeDTO> item) {
        this.item = item;
    }
}
