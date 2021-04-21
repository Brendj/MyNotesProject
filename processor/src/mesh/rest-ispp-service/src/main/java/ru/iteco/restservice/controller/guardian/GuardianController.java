/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.guardian;

import ru.iteco.restservice.controller.guardian.responsedto.GuardianResponseDTO;
import ru.iteco.restservice.servise.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/guardian")
public class GuardianController {
    private final Logger log = LoggerFactory.getLogger(GuardianController.class);

    private final ClientService clientService;

    public GuardianController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/getGuardiansByClient")
    @ResponseBody
    public List<GuardianResponseDTO> getGuardiansByClient(@NotNull @RequestParam Long contractId){
        try {
            return clientService.getGuardiansByClient(contractId);
        } catch (Exception e){
            log.error("Exception in getGuardiansByClientList ", e);
            throw e;
        }
    }
}