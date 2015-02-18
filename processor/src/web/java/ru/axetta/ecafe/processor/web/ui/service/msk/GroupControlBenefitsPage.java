/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.mail.File;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.richfaces.model.UploadItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 18.02.15
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class GroupControlBenefitsPage extends BasicWorkspacePage {

    public UploadItem uploadItem;
    private ArrayList<File> files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();
    private List<GroupControlBenefitsItems> groupControlBenefitsItems;

    @Override
    public String getPageFilename() {
        return "service/msk/group_control_benefit";
    }

    @Override
    public void onShow() throws Exception {
        groupControlBenefitsItems = new ArrayList<GroupControlBenefitsItems>();
        files = new ArrayList<ru.axetta.ecafe.processor.core.mail.File>();
    }


    public ArrayList<ru.axetta.ecafe.processor.core.mail.File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<ru.axetta.ecafe.processor.core.mail.File> files) {
        this.files = files;
    }

    public List<GroupControlBenefitsItems> getGroupControlBenefitsItems() {
        return groupControlBenefitsItems;
    }

    public void setGroupControlBenefitsItems(List<GroupControlBenefitsItems> groupControlBenefitsItems) {
        this.groupControlBenefitsItems = groupControlBenefitsItems;
    }
}
