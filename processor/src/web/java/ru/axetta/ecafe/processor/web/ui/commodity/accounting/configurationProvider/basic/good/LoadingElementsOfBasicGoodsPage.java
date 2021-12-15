/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import org.richfaces.model.UploadedFile;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anvarov on 14.02.2017.
 */

public class LoadingElementsOfBasicGoodsPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(LoadingElementsOfBasicGoodsPage.class);
    public static final String UTF8_BOM = "\uFEFF";

    public UploadedFile uploadItem;

    private List<LoadingElementsOfBasicGoodsItem> loadingElementsOfBasicGoodsItems;

    private ArrayList<ru.axetta.ecafe.processor.core.mail.File> files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

    public ArrayList<ru.axetta.ecafe.processor.core.mail.File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<ru.axetta.ecafe.processor.core.mail.File> files) {
        this.files = files;
    }

    public UploadedFile getUploadItem() {
        return uploadItem;
    }

    public void setUploadItem(UploadedFile uploadItem) {
        this.uploadItem = uploadItem;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/basicGood/load_elements";
    }

    public List<LoadingElementsOfBasicGoodsItem> getLoadingElementsOfBasicGoodsItems() {
        return loadingElementsOfBasicGoodsItems;
    }

    public void setLoadingElementsOfBasicGoodsItems(
            List<LoadingElementsOfBasicGoodsItem> loadingElementsOfBasicGoodsItems) {
        this.loadingElementsOfBasicGoodsItems = loadingElementsOfBasicGoodsItems;
    }

    private String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public void loadingElementsOfBasicGoodsGenerate(UploadedFile item, RuntimeContext runtimeContext) throws Exception {

        LoadingElementsOfBasicGoodsService loadingElementsOfBasicGoodsService = new LoadingElementsOfBasicGoodsService();

        String line;
        String csvSplitBy = ";";

        if (item != null) {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(item.getInputStream(), Charset.forName("UTF-8")));

            loadingElementsOfBasicGoodsItems = new ArrayList<LoadingElementsOfBasicGoodsItem>();
            files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

            Long rowNum = 0L;

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    ++rowNum;
                    line = removeUTF8BOM(line);
                    String[] separatedData = line.split(csvSplitBy);
                    try {
                        if (separatedData.length < 4) {
                            throw new Exception("Неверный формат файла");
                        }
                        String cpString = separatedData[0].trim();
                        String[] cp = cpString.split(",");
                        String nameOfGood = separatedData[1].trim();
                        String netWeight = separatedData[3].trim();
                        String unitScale = separatedData[2].trim().toLowerCase();
                        if (StringUtils.isEmpty(cpString) || StringUtils.isEmpty(nameOfGood) || StringUtils.isEmpty(netWeight) || StringUtils.isEmpty(unitScale)) {
                            throw new Exception("Поля с пустыми значениями не допускаются");
                        }
                        UnitScale unitScaleType = UnitScale.fromString(unitScale);
                        if (unitScaleType == null) throw new Exception("Единица измерения не распознана");

                        List<Long> list = new ArrayList<Long>();
                        for (String str : cp) {
                            list.add(Long.valueOf(str.trim()));
                        }
                        Boolean result = DAOService.getInstance()
                                .createBasicGood(nameOfGood, unitScaleType, Long.valueOf(netWeight), list);

                        if (result)
                            loadingElementsOfBasicGoodsItems
                                    .add(new LoadingElementsOfBasicGoodsItem(rowNum, separatedData[0],
                                            separatedData[1], separatedData[2], separatedData[3],
                                            "Базовый продукт загружен."));
                    } catch (Exception e) {
                        loadingElementsOfBasicGoodsItems
                                .add(new LoadingElementsOfBasicGoodsItem(rowNum, separatedData[0], separatedData[1], separatedData[2], separatedData[3],
                                        e.getMessage()));
                    }
                }
            } catch (Exception e) {
                logger.error("Error load basic basket file: ", e);
                printError(e.getMessage());
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        printError(e.getMessage());
                    }
                }
            }
        } else {
            printError("Выполните загрузку файла");
        }
    }
}