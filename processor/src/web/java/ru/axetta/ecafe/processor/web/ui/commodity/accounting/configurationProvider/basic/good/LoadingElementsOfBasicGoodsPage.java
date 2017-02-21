/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by anvarov on 14.02.2017.
 */

public class LoadingElementsOfBasicGoodsPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(LoadingElementsOfBasicGoodsPage.class);

    public UploadItem uploadItem;

    private List<LoadingElementsOfBasicGoodsItem> loadingElementsOfBasicGoodsItems;

    private ArrayList<ru.axetta.ecafe.processor.core.mail.File> files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

    public ArrayList<ru.axetta.ecafe.processor.core.mail.File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<ru.axetta.ecafe.processor.core.mail.File> files) {
        this.files = files;
    }

    public UploadItem getUploadItem() {
        return uploadItem;
    }

    public void setUploadItem(UploadItem uploadItem) {
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

    public void loadingElementsOfBasicGoodsGenerate(UploadItem item, RuntimeContext runtimeContext) throws Exception {

        LoadingElementsOfBasicGoodsService loadingElementsOfBasicGoodsService = new LoadingElementsOfBasicGoodsService();

        String line;
        String csvSplitBy = ";";

        if (item != null) {
            File file = item.getFile();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file.getAbsolutePath()), Charset.forName("UTF-8")));

            loadingElementsOfBasicGoodsItems = new ArrayList<LoadingElementsOfBasicGoodsItem>();
            files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();

            Long rowNum = 0L;

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    ++rowNum;
                    Session persistenceSession = runtimeContext.createPersistenceSession();
                    Transaction persistenceTransaction = null;
                    try {
                        persistenceTransaction = persistenceSession.beginTransaction();
                        String[] separatedData = line.split(csvSplitBy);

                        if (separatedData.length < 4) {
                            loadingElementsOfBasicGoodsItems
                                    .add(new LoadingElementsOfBasicGoodsItem(rowNum, separatedData[0], separatedData[1],
                                            separatedData[2], separatedData[3], "Неверный формат, вид .csv файла"));
                        } else {
                            ConfigurationProvider configurationProvider = loadingElementsOfBasicGoodsService
                                    .findConfigurationProviderByName(persistenceSession, separatedData[0].trim());

                            String nameOfGood = separatedData[1].trim();
                            String netWeight = separatedData[3].trim();

                            if (nameOfGood != null || netWeight != null) {
                                String unitScale = separatedData[2].trim().toLowerCase();
                                UnitScale unitScaleType = null;

                                for (UnitScale type : unitScaleType.values()) {
                                    if (unitScale.equalsIgnoreCase(type.toString())) {
                                        unitScaleType = type;
                                    }
                                }

                                BasicGoodItem basicGoodItem = new BasicGoodItem();
                                basicGoodItem.createEmptyEntity();

                                basicGoodItem.setLastUpdate(new Date());
                                basicGoodItem.setNameOfGood(nameOfGood);
                                basicGoodItem.setUnitsScale(unitScaleType);
                                basicGoodItem.setNetWeight(Long.valueOf(netWeight));

                                persistenceSession.save(basicGoodItem);

                                loadingElementsOfBasicGoodsItems
                                        .add(new LoadingElementsOfBasicGoodsItem(rowNum, separatedData[0],
                                                separatedData[1], separatedData[2], separatedData[3],
                                                "Базовый продукт загружен."));

                            } else {
                                if (nameOfGood == null) {
                                    loadingElementsOfBasicGoodsItems
                                            .add(new LoadingElementsOfBasicGoodsItem(rowNum, separatedData[0],
                                                    separatedData[1], separatedData[2], separatedData[3],
                                                    "Пустое наименование продукта."));
                                }
                                if (netWeight == null) {
                                    loadingElementsOfBasicGoodsItems
                                            .add(new LoadingElementsOfBasicGoodsItem(rowNum, separatedData[0],
                                                    separatedData[1], separatedData[2], separatedData[3],
                                                    "Пустое масса нетто (грамм)."));
                                }
                            }
                        } persistenceTransaction.commit();
                    } catch (Exception ex) {
                        HibernateUtils.rollback(persistenceTransaction, logger);
                    } finally {
                        HibernateUtils.close(persistenceSession, logger);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                printError("Файл не был найден");
            } catch (PersistenceException ex) {
                ex.printStackTrace();
                printError("Файл неверного формата");
            } catch (Exception e) {
                e.printStackTrace();
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
            printError("Сделайте загрузку файла");
        }
    }
}