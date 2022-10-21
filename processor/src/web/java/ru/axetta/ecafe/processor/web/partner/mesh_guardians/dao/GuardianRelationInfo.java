package ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao;

import java.util.LinkedList;
import java.util.List;

public class GuardianRelationInfo  implements IDAOEntity {
    private String childrenPersonGuid;
    private List<ClientInfo> guardianPersonGuids = new LinkedList<>();

    public String getChildrenPersonGuid() {
        return childrenPersonGuid;
    }

    public void setChildrenPersonGuid(String childrenPersonGuid) {
        this.childrenPersonGuid = childrenPersonGuid;
    }

    public List<ClientInfo> getGuardianPersonGuids() {
        return guardianPersonGuids;
    }

    public void setGuardianPersonGuids(List<ClientInfo> guardianPersonGuids) {
        this.guardianPersonGuids = guardianPersonGuids;
    }
}
