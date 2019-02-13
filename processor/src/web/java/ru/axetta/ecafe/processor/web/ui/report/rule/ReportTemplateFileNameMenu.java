/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.model.SelectItem;
import java.io.File;
import java.util.ArrayList;
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

    private SelectItem[] items;
    private SelectItem[] stringItems;

    private static SelectItem[] readAllItems() {
        String reportPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        List<File> templateFilesNameList = new ArrayList<File>();
        String fullReportPath="";
        if (reportPath!=null) {
            File rootFile = new File(reportPath);
            if (rootFile.exists()) fullReportPath = rootFile.getAbsolutePath();
            getTemplateFilesname(reportPath, templateFilesNameList);
        } else {
            logger.error("Report templates path is not specified");
        }
        SelectItem[] items = new SelectItem[templateFilesNameList.size()];
        int i = 0;
        for (File file : templateFilesNameList) {
            items[i] = new SelectItem(file.getAbsolutePath().substring(fullReportPath.length()+1));
            items[i++].setValue(file);
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
        if (items==null) items = readAllItems();
        return items;
    }

    public SelectItem[] getItemsForReportType(String reportType) {
        String reportSimpleName;
        int pos = reportType.lastIndexOf('.');
        reportSimpleName = reportType.substring(pos+1);
        ArrayList<SelectItem> items = new ArrayList<SelectItem>();
        for (SelectItem si : getItems()) {
            if (si.getLabel().contains(reportSimpleName)) items.add(si);
        }
        return items.toArray(new SelectItem[]{});
    }

    public SelectItem[] getStrItemsForReportType(String reportType) {
        String reportSimpleName;
        int pos = reportType.lastIndexOf('.');
        reportSimpleName = reportType.substring(pos+1);
        ArrayList<SelectItem> items = new ArrayList<SelectItem>();
        for (SelectItem si : getItems()) {
            if (si.getLabel().contains(reportSimpleName)){
                SelectItem item = new SelectItem(si.getLabel());
                items.add(item);
            }
        }
        return items.toArray(new SelectItem[]{});
    }

    public SelectItem[] getItemsWithForcedReload() {
        items = readAllItems();
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }

    public SelectItem[] getStringItems() {
        if(stringItems == null){
            stringItems = readStrItems();
        }
        return stringItems;
    }

    private SelectItem[] readStrItems() {
        SelectItem[] items = this.getItems();
        SelectItem[] strItems = new SelectItem[items.length];

        for(int i = 0; i < strItems.length; i++){
            File file = (File)items[i].getValue();
            strItems[i] = new SelectItem(file.getName());
        }
        return strItems;
    }

    public void setStringItems(SelectItem[] stringItems) {
        this.stringItems = stringItems;
    }
}