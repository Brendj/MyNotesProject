/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.service.CheckSumsDAOService;
import ru.axetta.ecafe.processor.core.service.CheckSumsMessageDigitsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    //Сервис для того чтобы можно было достать все записи из таблицы cf_checksums
    private final CheckSumsDAOService checkSumsDaoService = new CheckSumsDAOService();

    //Сервис который считает md5 для всех файлов
    private final CheckSumsMessageDigitsService checkSumsMessageDigitsService = new CheckSumsMessageDigitsService();

    //Массив для отрисовки таблицы итоговой
    private List<CheckSumsDAOService.ServiceCheckSumsPageItems> serviceCheckSumsPageItemsList;

    public void run() {
        Date currentDate = new Date();
        logger.info("Подсчет контрольной суммы ПО запущен: " + currentDate.toString());

        try {
            String[] md5s = checkSumsMessageDigitsService.getCheckSum();
            checkSumsMessageDigitsService.saveCheckSumToDB(md5s[0], md5s[1]);
        } catch (Exception e2) {
            logger.error("Ошибка работы сервиса подсчета контрольных сумм", e2);
            printError(String.format("Подсчет завершился с ошибкой (%s)", e2.getMessage()));
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

    public List<CheckSumsDAOService.ServiceCheckSumsPageItems> getServiceCheckSumsPageItemsList() {
        return serviceCheckSumsPageItemsList;
    }

    public void setServiceCheckSumsPageItemsList(
            List<CheckSumsDAOService.ServiceCheckSumsPageItems> serviceCheckSumsPageItemsList) {
        this.serviceCheckSumsPageItemsList = serviceCheckSumsPageItemsList;
    }
}
