package ru.axetta.ecafe.processor.core.service;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.*;
import ru.axetta.ecafe.processor.core.partner.mesh.json.MeshResponse;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = "singleton")
public class DulDetailService {

    private final MeshGuardiansService meshGuardiansService = RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);

    @Transactional(rollbackFor = Exception.class)
    public void validateAndSaveDulDetails(Session session, List<DulDetail> dulDetails, Long idOfClient) throws Exception {
        Client client = session.get(Client.class, idOfClient);
        Set<DulDetail> originDulDetails = new HashSet<>();
        Date currentDate = new Date();

        if(client == null)
            throw new Exception(String.format("Клиент с id %s не найден", idOfClient));

        if (client.getDulDetail() != null)
            originDulDetails = client.getDulDetail().stream()
                    .filter(d -> d.getDeleteState() == null || !d.getDeleteState())
                    .collect(Collectors.toSet());
        for (DulDetail dulDetail : dulDetails) {
            if (dulDetail.getDeleteState() == null) {
                dulDetail.setDeleteState(false);
            }
            if (Boolean.TRUE.equals(dulDetail.getDeleteState()) && dulDetail.getCreateDate() == null) {
                continue;
            }
            if (Boolean.TRUE.equals(dulDetail.getDeleteState())) {
                dulDetail.setLastUpdate(currentDate);
                deleteDulDetail(session, dulDetail, client);
            } else if (isChange(originDulDetails, dulDetail)) {
                dulDetail.setLastUpdate(currentDate);
                if (dulDetail.getCreateDate() == null) {
                    dulDetail.setCreateDate(currentDate);
                    saveDulDetail(session, dulDetail, client);
                } else {
                    updateDulDetail(session, dulDetail, client);
                }
            }
        }
    }

    public void updateDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        if(ClientManager.isClientGuardian(session, client.getIdOfClient())) {
            DocumentResponse documentResponse = meshGuardiansService.modifyPersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
        }
        session.merge(dulDetail);
    }

    public Long saveDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        if(ClientManager.isClientGuardian(session, client.getIdOfClient())) {
            DocumentResponse documentResponse = meshGuardiansService.createPersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
        }
        session.save(dulDetail);
        return dulDetail.getId();
    }

    public void deleteDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        if(ClientManager.isClientGuardian(session, client.getIdOfClient())) {
            DocumentResponse documentResponse = meshGuardiansService.deletePersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
        }
        session.merge(dulDetail);
    }

    private boolean isChange(Set<DulDetail> originDulDetails, DulDetail dulDetail) {
        for (DulDetail originDul : originDulDetails)
            if (dulDetail.equals(originDul))
                return false;
        return true;
    }

    private void checkError(DocumentResponse documentResponse) throws Exception {
        if (documentResponse.getCode() != 0)
            throw new MeshDocumentSaveException(documentResponse.getMessage());
    }

    //todo на переходный период (пока толстый клиент не доработался)
    public DulDetail getPassportDulDetailByClient(Client client, Long type) {
        if (client.getDulDetail() != null) {
            return client.getDulDetail().stream()
                    .filter(d -> Objects.equals(d.getDocumentTypeId(), type) && (
                            d.getDeleteState() == null || !d.getDeleteState()))
                    .findAny().orElse(null);
        }
        return null;
    }
}
