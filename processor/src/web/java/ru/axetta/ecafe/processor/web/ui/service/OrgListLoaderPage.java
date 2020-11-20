/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Session;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 22.10.14
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("session")
public class OrgListLoaderPage extends BasicWorkspacePage {

    private static Logger logger = LoggerFactory.getLogger(OrgListLoaderPage.class);
    private static final long MAX_LINE_NUMBER = 80000;
    private List<OrgEntry> lineResults = Collections.emptyList();
    private int successLineNumber = 0;
    public static final String UTF8_BOM = "\uFEFF";
    //public static final long DEFAULT_SUPPLIER_ID = 28L;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    protected static final String[][] COLUMN_NAMES = new String[][]{
            {"shortName", "Наименование"}, {"officialName", "Официальное наименование"}, {"address", "Адрес"},
            {"district", "Район"}, {"OGRN", "ОГРН"}, {"GUID", "GUID"}, {"supplierId", "ID поставщика"},
            {"orgType", "Тип организации(0-4)"}, {"position", "Должность руководителя"}, {"surname", "Фамилия"},
            {"firstName", "Имя"}, {"secondName", "Отчество"}, {"shortnameinfoservice", "Краткое наименование"}};

    public int getSuccessLineNumber() {
        return successLineNumber;
    }

    public List<OrgEntry> getLineResults() {
        return lineResults;
    }

    public int getLineResultSize() {
        return lineResults.size();
    }

    public void uploadFile(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();

        Map<String, String> columns;
        Map<Integer, Map<String, String>> results = new HashMap<>();
        Path path = Paths.get(item.getFile().toURI());
        int lineNum = 0;

        // Считывание данных из файла
        boolean firstLine = true;
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String str;
            while ((str = br.readLine()) != null) {
                if (firstLine) {
                    str = str.replace(UTF8_BOM, "");
                    firstLine = false;
                }
                if (lineNum == MAX_LINE_NUMBER) {
                    break;
                }
                columns = getColumns(str);
                if (!columns.isEmpty()) {
                    results.put(++lineNum, columns);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load orgs from file", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при загрузке/регистрации данных по ОУ: " + e.getMessage(), null));
        }
        uploadOrgs(results, lineNum);
    }

    public void uploadOrgs(Map<Integer, Map<String, String>> lines, int num) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        TimeZone localTimeZone = runtimeContext
                .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(localTimeZone);
        timeFormat.setTimeZone(localTimeZone);

        List<OrgEntry> result = new ArrayList<>();
        OrgEntry orgEntry;
        int successLineNum = 0;
        for (int i = 1; i < num + 1; i++) {
            try {
                orgEntry = RuntimeContext.getAppContext().getBean(OrgListLoaderPage.class).
                        createOrg(runtimeContext, i, lines.get(i));
            } catch (Exception e) {
                logger.warn("Failed", e);
                orgEntry = new OrgEntry(i, 500, e.getMessage(), null);
            }
            if (orgEntry != null) {
                result.add(orgEntry);
                if (orgEntry.getResultCode() == 0) {
                    ++successLineNum;
                }
            }
        }
        this.successLineNumber = successLineNum;
        this.lineResults = result;
    }

    @Transactional
    public OrgEntry createOrg(RuntimeContext runtimeContext, int lineNum, Map<String, String> columns)
            throws Exception {
        Session session = null;
        session = (Session) entityManager.getDelegate();
        return createOrg(session, lineNum, columns);
    }

    private OrgEntry createOrg(Session session, int lineNum, Map<String, String> columns) throws Exception {
        if (columns == null || columns.size() < 1 || lineNum == 0) {
            return new OrgEntry(lineNum, 1, "Недостаточно данных", null);
        }

        String shortName = columns.get("shortName");
        String officialName = columns.get("officialName");
        String address = columns.get("address");
        String district = columns.get("district");
        String guid = columns.get("GUID");
        String ogrn = columns.get("OGRN");
        String strSupplierID = columns.get("supplierId");
        long supplierID = 0L;
        String strOrgType = columns.get("orgType");
        String strshortnameinfoservice = columns.get("shortnameinfoservice");
        int orgType;

        if (NumberUtils.isNumber(strSupplierID)) {
            supplierID = NumberUtils.createLong(strSupplierID);
        } else {
            logger.warn("Failed to get supplier = " + strSupplierID + " row = " + lineNum);
            return new OrgEntry(lineNum, 1,
                    "Ошибка преобразования id поставщика = " + strSupplierID + " строка = " + lineNum, null);
        }

        Contragent currentSupplier = DAOUtils.findContragentIsSupplier(session, supplierID);
        if (currentSupplier == null) {
            logger.warn("Failed to find supplier id = " + supplierID);
            return new OrgEntry(lineNum, 1, "Не найден поставщик с id = " + supplierID, null);
        }

        if (strOrgType == null || !strOrgType.matches("[0-4]")) {
            logger.warn("Failed to get orgType = " + strOrgType);
            return new OrgEntry(lineNum, 1, "Не найден тип организации " + strOrgType, null);
        } else {
            orgType = Integer.parseInt(strOrgType);
        }

        String position = columns.get("position");
        String surname = columns.get("surname");
        String firstname = columns.get("firstName");
        String secondname = columns.get("secondName");

        Person officialPerson;
        Person currentPerson;
        if (firstname != null && surname != null && secondname != null) {
            currentPerson = DAOUtils.findPersonByFIO(session, firstname, surname, secondname);
            if (currentPerson != null) {
                officialPerson = currentPerson;
            } else {
                officialPerson = new Person(firstname, surname, secondname);
            }
        } else if (firstname != null && surname != null && secondname == null) {
            officialPerson = new Person(firstname, surname, "");
        } else {
            officialPerson = new Person("", "", "");
        }
        session.save(officialPerson);

        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        Date contractTime = cal.getTime();

        try {
            Org currentOrg = DAOUtils.findOrgByShortname(session, shortName);
            if (currentOrg != null) {
                Set<Client> clients = currentOrg.getClients();
                if ((clients != null && clients.size() > 0) || currentOrg.getStatus()
                        .equals(OrganizationStatus.ACTIVE)) {
                    return new OrgEntry(lineNum, 0, "Организация уже зарегистрирована", currentOrg.getIdOfOrg());
                }
                if (address != null && !address.equals("")) {
                    currentOrg.setAddress(address);
                }
                if (position != null && !position.equals("")) {
                    currentOrg.setOfficialPosition(position);
                }
                if (district != null) {
                    currentOrg.setDistrict(district);
                }
                if (ogrn != null && ogrn.length() > 12 && ogrn.length() < 33) { // ОГРН состоит из 13 цифр; размерность поля в БД = 32
                    currentOrg.setOGRN(ogrn);
                }
                if (guid != null) {
                    currentOrg.setGuid(guid);
                }
                currentOrg.setType(OrganizationType.fromInteger(orgType));
                currentOrg.setCoSupplier(currentSupplier);
                if (firstname != null && surname != null && secondname != null) {
                    currentOrg.setOfficialPerson(officialPerson);
                }
                session.update(currentOrg);
                return new OrgEntry(lineNum, 0, "Данные организации были обновлены", currentOrg.getIdOfOrg());
            }

            long version = DAOUtils.nextVersionByOrgStucture(session);
            if (position == null) {
                position = "";
            }

            Org org = new Org(shortName, strshortnameinfoservice, officialName, address, "", officialPerson, position, "", contractTime,
                    OrganizationType.fromInteger(orgType), 0, 0L, "", 0L, 0L, currentSupplier, "", "", "", "", "", "",
                    0L, 0L, 0L, "", 0L, "/", version, false);
            org.setStatus(OrganizationStatus.PLANNED);
            org.setSecurityLevel(OrganizationSecurityLevel.STANDARD);
            org.setPhotoRegistryDirective(PhotoRegistryDirective.DISALLOWED);
            org.setState(1); // Обслуживается
            org.setUpdateTime(new java.util.Date(java.lang.System.currentTimeMillis()));
            org.setDistrict(district);
            if (ogrn.length() > 12 && ogrn.length() < 33) { // ОГРН состоит из 13 цифр; размерность поля в БД = 32
                org.setOGRN(ogrn);
            }
            org.setGuid(guid);
            org.setPreorderlp(false); // Предварительные заявки по ЛП - для синхронизации
            session.save(org);

            OrgSync orgSync = new OrgSync();
            orgSync.setIdOfPacket(0L);
            orgSync.setOrg(org);
            session.persist(orgSync);

            org.setRefectoryType(null);
            if (org.getFriendlyOrg() == null) {
                org.setFriendlyOrg(new HashSet<Org>());
            }
            org.getFriendlyOrg().add(org);
            org.setUpdateTime(new java.util.Date(java.lang.System.currentTimeMillis()));
            org.setPhotoRegistryDirective(PhotoRegistryDirective.DISALLOWED);
            session.save(org);

            return new OrgEntry(lineNum, 0, "ОК", org.getIdOfOrg());
        } catch (Exception e) {
            logger.warn("Failed to create org", e);
            return new OrgEntry(lineNum, 1, e.getMessage(), null);
        }
    }

    private static Map<String, String> getColumns(String line) {
        String[] data = line.split(";");
        if (data == null || data.length < 1) {
            return Collections.EMPTY_MAP;
        }
        Map<String, String> columns = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            String str = data[i].replace("\"", "");
            if (str == null || StringUtils.isBlank(str)) {
                continue;
            }
            if (!StringUtils.equalsIgnoreCase(COLUMN_NAMES[i][1], str)) {
                //logger.warn("column_name = " + COLUMN_NAMES[i][1] + ", str = " + str);
                columns.put(COLUMN_NAMES[i][0], str);
            }
        }
        return columns;
    }

    public void downloadSample() {
        String result = "\"Наименование\";\"Официальное наименование\";\"Адрес\";\"Район\";\"ОГРН\";\"GUID\";"
                + "\"ID поставщика\";" + "\"Тип организации(0-4)\";\"Должность руководителя\";\"Фамилия\";\"Имя\";"
                + "\"Отчество\";\"Краткое наименование\";";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            facesContext.responseComplete();
            response.setContentType("application/csv");
            response.setHeader("Content-disposition", "attachment;filename=\"org.csv\"");
            servletOutputStream.write(result.getBytes(StandardCharsets.UTF_8));
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Не удалось сгенерировать пример файла для загрузки: " + e.getMessage(), null));
        }
    }

    @Override
    public String getPageFilename() {
        return "service/org_list_loader";
    }

    public static final class OrgEntry {

        private final long lineNo;
        private final int resultCode;
        private final String message;
        private final Long idOfOrg;

        public OrgEntry(long lineNo, int resultCode, String message, Long idOfOrg) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.message = message;
            this.idOfOrg = idOfOrg;
        }

        public long getLineNo() {
            return lineNo;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }
    }
}
