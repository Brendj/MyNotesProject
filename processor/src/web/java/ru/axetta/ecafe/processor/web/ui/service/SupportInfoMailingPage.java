/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.mail.File;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 01.12.16
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class SupportInfoMailingPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(SupportInfoMailingPage.class);
    private String address;
    private String text;
    private File file = null;
    private final List<SelectItem> guardianFilterItems = new ArrayList<SelectItem>();
    private int guardianFilter;
    private final List<SelectItem> genderFilterItems = new ArrayList<SelectItem>();
    private int genderFilter;
    private Boolean ageCategory;
    private Boolean ageBefore20;
    private Boolean age2125;
    private Boolean age2630;
    private Boolean age3135;
    private Boolean age3645;
    private Boolean ageOver46;

    public SupportInfoMailingPage() {
        super();
        guardianFilterItems.add(new SelectItem(0, "Любой"));
        guardianFilterItems.add(new SelectItem(1, "Только представители"));
        guardianFilterItems.add(new SelectItem(2, "Только дети"));
        guardianFilter = 0;
        genderFilterItems.add(new SelectItem(0, "Не важно"));
        genderFilterItems.add(new SelectItem(1, "Мужской"));
        genderFilterItems.add(new SelectItem(2, "Женский"));
        genderFilter = 0;
        ageBefore20 = false;
        age2125 = false;
        age2630 = false;
        age3135 = false;
        age3645 = false;
        ageOver46 = false;
    }

    public String getPageFilename() {
        return "service/support_info_mailing";
    }

    public void loadFileListener(FileUploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadedFile item = event.getUploadedFile();
        try {
            ru.axetta.ecafe.processor.core.mail.File file = new ru.axetta.ecafe.processor.core.mail.File();
            file.setFile(new java.io.File(item.getName()));
            file.setFileName(item.getName());
            file.setContentType(item.getContentType());
            loadFile(file);
        } catch (Exception e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при добавлении файла: " + e.getMessage(),
                            null));
        }
    }

    public void loadFile(File file) {
        this.file = file;
    }

    public String clearUploadData() {
        file = null;
        return null;
    }

    public Object sendSupportInfoMailing() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (file == null && StringUtils.isEmpty(address)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Загрузите файл с лицевыми счетами либо введите л/с получателя сообщения в поле Кому", null));
        }
        List<Long> clients;
        try {
            clients = getClients();
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format("При обработке файла с л/с и получателя сообщения произошла ошибка: %s", e.getMessage()), null));
            return null;
        }
        if (clients.size() == 0) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "В файле не найдено данных по лицевым счетам + поле получателя также пусто", null));
            return null;
        }
        if (text == null || StringUtils.isEmpty(text.trim())) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Введите текст информационного сообщения", null));
            return null;
        }
        final EventNotificationService notificationService = RuntimeContext.getAppContext().getBean(
                EventNotificationService.class);
        for (Long contractId : clients) {
            Client client = DAOReadonlyService.getInstance().getClientByContractId(contractId);
            String[] values = generateInfoMailingNotificationParams();
            notificationService.sendNotificationInfoMailingAsync(client, values, new Date());
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                String.format("Стартовала задача по отправке сообщений. Размер очереди сообщений: %s", clients.size()), null));
        return null;
    }

    private List<Long> getClients() throws Exception {
        List result = new ArrayList<Long>();
        if (!StringUtils.isEmpty(address)) result.add(Long.parseLong(address));
        if (file == null) {
            return result;
        }
        BufferedReader br = new BufferedReader(new FileReader(file.getFile().getCanonicalPath()));
        try {
            String line = br.readLine();
            while (line != null) {
                if (line != null && !StringUtils.isEmpty(line.trim())) {
                    result.add(Long.parseLong(line));
                }
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        return result;
    }

    private String[] generateInfoMailingNotificationParams() {
        String sAgeBefore20, sAge2125, sAge2630, sAge3135, sAge3645, sAgeOver46;
        if (!ageCategory) {
            sAgeBefore20 = sAge2125 = sAge2630 = sAge3135 = sAge3645 = sAgeOver46 = "1";
        } else {
            sAgeBefore20 = ageBefore20 ? "1" : "0";
            sAge2125 = age2125 ? "1" : "0";
            sAge2630 = age2630 ? "1" : "0";
            sAge3135 = age3135 ? "1" : "0";
            sAge3645 = age3645 ? "1" : "0";
            sAgeOver46 = ageOver46 ? "1" : "0";
        }
        String sClientType = new Integer(guardianFilter).toString();
        String sGender = new Integer(genderFilter).toString();
        return new String[] {
                "infoText", text,
                "clientType", sClientType,
                "gender", sGender,
                "ageBefore20", sAgeBefore20,
                "age2125", sAge2125,
                "age2630", sAge2630,
                "age3135", sAge3135,
                "age3645", sAge3645,
                "ageOver46", sAgeOver46,
                EventNotificationService.TARGET_VALUES_KEY, sClientType + sGender + sAgeBefore20 + sAge2125 + sAge2630 + sAge3135 + sAge3645 + sAgeOver46
        };
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<SelectItem> getGuardianFilterItems() {
        return guardianFilterItems;
    }

    public int getGuardianFilter() {
        return guardianFilter;
    }

    public void setGuardianFilter(int guardianFilter) {
        this.guardianFilter = guardianFilter;
    }

    public List<SelectItem> getGenderFilterItems() {
        return genderFilterItems;
    }

    public int getGenderFilter() {
        return genderFilter;
    }

    public void setGenderFilter(int genderFilter) {
        this.genderFilter = genderFilter;
    }

    public Boolean getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(Boolean ageCategory) {
        this.ageCategory = ageCategory;
    }

    public Boolean getAgeBefore20() {
        return ageBefore20;
    }

    public void setAgeBefore20(Boolean ageBefore20) {
        this.ageBefore20 = ageBefore20;
    }

    public Boolean getAge2125() {
        return age2125;
    }

    public void setAge2125(Boolean age2125) {
        this.age2125 = age2125;
    }

    public Boolean getAge2630() {
        return age2630;
    }

    public void setAge2630(Boolean age2630) {
        this.age2630 = age2630;
    }

    public Boolean getAge3135() {
        return age3135;
    }

    public void setAge3135(Boolean age3135) {
        this.age3135 = age3135;
    }

    public Boolean getAge3645() {
        return age3645;
    }

    public void setAge3645(Boolean age3645) {
        this.age3645 = age3645;
    }

    public Boolean getAgeOver46() {
        return ageOver46;
    }

    public void setAgeOver46(Boolean ageOver46) {
        this.ageOver46 = ageOver46;
    }
}
