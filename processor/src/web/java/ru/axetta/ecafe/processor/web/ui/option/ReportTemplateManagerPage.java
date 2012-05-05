/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportTemplateFileNameMenu;

import org.apache.commons.lang.StringUtils;
import org.apache.ws.security.util.StringUtil;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.05.12
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class ReportTemplateManagerPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(ReportTemplateManagerPage.class);
    private String relativePath;
    private String reportPath;

    public void removeTemplate(String removedReportTemplate) throws Exception {
        File file = new File(reportPath+removedReportTemplate);
        if (!file.exists())
            throw new Exception(String.format("Can not find template %s.", removedReportTemplate));
        if (!file.delete())
            throw new Exception(String.format("Can not delete template %s.", removedReportTemplate));
        load();
    }

    //public void processValueChange(ValueChangeEvent valueChangeEvent) {
    //    relativePath = (String)valueChangeEvent.getNewValue();
    //}

    public class Item {
        private String name = "";
        private String relativePath;

        public Item(String name) {
            this.name = name;
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

        public void setRelativePath(String relativePath) {
            this.relativePath = relativePath;
        }

        @Override
        public String toString() {
            return getNameWithPath();
        }

        public String getNameWithPath() {
            return relativePath+name;
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
        List<File> templateFilesNameList = new ArrayList<File>();
        reportPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        if (reportPath!=null) {
            ReportTemplateFileNameMenu.getTemplateFilesname(reportPath, templateFilesNameList);
        } else {
            logger.error("Report templates path is not specified");
        }
        items.clear();
        for (File file : templateFilesNameList) {
            items.add(new Item(file.getAbsolutePath().substring(reportPath.length())));
        }
    }

    public String getPageFilename() {
        return "option/reportTemplateManager";
    }


    public void checkAndSaveFile(UploadItem item) throws Exception {
        if (StringUtils.isNotEmpty(relativePath) && !relativePath.endsWith("/") && !relativePath.endsWith("\\")) relativePath+='/';
        String path = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + (relativePath==null?"":relativePath);
        File file = new File(path + item.getFileName());
        if (!file.exists()) {
            File dir = new File(path);
            if (!dir.exists())
                if (!dir.mkdirs())
                    throw new Exception(String.format("Can not make directory %s.", dir.getAbsolutePath()));
            if (!item.getFile().renameTo(new File(path, item.getFileName())))
                throw new Exception(String.format("Can not rename to %s%s.", path, item.getFileName()));
            load();
        } else {
            throw new Exception(String.format("File  %s already exist.", file.getAbsolutePath()));
        }
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    //public void loadFiles(File file) {
    //
    //}

}
