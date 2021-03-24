/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

@Service
public class FileSupportService {
    private final Logger log = LoggerFactory.getLogger(FileSupportService.class);

    private final static String fileName = "LastProcessRegistryDate.txt";

    public Long getLastProcessRegistryDate(){
        Long lastProcessRegistryDate = null;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))){
            String line = bufferedReader.readLine();
            lastProcessRegistryDate = Long.parseLong(line);
        } catch (Exception e){
            log.error("Can't get LastProcessRegistryDate", e);
            return null;
        }
        return lastProcessRegistryDate;
    }

    public void writeLastProcessRegistryDate(Long lastProcessRegistryDate){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))){
            bufferedWriter.write(lastProcessRegistryDate.toString());
        } catch (Exception e){
            log.error("Can't write LastProcessRegistryDate in file", e);
        }
    }
}
