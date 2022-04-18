package ru.axetta.ecafe.processor.core.service;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;

import java.util.Date;

@Component
@Scope(value = "singleton")
public class DulDetailService {

    public void validateDulDetailPassport(Session persistenceSession, Client client,
                                          String passportNumber, String passportSeries) throws Exception {

        DulDetail dulDetail = getDulDetail(client, Client.PASSPORT_RF_TYPE);

        if (passportNumber.isEmpty() && passportSeries.isEmpty() && dulDetail != null)
            deleteDulDetail(persistenceSession, client, Client.PASSPORT_RF_TYPE);

        if (!passportSeries.isEmpty() && passportNumber.isEmpty())
            throw new Exception("Не заполнено поле \"Номер паспорта\"");

        if (passportSeries.isEmpty() && !passportNumber.isEmpty())
            throw new Exception("Не заполнено поле \"Серия паспорта\"");

        if (!passportSeries.isEmpty() && dulDetail != null)
            updateDulDetail(persistenceSession, dulDetail, passportNumber, passportSeries);
        else if (!passportSeries.isEmpty())
            createDulDetail(persistenceSession, client.getIdOfClient(), Client.PASSPORT_RF_TYPE,
                    passportNumber, passportSeries);
    }

    public void deleteDulDetail(Session persistenceSession, Client client, int type) {
        DulDetail dulDetail = getDulDetail(client, type);
        if (dulDetail != null) {
            dulDetail.setDeleteState(true);
            dulDetail.setLastUpdate(new Date());
            persistenceSession.update(dulDetail);
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

    public DulDetail getDulDetail(Client client, int type) {
        return client.getDulDetail()
                .stream().filter(d -> d.getDocumentTypeId() == type && (
                        d.getDeleteState() == null || !d.getDeleteState()))
                .findAny().orElse(null);
    }
}
