/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileSupportService {
    private final Logger log = LoggerFactory.getLogger(FileSupportService.class);

    private final static String fileName = "LastProcessClient.txt";

    public Long getLastProcessIdOfClient(){
        Long idOfClient = null;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))){
            String line = bufferedReader.readLine();
            idOfClient = Long.parseLong(line);
        } catch (FileNotFoundException ignore){
        } catch (Exception e){
            log.error("Can't get LastProcessRegistryDate", e);
        }
        return idOfClient;
    }

    public void writeLastProcessIdOfClient(Long idOfClient){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))){
            bufferedWriter.write(idOfClient.toString());
        } catch (Exception e){
            log.error("Can't write LastProcessRegistryDate in file", e);
        }
    }
}
