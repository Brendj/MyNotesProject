/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.mesh.MeshPersonsSearchService;
import ru.axetta.ecafe.processor.core.partner.mesh.json.ResponsePersons;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Component
@Scope("session")
public class PupilCatalogFindPage extends BasicWorkspacePage {

    public PupilCatalogFindPage() {
    }
    String meshId;
    String familyName;
    String firstName;
    String secondName;
    public List<ResponsePersons> pupilInfos = new LinkedList<ResponsePersons>();

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Integer getClientTotalCount() {
        return pupilInfos == null ? 0 : pupilInfos.size();
    }

    @Transactional
    public void updateList() {
        try {
            if (StringUtils.isEmpty(meshId)){
                if (StringUtils.isEmpty(firstName) || StringUtils.isEmpty(familyName)) {
                    printError("При поиске без Mesh GUID необходимо указать фамилию и имя");
                    RuntimeContext.getAppContext().getBean(MeshPersonsSearchService.class).getMeshResponses().get().clear();
                    return;
                }
                if (StringUtils.isEmpty(firstName) && StringUtils.isEmpty(familyName)) {
                    printError("Укажите Mesh GUID или фамилию и имя");
                    RuntimeContext.getAppContext().getBean(MeshPersonsSearchService.class).getMeshResponses().get().clear();
                    return;
                }
            }
            RuntimeContext.getAppContext().getBean(MeshPersonsSearchService.class).loadPersons(0, meshId, familyName, firstName, secondName);
            pupilInfos =  RuntimeContext.getAppContext().getBean(MeshPersonsSearchService.class).getMeshResponses().get();
        } catch (Exception e) {
            super.logAndPrintMessage("Ошибка получения данных", e);
        }
    }

    public String getPageFilename() {
        return "service/msk/pupil_catalog_find_page";
    }

    public List<ResponsePersons> getPupilInfos() {
        return pupilInfos;
    }
    public void setPupilInfos(List<ResponsePersons> pupilInfos) {
        this.pupilInfos = pupilInfos;
    }
}