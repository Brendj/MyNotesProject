/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.ReportInfo;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicJasperReport;
import ru.axetta.ecafe.processor.core.report.DeliveredServicesElectronicCollationReport;
import ru.axetta.ecafe.processor.core.report.DeliveredServicesReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class ReportRepository extends BaseJpaDao {

    private static final Logger logger = LoggerFactory.getLogger(ReportRepository.class);

    private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override protected DateFormat initialValue() { return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); }
    };
    private ThreadLocal<DateFormat> dateFormatLetter = new ThreadLocal<DateFormat>() {
        @Override protected DateFormat initialValue() { return new SimpleDateFormat("dd.MM.yyyy"); }
    };

    public static ReportRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(ReportRepository.class);
    }

    public byte[] getDeliveredServicesReport(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        DeliveredServicesReportParameters reportParameters = new DeliveredServicesReportParameters(
                parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildDelivererdServicesReport(session,reportParameters);
        if (jasperReport == null
                || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] arr = stream.toByteArray();
        postReportToEmails(subject, reportParameters, arr);
        return arr;
    }

    public byte[] getDeliveredServicesElectronicCollationReport(List<ReportParameter> parameters, String subject) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        DeliveredServicesReportParameters reportParameters = new DeliveredServicesReportParameters(
                parameters).parse();
        if (!reportParameters.checkRequiredParameters()) {
            return null; //не переданы или заполнены с ошибкой обязательные параметры
        }
        BasicJasperReport jasperReport = buildDeliveredServicesElectronicCollationReport(session,
                reportParameters);
        if (jasperReport == null
                || isEmptyReportPrintPages(jasperReport)) {
            return null;
        }
        ByteArrayOutputStream stream = exportReportToJRXls(jasperReport);
        byte[] arr = stream.toByteArray();
        postReportToEmails(subject,reportParameters, arr);
        return arr;
    }

    private boolean isEmptyReportPrintPages(BasicJasperReport deliveredServicesReport) {
        return deliveredServicesReport.getPrint().getPages() != null
                && deliveredServicesReport.getPrint().getPages().get(0).getElements().size() == 0;
    }

    private void postReportToEmails(String subject, DeliveredServicesReportParameters reportParameters, byte[] arr) {
        String email = reportParameters.getEmail();
        if (email != null && !email.isEmpty()) {
            String[] emails = email.split(";");
            DateFormat df = dateFormatLetter.get();
            for (String em : emails) {
                postReport(em, subject + String.format(" (%s - %s)", df.format(reportParameters.getStartDate()), df.format(reportParameters.getEndDate())), arr);
            }
        }
    }

    private BasicJasperReport buildDelivererdServicesReport(Session session,DeliveredServicesReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + DeliveredServicesReport.class.getSimpleName() + ".jasper";
        DeliveredServicesReport.Builder builder = new DeliveredServicesReport.Builder(templateFilename);
        builder.setOrg(reportParameters.getIdOfOrg());
        try {
            BasicJasperReport deliveredServicesReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(), new GregorianCalendar(),
                            reportParameters.getIdOfOrg(), reportParameters.getIdOfContragent(), reportParameters.getIdOfContract(),
                            reportParameters.getRegion(), false);
            return deliveredServicesReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;  //не найдена организация
        }
    }

    private BasicJasperReport buildDeliveredServicesElectronicCollationReport(Session session, DeliveredServicesReportParameters reportParameters)
            throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + DeliveredServicesElectronicCollationReport.class.getSimpleName() + ".jasper";
        DeliveredServicesElectronicCollationReport.Builder builder = new DeliveredServicesElectronicCollationReport.Builder(
                templateFilename);
        builder.setOrg(reportParameters.getIdOfOrg());
        try {
            BasicJasperReport deliveredServicesReport = builder
                    .build(session, reportParameters.getStartDate(), reportParameters.getEndDate(),
                            new GregorianCalendar(), reportParameters.getIdOfOrg(), reportParameters.getIdOfContragent(), reportParameters.getIdOfContract(),
                            reportParameters.getRegion(), false);
            return deliveredServicesReport;
        } catch (EntityNotFoundException e) {
            logger.error("Not found organization to generate report");
            return null;  //не найдена организация
        }
    }

    private ByteArrayOutputStream exportReportToJRXls(BasicJasperReport deliveredServicesReport) throws JRException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JRXlsExporter xlsExport = new JRXlsExporter();
        xlsExport.setParameter(JRCsvExporterParameter.JASPER_PRINT, deliveredServicesReport.getPrint());
        xlsExport.setParameter(JRCsvExporterParameter.OUTPUT_STREAM, stream);
        xlsExport.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
        xlsExport.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        xlsExport.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        xlsExport.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
        xlsExport.exportReport();
        xlsExport.reset();
        return stream;
    }


    public boolean areParametersBad(List<ReportParameter> parameters) {
        Date startDate = null;
        Date endDate = null;
        Long idOfOrg = null;
        DateFormat safeDateFormat = dateFormat.get();
        try {
            for (ReportParameter parameter : parameters) {
                if (parameter.getParameterName().equals("startDate")) {
                    startDate = safeDateFormat.parse(parameter.getParameterValue());
                }
                if (parameter.getParameterName().equals("endDate")) {
                    endDate = safeDateFormat.parse(parameter.getParameterValue());
                }
                if (parameter.getParameterName().equals("idOfOrg")) {
                    idOfOrg = Long.parseLong(parameter.getParameterValue());
                }
            }
        } catch (Exception e) {
            return true;
        }
        if (idOfOrg == null || startDate == null || endDate == null || startDate.after(endDate)) {
            return true; //не переданы или заполнены с ошибкой обязательные параметры
        } else {
            return false;
        }
    }

    private void postReport(String address, String subject, byte[] report) {
        ReportPoster poster = new ReportPoster(RuntimeContext.getInstance().getPostman(), address, subject, report);
        new Thread(poster).start();
    }

    public List<ReportInfoItem> getReportInfos(Long idOfOrg, Date startDate, Date endDate) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        HashMap<Long, Org> orgMap = new HashMap<Long, Org>();
        Org org = (Org)session.load(Org.class, idOfOrg);
        orgMap.put(idOfOrg, org);
        List org_ids = new ArrayList<Long>();
        for (Org fOrg : org.getFriendlyOrg()) {
            org_ids.add(fOrg.getIdOfOrg());
            orgMap.put(fOrg.getIdOfOrg(), (Org)session.load(Org.class, fOrg.getIdOfOrg()));
        }

        startDate = CalendarUtils.truncateToDayOfMonth(startDate);
        endDate = CalendarUtils.endOfDay(endDate);

        Criteria criteria = session.createCriteria(ReportInfo.class);
        criteria.add(Restrictions.in("idOfOrg", org_ids));
        criteria.add(Restrictions.between("createdDate", startDate, endDate));
        criteria.add(Restrictions.ilike("reportFile", DeliveredServicesReport.class.getSimpleName(), MatchMode.ANYWHERE));
        List<ReportInfo> reportInfo = criteria.list();
        List<ReportInfoItem> result = new ArrayList<ReportInfoItem>();
        String basePath = (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.processor.autoreport.path");
        for(ReportInfo info : reportInfo) {
            File file = new File(basePath + info.getReportFile());
            if (file.exists() && file.isFile()) {
                Org o = orgMap.get(info.getIdOfOrg());
                ReportInfoItem item = new ReportInfoItem(info.getReportName(), o.getShortName(),
                        o.getAddress(), info.getCreatedDate(), info.getStartDate(), info.getEndDate(), info.getIdOfReportInfo());
                result.add(item);
            }
        }
        return result;
    }

    public byte[] getRepositoryReportById(Long idOfReport) throws Exception {
        byte[] result = null;
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ReportInfo.class);
        criteria.add(Restrictions.eq("idOfReportInfo", idOfReport));
        ReportInfo report = (ReportInfo)criteria.uniqueResult();
        String basePath = (String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.processor.autoreport.path");
        if (report != null && basePath != null && report.getReportFile() != null) {
            File file = new File(basePath + report.getReportFile());
            if (file.exists() && file.isFile()) {
                InputStream stream = new FileInputStream(file);
                result = IOUtils.toByteArray(stream);
            }
        }
        return result;
    }

    private class DeliveredServicesReportParameters {
        private List<ReportParameter> parameters;
        private Date startDate;
        private Date endDate;
        private Long idOfOrg;
        private Long idOfContragent;
        private Long idOfContract;
        private String region;
        private String email;

        public DeliveredServicesReportParameters(List<ReportParameter> parameters) {
            this.parameters = parameters;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public Long getIdOfContract() {
            return idOfContract;
        }

        public String getRegion() {
            return region;
        }

        public String getEmail() {
            return email;
        }

        public DeliveredServicesReportParameters parse() throws ParseException {
            startDate = null;
            endDate = null;
            idOfOrg = null;
            idOfContragent = null;
            idOfContract = null;
            region = null;
            email = null;
            DateFormat safeDateFormat = dateFormat.get();
            for (ReportParameter parameter : parameters) {
                if (parameter.getParameterName().equals("startDate")) {
                    startDate = safeDateFormat.parse(parameter.getParameterValue());
                    startDate = CalendarUtils.truncateToDayOfMonth(startDate);
                }
                if (parameter.getParameterName().equals("endDate")) {
                    endDate = safeDateFormat.parse(parameter.getParameterValue());
                    endDate = CalendarUtils.endOfDay(endDate);
                }
                if (parameter.getParameterName().equals("idOfOrg")) {
                    idOfOrg = Long.parseLong(parameter.getParameterValue());
                }
                if (parameter.getParameterName().equals("idOfContragent")) {
                    idOfContragent = Long.parseLong(parameter.getParameterValue());
                }
                if (parameter.getParameterName().equals("idOfContract")) {
                    idOfContract = Long.parseLong(parameter.getParameterValue());
                }
                if (parameter.getParameterName().equals("region")) {
                    region = parameter.getParameterValue();
                }
                if (parameter.getParameterName().equals("email")) {
                    email = parameter.getParameterValue();
                }
            }
            return this;
        }

        public boolean checkRequiredParameters() {
          return   idOfOrg != null && startDate != null
                    && endDate != null;
        }

    }
}
