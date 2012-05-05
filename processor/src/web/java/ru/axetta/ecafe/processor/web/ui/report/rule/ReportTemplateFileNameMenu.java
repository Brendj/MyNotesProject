/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.model.SelectItem;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
public class ReportTemplateFileNameMenu {
    private static final Logger logger = LoggerFactory.getLogger(ReportTemplateFileNameMenu.class);

    private SelectItem[] items = readAllItems();

    private static SelectItem[] readAllItems() {
        List<File> templateFilesNameList = new ArrayList<File>();
        String reportPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        if (reportPath!=null) {
            getTemplateFilesname(reportPath, templateFilesNameList);
        } else {
            logger.error("Report templates path is not specified");
        }
        SelectItem[] items = new SelectItem[templateFilesNameList.size()];
        int i = 0;
        for (File file : templateFilesNameList) {
            items[i++] = new SelectItem(file.getAbsolutePath().substring(reportPath.length()));
        }
        return items;
    }

    /**
     * рекурсивный поиск файлов отчетов
     * @param dirName - имя директории для поиска
     * @param templateFilesNameList - список имен файлов шаблонов отчетов
     */
    public static void getTemplateFilesname(String dirName, List<File> templateFilesNameList) {
        File dir = new File(dirName);
        File[] files = dir.listFiles();
        if (files!=null) {
            for (File file : files) {
                if (file.isDirectory())
                    getTemplateFilesname(file.getAbsolutePath(), templateFilesNameList);
                else if (file.getName().endsWith(".jasper")) {
                    templateFilesNameList.add(file);
                }
            }
        }
    }

    public SelectItem[] getItems() {
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }

}