/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;


import ru.axetta.ecafe.processor.core.utils.Base64;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 31.05.16
 * Time: 11:35
 */

@Component
@Scope("singleton")
public class CheckSumsMessageDigitsService {

    private List<String> resultList = new ArrayList<String>();

    // Рекурсивный поиск путей к файлам проход по всем каталогам и по всем файлам.
/*    public void getListResultFilesFromFolder(File folder) throws IOException {
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries) {
            if (entry.isDirectory()) {
                getListResultFilesFromFolder(entry);
                continue;
            }
            // иначе вам попался файл, обрабатывайте его!

            // Считали файл в строку
            String fileString = processFileRead(entry);

            String md5Counted = "";
            // Md5 в виде строки
            if (fileString != null) {
                md5Counted = processFilesMd5Count(fileString);
            }

            String result = entry + " " + md5Counted;
            resultList.add(result);
        }
    }*/

    public String processFilesFromFolder(File folder, Date currentDate) throws IOException {
        //getListResultFilesFromFolder(folder);
        //String dateString = CalendarUtils.dateTimeToString(currentDate).replaceAll(" ", "-").replaceAll(":", ".");

        //File file = generateFile(dateString);
        //fileWrite(file, resultList);
        //resultList.clear();

        // Считали файл в строку
        String fileString = processFileRead(folder);

        String md5Counted = "";
        // Md5 в виде строки
        if (fileString != null) {
            md5Counted = processFilesMd5Count(fileString);
        }

        return md5Counted;
    }

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

    // Считать файл в строку
    public String processFileRead(File file) throws IOException {
        String contents = readUsingScanner(file);
        return contents;
    }

    // Читаем файл с помощью Scanner
    private String readUsingScanner(File file) throws IOException {
        String data = "";

        File[] folderEntries = file.listFiles();

        if (folderEntries.length > 0) {
            if (folderEntries[0].getCanonicalPath().contains("ecafe_processor.war")) {

       /*         ZipFile zf = new ZipFile(folderEntries[0]);
                try {
                    for (Enumeration<? extends ZipEntry> e = zf.entries();
                            e.hasMoreElements();) {
                        ZipEntry ze = e.nextElement();
                        String name = ze.getName();
                        if (name.endsWith(".txt")) {
                            InputStream in = zf.getInputStream(ze);
                            // read from 'in'
                        }
                    }
                } finally {
                    zf.close();
                }*/

                BufferedReader inputStream = new BufferedReader(new FileReader(folderEntries[0]));
                char[] buffer = new char[1024];

                StringBuilder stringBuilder = new StringBuilder();

                int len;
                while ((len = inputStream.read(buffer)) != -1) {

                    stringBuilder.append(buffer, 0, len);
                }

                data = stringBuilder.toString();
            }
        }
        return data;
    }

    // Создает файл и папки если их нет
/*    public File generateFile(String dateString) {
        String filePath = "/processor/md5file";
        File dir = new File(filePath);
        boolean bool = dir.mkdirs();
        File file = new File(filePath + "/checkSums" + dateString + ".txt");

        return file;
    }*/

    // Запись итогов в Файл
/*    public void fileWrite(File file, List<String> resultList) {
        try {
            FileWriter writer = new FileWriter(file, false);

            for (String res : resultList) {
                writer.write(res);
                // запись по символам
                writer.append('\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }*/
}
