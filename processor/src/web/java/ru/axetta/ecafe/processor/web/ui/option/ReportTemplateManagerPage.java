/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import org.richfaces.model.UploadedFile;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.ReportRuleConstants;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportTemplateFileNameMenu;

import org.apache.commons.lang.StringUtils;

import javax.faces.model.SelectItem;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.05.12
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class ReportTemplateManagerPage extends BasicWorkspacePage {
    //private static final Logger logger = LoggerFactory.getLogger(ReportTemplateManagerPage.class);
    private String relativePath;

    public void removeTemplate(String removedReportTemplate) throws Exception {
        String reportPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        if (reportPath==null || reportPath.isEmpty()) {
            throw new Exception("Не настроен путь к директории шаблонов отчетов");
        }
        File file = new File(reportPath+removedReportTemplate);
        if (!file.exists())
            throw new Exception(String.format("Can not find template %s.", removedReportTemplate));
        if (!file.delete())
            throw new Exception(String.format("Can not delete template %s.", removedReportTemplate));
        load();
    }

    public class Item implements Comparable{
        private String reportName = "";
        private String name = "";
        private Date dateEdit;
        private long size;

        public Item(String reportName, String name, Date dateEdit, long size) {
            this.reportName = reportName;
            this.name = name;
            this.dateEdit = dateEdit;
            this.size = size;
        }

        public String getSizeInStr() {
            DecimalFormat f = new DecimalFormat("#####0.#");
            return f.format(size/1024.0);
        }

        public String getReportName() {
            return reportName;
        }

        public void setReportName(String reportName) {
            this.reportName = reportName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public Date getDateEdit() {
            return dateEdit;
        }

        public void setDateEdit(Date dateEdit) {
            this.dateEdit = dateEdit;
        }

        @Override
        public String toString() {
            return getNameWithPath();
        }

        public String getNameWithPath() {
            return relativePath+name;
        }

        @Override
        public int compareTo(Object o)  {
            if (o instanceof Item) {
                Item item = (Item)o;
                if (item.getName().indexOf("\\")>=0) {
                    if (this.getName().indexOf("\\")>=0) {
                        return item.getName().compareTo(this.getName())*-1;
                    } else {
                        return 1;
                    }
                } else {
                    if (this.getName().indexOf("\\")>=0) {
                        return -1;
                    } else {
                        return item.getName().compareTo(this.getName())*-1;
                    }
                }

            } else
                return 0;
        }
    }

    private List<Item> items = new ArrayList<Item>();

    public ReportTemplateManagerPage() {
        load();
    }

    public List<Item> getItems() {
        return items;
    }

    public void load() {
        items.clear();
        ReportTemplateFileNameMenu reportTemplateFileNameMenu = new ReportTemplateFileNameMenu();
        String reportPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        if (reportPath==null || reportPath.isEmpty()) {
            logAndPrintMessage("Не настроен путь к директории шаблонов отчетов", null);
        }
        else{
            SelectItem[] templateFilesNameList = reportTemplateFileNameMenu.getItemsWithForcedReload();
            for (SelectItem s : templateFilesNameList) {
                File file = (File)s.getValue();
                String name = s.getLabel();
                String reportName = ReportRuleConstants.REPORT_NAME_MAP.get(name);
                if (reportName == null) {
                    reportName = "Неизвестно";
                }
                items.add(new Item(reportName, name, new Date(file.lastModified()), file.length()));
            }
        }
    }

    public String getPageFilename() {
        return "option/reportTemplateManager";
    }

    public void checkPath() {
        if (relativePath==null) {
            relativePath=""; return;
        }
        int i = 0;
        while (i<relativePath.length() && (relativePath.charAt(i) == '.' ||
                relativePath.charAt(i) == '/' ||
                relativePath.charAt(i) == '\\')) {
            i++;
        }
        relativePath = relativePath.substring(i);
    }


    public void checkAndSaveFile(UploadedFile item) throws Exception {
        checkPath();

        if (StringUtils.isNotEmpty(relativePath) && !relativePath.endsWith("/") && !relativePath.endsWith("\\")) relativePath+='/';
        String path = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + (relativePath==null?"":relativePath);
        File file = new File(path + item.getName());
        if (!file.exists()) {
            File dir = new File(path);
            if (!dir.exists())
                if (!dir.mkdirs())
                    throw new Exception(String.format("Ошибка при создании директории %s.", dir.getAbsolutePath()));
            //if (!item.getFile().renameTo(new File(path, item.getName())))
            //    throw new Exception(String.format("Ошибка перемеинования файла %s%s.", path, item.getFileName()));
            load();
        } else {
            throw new Exception("Шаблон уже существует.");
        }
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
