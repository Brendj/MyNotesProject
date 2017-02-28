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
import org.hibernate.Query;
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
import javax.servlet.http.HttpSession;
import java.io.*;
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
    private int successLineNumber;
    public static final long DEFAULT_SUPPLIER_ID = 28L;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    protected static final String [][] COLUMN_NAMES = new String [][] {
            { "guid",                   "GUID" },
            { "shortName",              "Наименование ОО для поставщика" },
            { "officialName",           "Официальное наименование" },
            { "tags",                   "Тэги" },
            { "city",                   "Город" },
            { "region",                 "Район ( Округ)" },
            { "address",                "Адрес" },
            { "btiUnom",                "БТИ УНОМ" },
            { "btiUnad",                "БТИ УНАД" },
            { "introductionQueue",      "Очередь внедрения" },
            { "additionalId",           "Доп. ид. здания" },
            { "shortNameInfoService",   "Краткое наименование" },

    };

    public int getSuccessLineNumber() {
        return successLineNumber;
    }

    public List<OrgEntry> getLineResults() {
        return lineResults;
    }

    public int getLineResultSize() {
        return lineResults.size();
    }

    public void fileUploadListener(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        long dataSize = 0;
        try {
            if (item.isTempFile()) {
                File file = item.getFile();
                dataSize = file.length();
                inputStream = new FileInputStream(file);
            } else {
                byte[] data = item.getData();
                dataSize = data.length;
                inputStream = new ByteArrayInputStream(data);
            }

            uploadOrgs(inputStream, dataSize);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "ОУ загружены и зарегистрированы успешно", null));
        } catch (Exception e) {
            logger.error("Failed to load orgs from file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при загрузке/регистрации данных по ОУ: " + e.getMessage(),
                            null));
        } finally {
            close(inputStream);
        }
    }

    public void uploadOrgs(InputStream inputStream, long dataSize) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();

        TimeZone localTimeZone = runtimeContext
                .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        dateFormat.setTimeZone(localTimeZone);
        timeFormat.setTimeZone(localTimeZone);

        long lineCount = dataSize / 200 - 1;
        if (lineCount > MAX_LINE_NUMBER) {
            lineCount = MAX_LINE_NUMBER;
        }
        List<OrgEntry> lineResults = new ArrayList<OrgEntry>((int) lineCount);
        int lineNo = 0;
        int successLineNumber = 0;

        Map<String, Integer> columns = new HashMap<String, Integer>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
        String currLine = reader.readLine();
        while (null != currLine) {
            if(lineNo == 0) {
                columns = getColumns(currLine);
                currLine = reader.readLine();
                if (lineNo == MAX_LINE_NUMBER) {
                    break;
                }
                ++lineNo;
                continue;
            }

            OrgEntry result = null;
            try {
                result = RuntimeContext.getAppContext().getBean(OrgListLoaderPage.class).
                                                        createOrg(runtimeContext, currLine, lineNo, columns);
            } catch (Exception e) {
                logger.warn("Failed", e);
                result = new OrgEntry(lineNo, 500, e.getMessage(), null);
            }
            if (result != null && result.getResultCode() == 0) {
                ++successLineNumber;
            }
            if(result != null) {
                lineResults.add(result);
            }
            currLine = reader.readLine();
            if (lineNo == MAX_LINE_NUMBER) {
                break;
            }
            ++lineNo;
        }

        this.lineResults = lineResults;
        this.successLineNumber = successLineNumber;
    }

    private static Map<String, Integer> getColumns(String line) {
        String[] tokens = line.split(";");
        if(tokens == null || tokens.length < 1) {
            return Collections.EMPTY_MAP;
        }

        Map<String, Integer> columns = new HashMap<String, Integer>();
        for(int i=0; i<tokens.length; i++) {
            String t = tokens[i];
            if(t == null || StringUtils.isBlank(t)) {
                continue;
            }

            for(String [] col : COLUMN_NAMES) {
                if(col[1].equals(t)) {
                    columns.put(col[0], i);
                }
            }
        }
        return columns;
    }

    private static void close(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (Exception e) {
                logger.error("failed to close input stream", e);
            }
        }
    }

    @Transactional
    public OrgEntry createOrg(RuntimeContext runtimeContext, String line, int lineNo, Map<String, Integer> columns) throws Exception {
        Session session = null;
        session = (Session) entityManager.getDelegate();
        return createOrg(runtimeContext, line, lineNo, session, columns);
    }

    private OrgEntry createOrg(RuntimeContext runtimeContext, String line, int lineNo, Session session, Map<String, Integer> columns) throws Exception {
        if(columns == null || columns.size() < 1 || line == null || StringUtils.isBlank(line)) {
            return new OrgEntry(lineNo, 1, "Not enough data", null);
        }
        String[] tokens = line.split(";");
        if(tokens.length != columns.size()) {
            return null;
        }

        String guid = toStr(getFromColumn(tokens, columns, "guid"));
        String shortName = toStr(getFromColumn(tokens, columns, "shortName"));
        String officialName = toStr(getFromColumn(tokens, columns, "officialName"));
        String tags = toStr(getFromColumn(tokens, columns, "tags"));
        String city = toStr(getFromColumn(tokens, columns, "city"));
        String region = toStr(getFromColumn(tokens, columns, "region"));
        String address = toStr(getFromColumn(tokens, columns, "address"));
        String shortAddress = toStr(getFromColumn(tokens, columns, "shortAddress"));
        Long btiUnom = toLong(getFromColumn(tokens, columns, "btiUnom"));
        Long btiUnad = toLong(getFromColumn(tokens, columns, "btiUnad"));
        Long uniqueAddressId = toLong(getFromColumn(tokens, columns, "uniqueAddressId"));
        String introductionQueue = toStr(getFromColumn(tokens, columns, "introductionQueue"));
        Long additionalId = toLong(getFromColumn(tokens, columns, "additionalId"));
        String shortNameInfoService = toStr(getFromColumn(tokens, columns, "shortNameInfoService"));


        if(introductionQueue.toLowerCase().indexOf("не планируется") < 0) {
            Contragent defaultSupplier = DAOUtils.findContragent(session, DEFAULT_SUPPLIER_ID);

            Person officialPerson = new Person("", "", "");
            session.save(officialPerson);

            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR, 0);
            Date contractTime = cal.getTime();


            try {
                String orgNumber = getOrgNumber(shortName);
                Org currentOrg = DAOUtils.findOrgByShortname(session, shortName);
                if(currentOrg != null) {
                    Set<Client> clients = currentOrg.getClients();
                    if((clients != null && clients.size() > 0) || currentOrg.getStatus().equals(OrganizationStatus.ACTIVE)) {
                        return new OrgEntry(lineNo, 0, "Org is already registered", currentOrg.getIdOfOrg());
                    }
                    currentOrg.setAddress(address);
                    currentOrg.setTag(tags);
                    currentOrg.setCity(city);
                    currentOrg.setDistrict(region);
                    currentOrg.setBtiUnom(btiUnom);
                    currentOrg.setBtiUnad(btiUnad);
                    currentOrg.setIntroductionQueue(introductionQueue);
                    currentOrg.setAdditionalIdBuilding(additionalId);
                    //currentOrg.setShortNameInfoService(shortNameInfoService);
                    session.update(currentOrg);
                    return new OrgEntry(lineNo, 0, "Org has been modified", currentOrg.getIdOfOrg());
                }

                long version = DAOUtils.nextVersionByOrgStucture(session);

                Org org = new Org(shortName, shortNameInfoService, officialName, address, shortAddress, officialPerson, "",
                        "", contractTime, OrganizationType.SCHOOL, 0, 0L, "", 0L,
                        0L, defaultSupplier, "", "", "",
                        "", "", "", btiUnom,  btiUnad, uniqueAddressId, introductionQueue, additionalId, "/", version, false);
                org.setCity(city);
                org.setDistrict(region);
                org.setLocation("");
                org.setLongitude("");
                org.setLatitude("");
                org.setGuid(guid);
                org.setPhone("");
                org.setSmsSender("");
                org.setTag(tags);
                org.setStatus(OrganizationStatus.PLANNED);
                org.setState(0);
                /*if (StringUtils.isNotEmpty("")) {
                    org.setSsoPassword(plainSsoPassword);
                }*/
                /*org.setRefectoryType(null);
                if(org.getFriendlyOrg() == null) {
                    org.setFriendlyOrg(new HashSet<Org>());
                }
                org.getFriendlyOrg().add(org);*/
                org.setUpdateTime(new java.util.Date(java.lang.System.currentTimeMillis()));
                session.save(org);

                org.setRefectoryType(null);
                if(org.getFriendlyOrg() == null) {
                    org.setFriendlyOrg(new HashSet<Org>());
                }
                org.getFriendlyOrg().add(org);
                org.setUpdateTime(new java.util.Date(java.lang.System.currentTimeMillis()));
                org.setPhotoRegistryDirective(PhotoRegistryDirective.DISALLOWED);
                session.save(org);

                return new OrgEntry(lineNo, 0, "Ok", org.getIdOfOrg());
            } catch (Exception e) {
                logger.debug("Failed to create org", e);
                return new OrgEntry(lineNo, 1, e.getMessage(), null);
            }
        } else {
            try {
                String sql = "";
                boolean exists = DAOUtils.isNotPlannedOrgExists(session, shortName, additionalId);
                if(exists) {
                    sql = "UPDATE cf_not_planned_orgs SET  "
                            + "guid=:guid, officialName=:officialName, tag=:tags, city=:city, district=:region, "
                            + "address=:address, btiUnom=:btiUnom, btiUnad=:btiUnad, introductionQueue=:introductionQueue, "
                            + "shortNameInfoService=:shortNameInfoService "
                        + "WHERE shortName=:shortName AND additionalIdBuilding=:additionalId";
                } else {
                    sql = "INSERT INTO cf_not_planned_orgs "
                            + "(guid, shortName, officialName, tag, city, district, address, btiUnom, btiUnad, introductionQueue, additionalIdBuilding, shortNameInfoService) "
                            + "VALUES "
                            + "(:guid, :shortName, :officialName, :tags, :city, :region, :address, :btiUnom, :btiUnad, :introductionQueue, :additionalId, :shortNameInfoService)";
                }

                Query q = session.createSQLQuery(sql);
                q.setParameter("guid", guid);
                q.setParameter("shortName", shortName);
                q.setParameter("officialName", officialName);
                q.setParameter("tags", tags);
                q.setParameter("city", city);
                q.setParameter("region", region);
                q.setParameter("address", address);
                q.setParameter("btiUnom", btiUnom);
                q.setParameter("btiUnad", btiUnad);
                q.setParameter("introductionQueue", introductionQueue);
                q.setParameter("additionalId", additionalId);
                q.setParameter("shortNameInfoService", shortNameInfoService);
                q.executeUpdate();
                if(exists) {
                    return new OrgEntry(lineNo, 0, "Not planned org has been modified", additionalId);
                } else {
                    return new OrgEntry(lineNo, 0, "Ok", additionalId);
                }
            } catch (Exception e) {
                logger.debug("Failed to create org", e);
                return new OrgEntry(lineNo, 1, e.getMessage(), null);
            }
        }
    }

    public String getOrgNumber(String shortname) {
        int numPos = shortname.indexOf("№");
        if(numPos < 0) {
            return shortname;
        }
        /*int spacePos = shortname.indexOf(" ", numPos);
        if(spacePos < 0) {*/
            return shortname.substring(numPos + 1).trim();
        /*} else {
            return shortname.substring(numPos + 1, spacePos);
        }*/
    }

    protected String getFromColumn(String[] tokens, Map<String, Integer> columns, String colName) {
        Integer colPos = columns.get(colName);
        if(colPos == null) {
            return "";
        }
        return tokens[colPos];
    }

    protected String toStr(String str) {
        if(str == null) {
            return "";
        }
        str = StringUtils.trim(str);
        return str;
    }

    protected Long toLong(String str) {
        if(str == null) {
            return 0L;
        }
        Long res = NumberUtils.toLong(str);
        return res;
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
