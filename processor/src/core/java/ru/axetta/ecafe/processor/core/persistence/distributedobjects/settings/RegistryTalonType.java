package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 21.04.14
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public enum RegistryTalonType {

    Benefit_Plan("Льготный реестр талонов "),
    Pay_Plan("Платный реестр талонов"),
    Subscriber_Feeding_Plan("Абонементный реестр талонов");

    private String description;

    private RegistryTalonType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return  description;
    }
}
