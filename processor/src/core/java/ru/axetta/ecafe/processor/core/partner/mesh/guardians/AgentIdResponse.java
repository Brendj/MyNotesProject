package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class AgentIdResponse {

    private Integer agentId;
    private String agentMeshGuid;

    public AgentIdResponse(Integer agentId, String agentMeshGuid) {
        this.agentId = agentId;
        this.agentMeshGuid = agentMeshGuid;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getAgentMeshGuid() {
        return agentMeshGuid;
    }

    public void setAgentMeshGuid(String agentMeshGuid) {
        this.agentMeshGuid = agentMeshGuid;
    }
}