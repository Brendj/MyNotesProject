package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.partner.mesh.json.ErrorResponse;
import ru.axetta.ecafe.processor.core.partner.mesh.json.PersonAgent;
import ru.axetta.ecafe.processor.core.partner.mesh.json.PersonDocument;
import ru.axetta.ecafe.processor.core.partner.mesh.json.SimilarPerson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MeshGuardianConverter {
    public List<MeshGuardianPerson> toDTO(List<SimilarPerson> similarPersons) throws Exception {
        List<MeshGuardianPerson> result = new ArrayList<>();
        for (SimilarPerson similarPerson : similarPersons) {
            MeshGuardianPerson meshGuardianPerson = toDTO(similarPerson);
            meshGuardianPerson.setDocument(documentsToDTO(similarPerson));
            result.add(meshGuardianPerson);
        }
        return result;
    }

    private List<MeshDocumentResponse> documentsToDTO(SimilarPerson similarPerson) throws ParseException {
        List<PersonDocument> personDocuments = similarPerson.getPerson().getDocuments();
        if (personDocuments != null && !personDocuments.isEmpty()) {
            List<MeshDocumentResponse> meshDocumentResponses = new ArrayList<>();
            for (PersonDocument personDocument : personDocuments) {
                meshDocumentResponses.add(toDTO(personDocument));
            }
            return meshDocumentResponses;
        }
        return new ArrayList<>();
    }

    public MeshGuardianPerson toDTO(SimilarPerson similarPerson) throws Exception {
        return new MeshGuardianPerson(similarPerson);
    }

    public PersonResponse toPersonDTO(ErrorResponse errorResponse) {
        return new PersonResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }

    public PersonListResponse toPersonListDTO(ErrorResponse errorResponse) {
        return new PersonListResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }
    public AgentListResponse toAgentListDTO(ErrorResponse errorResponse) {
        return new AgentListResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }

    public MeshDocumentResponse toDTO(PersonDocument personDocument) throws ParseException {
        return new MeshDocumentResponse(personDocument);
    }

    public MeshDocumentResponse toDTO(ErrorResponse errorResponse) {
        return new MeshDocumentResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }

    public List<AgentResponse> agentToDTO(List<SimilarPerson> similarPersons) {
        List<AgentResponse> agentResponses = new ArrayList<>();
        for (SimilarPerson similarPerson : similarPersons) {
            for (PersonAgent personAgent : similarPerson.getPerson().getAgent()) {
                agentResponses.add(new AgentResponse(personAgent.getId(), personAgent.getAgentPersonId()));
            }
        }
        return agentResponses;
    }
}
