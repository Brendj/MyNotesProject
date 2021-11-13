/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.payment.PaymentAdditionalTasksProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.service.clients.ClientService;
import ru.axetta.ecafe.processor.core.persistence.service.menu.WtComplexCopyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.ProjectStateReportService;
import ru.axetta.ecafe.processor.core.service.*;
import ru.axetta.ecafe.processor.core.service.finoperator.FinManagerService;
import ru.axetta.ecafe.processor.core.service.meal.MealManager;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.service.scud.ScudManager;
import ru.axetta.ecafe.processor.core.service.spb.CardsUidUpdateService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.SyncStatsManager;
import ru.axetta.ecafe.processor.web.partner.nsi.NSIRepairService;
import ru.axetta.ecafe.processor.web.partner.preorder.PreorderDAOService;
import ru.axetta.ecafe.processor.web.partner.preorder.PreorderOperationsService;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ru.axetta.ecafe.processor.core.service.ImportRegisterFileService.*;

@Component
@Scope("session")
public class OtherActionsPage extends OnlineReportPage {

    private String passwordForSearch;
    private String orgsForGenerateGuardians;
    private List<Long> clientsIds = null;
    private Date summaryDate;
    private Date summaryFinOperatorDate;
    private String orgsForSpbCardsUidUpdate;
    private String guidForDiscountsUpdate;
    private String updateSpbClientDoubles;
    private Date startDateEMP;
    private Date endDateEMP;
    private Long contractId;
    private boolean allowGenerateGuardians = true;
    private String orgsforCleaninig;
    private static final Logger logger = LoggerFactory.getLogger(OtherActionsPage.class);

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public OtherActionsPage() {
        super();
        summaryDate = new Date();
        summaryFinOperatorDate = new Date();
        startDate = CalendarUtils.addDays(new Date(), 2);
        endDateEMP = new Date();
        startDateEMP = CalendarUtils.addDays(endDateEMP, -7);
    }

    public String getOrgsforCleaninig() {
        return this.orgsforCleaninig;
    }

    public void setOrgsforCleaninig(String orgsforCleaninig) {
        this.orgsforCleaninig = orgsforCleaninig;
    }

    public void rubBIExport() throws Exception {
        RuntimeContext.getAppContext().getBean(BIDataExportService.class).run(); // DEF
        printMessage("Генерация данных ключевых показателей выполнена");
    }

    public void runProjectStateGenerator() throws Exception {
        RuntimeContext.getAppContext().getBean(ProjectStateReportService.class).run(); //DEF
        //RuntimeContext.getAppContext().getBean(SignHandlerService.class).testCall();
        //RuntimeContext.getAppContext().getBean("importRegisterClientsService", ImportRegisterClientsService.class).run();
        //RuntimeContext.getAppContext().getBean(BIDataExportService.class).run();
        printMessage("Генерация данных ключевых показателей выполнена");
    }

    public void archvedExeption() throws Exception {
        ArchivedExeptionService.archivedExeption.manualStart();
        printMessage("Архивирование событий выполнено");
    }

    public void runImportRegisterClients() throws Exception {
        RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class)
                .run(); //DEF
        printMessage("Импорт клиентов из Реестров выполнен");
    }

    public void runBenefitsRecalculation() throws Exception {
        RuntimeContext.getAppContext().getBean(BenefitsRecalculationService.class).runForcibly(); //DEF
        printMessage("Пересчет льготных правил выполнен");
    }

    public void runImportRNIPPayment() throws Exception {
        RuntimeContext.getAppContext().getBean(RNIPLoadPaymentsService.class).runRequests(); //DEF
        printMessage("Импорт платежей RNIP был выполнен успешно");
    }

    public void runRepositoryReportsCleanup() throws Exception {
        RuntimeContext.getAppContext().getBean(CleanupReportsService.class).run(); //DEF
        printMessage("Очистка Репозитория успешно завершена");
    }

    public void runSynchCleanup() throws Exception {
        RuntimeContext.getAppContext().getBean(SynchCleanupService.class).run(); //DEF
        printMessage("Очистка журналов синхронизации успешно завершена");
    }

    public void runBindEMPClients() throws Exception {
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).runBindClients(); //DEF
        printMessage("Привязка клиентов с ЕМП завершена");
    }

    public void runReceiveEMPUpdates() throws Exception {
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("кнопка \"Запустить загрузку обновления из ЕМП\" в Сервис/Другое");
        User user = MainPage.getSessionInstance().getCurrentUser();
        clientsMobileHistory.setUser(user);
        clientsMobileHistory.setShowing("Изменено в веб.приложении. Пользователь:" + user.getUserName());
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).runReceiveUpdates(clientsMobileHistory); //DEF
        printMessage("Загрузка обновление из ЕМП завершена");
    }

    public void runSendEMPEvent() throws Exception {
        //Client client = DAOService.getInstance().getClientByGuid("e5000805-29a9-1388-e043-a2997e0ab714");
        Client client = DAOReadonlyService.getInstance().findClientById(1069L);

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(new Date(System.currentTimeMillis()));
        String[] values = new String[]{
                "paySum", CurrencyStringUtils.copecksToRubles(100000), "balance",
                CurrencyStringUtils.copecksToRubles(client.getBalance()), "contractId",
                String.valueOf(client.getContractId()), "surname", "Тестеров"/*client.getPerson().getSurname()*/,
                "firstName", "Тест"/*client.getPerson().getFirstName()*/, "empTime", empTime, "targetId", "" + 1};
        values = EventNotificationService.attachTargetIdToValues(1L, values);

        RuntimeContext.getAppContext().getBean(EventNotificationService.class).
                sendSMS(client, null, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, values, new Date()); //DEF
        printMessage("Пробное  событие успешно отправлено на ЕМП");
    }

    public void cleaningMenu() {
        MaintenanceService maintenanceService = new MaintenanceService();
        maintenanceService.run();
        printMessage("Очистка выполнена");
    }

    public void cleaningMenuVersion2() {
        MaintenanceService maintenanceService = RuntimeContext.getAppContext().getBean(MaintenanceService.class);
        MaintenanceService.setOrgsforCleaninig(orgsforCleaninig);
        maintenanceService.runVersion2(false);
        printMessage("Процесс запущен");
    }

    public void runSendEMPEventEMIAS() throws Exception {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.findClientByContractId(persistenceSession, getContractId());
            if (client == null)
            {
                printMessage("Клиент не найден");
                return;
            }
            List<EMIAS> emias = DAOReadonlyService.getInstance()
                    .findEMIASbyClientandBeetwenDates(client, getStartDateEMP(), getEndDateEMP());
            for (EMIAS emias1 : emias) {
                Integer eventsStatus = -1;
                switch (emias1.getTypeEventEMIAS().intValue()) {
                    case 1:
                        eventsStatus = 2;
                        break;
                    case 2:
                        eventsStatus = 3;
                        break;
                    case 3:
                        eventsStatus = 4;
                        break;
                    case 4:
                        eventsStatus = 5;
                        break;
                }

                ExternalEvent event = DAOReadonlyService.getInstance()
                        .getExternalEvent(client, client.getOrg().getShortNameInfoService(), client.getOrg().getOfficialName(),
                                ExternalEventType.SPECIAL, emias1.getDateLiberate(),
                                ExternalEventStatus.fromInteger(eventsStatus));
                event.setForTest(true);
                ExternalEventNotificationService notificationService = RuntimeContext.getAppContext()
                        .getBean(ExternalEventNotificationService.class);
                notificationService.setSTART_DATE(emias1.getStartDateLiberate());
                notificationService.setEND_DATE(emias1.getEndDateLiberate());
                notificationService.sendNotification(client, event);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            printMessage("События клиента отправлены (" + emias.size() + ")");
        } catch (Exception e) {
            logger.error("Ошибка при отправке тестового события", e);
            printMessage("Ошибка при отправке тестового события");
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

    }

    public void runRecalculateEMPStatistics() throws Exception {
        RuntimeContext.getAppContext().getBean(EMPProcessor.class).recalculateEMPClientsCount(); //DEF
        printMessage("Статистика ЕМП обновлена");
    }

    public void repairNSI() throws Exception {
        RuntimeContext.getAppContext().getBean(NSIRepairService.class).run(); //DEF
        printMessage("Записи из Реестров исправлены");
    }

    public void runShowShortLog() throws Exception {
        SyncStatsManager syncStatsManager = new SyncStatsManager();
        syncStatsManager.shortDataLog();
    }

    public void runShowDailyLog() throws Exception {
        SyncStatsManager syncStatsManager = new SyncStatsManager();
        syncStatsManager.processLogData(0);
    }

    public void runTest() throws Exception {
        RuntimeContext.getAppContext().getBean(SMSResendingService.class).executeResending(); //DEF
        printMessage("Повторная отправка не доставленных СМС завершена");
    }

    public void runSmsDeliveryRecalculation() throws Exception {
        RuntimeContext.getAppContext().getBean(SmsDeliveryCalculationService.class).doRun(); //DEF
        printMessage("Пересчет показателей по СМС завершен");
    }

    @Override
    public String getPageFilename() {
        return "service/other_actions";
    }

    public String getPasswordForSearch() {
        return passwordForSearch;
    }

    public void setPasswordForSearch(String passwordForSearch) {
        this.passwordForSearch = passwordForSearch;
    }

    public void runPasswordReplacer() {
        try {
            clientsIds = ClientService.getInstance().modifyPasswords(passwordForSearch);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runGenerateGuardians() {
        if (!allowGenerateGuardians) return;
        try {
            allowGenerateGuardians = false;
            List<Long> orgs;

            int count = 0;
            try {
                orgs = getOrgsForGenGuardians();
                ClientsMobileHistory clientsMobileHistory =
                        new ClientsMobileHistory("кнопка \"Генерировать представителей\" в Сервис/Другое");
                User user = MainPage.getSessionInstance().getCurrentUser();
                clientsMobileHistory.setUser(user);
                clientsMobileHistory.setShowing("Изменено в веб.приложении. Пользователь:" + user.getUserName());
				ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
                clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
                clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
                clientGuardianHistory.setReason("Нажата кнопка \"Генерировать представителей\" в Сервис/Другое");
                count = ClientService.getInstance().generateGuardians(orgs, clientsMobileHistory, clientGuardianHistory);
            } catch (Exception e) {
                printError(String.format("Операция завершилась с ошибкой: %s", e.getMessage()));
                return;
            }

            printMessage(String.format("Операция выполнена успешно. Сгенерированы представители для %s клиентов", count));
        } finally {
            allowGenerateGuardians = true;
        }
    }

    public void runMealTest() {
        MealManager manager = RuntimeContext.getAppContext().getBean(MealManager.class);
        try {
            manager.sendToExternal(10);
        } catch (Exception e) {
            getLogger().error("Error in test meal run: ", e);
            printError(e.getMessage());
        }
    }

    public void runScudTest() {
        ScudManager manager = RuntimeContext.getAppContext().getBean(ScudManager.class);
        try {
            manager.sendToExternal(10);
        } catch (Exception e) {
            getLogger().error("Error in test SCUD run: ", e);
            printError(e.getMessage());
        }
    }

    public void loadNSIFile() throws Exception {
        if (!DAOReadonlyService.getInstance().isSverkaEnabled()) {
            printError("Сверка отключена в настройках. Загрузка файла не будет выполнена");
            return;
        }
        try {
            String mode = RuntimeContext.getInstance().getPropertiesValue(MODE_PROPERTY, null);
            if (mode.equals(MODE_FILE)) {
                RuntimeContext.getAppContext().getBean("ImportRegisterFileService", ImportRegisterFileService.class)
                        .loadNSIFile();
            }
            if (mode.equals(MODE_SYMMETRIC)) {
                RuntimeContext.getAppContext()
                        .getBean("ImportRegisterSymmetricService", ImportRegisterSymmetricService.class)
                        .loadClientsFromSymmetric();
            }
            printMessage("Файл загружен");
        } catch (Exception e) {
            getLogger().error("Error run load NSI file: ", e);
            printError("Во время загрузки из файла произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void loadNSIEmployeeFile() throws Exception {
        try {
            RuntimeContext.getAppContext()
                    .getBean("ImportRegisterEmployeeFileService", ImportRegisterEmployeeFileService.class)
                    .loadNSIFile();
            printMessage("Файл загружен");
        } catch (Exception e) {
            getLogger().error("Error run load NSI file: ", e);
            printError("Во время загрузки из файла произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void runGenerateSummaryDownloadFile() {
        summaryDate = CalendarUtils.addMinute(summaryDate, 60 * 12);
        Date endDate = CalendarUtils.endOfDay(summaryDate);
        Date startDate = CalendarUtils.truncateToDayOfMonth(summaryDate);
        try {
            RuntimeContext.getAppContext().getBean(SummaryDownloadMakerService.class).run(startDate, endDate);
            printMessage("Файл сгенерирован.");
        } catch (Exception e) {
            printError(String.format("Не удалось сгенерировать файл. Текст ошибки: %s", e.getMessage()));
        }
    }

    public void runGenerateSummaryFinOperatorFile() {
        summaryFinOperatorDate = CalendarUtils.addMinute(summaryFinOperatorDate, 60 * 12);
        Date endDate = CalendarUtils.endOfDay(summaryFinOperatorDate);
        Date startDate = CalendarUtils.truncateToDayOfMonth(summaryFinOperatorDate);
        try {
            RuntimeContext.getAppContext().getBean(FinManagerService.class).run(startDate, endDate);
            printMessage("Файл сгенерирован.");
        } catch (Exception e) {
            printError(String.format("Не удалось сгенерировать файл. Текст ошибки: %s", e.getMessage()));
        }
    }

    private List<Long> getOrgsForGenGuardians() throws Exception {
        return getOrgs(orgsForGenerateGuardians);
    }

    private List<Long> getOrgs(String field) throws Exception {
        if (field.equals("ALL")) {
            return null;
        }
        List<Long> orgs = new ArrayList<Long>();
        try {
            String[] sOrgs = field.split(",");
            for (String s : sOrgs) {
                Long org = Long.parseLong(s.trim());
                orgs.add(org);
            }
        } catch (Exception e) {
            throw new Exception("Корректно задайте список идентификаторов организаций через запятую");
        }
        return orgs;
    }

    public void download() throws IOException {
        if ((clientsIds == null) || (clientsIds.isEmpty())) {
            return;
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

        response.reset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        //response.setContentType(contentType); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ServletContext#getMimeType() for auto-detection based on filename.
        //response.setContentLength(contentLength); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        response.setHeader("Content-Disposition",
                "attachment; filename=\"text.txt\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.


        // Now you can write the InputStream of the file to the above OutputStream the usual way.
        // ...


        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        StringBuilder stringBuilder = new StringBuilder();
        for (Long clientsId : clientsIds) {
            stringBuilder.append(clientsId);
            stringBuilder.append("\n");
        }
        try {
            // Open streams.
            input = new BufferedInputStream(new ByteArrayInputStream(stringBuilder.toString().getBytes()), 10240);
            output = new BufferedOutputStream(response.getOutputStream(), 10240);

            // Write file contents to response.
            byte[] buffer = new byte[10240];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            close(output);
            close(input);
            fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.

        }

    }

    public boolean isDownloadable() {
        return clientsIds != null && !clientsIds.isEmpty();
    }

    public String getOrgsForGenerateGuardians() {
        return orgsForGenerateGuardians;
    }

    public void setOrgsForGenerateGuardians(String orgsForGenerateGuardians) {
        this.orgsForGenerateGuardians = orgsForGenerateGuardians;
    }

    public Date getSummaryDate() {
        return summaryDate;
    }

    public void setSummaryDate(Date summaryDate) {
        this.summaryDate = summaryDate;
    }

    public Date getSummaryFinOperatorDate() {
        return summaryFinOperatorDate;
    }

    public void setSummaryFinOperatorDate(Date summaryFinOperatorDate) {
        this.summaryFinOperatorDate = summaryFinOperatorDate;
    }

    public void loadESZMigrantsFile() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean("ImportMigrantsFileService", ImportMigrantsFileService.class)
                    .loadMigrantsFile();
            printMessage("Файл загружен");
        } catch (Exception e) {
            getLogger().error("Error run load ESZ migrants file: ", e);
            printError("Во время загрузки из файла произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void loadESZMigrants() throws Exception {
        try {
            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("кнопка \"Обработка мигрантов\" в Сервис/Другое");
            User user = MainPage.getSessionInstance().getCurrentUser();
            clientsMobileHistory.setUser(user);
            clientsMobileHistory.setShowing("Изменено в веб.приложении. Пользователь:" + user.getUserName());
			ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("Нажата кнопка \"Обработка мигрантов\" в Сервис/Другое");
            clientGuardianHistory.setAction("Обработка мигрантов");
            clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
            clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
            RuntimeContext.getAppContext().getBean("ImportMigrantsService", ImportMigrantsService.class).loadMigrants(clientsMobileHistory, clientGuardianHistory);
            printMessage("Обработка мигрантов завершена");
        } catch (Exception e) {
            getLogger().error("Error run load ESZ migrants: ", e);
            printError("Во время обработки произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void specialDatesLoadFileListener(UploadEvent event) {
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        try {
            if (item.isTempFile()) {
                File file = item.getFile();
                inputStream = new FileInputStream(file);
            } else {
                byte[] data = item.getData();
                inputStream = new ByteArrayInputStream(data);
            }
            DAOService.getInstance().loadProductionCalendar(inputStream);
            printMessage("Файл загружен успешно");
        } catch (Exception e) {
            getLogger().error("Failed to load special dates from file", e);
            printMessage("Ошибка при загрузке производственного календаря : " + e.getMessage());
        } finally {
            close(inputStream);
        }
    }

    public Object downloadSampleFile() {

        Map<String, Map<String, String>> dates = DAOReadonlyService.getInstance().getProductionCalendar();
        String result = "\"Год/Месяц\",\"Январь\",\"Февраль\",\"Март\",\"Апрель\",\"Май\",\"Июнь\",\"Июль\",\"Август\",\"Сентябрь\",\"Октябрь\",\"Ноябрь\",\"Декабрь\"\n";
        for (Map.Entry<String, Map<String, String>> entry : dates.entrySet()) {
            String str = entry.getKey() + ",";
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                str += "\"" + entry2.getValue() + "\",";
            }
            result += str.substring(0, str.length() - 1) + "\n";
        }
        result = result.substring(0, result.length() - 1);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            ServletOutputStream servletOutputStream = response.getOutputStream();

            facesContext.responseComplete();
            response.setContentType("application/csv");
            response.setHeader("Content-disposition", "attachment;filename=\"pk.csv\"");
            servletOutputStream.write(result.getBytes(Charset.forName("UTF-8")));
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            getLogger().error("Failed export report : ", e);
            printError("Не удалось сгенерировать пример файла для загрузки: " + e.getMessage());
        }
        return null;
    }

    public void sendGoodRequestsNewReports() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean("PreorderRequestsReportService", PreorderRequestsReportService.class)
                    .runGeneratePreorderRequests(new PreorderRequestsReportServiceParam(new Date()));
            printMessage("Отправка отчетов завершена");
        } catch (Exception e) {
            getLogger().error("Error send PreorderRequestsReport: ", e);
            printError("Во время отправки произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void createRegularPreorders() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(DAOService.class).getPreorderDAOOperationsImpl()
                    .generatePreordersBySchedule(new PreorderRequestsReportServiceParam(new Date()));
            printMessage("Создание регулярных предзаказов завершено");
        } catch (Exception e) {
            getLogger().error("Error create RegularPreorders: ", e);
            printError("Во время создания регулярных предзаказов произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void relevancePreordersToOrgs() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .relevancePreordersToOrgs(new PreorderRequestsReportServiceParam(new Date()));
            printMessage("Проверка соответствия ОО клиента и предзаказа завершена");
        } catch (Exception e) {
            getLogger().error("Error create relevancePreordersToOrgs: ", e);
            printError("Во время проверки соответствия ОО клиента и предзаказа произошла ошибка с текстом " + e
                    .getMessage());
        }
    }

    public void checkPreorderClientGroups() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .checkPreorderClientGroups(new PreorderRequestsReportServiceParam(new Date()));
            printMessage("Проверка групп клиентов завершена");
        } catch (Exception e) {
            getLogger().error("Error create checkPreorderClientGroups: ", e);
            printError("Во время проверки групп клиентов произошла ошибка с текстом " + e
                    .getMessage());
        }
    }

    public void relevancePreordersToMenu() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(PreorderOperationsService.class)
                    .runRelevancePreordersToMenu(new PreorderRequestsReportServiceParam(new Date()));
            printMessage("Проверка соответствия меню и предзаказа завершена");
        } catch (Exception e) {
            getLogger().error("Error create relevancePreordersToMenu: ", e);
            printError("Во время проверки соответствия меню и предзаказа произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void relevancePreordersToOrgFlag() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .relevancePreordersToOrgFlag(new PreorderRequestsReportServiceParam(new Date()));
            printMessage("Проверка соответствия флага включения функционала предзаказа ОО завершена");
        } catch (Exception e) {
            getLogger().error("Error create relevancePreordersToOrgFlag: ", e);
            printError(
                    "Во время проверки соответствия флага включения функционала предзаказа ОО произошла ошибка с текстом "
                            + e.getMessage());
        }
    }

    public void preordersCheck() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).dailyCheckPreorders();
            printMessage("Расчет количественных показателей по предзаказам завершен");
        } catch (Exception e) {
            getLogger().error("Error create preordersCheck: ", e);
            printError(
                    "Во время расчета количественных показателей по предзаказам произошла ошибка с текстом "
                            + e.getMessage());
        }
    }

    public void sendToAtol() {
        RuntimeContext.getAppContext().getBean(PaymentAdditionalTasksProcessor.class).runNotifications();
        printMessage("Отправка платежей в Атол выполнена");
    }

    public void processWtComplexes() {
        RuntimeContext.getAppContext().getBean(WtComplexCopyService.class).runTask();
        printMessage("Обработка копий комплексов веб арма ПП выполнена");
    }

    public void preorderRequestsManualGenerate() throws Exception {
        PreorderRequestsReportServiceParam params = new PreorderRequestsReportServiceParam(startDate);
        params.getIdOfOrgList().clear();
        if (idOfOrgList != null) {
            params.getIdOfOrgList().addAll(idOfOrgList);
        }
        params.getIdOfClientList().clear();
        if (getClientList() != null) {
            for (ClientSelectListPage.Item item : getClientList()) {
                params.getIdOfClientList().add(item.getIdOfClient());
            }
        }
        if (params.isEmpty()) {
            printError("Не указаны организация или клиент для выборочной генерации заявок");
            return;
        }
        RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).runTask(params, "1", "1");
        printMessage("Операция по созданию заявок выполнена");
    }

    public void runApplicationForFoodProcessingService() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(ApplicationForFoodProcessingService.class).runTask();
            printMessage("Обработка заявлений завершена");
        } catch (Exception e) {
            getLogger().error("Error processing applications for food: ", e);
            printError("Во время обработки заявлений на льготное питание произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void runDTSZNDiscountsReviseService() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).runTask();
            printMessage("Сверка завершена");
        } catch (Exception e) {
            getLogger().error("Error discounts revise service: ", e);
            printError("Во время сверки произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void sendToAISContingent() {
        try {
            RuntimeContext.getAppContext().getBean(ETPMVService.class).sendToAISContingentTask();
            printMessage("Операция выполнена");
        } catch (Exception e) {
            getLogger().error("Error in sendToAISContingent: ", e);
            printError("Во время выполнения операции произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void runDTSZNDiscountsReviseServicePart2() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).runTaskPart2();
            printMessage("Сверка (этап 2) завершена");
        } catch (Exception e) {
            getLogger().error("Error discounts revise service: ", e);
            printError("Во время сверки произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void runUpdateApplicationsForFoodTask() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).updateApplicationsForFoodTask(true, null);
            printMessage("Обработка ЗЛП завершена");
        } catch (Exception e) {
            getLogger().error("Error in runUpdateApplicationsForFoodTask: ", e);
            printError("Во время выполнения операции произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void runUpdateDiscounts() throws Exception {
        try {
            RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class)
                    .updateDiscountsForGUID(guidForDiscountsUpdate);
            printMessage("Обновление льгот завершено");
        } catch (Exception e) {
            getLogger().error("Error in runUpdateDiscounts: ", e);
            printError("Во время выполнения операции произошла ошибка с текстом " + e.getMessage());
        }
    }

    public String getOrgsForSpbCardsUidUpdate() {
        return orgsForSpbCardsUidUpdate;
    }

    public void setOrgsForSpbCardsUidUpdate(String orgsForSpbCardsUidUpdate) {
        this.orgsForSpbCardsUidUpdate = orgsForSpbCardsUidUpdate;
    }

    public Boolean isSpb() {
        //TODO
        return RuntimeContext.RegistryType.isSpb() || RuntimeContext.getInstance().isTestMode();
    }

    private List<Long> getOrgsForCardsUpdate() throws Exception {
        return getOrgs(orgsForSpbCardsUidUpdate);
    }

    public String getGuidForDiscountsUpdate() {
        return guidForDiscountsUpdate;
    }

    public void setGuidForDiscountsUpdate(String guidForDiscountsUpdate) {
        this.guidForDiscountsUpdate = guidForDiscountsUpdate;
    }

    public void runUpdateSpbCardUids() throws Exception {
        try {
            List<Long> orgs = getOrgsForCardsUpdate();
            RuntimeContext.getAppContext().getBean(CardsUidUpdateService.class).updateCards(orgs);
            printMessage("Преобразование номеров карт завершено");
        } catch (Exception e) {
            getLogger().error("Error update card uids: ", e);
            printError("Во время преобразования номеров карт произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void runProcessClientDoubles() throws Exception {
        try {
            Long idOfOrg = Long.parseLong(updateSpbClientDoubles);
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("Нажата кнопка \"Обработка дублей клиентов\" в Сервис/Другое");
            clientGuardianHistory.setAction("Обработка дублей клиентов");
            clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
            clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
            RuntimeContext.getAppContext().getBean(CardsUidUpdateService.class).processClientDoubles(idOfOrg, clientGuardianHistory);
            printMessage("Обработка дублей клиентов завершена");
        } catch (Exception e) {
            getLogger().error("Error update card uids: ", e);
            printError("Во время обработки дублей клиентов произошла ошибка с текстом " + e.getMessage());
        }
    }

    public void runMSRToFTP() {
        Date endDate = CalendarUtils.endOfDay(new Date());
        Date startDate = CalendarUtils.truncateToDayOfMonth(new Date());
        RuntimeContext.getAppContext().getBean(SummaryCardsMSRService.class).run(startDate, endDate);
        printMessage("Выгрузка в МСР выполнена.");
    }


    public void updateESZMigrants() throws Exception {
        try {
            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("нажата кнопка \"Обработка мигрантов (перевод в выбывшие)\" в Сервис/Другое");
            User user = MainPage.getSessionInstance().getCurrentUser();
            clientsMobileHistory.setUser(user);
            clientsMobileHistory.setShowing("Изменено в веб.приложении. Пользователь:" + user.getUserName());
			ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setReason("Нажата кнопка \"Обработка мигрантов (перевод в выбывшие)\" в Сервис/Другое");
            clientGuardianHistory.setAction("Обработка мигрантов  (перевод в выбывшие)");
            clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
            clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
            RuntimeContext.getAppContext().getBean("ESZMigrantsUpdateService", ESZMigrantsUpdateService.class)
                    .updateMigrants(clientsMobileHistory, clientGuardianHistory);
            printMessage("Обработка мигрантов завершена");
        } catch (Exception e) {
            getLogger().error("Error run update ESZ migrants: ", e);
            printError("Во время обработки произошла ошибка с текстом " + e.getMessage());
        }
    }

    public String getUpdateSpbClientDoubles() {
        return updateSpbClientDoubles;
    }

    public void setUpdateSpbClientDoubles(String updateSpbClientDoubles) {
        this.updateSpbClientDoubles = updateSpbClientDoubles;
    }

    public Date getStartDateEMP() {
        return startDateEMP;
    }

    public void setStartDateEMP(Date startDateEMP) {
        this.startDateEMP = startDateEMP;
    }

    public Date getEndDateEMP() {
        return endDateEMP;
    }

    public void setEndDateEMP(Date endDateEMP) {
        this.endDateEMP = endDateEMP;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}
