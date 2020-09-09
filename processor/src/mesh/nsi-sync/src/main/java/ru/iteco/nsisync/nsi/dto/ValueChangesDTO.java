package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueChangesDTO implements Serializable {
    private GroupValueDTO groupvalue;
    private List<ValueDTO> value;

    public ValueChangesDTO(GroupValueDTO groupvalue, List<ValueDTO> value) {
        this.groupvalue = groupvalue;
        this.value = value;
    }

    public ValueChangesDTO() {
    }

    public GroupValueDTO getGroupvalue() {
        return groupvalue;
    }

    public void setGroupvalue(GroupValueDTO groupvalue) {
        this.groupvalue = groupvalue;
    }

    public List<ValueDTO> getValue() {
        return value;
    }

    public void setValue(List<ValueDTO> value) {
        this.value = value;
    }
}
