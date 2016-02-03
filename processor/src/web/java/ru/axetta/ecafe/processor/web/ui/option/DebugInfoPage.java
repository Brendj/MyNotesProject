/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.nsi.OrgMskNSIService;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component
@Scope("session")
public class DebugInfoPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(DebugInfoPage.class);

    @Override
    public String getPageFilename() {
        return "option/debug_info_page";
    }

    public void runTestRNIP() throws Exception {


        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        try {
            fileInputStream = new FileInputStream("/home/jbosser/processor/Debugs/rnip.txt");
            inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            String s;

            Map<String, String> map = new HashMap<String, String>();
            while((s = bufferedReader.readLine()) != null) {
                if (s.substring(0,1).equals("-")) {
                    mapList.add(map);
                    map = new HashMap<String, String>();
                } else {
                    String[] arr = s.split("=");
                    map.put(arr[0], arr[1]);
                }
            }
            mapList.add(map);
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(fileInputStream);
        }
        if (mapList.size() > 0) {
            RNIPLoadPaymentsService rnip = RuntimeContext.getAppContext().getBean(RNIPLoadPaymentsService.class);
            rnip.addPaymentsToDb(mapList, true);
        }
    }

    public void runTest2() {
        CardService cardService = CardService.getInstance();
        System.out.println(DAOService.getInstance().runDebugTest2());
    }

    public void runTestAISReestr() throws Exception {
        OrgMskNSIService service = RuntimeContext.getAppContext().getBean(OrgMskNSIService.class);
        Set<String> guids = new HashSet<String>();
        guids.add("AD1CA6A0-7390-4270-8F76-ED31C1E52A2D");
        guids.add("9290FB1E-5512-41D3-A447-8A865D0E8215");
        guids.add("D88D3D17-16C6-497E-93CB-68DD209C937C");
        guids.add("D88D3D17-16C6-497E-93CB-68DD209C927C");
        guids.add("980D134B-0F61-418F-B09F-2502A532CC12");
        List<String> list = service.getBadGuids(guids);
        if (list != null) {
            for (String s : list) {
                System.out.println(String.format("Гуид %s не найден в АИС Реестр", s));
            }
        }
    }

}
