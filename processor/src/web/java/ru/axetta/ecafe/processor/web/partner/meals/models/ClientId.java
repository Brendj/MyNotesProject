package ru.axetta.ecafe.processor.web.partner.meals.models;


import java.util.Objects;

import java.util.UUID;

/**
 * Идентификатор персоны.
 */
public class ClientId {
    private Long contractId = null;
    private Long staffId = null;
    private UUID personId = null;
    public ClientId contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }



    /**
     * Номер лицевого счета.
     * @return contractId
     **/
    public Long getContractId() {
        return contractId;
    }
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
    public ClientId staffId(Long staffId) {
        this.staffId = staffId;
        return this;
    }



    /**
     * Идентификатор сотрудника из реестра кадров.
     * @return staffId
     **/
    public Long getStaffId() {
        return staffId;
    }
    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
    public ClientId personId(UUID personId) {
        this.personId = personId;
        return this;
    }



    /**
     * Идентификатор персоны из МЭШ.Контингента.
     * @return personId
     **/
    public UUID getPersonId() {
        return personId;
    }
    public void setPersonId(UUID personId) {
        this.personId = personId;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientId clientId = (ClientId) o;
        return Objects.equals(this.contractId, clientId.contractId) &&
                Objects.equals(this.staffId, clientId.staffId) &&
                Objects.equals(this.personId, clientId.personId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(contractId, staffId, personId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ClientId {\n");

        sb.append("    contractId: ").append(toIndentedString(contractId)).append("\n");
        sb.append("    staffId: ").append(toIndentedString(staffId)).append("\n");
        sb.append("    personId: ").append(toIndentedString(personId)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

