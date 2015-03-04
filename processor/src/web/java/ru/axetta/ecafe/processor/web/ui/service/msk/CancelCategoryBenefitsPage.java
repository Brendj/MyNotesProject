/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 04.03.15
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public class CancelCategoryBenefitsPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(CancelCategoryBenefitsPage.class);

    private List<GroupControlBenefitsItems> groupControlBenefitsItems;

    @Override
    public String getPageFilename() {
        return "service/msk/cancel_category_benefits";
    }

    public void onShow() throws Exception {

    }

    public void cancelCategoryBenefitsGenerate(RuntimeContext runtimeContext) throws Exception{

    }

    public List<GroupControlBenefitsItems> getGroupControlBenefitsItems() {
        return groupControlBenefitsItems;
    }

    public void setGroupControlBenefitsItems(List<GroupControlBenefitsItems> groupControlBenefitsItems) {
        this.groupControlBenefitsItems = groupControlBenefitsItems;
    }
}
