package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.partner.mesh.json.ErrorResponse;
import ru.axetta.ecafe.processor.core.partner.mesh.json.PersonDocument;
import ru.axetta.ecafe.processor.core.partner.mesh.json.SimilarPerson;

import java.util.ArrayList;
import java.util.List;

@Component
public class MeshGuardianConverter {
    public List<MeshGuardianPerson> toDTO(List<SimilarPerson> similarPersons) throws Exception {
        List<MeshGuardianPerson> result = new ArrayList<>();
        for (SimilarPerson similarPerson : similarPersons) {
            result.add(toDTO(similarPerson));
        }
        return result;
    }

    public MeshGuardianPerson toDTO(SimilarPerson similarPerson) throws Exception {
        return new MeshGuardianPerson(similarPerson);
    }

    public DocumentResponse toDTO(PersonDocument personDocument) {
        return new DocumentResponse(personDocument);
    }

    public DocumentResponse toDTO(ErrorResponse errorResponse) {
        return new DocumentResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }
}
