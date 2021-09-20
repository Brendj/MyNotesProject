package ru.iteco.meshsync.models;

import ru.iteco.meshsync.audit.AuditEntity;
import ru.iteco.meshsync.audit.AuditEntityListener;
import ru.iteco.meshsync.audit.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_mh_Persons")
public class Person implements Serializable, Auditable {

    @Id
    @Column(name = "personguid")
    private String personGUID;

    @Column(name = "birthdate")
    private Date birthDate;

    @Column(name = "genderid")
    private Integer genderId;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "classuid")
    private String classUID;

    @Column(name = "classname")
    private String className;

    @Column(name = "parallelid")
    private Integer parallelID;

    @Column(name = "deletestate")
    private Boolean deleteState = false;

    @Column(name = "organizationid")
    private Long organizationId;

    @Column(name = "invaliddata", nullable = false)
    private Boolean invalidData;

    @Column(name = "educationstageid")
    private Integer educationStageId;

    @Column(name = "guidnsi")
    private String guidNSI;

    @Embedded
    private AuditEntity audit;

    public Person() {
    }

    @ManyToOne
    @JoinColumn(name = "idofclass")
    private ClassEntity classEntity;

    public Person(String personGUID, Date birthDate, Integer genderId, String lastName, String firstName,
                  String patronymic, String classUID, String className, Integer parallelID, Boolean deleteState,
                  Long organizationId, Boolean invalidData, Integer educationStageId, String guidNSI) {
        this.personGUID = personGUID;
        this.birthDate = birthDate;
        this.genderId = genderId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.classUID = classUID;
        this.className = className;
        this.parallelID = parallelID;
        this.deleteState = deleteState;
        this.organizationId = organizationId;
        this.invalidData = invalidData;
        this.educationStageId = educationStageId;
        this.guidNSI = guidNSI;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public String getPersonGUID() {
        return personGUID;
    }

    public void setPersonGUID(String personGUID) {
        this.personGUID = personGUID;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getClassUID() {
        return classUID;
    }

    public void setClassUID(String classUID) {
        this.classUID = classUID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getParallelID() {
        return parallelID;
    }

    public void setParallelID(Integer parallelID) {
        this.parallelID = parallelID;
    }

    public Boolean getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Boolean deleteState) {
        this.deleteState = deleteState;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getInvalidData() {
        return invalidData;
    }

    public void setInvalidData(Boolean invalidData) {
        this.invalidData = invalidData;
    }

    public Integer getEducationStageId() {
        return educationStageId;
    }

    public void setEducationStageId(Integer educationStageId) {
        this.educationStageId = educationStageId;
    }

    public String getGuidNSI() {
        return guidNSI;
    }

    public void setGuidNSI(String guidNSI) {
        this.guidNSI = guidNSI;
    }

    @Override
    public AuditEntity getAudit() {
        return audit;
    }

    @Override
    public void setAudit(AuditEntity audit) {
        this.audit = audit;
    }
}
