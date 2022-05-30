package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.Objects;

public class Organization {
    private String name = null;
    private String type = null;
    private String address = null;
    public Organization name(String name) {
        this.name = name;
        return this;
    }



    /**
     * Название организации.
     * @return name
     **/
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Organization type(String type) {
        this.type = type;
        return this;
    }



    /**
     * Тип организации.
     * @return type
     **/
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Organization address(String address) {
        this.address = address;
        return this;
    }



    /**
     * Адрес организации.
     * @return address
     **/
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organization organization = (Organization) o;
        return Objects.equals(this.name, organization.name) &&
                Objects.equals(this.type, organization.type) &&
                Objects.equals(this.address, organization.address);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, type, address);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Organization {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    address: ").append(toIndentedString(address)).append("\n");
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

