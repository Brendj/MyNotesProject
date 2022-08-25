package ru.iteco.meshsync.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.iteco.meshsync.enums.ActionType;
import ru.iteco.meshsync.enums.EntityType;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityChangeEventDTO {
    private ActionType action;
    private EntityType entity_name;
    private String person_id;
    private String entity_id;
    private List<String> merged_person_ids;
    private String uid;
    private String updated_by;

    public EntityChangeEventDTO() {
    }

    public EntityChangeEventDTO(ActionType action, EntityType entity_name, String person_id, String entity_id) {
        this.action = action;
        this.entity_name = entity_name;
        this.person_id = person_id;
        this.entity_id = entity_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public EntityType getEntity_name() {
        return entity_name;
    }

    public void setEntity_name(EntityType entity) {
        this.entity_name = entity;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public List<String> getMerged_person_ids() {
        return merged_person_ids;
    }

    public void setMerged_person_ids(List<String> merged_person_ids) {
        this.merged_person_ids = merged_person_ids;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }
}
