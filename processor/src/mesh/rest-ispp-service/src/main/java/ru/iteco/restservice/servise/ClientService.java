/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.model.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ClientService {
    private final Logger log = LoggerFactory.getLogger(ClientService.class);
    private static final Pattern phonePattern = Pattern.compile("^7\\d{10}$");



    public List<Client> getClientsByGuardianPhone(String guardPhone) {
        if(!phonePattern.matcher(guardPhone).matches()){
            throw new IllegalArgumentException("Номер телефона не соотвествует паттерну");
        }
        return null; //clientRepo.getChildsByPhone(guardPhone);
    }
}
