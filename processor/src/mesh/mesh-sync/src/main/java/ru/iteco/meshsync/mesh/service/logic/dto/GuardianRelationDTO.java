package ru.iteco.meshsync.mesh.service.logic.dto;

import ru.iteco.client.model.PersonAgent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class GuardianRelationDTO implements Serializable {
    private String childrenPersonGuid;
    private List<String> guardianPersonGuids = new LinkedList<>();

    public static GuardianRelationDTO build(String childrenPersonGuid, List<PersonAgent> guardians) {
        GuardianRelationDTO dto = new GuardianRelationDTO();

        dto.childrenPersonGuid = childrenPersonGuid;

        for(PersonAgent g : guardians){
            dto.guardianPersonGuids.add(g.getPersonId().toString());
        }

        return dto;
    }

    public String getChildrenPersonGuid() {
        return childrenPersonGuid;
    }

    public void setChildrenPersonGuid(String childrenPersonGuid) {
        this.childrenPersonGuid = childrenPersonGuid;
    }

    public List<String> getGuardianPersonGuids() {
        return guardianPersonGuids;
    }

    public void setGuardianPersonGuids(List<String> guardianPersonGuids) {
        this.guardianPersonGuids = guardianPersonGuids;
    }
}
