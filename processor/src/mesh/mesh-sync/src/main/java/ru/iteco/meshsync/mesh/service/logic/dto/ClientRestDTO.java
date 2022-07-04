package ru.iteco.meshsync.mesh.service.logic.dto;

import ru.iteco.client.model.PersonContact;
import ru.iteco.client.model.PersonDocument;
import ru.iteco.client.model.PersonInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ClientRestDTO implements Serializable {
    private static final Integer PHONE_ID = 1;
    private static final Integer EMAIL_ID = 3;

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
    private String childrenPersonGUID;
    private List<DocumentDTO> documents = new LinkedList<>();

    public static ClientRestDTO build(PersonInfo info) throws Exception {
        ClientRestDTO dto = new ClientRestDTO();
        dto.personGUID = info.getPersonId().toString();
        dto.firstname = info.getFirstname();
        dto.patronymic = info.getPatronymic();
        dto.lastname = info.getLastname();
        dto.genderId = info.getGenderId();
        dto.birthdate = DateUtils.parseSimpleDate(info.getBirthdate().toString());
        dto.address = info.getAddresses().get(0).getAddress().getAddress();

        PersonContact phone = info.getContacts().stream().filter(c -> c.getTypeId().equals(PHONE_ID)).findFirst().orElse(null);
        dto.phone = phone == null ? null : phone.getData();

        PersonContact email = info.getContacts().stream().filter(c -> c.getTypeId().equals(EMAIL_ID)).findFirst().orElse(null);
        dto.email = email == null ? null : email.getData();

        for(PersonDocument pd : info.getDocuments()){
            dto.documents.add(DocumentDTO.build(pd));
        }

        return dto;
    }

    public static ClientRestDTO build(PersonInfo info, String childrenPersonId) throws Exception {
        ClientRestDTO dto = build(info);
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

    public String getChildrenPersonGUID() {
        return childrenPersonGUID;
    }

    public void setChildrenPersonGUID(String childrenPersonGUID) {
        this.childrenPersonGUID = childrenPersonGUID;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }
}
