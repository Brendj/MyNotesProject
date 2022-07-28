package ru.axetta.ecafe.processor.web.ui.client;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.MeshDocumentResponse;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.MeshGuardianPerson;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.MeshGuardiansService;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.PersonListResponse;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;
import ru.axetta.ecafe.processor.core.persistence.DulGuide;
import ru.axetta.ecafe.processor.core.service.DulDetailService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.dul.DulSelectPage;

import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MeshClientSelectPage extends BasicPage implements DulSelectPage.CompleteHandler{

    @Override
    public void completeDulSelection(Session session, DulGuide dulGuide) throws Exception {
        this.dulDetail.add(new DulDetail(dulGuide.getDocumentTypeId(), dulGuide));
    }

    public interface CompleteHandler {
        void completeMeshClientSelection(Session session, MeshGuardianPerson meshGuardianPerson) throws Exception;
    }

    private String surname;
    private String firstName;
    private String secondName;
    private Integer gender;
    private Date birthDate;
    private String san;
    private String mobileNumber;
    private final ClientGenderMenu clientGenderMenu = new ClientGenderMenu();
    private List<MeshGuardianPerson> meshGuardianPersonList;
    private List<DulDetail> dulDetail = new ArrayList<>();
    private Boolean disableCreatePersonKey = true;
    private MeshGuardianPerson meshGuardianPerson;

    public void searchMeshPerson(Session session) throws Exception {
        if ((this.san == null || this.san.isEmpty()) && (this.dulDetail == null || this.dulDetail.isEmpty())) {
            throw new Exception("Не заполнено поле \"СНИЛС\" или \"Документы\"");
        }
        if (StringUtils.isEmpty(this.surname) || StringUtils.isEmpty(this.firstName)) {
            throw new Exception("Укажите фамилия и имя обслуживаемого лица");
        }
        if (birthDate == null) {
            throw new Exception("Не заполнено поле \"Дата рождения\"");
        }
        if (this.san != null && !this.san.isEmpty()) {
            this.san = this.san.replaceAll("[\\D]", "");
            ClientManager.validateSan(session, this.san, null);
        }
        RuntimeContext.getAppContext().getBean(DulDetailService.class).validateDulList(session, this.dulDetail, null);
        ClientManager.validateFio(this.surname, this.firstName, this.secondName);
        this.mobileNumber = Client.checkAndConvertMobile(this.mobileNumber);

        this.dulDetail = this.dulDetail.stream()
                .filter(d -> d.getDeleteState() == null || !d.getDeleteState()).collect(Collectors.toList());

        PersonListResponse personListResponse = getMeshGuardiansService().searchPerson(getFirstName(),
                getSecondName(), getSurname(), this.gender, this.birthDate,
                this.san, this.mobileNumber, null, this.dulDetail);
        if (!personListResponse.getCode().equals(PersonListResponse.OK_CODE))
            throw new Exception(personListResponse.getMessage());

        this.meshGuardianPersonList = personListResponse.getResponse()
                .stream().filter(m -> m.getDegree() > 71).collect(Collectors.toList());

        if (!this.meshGuardianPersonList.isEmpty()) {
            this.disableCreatePersonKey = true;
        } else {
            this.meshGuardianPersonList = personListResponse.getResponse();
            this.disableCreatePersonKey = false;
        }
        if (this.meshGuardianPersonList != null && !this.meshGuardianPersonList.isEmpty()) {
            List<String> meshGuidList = this.meshGuardianPersonList
                    .stream().map(MeshGuardianPerson::getMeshGuid).collect(Collectors.toList());
            Query query = session.createQuery("select c.meshGUID from Client c "
                    + "where meshGuid in :meshGuidList");
            query.setParameter("meshGuidList", meshGuidList);
            List<String> list = query.getResultList();
            this.meshGuardianPersonList.forEach(p -> p.setAlreadyInISPP(list.contains(p.getMeshGuid())));
        }
    }

    public String getMeshGuardianPersonStr() {
        if (this.meshGuardianPerson == null)
            return "";
        return String.format("%s %s %s, %s, %s, %s",
                this.meshGuardianPerson.getSurname() == null ? "" : this.meshGuardianPerson.getSurname(),
                this.meshGuardianPerson.getFirstName() == null ? "" : this.meshGuardianPerson.getFirstName(),
                this.meshGuardianPerson.getSecondName() == null ? "" : this.meshGuardianPerson.getSecondName(),
                this.meshGuardianPerson.getIsppGender() == 1 ? "Мужской" : "Женский",
                new SimpleDateFormat("dd.MM.yyyy").format(this.meshGuardianPerson.getBirthDate()),
                this.meshGuardianPerson.getMobile() == null ? "" : this.meshGuardianPerson.getMobile());
    }

    public void fill(Session persistenceSession) {
    }

    public void cancelMeshClientSelection() {
        this.meshGuardianPerson = null;
    }

    public Object clear() {
        this.surname = null;
        this.firstName = null;
        this.secondName = null;
        this.gender = null;
        this.birthDate = null;
        this.san = null;
        this.mobileNumber = null;
        this.meshGuardianPersonList = new ArrayList<>();
        this.dulDetail = new ArrayList<>();
        this.disableCreatePersonKey = true;
        this.meshGuardianPerson = null;
        return null;
    }

    private MeshGuardiansService getMeshGuardiansService() {
        return RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);
    }
    
    private final Stack<MeshClientSelectPage.CompleteHandler> completeHandlers = new Stack<>();

    public void pushCompleteHandler(MeshClientSelectPage.CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeMeshClientSelection(Session persistenceSession) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeMeshClientSelection(persistenceSession, this.meshGuardianPerson);
            completeHandlers.pop();
        }
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public ClientGenderMenu getClientGenderMenu() {
        return clientGenderMenu;
    }

    public List<MeshGuardianPerson> getMeshGuardianPersonList() {
        return meshGuardianPersonList;
    }

    public void setMeshGuardianPersonList(List<MeshGuardianPerson> meshGuardianPersonList) {
        this.meshGuardianPersonList = meshGuardianPersonList;
    }

    public List<DulDetail> getDulDetail() {
        return dulDetail;
    }

    public void setDulDetail(List<DulDetail> dulDetail) {
        this.dulDetail = dulDetail;
    }

    public Boolean getDisableCreatePersonKey() {
        return disableCreatePersonKey;
    }

    public void setDisableCreatePersonKey(Boolean disableCreatePersonKey) {
        this.disableCreatePersonKey = disableCreatePersonKey;
    }

    public MeshGuardianPerson getMeshGuardianPerson() {
        return meshGuardianPerson;
    }

    public void setMeshGuardianPerson(MeshGuardianPerson meshGuardianPerson) {
        this.meshGuardianPerson = meshGuardianPerson;
    }
}
