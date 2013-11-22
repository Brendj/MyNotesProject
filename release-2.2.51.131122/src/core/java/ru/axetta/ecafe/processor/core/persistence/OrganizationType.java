package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.09.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public enum OrganizationType {

    CONSUMER("Потребитель"), SUPPLIER("Поставщик");

    private final String description;

    private OrganizationType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
