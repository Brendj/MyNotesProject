package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.partner.mesh.json.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MeshGuardianConverter {
    public List<MeshGuardianPerson> toDTO(List<SimilarPerson> similarPersons) throws Exception {
        List<MeshGuardianPerson> result = new ArrayList<>();
        for (SimilarPerson similarPerson : similarPersons) {
            MeshGuardianPerson meshGuardianPerson = toDTO(similarPerson);
            result.add(meshGuardianPerson);
        }
        return result;
    }

    public MeshGuardianPerson toDTO(SimilarPerson similarPerson) throws Exception {
        return new MeshGuardianPerson(similarPerson);
    }

    public MeshGuardianPerson toDTO(ResponsePersons responsePersons) throws Exception {
        return new MeshGuardianPerson(responsePersons);
    }

    public MeshAgentResponse toAgentDTO(PersonAgent personAgent) throws Exception {
        return new MeshAgentResponse(personAgent);
    }

    public MeshAgentResponse toAgentErrorDTO(ErrorResponse errorResponse) {
        return new MeshAgentResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }

    public PersonResponse toPersonDTO(ErrorResponse errorResponse) {
        return new PersonResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }

    public PersonListResponse toPersonListDTO(ErrorResponse errorResponse) {
        return new PersonListResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }

    public IdListResponse toIdListDTO(ErrorResponse errorResponse) {
        return new IdListResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }

    public MeshDocumentResponse toDTO(PersonDocument personDocument) throws ParseException {
        return new MeshDocumentResponse(personDocument);
    }

    public MeshDocumentResponse toDTO(ErrorResponse errorResponse) {
        return new MeshDocumentResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }

    public List<AgentIdResponse> agentIdToDTO(ResponsePersons responsePersons) {
        if (responsePersons.getAgents() == null || responsePersons.getAgents().isEmpty())
            return new ArrayList<>();
        return responsePersons.getAgents()
                .stream().map(a -> new AgentIdResponse(a.getId(), a.getAgentPersonId()))
                .collect(Collectors.toList());
    }

    public List<DocumentIdResponse> documentIdToDTO(ResponsePersons responsePersons) {
        if (responsePersons.getDocuments() == null || responsePersons.getDocuments().isEmpty())
            return new ArrayList<>();
        return responsePersons.getDocuments()
                .stream().map(a -> new DocumentIdResponse(a.getId(), a.getDocumentTypeId()))
                .collect(Collectors.toList());
    }

    public List<ContactsIdResponse> contactIdToDTO(ResponsePersons responsePersons) {
        if (responsePersons.getContacts() == null || responsePersons.getContacts().isEmpty())
            return new ArrayList<>();
        return responsePersons.getContacts()
                .stream().map(a -> new ContactsIdResponse(a.getId(), a.getTypeId()))
                .collect(Collectors.toList());
    }

    public MeshContactResponse toContactDTO(Contact contact) {
        return new MeshContactResponse(contact);
    }

    public MeshContactResponse toContactErrorDTO(ErrorResponse errorResponse) {
        return new MeshContactResponse(new Integer(errorResponse.getErrorCode()), errorResponse.getErrorDescription());
    }
}
