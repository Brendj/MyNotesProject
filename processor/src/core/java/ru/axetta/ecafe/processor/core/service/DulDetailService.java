package ru.axetta.ecafe.processor.core.service;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = "singleton")
public class DulDetailService {

    @Transactional(rollbackFor = Exception.class)
    public void validateAndSaveDulDetails(Session session, List<DulDetail> dulDetails, Long idOfClient) throws Exception {
        Client client = session.get(Client.class, idOfClient);
        Set<DulDetail> originDulDetails = new HashSet<>();
        Date currentDate = new Date();
        boolean change;

        if (client.getDulDetail() != null)
            originDulDetails = client.getDulDetail().stream()
                    .filter(d -> d.getDeleteState() == null || !d.getDeleteState())
                    .collect(Collectors.toSet());

        for (DulDetail dulDetail : dulDetails) {
            if (dulDetail.getDeleteState() == null) {
                dulDetail.setDeleteState(false);
            }
            if (dulDetail.getDeleteState() && dulDetail.getCreateDate() == null) {
                continue;
            }
            if (dulDetail.getDeleteState()) {
                dulDetail.setLastUpdate(currentDate);
                deleteDulDetail(session, dulDetail);
                continue;
            }
            change = true;
            for (DulDetail originDul : originDulDetails) {
                if (dulDetail.equals(originDul)) {
                    change = false;
                    break;
                }
            }
            if (change) {
                if (dulDetail.getCreateDate() == null) {
                    dulDetail.setCreateDate(currentDate);
                    dulDetail.setLastUpdate(currentDate);
                    saveDulDetail(session, dulDetail);
                } else {
                    dulDetail.setLastUpdate(currentDate);
                    updateDulDetail(session, dulDetail);
                }
            }
        }
    }

    private void updateDulDetail(Session session, DulDetail dulDetail) {
        session.merge(dulDetail);
    }

    private void saveDulDetail(Session session, DulDetail dulDetail) {
        session.save(dulDetail);
    }

    private void deleteDulDetail(Session session, DulDetail dulDetail) {
        session.merge(dulDetail);
    }

    //todo на переходный период (пока толстый клиент не доработался)
    public DulDetail getPassportDulDetailByClient(Client client, Long type) {
        if (client.getDulDetail() != null) {
            return client.getDulDetail()
                    .stream().filter(d -> Objects.equals(d.getDocumentTypeId(), type) && (
                            d.getDeleteState() == null || !d.getDeleteState()))
                    .findAny().orElse(null);
        }
        return null;
    }

}
