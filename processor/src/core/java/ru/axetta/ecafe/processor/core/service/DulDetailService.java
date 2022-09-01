package ru.axetta.ecafe.processor.core.service;

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
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Scope(value = "singleton")
public class DulDetailService {

    private static final Logger logger = LoggerFactory.getLogger(DulDetailService.class);
    //Не должно быть двух персон с одинаковыми действующими документами следующих типов:
    private final List<Long> dulTypes = Arrays.asList(3L, 124L, 17L, 9L, 1L, 189L, 15L, 900L, 902L, 11L, 13L);
    private final String validateError = "Неверное поле \"Серия\" или \"Номер\" документа";

    /**
     * {@link #validateDul(Session, DulDetail, boolean)
     * Использовать валидацию перед сохранением документа.}
     */
    public MeshDocumentResponse updateDulDetail(Session session, DulDetail dulDetail, Client client, boolean saveToMK) throws Exception {
        if (!isChange(dulDetail, client)) {
            return new MeshDocumentResponse().okResponse();
        }
        dulDetail.setLastUpdate(new Date());
        MeshDocumentResponse documentResponse = new MeshDocumentResponse().okResponse();
        if (saveToMK && client.getMeshGUID() != null) {
            documentResponse = getMeshGuardiansService().modifyPersonDocument(client.getMeshGUID(), dulDetail);
            if (documentResponse.getCode().equals(MeshDocumentResponse.OK_CODE))
                dulDetail.setIdMkDocument(documentResponse.getId());
            else
                return documentResponse;
        }
        session.merge(dulDetail);
        return documentResponse;
    }

    /**
     * {@link #validateDul(Session, DulDetail, boolean)
     * Использовать валидацию перед сохранением документа.}
     */
    public MeshDocumentResponse saveDulDetail(Session session, DulDetail dulDetail, Client client, boolean saveToMK) throws Exception {
        if (documentExists(session, client, dulDetail.getDocumentTypeId(), dulDetail.getId()))
            throw new DocumentExistsException("У клиента уже есть документ этого типа", null, dulDetail.getDocumentTypeId());
        Date currentDate = new Date();
        dulDetail.setLastUpdate(currentDate);
        dulDetail.setCreateDate(currentDate);
        dulDetail.setDeleteState(false);
        MeshDocumentResponse documentResponse = new MeshDocumentResponse().okResponse();
        if (saveToMK && client.getMeshGUID() != null) {
            documentResponse = getMeshGuardiansService().createPersonDocument(client.getMeshGUID(), dulDetail);
            if (documentResponse.getCode().equals(MeshDocumentResponse.OK_CODE))
                dulDetail.setIdMkDocument(documentResponse.getId());
            else
                return documentResponse;
        }
        session.save(dulDetail);
        return documentResponse;
    }

    public MeshDocumentResponse deleteDulDetail(Session session, DulDetail dulDetail, Client client, boolean saveToMK) throws Exception {
        dulDetail.setLastUpdate(new Date());
        dulDetail.setDeleteState(true);
        MeshDocumentResponse documentResponse = new MeshDocumentResponse().okResponse();
        if (saveToMK && client.getMeshGUID() != null) {
            documentResponse = getMeshGuardiansService().deletePersonDocument(client.getMeshGUID(), dulDetail);
        }
        session.merge(dulDetail);
        return documentResponse;
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

    public void validateDulList(Session session, List<DulDetail> dulDetail, boolean checkAnotherClient) throws Exception {
        for (DulDetail detail : dulDetail) {
            validateDul(session, detail, checkAnotherClient);
        }
    }

    public void validateDul(Session session, DulDetail dulDetail, boolean checkAnotherClient) throws Exception {
        try {
            if (isBlank(dulDetail.getNumber()))
                throw new DocumentValidateException("Не заполнено поле \"Номер\" документа", dulDetail.getDocumentTypeId());
            validateDulNumberAndSeries(session, dulDetail);
            if (dulDetail.getExpiration() != null && dulDetail.getIssued() != null && dulDetail.getExpiration().before(dulDetail.getIssued()))
                throw new DocumentValidateException("Дата истечения срока действия документа, должна быть больше значения «Когда выдан»", dulDetail.getDocumentTypeId());
            if (checkAnotherClient) {
                List<Long> ids = checkAnotherClient(session, dulDetail);
                if (!ids.isEmpty())
                    throw new DocumentExistsException(String
                            .format("Персона c данным документом уже существует, идентификатор клиента: %s", ids.get(0)), ids.get(0), dulDetail.getDocumentTypeId());
            }
        } catch (Exception e) {
            if (!(e instanceof DocumentValidateException) && !(e instanceof DocumentExistsException)) {
                logger.error(e.getMessage());
                throw new DocumentValidateException("Неизвестная ошибка сохранения документа", dulDetail.getDocumentTypeId());
            } else
                throw e;
        }
    }

    public void validateDulNumberAndSeries(Session session, DulDetail dulDetail) throws Exception {
        if (dulDetail.getDocumentTypeId().equals(15L)) {
            validatePassport(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(1L)) {
            validateSSSR(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(16L)) {
            validateInternationalPassport(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(900L)) {
            validateForeignPassport(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(19L)) {
            validateSailorPassport(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(3L)) {
            validateBirthCertificate(session, dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(6L)) {
            validateMilitaryPassport(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(18L)) {
            validateSoldierID(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(13L)) {
            validateTemporaryResidence(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(12L)) {
            validateTemporaryID(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(10L)) {
            validateResidentCard(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(11L)) {
            validateRefugeeID(dulDetail);
        } else if (dulDetail.getDocumentTypeId().equals(5L)) {
            validateReleaseNote(dulDetail);
        }
    }

    private void validateReleaseNote(DulDetail dulDetail) throws DocumentValidateException {
        if (dulDetail.getNumber().length() > 12 || !containsDigit(dulDetail.getNumber())) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
    }

    private void validateRefugeeID(DulDetail dulDetail) throws DocumentValidateException {
        String pattern = "^[0-9-]+$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(dulDetail.getNumber());

        if (!containsDigit(dulDetail.getNumber()) || dulDetail.getNumber().length() != 10 || !m.matches()) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
    }

    private void validateResidentCard(DulDetail dulDetail) throws DocumentValidateException {
        String pattern = "^[а-я0-9a-z-]+$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(dulDetail.getNumber());

        if (!containsDigit(dulDetail.getNumber()) || dulDetail.getNumber().length() > 16 || !m.matches()) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
    }

    private void validateTemporaryID(DulDetail dulDetail) throws DocumentValidateException {
        if (!containsDigit(dulDetail.getNumber()) || dulDetail.getNumber().length() > 12) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
    }

    private void validateTemporaryResidence(DulDetail dulDetail) throws DocumentValidateException {
        String pattern = "^[а-я0-9a-z-]+$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(dulDetail.getNumber());

        if (!containsDigit(dulDetail.getNumber()) || dulDetail.getNumber().length() > 16 || !m.matches()) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
    }

    private void validateSoldierID(DulDetail dulDetail) throws DocumentValidateException {
        String cyrillic = ".*[а-яА-Я]+.*";
        String latin = ".*[a-zA-Z]+.*";

        if (isBlank(dulDetail.getSeries()) || dulDetail.getSeries().length() != 2
                || dulDetail.getNumber().length() != 7 || !isDigit(dulDetail.getNumber())
                || !dulDetail.getSeries().matches(cyrillic) || dulDetail.getSeries().matches(latin)
                || containsDigit(dulDetail.getSeries())) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }

    }

    private void validatePassport(DulDetail dulDetail) throws DocumentValidateException {
        if (isBlank(dulDetail.getSeries()) || dulDetail.getSeries().length() != 4 || dulDetail.getNumber().length() != 6
                || !isDigit(dulDetail.getSeries()) || !isDigit(dulDetail.getNumber()))
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
    }

    private void validateInternationalPassport(DulDetail dulDetail) throws DocumentValidateException {
        if (dulDetail.getNumber().length() != 9 || !isDigit(dulDetail.getNumber()))
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
    }

    private void validateBirthCertificate(Session session, DulDetail dulDetail) throws DocumentValidateException {
        String cyrillic = ".*[а-яА-Я]+.*";

        if (isBlank(dulDetail.getSeries()) || dulDetail.getNumber().length() != 6
                || dulDetail.getSeries().length() < 3)
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());

        if (dulDetail.getIdOfClient() != null && dulDetail.getIdOfClient() != 0L) {
            Client client = session.get(Client.class, dulDetail.getIdOfClient());
            if (client != null && client.getBirthDate() != null) {
                LocalDate birthDate = client.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentDate = LocalDate.now();
                int age = Period.between(birthDate, currentDate).getYears() * 12 + Period.between(birthDate, currentDate).getMonths();
                if (age > 170) {
                    throw new DocumentValidateException("Снилс разрешен для пассажиров, возраст " +
                            "которых не превышает 14 лет и 2 месяцев", dulDetail.getDocumentTypeId());
                }
            }
        }
        validateRoman(dulDetail, cyrillic);
    }

    private void validateMilitaryPassport(DulDetail dulDetail) throws DocumentValidateException {
        String cyrillic = ".*[а-яА-Я]+.*";
        String latin = ".*[a-zA-Z]+.*";

        if (isBlank(dulDetail.getSeries()) || dulDetail.getSeries().length() != 2
                || dulDetail.getNumber().length() > 7 || dulDetail.getNumber().length() < 6 || !isDigit(dulDetail.getNumber())
                || !dulDetail.getSeries().matches(cyrillic) || dulDetail.getSeries().matches(latin) || containsDigit(dulDetail.getSeries())) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
    }

    private void validateSailorPassport(DulDetail dulDetail) throws DocumentValidateException {
        String cyrillic = ".*[а-яА-Я]+.*";
        String latin = ".*[a-zA-Z]+.*";
        boolean latinOrCyrillicOrDigit = dulDetail.getSeries().matches(latin);

        if (isBlank(dulDetail.getSeries()) || dulDetail.getSeries().length() != 2
                || dulDetail.getNumber().length() != 7 || !isDigit(dulDetail.getNumber())) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
        if (dulDetail.getSeries().matches(cyrillic)) {
            if (latinOrCyrillicOrDigit) {
                throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
            }
            latinOrCyrillicOrDigit = true;
        }
        if (containsDigit(dulDetail.getSeries())) {
            if (latinOrCyrillicOrDigit) {
                throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
            }
        }
    }

    private boolean containsDigit(String str) {
        for (char c : str.toCharArray()) {
            if (isDigit(Character.toString(c))) {
                return true;
            }
        }
        return false;
    }

    private void validateForeignPassport(DulDetail dulDetail) throws DocumentValidateException {
        String cyrillic = ".*[а-яА-Я]+.*";

        if (dulDetail.getNumber().length() < 5 || dulDetail.getNumber().length() > 14) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
        if (!containsDigit(dulDetail.getNumber()) && Pattern.compile(cyrillic).matcher(dulDetail.getNumber()).matches()) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }

    }

    private boolean isDigit(String number) {
        return number.matches("\\d+");
    }

    private void validateSSSR(DulDetail dulDetail) throws Exception {
        String cyrillic = ".*[а-яА-Я]+.*";

        if (isBlank(dulDetail.getSeries()) || !isDigit(dulDetail.getNumber())
                || dulDetail.getNumber().length() != 6 || Integer.parseInt(dulDetail.getNumber()) < 500001
                || Integer.parseInt(dulDetail.getNumber()) > 750000 || dulDetail.getSeries().length() < 3) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
        validateRoman(dulDetail, cyrillic);
    }

    private void validateRoman(DulDetail dulDetail, String cyrillic) throws DocumentValidateException {
        try {
            String russian = dulDetail.getSeries().substring(dulDetail.getSeries().length() - 2);
            String roman = dulDetail.getSeries().substring(0, dulDetail.getSeries().length() - 2).trim();

            if (!Pattern.compile(cyrillic).matcher(russian).matches()) {
                throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
            }
            if (romanToInt(roman) < 1 || romanToInt(roman) > 35) {
                throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
            }
        } catch (Exception e) {
            throw new DocumentValidateException(validateError, dulDetail.getDocumentTypeId());
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    @SuppressWarnings("unchecked")
    public List<Long> checkAnotherClient(Session session, DulDetail dulDetail) {
        if (!this.dulTypes.contains(dulDetail.getDocumentTypeId())) {
            return new ArrayList<>();
        }
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
            return new ArrayList<>();
        if (idOfClients.size() == 1 && idOfClients.get(0).equals(dulDetail.getIdOfClient()))
            return new ArrayList<>();
        return idOfClients;
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

    public boolean documentExists(Session session, Client client, DulDetail dulDetail) {
        String query_str = "select d.id from DulDetail d where d.idOfClient = :idOfClient " +
                "and d.documentTypeId = :documentTypeId and d.deleteState = false and d.number = :number";
        if (dulDetail.getSeries() != null && !dulDetail.getSeries().isEmpty())
            query_str += " and d.series = :series";
        Query query = session.createQuery(query_str);
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("number", dulDetail.getNumber());
        query.setParameter("documentTypeId", dulDetail.getDocumentTypeId());
        if (dulDetail.getSeries() != null && !dulDetail.getSeries().isEmpty()) {
            query.setParameter("series", dulDetail.getSeries());
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

    private MeshGuardiansService getMeshGuardiansService() {
        return RuntimeContext.getAppContext().getBean(MeshGuardiansService.class);
    }

    @SuppressWarnings("unchecked")
    public void saveDulOnlyISPP(Session session, List<DulDetail> dulDetails, Long idOfClient) throws Exception {
        validateDulList(session, dulDetails, true);
        //проверка на случай отсутсвия типа документа из мк в испп
        Criteria criteria = session.createCriteria(DulGuide.class);
        List<DulGuide> allDulGuides = (List<DulGuide>) criteria.list();
        List<Long> dulGuidesId = allDulGuides.stream()
                .map(DulGuide::getDocumentTypeId).collect(Collectors.toList());
        Date currentDate = new Date();

        for (DulDetail dulDetail : dulDetails) {
            if (!dulGuidesId.contains(dulDetail.getDocumentTypeId()))
                continue;
            if (dulDetail.getCreateDate() == null)
                dulDetail.setCreateDate(currentDate);
            if (dulDetail.getLastUpdate() == null)
                dulDetail.setLastUpdate(currentDate);
            if (dulDetail.getDeleteState() == null)
                dulDetail.setDeleteState(false);
            dulDetail.setIdOfClient(idOfClient);
            session.save(dulDetail);
        }
    }

    private int romanToInt(String s) {

        int output = 0;
        Map<String, Integer> map = new HashMap<>();
        map.put("I", 1);
        map.put("V", 5);
        map.put("X", 10);
        map.put("L", 50);
        map.put("C", 100);
        map.put("D", 500);
        map.put("M", 1000);

        if (s.length() == 0) return 0;
        if (s.length() == 1) return map.get(String.valueOf(s.charAt(0)));

        for (int i = 0; i < s.length() - 1; i++) {
            if (map.get(String.valueOf(s.charAt(i))) >=
                    map.get(String.valueOf(s.charAt(i + 1)))) {
                output = output + map.get(String.valueOf(s.charAt(i)));
            } else {
                output = output - map.get(String.valueOf(s.charAt(i)));
            }
        }
        output = output + map.get(String.valueOf(s.charAt(s.length() - 1)));
        return output;
    }
}
