package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.iteco.nsisync.nsi.ActionType;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangesDTO implements Serializable {
    private ActionType action;
    private List<AttributeChangesDTO> attribute;

    public ChangesDTO(ActionType action, List<AttributeChangesDTO> attribute) {
        this.action = action;
        this.attribute = attribute;
    }

    public ChangesDTO() {
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public List<AttributeChangesDTO> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<AttributeChangesDTO> attribute) {
        this.attribute = attribute;
    }
}
