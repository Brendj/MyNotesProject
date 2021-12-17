/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CheckSums;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 31.05.16
 * Time: 11:35
 */

@Component
@Scope("singleton")
public class CheckSumsMessageDigitsService {

    //private List<String> resultList = new ArrayList<String>();

    private static final String configString = "ecafe.processor.checkSumDir";
    private String baseDir = RuntimeContext.getInstance().getConfigProperties()
            .getProperty(configString, "");

    public String[] getCheckSum() throws Exception {
        File fBaseDir = new File(baseDir);
        if (StringUtils.isEmpty(baseDir)) {
            throw new Exception(String.format("В конфигурации не найдена настройка %s", configString));
        }
        if (!fBaseDir.exists()) {
            if (!tryDirCreate(fBaseDir)) {
                throw new Exception(String.format("Не удается найти или создать каталог %s для хранения файлов с контрольными суммами приложения", baseDir));
            }
        }
        List<String> sb = new ArrayList<String>();
        List<String> files = getClassFiles();
        String checkSum = "";
        for (String file : files) {
            checkSum = getCheckSumOfFile(file);
            file = file.replace("\\", "/");
            sb.add(file.substring(file.indexOf("/WEB-INF/")) + " = " + checkSum);
        }
        Collections.sort(sb);
        String resultClasses = "";
        for (String sss : sb) {
            resultClasses += sss + "\n";
        }
        String resultSettings = getSecuritySettings();
        saveToLog(resultClasses + resultSettings);

        String classesCheckSum = processFilesMd5Count(resultClasses);
        String settingsCheckSum = processFilesMd5Count(resultSettings);

        return new String[] {classesCheckSum, settingsCheckSum};
    }

    private String getSecuritySettings() {
        String result = "";
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        result += "periodBlockLoginReUse = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_PERIOD_BLOCK_LOGIN_REUSE) + "\n";
        result += "periodBlockUnusedLogin = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_PERIOD_BLOCK_UNUSED_LOGIN_AFTER) + "\n";
        result += "periodSmsCodeAlive = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_PERIOD_SMS_CODE_ALIVE) + "\n";
        result += "periodPasswordChange = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_PERIOD_PASSWORD_CHANGE) + "\n";
        result += "maxAuthFaultCount = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_MAX_AUTH_FAULT_COUNT) + "\n";
        result += "tmpBlockAccTime = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_TMP_BLOCK_ACC_TIME) + "\n";
        result += "clientPeriodBlockLoginReUse = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_LOGIN_REUSE) + "\n";
        result += "clientPeriodBlockUnusedLogin = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_UNUSED_LOGIN_AFTER) + "\n";
        result += "clientPeriodPasswordChange = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_PASSWORD_CHANGE) + "\n";
        result += "clientMaxAuthFaultCount = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_MAX_AUTH_FAULT_COUNT) + "\n";
        result += "clientTmpBlockAccTime = " + runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_TMP_BLOCK_ACC_TIME);
        return result;
    }

    private boolean tryDirCreate(File dir) {
        try {
            return dir.mkdirs();
        } catch (Exception e) {
            return false;
        }
    }

    public void saveCheckSumToDB(String checkSumClasses, String checkSumSettings) {
        String version = String.valueOf(RuntimeContext.getInstance().getCurrentDBSchemaVersion());
        CheckSums checkSumDB = new CheckSums(new Date(), version, checkSumClasses, checkSumSettings);
        CheckSumsDAOService checkSumsDaoService = new CheckSumsDAOService();
        checkSumsDaoService.saveCheckSums(checkSumDB);
    }

    private void saveToLog(String str) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh_mm_ss");
        StringBuilder sb = new StringBuilder();
        Date date = new Date(System.currentTimeMillis());
        sb.append(baseDir).append("/").append(dateFormat.format(date)).append(".log");
        String fileName = sb.toString();
        File file = new File(fileName);
        if (file.createNewFile()) {
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                outputStream.write(str.getBytes());
            } finally {
                outputStream.close();
            }
        } else {
            throw new Exception(String.format("Не удается создать файл %s для сохранения лога расчета контрольной суммы", fileName));
        }

    }

    private String getCheckSumOfFile(String file) throws Exception {
        String fileAsString = getFileAsString(file);
        return processFilesMd5Count(fileAsString);
    }

    private String getFileAsString(String strFile) throws Exception {
        File file = new File(strFile);
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("Can't find class file %s", strFile));
        }
        BufferedReader inputStream = new BufferedReader(new FileReader(file));
        char[] buffer = new char[1024];

        StringBuilder stringBuilder = new StringBuilder();

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            stringBuilder.append(buffer, 0, len);
        }
        String data = stringBuilder.toString();
        inputStream.close();
        return data;
    }

    private List<String> getClassFiles() throws IOException {
        List<String> res = new ArrayList<String>();
        Enumeration<URL> en = getClass().getClassLoader().getResources(".");
        while (en.hasMoreElements()) {
            URL url = en.nextElement();
            String surl = url.toString();
            if (surl.startsWith("vfs:/") && surl.endsWith("/WEB-INF/classes/")) {
                /*VirtualFile classFolder = VFS.getChild(surl.substring(5));
                List<VirtualFile> virtualFiles = classFolder.getChildrenRecursively();
                for (VirtualFile vf : virtualFiles) {
                    if (vf.isFile()) {
                        res.add(vf.getPhysicalFile().getAbsolutePath());
                    }
                }*/
            }
        }
        return res;
    }

    /*public String processFilesFromFolder(File folder) throws IOException {
        String fileString = processFileRead(folder);

        String md5Counted = "";
        // Md5 в виде строки
        if (fileString != null) {
            md5Counted = processFilesMd5Count(fileString);
        }

        return md5Counted;
    }*/

    // Подсчет Md5 - для файла
    public String processFilesMd5Count(String fileString) {
        MessageDigest md5;
        StringBuffer hexString = new StringBuffer();

        try {
            md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(fileString.getBytes());
            byte messageDigest[] = md5.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            return e.toString();
        }

        return hexString.toString();
    }

    /*// Считать файл в строку
    public String processFileRead(File file) throws IOException {
        String contents = readUsingScanner(file);
        return contents;
    }

    private String readUsingScanner(File file) throws IOException {
        String data = "";

        File[] folderEntries = file.listFiles();

        for (File entry : folderEntries) {
            if (entry.isDirectory()) {
                continue;
            }

            if (entry.getCanonicalPath().endsWith("ecafe_processor.war")) {
                BufferedReader inputStream = new BufferedReader(new FileReader(entry));
                char[] buffer = new char[1024];

                StringBuilder stringBuilder = new StringBuilder();

                int len;
                while ((len = inputStream.read(buffer)) != -1) {

                    stringBuilder.append(buffer, 0, len);
                }
                data = stringBuilder.toString();
                inputStream.close();
                break;
            }
        }

        if (data.equals("")) {
            throw new IOException("Не найден файл ecafe_processor.war");
        } else {

            return data;
        }
    } */

}
