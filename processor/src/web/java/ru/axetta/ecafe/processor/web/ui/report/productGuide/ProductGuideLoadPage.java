/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.productGuide;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderMenu;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 19.05.12
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class ProductGuideLoadPage extends BasicWorkspacePage {

    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;
    private static final Logger logger = LoggerFactory.getLogger(ProductGuideLoadPage.class);
    private static final long MAX_LINE_NUMBER = 80000;
    private final ConfigurationProviderMenu configurationProviderMenu = new ConfigurationProviderMenu();

    public ConfigurationProviderMenu getConfigurationProviderMenu() {
        return configurationProviderMenu;
    }

    public void fillConfigurationProviderComboBox(Session persistenceSession) throws Exception {
        configurationProviderMenu.readAllItems(persistenceSession);
        if (MainPage.getSessionInstance().getCurrentConfigurationProvider()==null &&
                configurationProviderMenu.getItems() != null && configurationProviderMenu.getItems().length > 0) {
            MainPage.getSessionInstance().setCurrentConfigurationProvider(configurationProviderMenu.getItems()[0].getLabel());
        }
    }

    public void loadProductGuide(InputStream inputStream, long dataSize) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        try{
            TimeZone localTimeZone = runtimeContext
                    .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setTimeZone(localTimeZone);

            long lineCount = dataSize / 20;
            if (lineCount > MAX_LINE_NUMBER) {
                lineCount = MAX_LINE_NUMBER;
            }
            List<LineResult> lineResults = new ArrayList<LineResult>((int) lineCount);
            int lineNo = 0;
            int successLineNumber = 0;
            /* массив с именами колонок */
            String colums[]={}; //= {"ContractState", "MobilePhone","NotifyViaSMS",
            // "PersonFirstName","PersonSurName","PersonSecondName"};

            ProductGuideManager.ProductGuideFieldConfig fieldConfig = new ProductGuideManager.ProductGuideFieldConfig();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
            String currLine = reader.readLine();
            while (null != currLine) {
                if (lineNo==0) {
                    if (!currLine.startsWith("!")) currLine="!"+currLine;
                    parseLineConfig(fieldConfig, currLine);
                } else {
                    LineResult result = insertProductGuide(fieldConfig, currLine, lineNo);
                    if (result.getResultCode() == 0) {
                        ++successLineNumber;
                    }
                    lineResults.add(result);
                }
                currLine = reader.readLine();
                if (lineNo == MAX_LINE_NUMBER) {
                    break;
                }
                ++lineNo;
            }
            this.lineResults = lineResults;
            this.successLineNumber = successLineNumber;
        } finally {

        }
    }

    private LineResult insertProductGuide(ProductGuideManager.ProductGuideFieldConfig fieldConfig, String line,
            int lineNo) {
        String[] tokens = line.split(";");
        try {
            fieldConfig.setValues(tokens);
        } catch (Exception e) {
            return new LineResult(lineNo, 1, e.getMessage(), null);
        }
        try {
            long idOfProductGuide = ProductGuideManager.insertProductGuide(fieldConfig);
            return new LineResult(lineNo, 0, "Ok", idOfProductGuide);
        } catch (Exception e) {
            return new LineResult(lineNo, -1, "Ошибка: "+e.getMessage(), -1L);
        }
    }


    private void parseLineConfig(ProductGuideManager.ProductGuideFieldConfig fieldConfig, String currLine) throws Exception {
        String attrs[] = currLine.substring(1).split(";");
        for (int n=0;n<attrs.length;++n) {
            fieldConfig.registerField(attrs[n]);
        }
        fieldConfig.checkRequiredFields();
    }

    public static class LineResult {

        private final long lineNo;
        private final int resultCode;
        private final String message;
        private final Long idOfProductGuide;

        public LineResult(long lineNo, int resultCode, String resultDescription, Long idOfProductGuide) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.message = resultDescription;
            this.idOfProductGuide = idOfProductGuide;
        }

        public long getLineNo() {
            return lineNo;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public Long getIdOfProductGuide() {
            return idOfProductGuide;
        }
    }

    //public static class ProductGuideItem {
    //
    //    private final Long idOfProductGuide;
    //    //private final String shortName;
    //    //private final String officialName;
    //
    //    public ProductGuideItem() {
    //        this.idOfProductGuide = null;
    //        //this.shortName = null;
    //        //this.officialName = null;
    //    }
    //
    //    public ProductGuideItem(ProductGuide productGuide) {
    //        this.idOfProductGuide = productGuide.getIdOfProductGuide();
    //        //this.shortName = org.getShortName();
    //        //this.officialName = org.getOfficialName();
    //    }
    //
    //    public Long getIdOfProductGuide() {
    //        return idOfProductGuide;
    //    }
    //
    //    //public String getShortName() {
    //    //    return shortName;
    //    //}
    //    //
    //    //public String getOfficialName() {
    //    //    return officialName;
    //    //}
    //}

    public String getPageFilename() {
        return "report/product_guide/load_file";
    }

    public List<LineResult> getLineResults() {
        return lineResults;
    }

    public int getLineResultSize() {
        return lineResults.size();
    }

    public int getSuccessLineNumber() {
        return successLineNumber;
    }

}
