package ru.axetta.ecafe.processor.core.service;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Scope(value = "singleton")
public class DulDetailService {

    public void validateAndSetDulDetailPassport(Session session, Client client,
                                                String passportNumber, String passportSeries) throws Exception {

        DulDetail dulDetail = getDulDetailByClient(client, Client.PASSPORT_RF_TYPE);

        if (passportSeries == null || passportSeries.isEmpty())
            throw new Exception("Не заполнено поле \"Серия паспорта\"");

        if (passportNumber == null || passportNumber.isEmpty())
            throw new Exception("Не заполнено поле \"Номер паспорта\"");

        if (dulDetail != null) {
            updateDulDetail(session, dulDetail, passportNumber, passportSeries);
        } else {
            createDulDetail(session, client.getIdOfClient(), Client.PASSPORT_RF_TYPE,
                    passportNumber, passportSeries);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void validateDulDetails(Session session, List<DulDetail> dulDetails) throws Exception {
        Date currentDate = new Date();
        validateDulDetailPassport(dulDetails);

        for (DulDetail dulDetail : dulDetails) {
            if (dulDetail.getNumber() == null || dulDetail.getNumber().isEmpty()) {
                throw new Exception("Не заполнено обязательное поле \"Номер документа\"");
            }
            if (dulDetail.getCreateDate() == null) {
                dulDetail.setCreateDate(currentDate);
            }
            dulDetail.setLastUpdate(currentDate);
            session.saveOrUpdate(dulDetail);
        }
    }

    private void validateDulDetailPassport(List<DulDetail> dulDetails) throws Exception {
        DulDetail dulDetail = getDulDetailByType(dulDetails, Client.PASSPORT_RF_TYPE);

        if (dulDetail != null) {
            if (!dulDetail.getSeries().isEmpty() && dulDetail.getNumber().isEmpty())
                throw new Exception("Не заполнено поле \"Номер документа\"");
            if (dulDetail.getSeries().isEmpty() && !dulDetail.getNumber().isEmpty())
                throw new Exception("Не заполнено поле \"Серия документа\"");
        }

    }

    public void deleteDulDetail(Session session, List<DulDetail> dulDetails) {
        for(DulDetail dulDetail: dulDetails) {
            if (dulDetail != null) {
                dulDetail.setDeleteState(true);
                dulDetail.setLastUpdate(new Date());
                session.update(dulDetail);
            }
        }
    }

    public void updateDulDetail(Session session, DulDetail dulDetail, String number, String series) {
        dulDetail.setSeries(series);
        dulDetail.setNumber(number);
        dulDetail.setLastUpdate(new Date());
        session.update(dulDetail);
    }

    public void createDulDetail(Session session, Long idOfClient, int type, String number, String series) {
        Date date = new Date();
        DulDetail dulDetail = new DulDetail();
        dulDetail.setNumber(number);
        dulDetail.setSeries(series);
        dulDetail.setCreateDate(date);
        dulDetail.setLastUpdate(date);
        dulDetail.setDocumentTypeId(type);
        dulDetail.setDeleteState(false);
        Client client = session.load(Client.class, idOfClient);
        dulDetail.setClient(client);
        session.save(dulDetail);
    }

    public DulDetail getDulDetailByClient(Client client, int type) {
        if (client.getDulDetail() != null) {
            return client.getDulDetail()
                    .stream().filter(d -> d.getDocumentTypeId() == type && (
                            d.getDeleteState() == null || !d.getDeleteState()))
                    .findAny().orElse(null);
        }
        return null;
    }

    public DulDetail getDulDetailByType(List<DulDetail> dulDetails, int type) {
        return dulDetails
                .stream().filter(d -> d.getDocumentTypeId() == type)
                .findAny().orElse(null);
    }
}
