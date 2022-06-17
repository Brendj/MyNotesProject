package ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao;

import java.util.LinkedList;
import java.util.List;

public class GuardianRelationInfo  implements IDAOEntity {
    private String childrenPersonGuid;
    private List<String> guardianPersonGuids = new LinkedList<>();

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
