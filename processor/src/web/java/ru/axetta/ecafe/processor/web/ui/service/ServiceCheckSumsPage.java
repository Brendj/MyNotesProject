/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CheckSums;
import ru.axetta.ecafe.processor.core.service.CheckSumsDAOService;
import ru.axetta.ecafe.processor.core.service.CheckSumsMessageDigitsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 30.05.16
 * Time: 11:08
 */

@Component
@Scope("session")
public class ServiceCheckSumsPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCheckSumsPage.class);

    private String errorMessages;

    public String getErrorMessages() {
        return errorMessages;
    }

    //Сервис для того чтобы можно было достать все записи из таблицы cf_checksums
    public CheckSumsDAOService checkSumsDaoService = new CheckSumsDAOService();

    //Сервис который считает md5 для всех файлов
    public CheckSumsMessageDigitsService checkSumsMessageDigitsService = new CheckSumsMessageDigitsService();

    //Массив для отрисовки таблицы итоговой
    private List<CheckSumsDAOService.ServiceCheckSumsPageItems> serviceCheckSumsPageItemsList;

    //Путь до каталога по каким нужно считать md5
    private String folderPatch = RuntimeContext.getInstance().getConfigProperties()
            .getProperty("ecafe.processor.checksums.folder.path", "");

    File f = new File(folderPatch);

    public void run() {
        Date currentDate = new Date();
        logger.error("Подсчет контрольной суммы ПО запущен: " + currentDate.toString());

        if (f != null && f.exists()) {
            try {
                String md5ForAllFiles = checkSumsMessageDigitsService.processFilesFromFolder(f, currentDate);
                String version = String.valueOf(RuntimeContext.getInstance().getCurrentDBSchemaVersion());
                CheckSums checkSums = new CheckSums(currentDate, version, md5ForAllFiles);
                checkSumsDaoService.saveCheckSums(checkSums);
            } catch (Exception e2) {
                logger.error(String.format("Ошибка работы сервиса (%s)", f), e2);
                errorMessages = String.format("Ошибка работы сервиса (%s)", f);
                printError(String.format("Ошибка работы сервиса (%s)", f));
            }
        } else {
            logger.error(String.format("Не удается найти файлы по заданному пути (%s)", f));
            errorMessages = String.format("Не удается найти файлы по заданному пути (%s)", f);
            printError(String.format("Не удается найти файлы по заданному пути (%s)", f));
            printError("Добавьте в \"Настройки / Настройки / Конфигурация\" строку вида, ecafe.processor.checksums.folder.path=\"Путь до WEB-INF\"");
        }

        //После вычисления и записи в БД
        serviceCheckSumsPageItemsList = new ArrayList<CheckSumsDAOService.ServiceCheckSumsPageItems>();
        serviceCheckSumsPageItemsList.addAll(checkSumsDaoService.getCheckSums());
    }

    public String getPageFilename() {
        return "service/checksums_service";
    }

    @Override
    public void onShow() throws Exception {
        serviceCheckSumsPageItemsList = new ArrayList<CheckSumsDAOService.ServiceCheckSumsPageItems>();
        serviceCheckSumsPageItemsList.addAll(checkSumsDaoService.getCheckSums());
    }

    public Logger getLogger() {
        return logger;
    }

    public CheckSumsDAOService getCheckSumsDaoService() {
        return checkSumsDaoService;
    }

    public void setCheckSumsDaoService(CheckSumsDAOService checkSumsDaoService) {
        this.checkSumsDaoService = checkSumsDaoService;
    }

    public List<CheckSumsDAOService.ServiceCheckSumsPageItems> getServiceCheckSumsPageItemsList() {
        return serviceCheckSumsPageItemsList;
    }

    public void setServiceCheckSumsPageItemsList(
            List<CheckSumsDAOService.ServiceCheckSumsPageItems> serviceCheckSumsPageItemsList) {
        this.serviceCheckSumsPageItemsList = serviceCheckSumsPageItemsList;
    }
}
