/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.guardian.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.controller.base.BaseResponseDTO;
import ru.iteco.restservice.model.enums.ClientGuardianRelationType;
import ru.iteco.restservice.model.enums.ClientGuardianRepresentType;

@Schema(name = "GuardianResponseDTO", description = "Данные по представителю")
public class GuardianResponseDTO extends BaseResponseDTO {

    @Schema(description = "Степень родства представителя обучающегося по отношению к обучающемуся", example = "Отец")
    private String relation;

    @Schema(description = "Роль представителя обучающегося по отношению к обучающемуся", example = "Законный представитель")
    private String isLegalRepresent;

    public GuardianResponseDTO(Long contractId, String firstName, String lastName, String middleName, String grade,
            String orgName, String orgType, Integer relation, Integer isLegalRepresent) {
        super(contractId, firstName, middleName, lastName, grade, orgType, orgName);

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
