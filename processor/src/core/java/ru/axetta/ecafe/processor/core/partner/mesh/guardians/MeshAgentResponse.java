package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import ru.axetta.ecafe.processor.core.partner.mesh.json.PersonAgent;

public class MeshAgentResponse extends MeshGuardianResponse {
    private Integer id;

    private String personId;

    private String agentPersonId;

    private Integer agentTypeId;

    private MeshGuardianPerson agentPerson;

    public MeshAgentResponse() {
        super();
    }

    public MeshAgentResponse(Integer code, String message) {
        super(code, message);
    }

    public MeshAgentResponse(PersonAgent personAgent) throws Exception {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        this.id = personAgent.getId();
        this.personId = personAgent.getPersonId();
        this.agentPersonId = personAgent.getAgentPersonId();
        this.agentTypeId = personAgent.getAgentTypeId();
        this.agentPerson = new MeshGuardianPerson(personAgent);
    }

    public MeshAgentResponse okResponse() {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        return this;
    }

    public MeshAgentResponse internalErrorResponse() {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE);
        return this;
    }

    public MeshAgentResponse internalErrorResponse(String message) {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE + " " + message);
        return this;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getAgentPersonId() {
        return agentPersonId;
    }

    public void setAgentPersonId(String agentPersonId) {
        this.agentPersonId = agentPersonId;
    }

    public Integer getAgentTypeId() {
        return agentTypeId;
    }

    public void setAgentTypeId(Integer agentTypeId) {
        this.agentTypeId = agentTypeId;
    }

    public MeshGuardianPerson getAgentPerson() {
        return agentPerson;
    }

    public void setAgentPerson(MeshGuardianPerson agentPerson) {
        this.agentPerson = agentPerson;
    }

}

