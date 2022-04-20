package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import ru.axetta.ecafe.processor.core.partner.mesh.json.SimilarPerson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeshGuardianPerson {
    private String firstName;
    private String surname;
    private String secondName;
    private String meshGuid;
    private Date birthDate;
    private String snils;
    private Integer gender;
    private Integer degree;

    public MeshGuardianPerson() {

    }

    public MeshGuardianPerson(SimilarPerson similarPerson) throws Exception {
        this.meshGuid = similarPerson.getPerson().getPersonId();
        this.firstName = similarPerson.getPerson().getFirstname();
        this.secondName = similarPerson.getPerson().getPatronymic();
        this.surname = similarPerson.getPerson().getLastname();
        this.birthDate = getDateFromString(similarPerson.getPerson().getBirthdate());
        this.snils = similarPerson.getPerson().getSnils();
        this.gender = getMeshGender(similarPerson.getPerson().getGenderId());
        this.degree = similarPerson.getDegree();
    }

    private Date getDateFromString(String date) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat(MeshGuardiansService.DATE_PATTERN);
        return dateFormat.parse(date);
    }

    private Integer getMeshGender(Integer gender) {
        return gender;
    }
}
