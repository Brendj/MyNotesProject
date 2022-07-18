package ru.axetta.ecafe.processor.core.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.*;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;
import ru.axetta.ecafe.processor.core.persistence.DulGuide;

import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = "singleton")
public class DulDetailService {

    private static final Logger logger = LoggerFactory.getLogger(DulDetailService.class);

    public void validateAndSaveDulDetails(Session session, List<DulDetail> dulDetails, Long idOfClient) throws Exception {
        Client client = session.get(Client.class, idOfClient);
        MeshDocumentResponse meshDocumentResponse;

        for (DulDetail dulDetail : dulDetails) {
            if (dulDetail.getDeleteState() == null) {
                dulDetail.setDeleteState(false);
            }
            if (dulDetail.getDeleteState() && dulDetail.getCreateDate() == null) {
                continue;
            }
            if (dulDetail.getDeleteState()) {
                meshDocumentResponse = deleteDulDetail(session, dulDetail, client);
                checkError(meshDocumentResponse);
            } else if (isChange(dulDetail, client)) {
                if (dulDetail.getCreateDate() == null) {
                    meshDocumentResponse = saveDulDetail(session, dulDetail, client);
                } else {
                    meshDocumentResponse = updateDulDetail(session, dulDetail, client);
                }
                checkError(meshDocumentResponse);
            }
        }
    }

    public MeshDocumentResponse updateDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        if (!isChange(dulDetail, client)) {
            return new MeshDocumentResponse().okResponse();
        }
        validateDul(session, dulDetail, client);
        dulDetail.setLastUpdate(new Date());
        MeshDocumentResponse documentResponse = new MeshDocumentResponse();
        if (client.getMeshGUID() != null) {
            documentResponse = getMeshGuardiansService().modifyPersonDocument(client.getMeshGUID(), dulDetail);
            if (documentResponse.getCode().equals(MeshDocumentResponse.OK_CODE))
                dulDetail.setIdMkDocument(documentResponse.getId());
            else
                return documentResponse;
        }
        session.merge(dulDetail);
        return documentResponse.okResponse();
    }

    private boolean isChange(DulDetail dulDetail, Client client) {
        Set<DulDetail> originDulDetails = new HashSet<>();
        if (client.getDulDetail() != null) {
            originDulDetails = client.getDulDetail().stream()
                    .filter(d -> d.getDeleteState() == null || !d.getDeleteState())
                    .collect(Collectors.toSet());
        }
        for (DulDetail originDul : originDulDetails)
            if (dulDetail.equals(originDul))
                return false;
        return true;
    }

    public MeshDocumentResponse saveDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        validateDul(session, dulDetail, client);
        Date currentDate = new Date();
        dulDetail.setLastUpdate(currentDate);
        dulDetail.setCreateDate(currentDate);
        dulDetail.setDeleteState(false);
        MeshDocumentResponse documentResponse = new MeshDocumentResponse();
        if (client.getMeshGUID() != null) {
            documentResponse = getMeshGuardiansService().createPersonDocument(client.getMeshGUID(), dulDetail);
            if (documentResponse.getCode().equals(MeshDocumentResponse.OK_CODE))
                dulDetail.setIdMkDocument(documentResponse.getId());
            else
                return documentResponse;
        }
        session.save(dulDetail);
        return documentResponse.okResponse();
    }

    public MeshDocumentResponse deleteDulDetail(Session session, DulDetail dulDetail, Client client) throws Exception {
        dulDetail.setLastUpdate(new Date());
        dulDetail.setDeleteState(true);
        MeshDocumentResponse documentResponse = new MeshDocumentResponse();
        if (client.getMeshGUID() != null) {
            documentResponse = getMeshGuardiansService().deletePersonDocument(client.getMeshGUID(), dulDetail);
        }
        session.merge(dulDetail);
        return documentResponse.okResponse();

    }

    public void validateDulList(Session session, List<DulDetail> dulDetail, Client client) throws Exception {
        for (DulDetail detail : dulDetail) {
            validateDul(session, detail, client);
        }
    }

    private void validateDul(Session session, DulDetail dulDetail, Client client) throws Exception {
        if (client != null)
            if (documentExists(session, client, dulDetail.getDocumentTypeId(), dulDetail.getId()))
                throw new DocumentExistsException("У клиента уже есть документ этого типа");
        if (dulDetail.getExpiration() != null && dulDetail.getIssued() != null && dulDetail.getExpiration().before(dulDetail.getIssued()))
            throw new Exception("Дата истечения срока действия документа, должна быть больше значения «Когда выдан»");
        checkAnotherClient(session, dulDetail, client);
    }

    private void checkAnotherClient(Session session, DulDetail dulDetail, Client client) throws DocumentExistsException {
        //Не должно быть двух персон с одинаковыми действующими документами следующих типов:
        List<Long> dulTypes = Arrays.asList(3L, 124L, 17L, 9L, 1L, 189L, 15L, 900L);

        if (dulTypes.contains(dulDetail.getDocumentTypeId())) {
            String query_str = "select d.idOfClient from DulDetail d where d.documentTypeId = :documentTypeId " +
                    "and d.deleteState = false and d.number = :number ";
            if (dulDetail.getSeries() != null && !dulDetail.getSeries().isEmpty())
                query_str += " and d.series = :series";
            Query query = session.createQuery(query_str);
            query.setParameter("number", dulDetail.getNumber());
            query.setParameter("documentTypeId", dulDetail.getDocumentTypeId());
            if (dulDetail.getSeries() != null && !dulDetail.getSeries().isEmpty()) {
                query.setParameter("series", dulDetail.getSeries());
            }
            List<Long> idOfClients = query.getResultList();
            if (idOfClients == null || idOfClients.isEmpty())
                return;
            if (client != null && idOfClients.size() == 1 && idOfClients.get(0).equals(client.getIdOfClient()))
                return;
            if (idOfClients.size() > 1) {
                Long id = client == null ? null : client.getIdOfClient();
                StringBuilder ids = new StringBuilder();
                for (Long idOfClient : idOfClients) {
                    if (!idOfClient.equals(id))
                        ids.append(idOfClient);
                }
                throw new DocumentExistsException(String.format("Персона c данным документом уже существует, идентификатор клиента: %s", ids));
            }
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

    public Client findClientByDulDetail(Session session, DulDetail dulDetail) {
        String query_str = "select dd.idOfClient from DulDetail dd where dd.documentTypeId = :typeId " +
                "and dd.number = :number";
        if (!StringUtils.isEmpty(dulDetail.getSeries())) {
            query_str += " and dd.series = :series";
        }
        Query query = session.createQuery(query_str);
        query.setParameter("typeId", dulDetail.getDocumentTypeId());
        query.setParameter("number", dulDetail.getNumber());
        if (!StringUtils.isEmpty(dulDetail.getSeries())) {
            query.setParameter("series", dulDetail.getSeries());
        }
        List<Long> list = query.getResultList();
        if (list.size() == 0) return null;
        List<Client> list2 = session.createQuery("select c from Client c join fetch c.person where c.idOfClient = :id")
                .setParameter("id", list.get(0))
                .getResultList();
        return list2.get(0);
    }

    private MeshGuardiansService getMeshGuardiansService() {
        return RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);
    }

    private void checkError(MeshDocumentResponse documentResponse) throws Exception {
        if (!documentResponse.getCode().equals(MeshDocumentResponse.OK_CODE)) {
            logger.error(String.format("%s: %s", documentResponse.getCode(), documentResponse.getMessage()));
            throw new Exception(documentResponse.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void saveDulFromMk(Session session, List<DulDetail> dulDetails, Long idOfClient) {
        Criteria criteria = session.createCriteria(DulGuide.class);
        List<DulGuide> allDulGuides = (List<DulGuide>) criteria.list();
        List<Long> dulGuidesId = allDulGuides.stream().map(DulGuide::getDocumentTypeId).collect(Collectors.toList());
        Date currentDate = new Date();

        for (DulDetail dulDetail : dulDetails) {
            if (!dulGuidesId.contains(dulDetail.getDocumentTypeId()))
                continue;
            if (dulDetail.getCreateDate() == null)
                dulDetail.setCreateDate(currentDate);
            if (dulDetail.getLastUpdate() == null)
                dulDetail.setLastUpdate(currentDate);
            dulDetail.setIdOfClient(idOfClient);
            session.save(dulDetail);
        }
    }
}
