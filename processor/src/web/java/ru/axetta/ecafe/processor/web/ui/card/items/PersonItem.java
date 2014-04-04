package ru.axetta.ecafe.processor.web.ui.card.items;

import ru.axetta.ecafe.processor.core.persistence.Person;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 */

public class PersonItem {

    private String firstName;
    private String surname;
    private String secondName;
    private String idDocument;

    public PersonItem() {

    }

    public PersonItem(Person person) {
        this.firstName = person.getFirstName();
        this.surname = person.getSurname();
        this.secondName = person.getSecondName();
        this.idDocument = person.getIdDocument();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getIdDocument() {
        return idDocument;
    }
}
