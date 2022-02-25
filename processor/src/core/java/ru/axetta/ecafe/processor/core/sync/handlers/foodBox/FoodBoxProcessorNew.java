/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxPreorder.FoodBoxPreorderNew;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.ResFoodBoxChanged.ResFoodBoxChanged;

public class FoodBoxProcessorNew extends AbstractProcessor<ResFoodBoxChanged> {

    private Long maxVersion;
    private Long idOfOrg;
    private static final Logger logger = LoggerFactory.getLogger(FoodBoxProcessorNew.class);

    public FoodBoxProcessorNew(Session session, Long idOfOrg, Long maxVersion) {
        super(session);
        this.maxVersion = maxVersion;
        this.idOfOrg = idOfOrg;
    }

    @Override
    public FoodBoxPreorderNew process() throws Exception {
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        return daoReadonlyService.getFoodBoxPreorders(maxVersion, daoReadonlyService.findOrg(idOfOrg));
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }
}
