package ru.axetta.ecafe.processor.web.partner.meals.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientData")
public class ClientData {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ClientId clientId = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Organization organization = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean preorderAllowed = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long balance = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean foodboxAllowed = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean foodboxAvailable = null;

    public ClientData clientId(ClientId clientId) {
        this.clientId = clientId;
        return this;
    }



    /**
     * Get clientId
     * @return clientId
     **/
    public ClientId getClientId() {
        if (clientId == null)
            clientId = new ClientId();
        return clientId;
    }
    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }
    public ClientData organization(Organization organization) {
        this.organization = organization;
        return this;
    }



    /**
     * Get organization
     * @return organization
     **/
    public Organization getOrganization() {
        if (organization == null)
            organization = new Organization();
        return organization;
    }
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    public ClientData preorderAllowed(Boolean preorderAllowed) {
        this.preorderAllowed = preorderAllowed;
        return this;
    }



    /**
     * Признак согласия представителя на получение услуги предзаказа.
     * @return preorderAllowed
     **/
    public Boolean isPreorderAllowed() {
        return preorderAllowed;
    }
    public void setPreorderAllowed(Boolean preorderAllowed) {
        this.preorderAllowed = preorderAllowed;
    }
    public ClientData balance(Long balance) {
        this.balance = balance;
        return this;
    }



    /**
     * Баланс в копейках.
     * @return balance
     **/
    public Long getBalance() {
        return balance;
    }
    public void setBalance(Long balance) {
        this.balance = balance;
    }
    public ClientData foodboxAllowed(Boolean foodboxAllowed) {
        this.foodboxAllowed = foodboxAllowed;
        return this;
    }



    /**
     * Признак разрешения представителем на использование фудбокса ребёнком
     * @return foodboxAllowed
     **/
    public Boolean isFoodboxAllowed() {
        return foodboxAllowed;
    }
    public void setFoodboxAllowed(Boolean foodboxAllowed) {
        this.foodboxAllowed = foodboxAllowed;
    }
    public ClientData foodboxAvailablе(Boolean foodboxAvailablе) {
        this.foodboxAvailable = foodboxAvailablе;
        return this;
    }



    /**
     * Признак доступности использования фудбокса для образовательной организации
     * @return foodboxAvailablе
     **/
    public Boolean isFoodboxAvailablе() {
        return foodboxAvailable;
    }
    public void setFoodboxAvailable(Boolean foodboxAvailable) {
        this.foodboxAvailable = foodboxAvailable;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientData clientData = (ClientData) o;
        return Objects.equals(this.clientId, clientData.clientId) &&
                Objects.equals(this.organization, clientData.organization) &&
                Objects.equals(this.preorderAllowed, clientData.preorderAllowed) &&
                Objects.equals(this.balance, clientData.balance) &&
                Objects.equals(this.foodboxAllowed, clientData.foodboxAllowed) &&
                Objects.equals(this.foodboxAvailable, clientData.foodboxAvailable);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(clientId, organization, preorderAllowed, balance, foodboxAllowed, foodboxAvailable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ClientData {\n");

        sb.append("    clientId: ").append(toIndentedString(clientId)).append("\n");
        sb.append("    organization: ").append(toIndentedString(organization)).append("\n");
        sb.append("    preorderAllowed: ").append(toIndentedString(preorderAllowed)).append("\n");
        sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
        sb.append("    foodboxAllowed: ").append(toIndentedString(foodboxAllowed)).append("\n");
        sb.append("    foodboxAvailablе: ").append(toIndentedString(foodboxAvailable)).append("\n");
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
