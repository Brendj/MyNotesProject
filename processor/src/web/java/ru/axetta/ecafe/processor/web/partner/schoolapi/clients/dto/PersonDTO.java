/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

import java.util.Objects;

public class PersonDTO {
    private String surname;
    private String firstName;
    private String secondName;

    protected PersonDTO() {

    }

    public PersonDTO(String surname, String firstName, String secondName) {
        this(surname, firstName);
        this.secondName = secondName;
    }

    public PersonDTO(String surname, String firstName) {
        Objects.requireNonNull(surname);
        Objects.requireNonNull(firstName);
        this.surname = surname;
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonDTO personDTO = (PersonDTO) o;

        if (!surname.equals(personDTO.surname)) {
            return false;
        }
        if (!firstName.equals(personDTO.firstName)) {
            return false;
        }
        return secondName != null ? secondName.equals(personDTO.secondName) : personDTO.secondName == null;
    }

    @Override
    public int hashCode() {
        int result = surname.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + (secondName != null ? secondName.hashCode() : 0);
        return result;
    }
}
