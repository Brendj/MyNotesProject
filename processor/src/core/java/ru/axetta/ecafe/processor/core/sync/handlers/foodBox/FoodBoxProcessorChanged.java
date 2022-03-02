/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxCells;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorder;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxStateTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxChanged.FoodBoxPreorderChanged;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxChanged.FoodBoxPreorderChangedItem;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.ResFoodBoxChanged.ResFoodBoxChanged;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.ResFoodBoxChanged.ResFoodBoxChangedItem;

public class FoodBoxProcessorChanged extends AbstractProcessor<ResFoodBoxChanged> {

    private final FoodBoxPreorderChanged foodBoxPreorderChanged;
    private Long idOfOrg;
    private static final Logger logger = LoggerFactory.getLogger(FoodBoxProcessorChanged.class);

    public FoodBoxProcessorChanged(Session session, FoodBoxPreorderChanged foodBoxPreorderChanged, Long idOfOrg) {
        super(session);
        this.foodBoxPreorderChanged = foodBoxPreorderChanged;
        this.idOfOrg = idOfOrg;
    }

    @Override
    public ResFoodBoxChanged process() throws Exception {
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        ResFoodBoxChanged resFoodBoxChanged = new ResFoodBoxChanged();
        for (FoodBoxPreorderChangedItem foodBoxPreorderChangedItem : foodBoxPreorderChanged.getItems()) {
            Long version = daoReadonlyService.getMaxVersionOfFoodBoxPreorder();
            FoodBoxPreorder foodBoxPreorder = daoReadonlyService.findFoodBoxPreorderById(foodBoxPreorderChangedItem.getId());
            if (foodBoxPreorder == null) {
                logger.error(String.format("Не найден предзаказ фудбокса с Id == %s",
                        foodBoxPreorderChangedItem.getId()));
                return resFoodBoxChanged;
            }
            foodBoxPreorder.setError(foodBoxPreorderChangedItem.getError());
            foodBoxPreorder.setIdOfFoodBox(foodBoxPreorderChangedItem.getIdOfFoodBox());
            foodBoxPreorder.setCellNumber(foodBoxPreorderChangedItem.getCellNumber());
            if (foodBoxPreorder.getIdOfFoodBox() != null) {
                foodBoxPreorder.setLocated(true);
                if (foodBoxPreorder.getState().equals(FoodBoxStateTypeEnum.NEW)) {
                    Org org = (Org) session.load(Org.class, idOfOrg);
                    //Забираем ячейку
                    FoodBoxCells foodBoxCells = daoReadonlyService.getFoodBoxCellsByOrgAndFoodBoxId(org, foodBoxPreorder.getIdOfFoodBox());
                    foodBoxCells.setBusycells(foodBoxCells.getBusycells() + 1);
                    session.merge(foodBoxCells);
                }
            }
            foodBoxPreorder.setState(foodBoxPreorderChangedItem.getState());
            if (foodBoxPreorder.getState().equals(FoodBoxStateTypeEnum.EXECUTED) || foodBoxPreorder.getState().equals(FoodBoxStateTypeEnum.CANCELED))
            {
                Org org = (Org) session.load(Org.class, idOfOrg);
                //Освобождаем ячейку
                FoodBoxCells foodBoxCells = daoReadonlyService.getFoodBoxCellsByOrgAndFoodBoxId(org, foodBoxPreorder.getIdOfFoodBox());
                foodBoxCells.setBusycells(foodBoxCells.getBusycells()-1);
                session.merge(foodBoxCells);
            }
            foodBoxPreorder.setIdOfOrder(foodBoxPreorderChangedItem.getIdOfOrder());
            foodBoxPreorder.setCancelReason(foodBoxPreorderChangedItem.getCancelReason());
            foodBoxPreorder.setVersion(version+1);
            session.merge(foodBoxPreorder);
            ResFoodBoxChangedItem resFoodBoxChangedItem = new ResFoodBoxChangedItem();
            resFoodBoxChangedItem.setError("");
            resFoodBoxChangedItem.setId(foodBoxPreorder.getIdFoodBoxPreorder());
            resFoodBoxChangedItem.setRes(0);
            resFoodBoxChangedItem.setVersion(version+1);
            resFoodBoxChanged.getItems().add(resFoodBoxChangedItem);
        }
        return resFoodBoxChanged;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
