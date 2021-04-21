/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.guardian.responsedto;

import ru.iteco.restservice.controller.base.BaseResponseDTO;
import ru.iteco.restservice.model.enums.ClientGuardianRelationType;
import ru.iteco.restservice.model.enums.ClientGuardianRepresentType;

public class GuardianResponseDTO extends BaseResponseDTO {
    private String relation;
    private String isLegalRepresent;

    public GuardianResponseDTO(Long contractId, String firstName, String middleName, String lastname, String grade,
            String orgType, String orgName, Integer relation, Integer isLegalRepresent) {
        super(contractId, firstName, middleName, lastname, grade, orgType, orgName);

        this.relation = ClientGuardianRelationType.of(relation).toString();
        this.isLegalRepresent = ClientGuardianRepresentType.of(isLegalRepresent).toString();
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getIsLegalRepresent() {
        return isLegalRepresent;
    }

    public void setIsLegalRepresent(String isLegalRepresent) {
        this.isLegalRepresent = isLegalRepresent;
    }
}
