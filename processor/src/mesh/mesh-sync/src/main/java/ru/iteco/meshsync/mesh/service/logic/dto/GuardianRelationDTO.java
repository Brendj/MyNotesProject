package ru.iteco.meshsync.mesh.service.logic.dto;

import ru.iteco.client.model.PersonAgent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class GuardianRelationDTO implements Serializable {
    private String childrenPersonGuid;
    private List<ClientRestDTO> guardianPersonGuids = new LinkedList<>();

    public static GuardianRelationDTO build(String childPersonGuid, List<PersonAgent> agents) throws Exception {
        GuardianRelationDTO dto = new GuardianRelationDTO();

        dto.childrenPersonGuid = childPersonGuid;

        if (agents != null) {
            for (PersonAgent agent : agents) {
                ClientRestDTO clientDTO = ClientRestDTO.build(agent.getAgentPerson());
                clientDTO.setAgentTypeId(agent.getAgentTypeId());
                dto.guardianPersonGuids.add(clientDTO);
            }
        }
        return dto;
    }

    public String getChildrenPersonGuid() {
        return childrenPersonGuid;
    }

    public void setChildrenPersonGuid(String childrenPersonGuid) {
        this.childrenPersonGuid = childrenPersonGuid;
    }

    public List<ClientRestDTO> getGuardianPersonGuids() {
        return guardianPersonGuids;
    }

    public void setGuardianPersonGuids(List<ClientRestDTO> guardianPersonGuids) {
        this.guardianPersonGuids = guardianPersonGuids;
    }
}
