package ru.iteco.meshsync.mesh.service.logic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.threeten.bp.OffsetDateTime;
import ru.iteco.client.model.PersonContact;
import ru.iteco.client.model.PersonDocument;
import ru.iteco.client.model.PersonInfo;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientRestDTO implements Serializable {
    private static final Integer PHONE_ID = 1;
    private static final Integer EMAIL_ID = 3;

    private String personGUID;
    private String firstname;
    private String patronymic;
    private String lastname;
    private Integer genderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone = "Europe/Moscow")
    private Date birthdate;

    private String mobile = "";
    private String email = "";
    private String childrenPersonGUID;
    private List<DocumentDTO> documents = new LinkedList<>();
    private Integer agentTypeId;
    private String snils;

    public static ClientRestDTO build(PersonInfo info) throws Exception {
        ClientRestDTO dto = new ClientRestDTO();
        dto.personGUID = info.getPersonId().toString();
        dto.firstname = info.getFirstname();
        dto.patronymic = info.getPatronymic();
        dto.lastname = info.getLastname();
        dto.genderId = info.getGenderId();
        dto.birthdate = DateUtils.parseSimpleDate(info.getBirthdate().toString());
        dto.snils = info.getSnils();
        List<PersonContact> personContacts = info.getContacts();
        if (CollectionUtils.isNotEmpty(personContacts)) {
            dto.mobile = checkAndConvertMobile(getContact(personContacts, PHONE_ID));
            dto.email = getContact(personContacts, EMAIL_ID);
        }

        if(CollectionUtils.isNotEmpty(info.getDocuments())) {
            for (PersonDocument pd : info.getDocuments()) {
                dto.documents.add(DocumentDTO.build(pd));
            }
        }

        return dto;
    }

    public static ClientRestDTO build(PersonInfo info, String childrenPersonId) throws Exception {
        ClientRestDTO dto = build(info);
        dto.childrenPersonGUID = childrenPersonId;

        return dto;
    }

    private static String checkAndConvertMobile(String mobilePhone) {
        if (mobilePhone == null || mobilePhone.length() == 0) {
            return mobilePhone;
        }
        mobilePhone = mobilePhone.replaceAll("[+ \\-()]", "");
        if (mobilePhone.startsWith("8")) {
            mobilePhone = "7" + mobilePhone.substring(1);
        }
        if (mobilePhone.length() == 10) {
            mobilePhone = "7" + mobilePhone;
        } else if (mobilePhone.length() != 11) {
            return null;
        }
        return mobilePhone;
    }

    private static String getContact(List<PersonContact> contacts, Integer typeId) {
        List<PersonContact> list = contacts.stream()
                .filter(contact -> (contact.getTypeId().equals(typeId)) && (contact.isDefault()))
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            list = contacts.stream()
                    .filter(contact -> (contact.getTypeId().equals(typeId)))
                    .sorted((PersonContact c1, PersonContact c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                    .collect(Collectors.toList());
        }
        return list.isEmpty() ? null : list.get(0).getData();
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

    public Integer getAgentTypeId() {
        return agentTypeId;
    }

    public void setAgentTypeId(Integer agentTypeId) {
        this.agentTypeId = agentTypeId;
    }

    public String getSnils() {
        return snils;
    }
    public void setSnils(String snils) {
        this.snils = snils;
    }
}
