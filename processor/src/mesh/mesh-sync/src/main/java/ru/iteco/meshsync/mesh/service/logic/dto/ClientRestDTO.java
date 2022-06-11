package ru.iteco.meshsync.mesh.service.logic.dto;

import ru.iteco.client.model.PersonInfo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientRestDTO implements Serializable {
    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private String personGUID;
    private String firstname;
    private String patronymic;
    private String lastname;
    private Integer genderId;
    private Date birthdate;
    private String address;
    private String phone;
    private String mobile;
    private String email;
    private String passportSeries;
    private String passportNumber;
    private String san;
    private String childrenPersonGUID;

    public static ClientRestDTO build(PersonInfo info, String childrenPersonId) throws Exception {
        ClientRestDTO dto = new ClientRestDTO();
        dto.personGUID = info.getPersonId().toString();
        dto.firstname = info.getFirstname();
        dto.patronymic = info.getPatronymic();
        dto.lastname = info.getLastname();
        dto.genderId = info.getGenderId();
        dto.birthdate = df.parse(info.getBirthdate().toString());
        dto.childrenPersonGUID = childrenPersonId;

        return dto;
    }

    public String getPersonGUID() {
        return personGUID;
    }

    public void setPersonGUID(String personGUID) {
        this.personGUID = personGUID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }

    public String getChildrenPersonGUID() {
        return childrenPersonGUID;
    }

    public void setChildrenPersonGUID(String childrenPersonGUID) {
        this.childrenPersonGUID = childrenPersonGUID;
    }
}
