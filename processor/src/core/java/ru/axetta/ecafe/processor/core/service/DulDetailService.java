package ru.axetta.ecafe.processor.core.service;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.*;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;

import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = "singleton")
public class DulDetailService {

    private final MeshGuardiansService meshGuardiansService = RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);
    private static final Logger logger = LoggerFactory.getLogger(DulDetailService.class);

    @Transactional(rollbackFor = Exception.class)
    public void validateAndSaveDulDetails(Session session, List<DulDetail> dulDetails, Long idOfClient) throws Exception {
        Client client = session.get(Client.class, idOfClient);
        Set<DulDetail> originDulDetails = new HashSet<>();
        Date currentDate = new Date();

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
        if (documentExists(session, client, dulDetail.getDocumentTypeId(), dulDetail.getId()))
            throw new DocumentExistsException("У клиента уже есть документ этого типа");
        if(ClientManager.isClientGuardian(client)) {
            MeshDocumentResponse documentResponse = meshGuardiansService.modifyPersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
        }
        session.merge(dulDetail);
    }

    public Long saveDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        if (documentExists(session, client, dulDetail.getDocumentTypeId(), null))
            throw new DocumentExistsException("У клиента уже есть документ этого типа");
        if(ClientManager.isClientGuardian(client)) {
            MeshDocumentResponse documentResponse = meshGuardiansService.createPersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
            dulDetail.setIdMkDocument(documentResponse.getId());
        }
        session.save(dulDetail);
        return dulDetail.getId();
    }

    public void deleteDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        if(ClientManager.isClientGuardian(client)) {
            MeshDocumentResponse documentResponse = meshGuardiansService.deletePersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
        }
        session.merge(dulDetail);
    }

    private boolean documentExists(Session session, Client client, Long documentTypeId, Long id) {
        String query_str = "select d.id from DulDetail d where d.idOfClient = :idOfClient " +
                "and d.documentTypeId = :documentTypeId and d.deleteState = false";
        if (id != null) query_str += " and d.id <>:id";
        Query query = session.createQuery(query_str);
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("documentTypeId", documentTypeId);
        if (id != null) {
            query.setParameter("id", id);
        }
        return query.getResultList().size() > 0;
    }

    private boolean isChange(Set<DulDetail> originDulDetails, DulDetail dulDetail) {
        for (DulDetail originDul : originDulDetails)
            if (dulDetail.equals(originDul))
                return false;
        return true;
    }

    private void checkError(MeshDocumentResponse documentResponse) throws Exception {
        if (documentResponse.getCode() != 0) {
            logger.error(String.format("%s: %s", documentResponse.getCode(), documentResponse.getMessage()));
            throw new MeshDocumentSaveException(documentResponse.getMessage());
        }
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
