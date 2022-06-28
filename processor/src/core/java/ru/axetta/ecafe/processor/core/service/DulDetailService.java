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

    private static final Logger logger = LoggerFactory.getLogger(DulDetailService.class);

    @Transactional(rollbackFor = Exception.class)
    public void validateAndSaveDulDetails(Session session, List<DulDetail> dulDetails, Long idOfClient) throws Exception {
        Client client = session.get(Client.class, idOfClient);
        Set<DulDetail> originDulDetails = new HashSet<>();
        Date currentDate = new Date();

        if (client.getDulDetail() != null) {
            originDulDetails = client.getDulDetail().stream()
                    .filter(d -> d.getDeleteState() == null || !d.getDeleteState())
                    .collect(Collectors.toSet());
        }
        for (DulDetail dulDetail : dulDetails) {
            if (dulDetail.getDeleteState() == null) {
                dulDetail.setDeleteState(false);
            }
            if (dulDetail.getDeleteState() && dulDetail.getCreateDate() == null) {
                continue;
            }
            if (dulDetail.getDeleteState()) {
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
        validateDul(session, dulDetail, client);
        if(ClientManager.isClientGuardian(client)) {
            MeshDocumentResponse documentResponse = getMeshGuardiansService().modifyPersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
        }
        session.merge(dulDetail);
    }

    public Long saveDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        validateDul(session, dulDetail, client);
        if(ClientManager.isClientGuardian(client)) {
            MeshDocumentResponse documentResponse = getMeshGuardiansService().createPersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
            dulDetail.setIdMkDocument(documentResponse.getId());
        }
        session.save(dulDetail);
        return dulDetail.getId();
    }

    public void deleteDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        if(ClientManager.isClientGuardian(client)) {
            MeshDocumentResponse documentResponse = getMeshGuardiansService().deletePersonDocument(client.getMeshGUID(), dulDetail);
            checkError(documentResponse);
        }
        session.merge(dulDetail);
    }

    private void validateDul(Session session, DulDetail dulDetail, Client client) throws Exception {
        if (documentExists(session, client, dulDetail.getDocumentTypeId(), dulDetail.getId()))
            throw new DocumentExistsException("У клиента уже есть документ этого типа");
        if (dulDetail.getExpiration().before(dulDetail.getIssued()))
            throw new Exception("Дата истечения срока действия документа, должна быть больше значения «Когда выдан»");
        checkAnotherClient(session, dulDetail, client);
    }

    private void checkAnotherClient(Session session, DulDetail dulDetail, Client client) throws DocumentExistsException {
        //Не должно быть двух персон с одинаковыми действующими документами следующих типов:
        List<Long> dulTypes = Arrays.asList(3L, 124L, 17L, 9L, 1L, 189L, 15L, 900L);

        if (dulTypes.contains(dulDetail.getDocumentTypeId())) {
            String query_str = "select d.idOfClient from DulDetail d where d.documentTypeId = :documentTypeId " +
                    "and d.deleteState = false and d.number = :number ";
            if (dulDetail.getSeries() != null && !dulDetail.getSeries().isEmpty()) query_str += " and d.series = :series";
            Query query = session.createQuery(query_str);
            query.setParameter("number", dulDetail.getNumber());
            query.setParameter("documentTypeId", dulDetail.getDocumentTypeId());
            if (dulDetail.getSeries() != null && !dulDetail.getSeries().isEmpty()) {
                query.setParameter("series", dulDetail.getSeries());
            }
            List<Long> longList = query.getResultList();
            if (longList == null || longList.isEmpty())
                return;
            if (longList.size() > 1 || !longList.get(0).equals(client.getIdOfClient()))
                throw new DocumentExistsException("Не должно быть двух персон с одинаковыми действующими документами");
        }
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

    private MeshGuardiansService getMeshGuardiansService() {
        return RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);
    }
}
