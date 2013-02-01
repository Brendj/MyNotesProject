package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basicGood;

import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Scope("session")
public class BasicGoodListPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(BasicGoodListPage.class);

    private List<GoodsBasicBasket> basicGoodList;
    private GoodsBasicBasket newBasicGood;
    private GoodsBasicBasket editBasicGood;
    private UIComponent createGoodDiv;
    private UIComponent editGoodDiv;
    private List<SelectItem> unitsScaleSelectItemList;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        reload();
    }

    @Transactional
    public void reload() throws Exception{
        newBasicGood = new GoodsBasicBasket();
        editBasicGood = null;

        TypedQuery<GoodsBasicBasket> query = entityManager.createQuery("from GoodsBasicBasket", GoodsBasicBasket.class);
        basicGoodList = query.getResultList();
    }

    public Object onCreate() {
        try {
            if (null == newBasicGood.getNameOfGood() || newBasicGood.getNameOfGood().equals("")) {
                printError("Поле 'Наименование' обязательное.");
                return null;
            }
            if (null == newBasicGood.getUnitsScale()) {
                printError("Поле 'Единица измерение' обязательное.");
                return null;
            }
            if (null == newBasicGood.getNetWeight() || 0 == newBasicGood.getNetWeight()) {
                printError("Поле 'Масса нетто' обязательное.");
                return null;
            }
            Date date = new Date();
            newBasicGood.setCreatedDate(date);
            newBasicGood.setLastUpdate(date);
            newBasicGood.setGuid(UUID.randomUUID().toString());
            daoService.persistEntity(newBasicGood);
            reload();
            printMessage("Базовый товар добавлен успешно");
        } catch (Exception e) {
            printError("Ошибка при создании базового товара");
            logger.error("Error creating basic good", e);
        }
        return null;
    }

    public Object onEdit() {
        try {
            if (null == editBasicGood.getNameOfGood() || editBasicGood.getNameOfGood().equals("")) {
                printError("Поле 'Наименование' обязательное.");
                return null;
            }
            if (null == editBasicGood.getUnitsScale()) {
                printError("Поле 'Единица измерение' обязательное.");
                return null;
            }
            if (null == editBasicGood.getNetWeight() || 0 == editBasicGood.getNetWeight()) {
                printError("Поле 'Масса нетто' обязательное.");
                return null;
            }
            GoodsBasicBasket goodsBasicBasket = entityManager.find(GoodsBasicBasket.class, editBasicGood.getIdOfBasicGood());
            goodsBasicBasket.setLastUpdate(new Date());
            goodsBasicBasket.setNameOfGood(editBasicGood.getNameOfGood());
            goodsBasicBasket.setUnitsScale(editBasicGood.getUnitsScale());
            goodsBasicBasket.setNetWeight(editBasicGood.getNetWeight());
            daoService.persistEntity(goodsBasicBasket);
            reload();
            printMessage("Изменения внесены успешно");
        } catch (Exception e) {
            printError("Ошибка при редактировании базового товара");
            logger.error("Error editing basic good", e);
        }
        return null;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/basicGood/basicgood";
    }

    public List<GoodsBasicBasket> getBasicGoodList() {
        return basicGoodList;
    }

    public void setBasicGoodList(List<GoodsBasicBasket> basicGoodList) {
        this.basicGoodList = basicGoodList;
    }

    public GoodsBasicBasket getNewBasicGood() {
        return newBasicGood;
    }

    public void setNewBasicGood(GoodsBasicBasket newBasicGood) {
        this.newBasicGood = newBasicGood;
    }

    public GoodsBasicBasket getEditBasicGood() {
        return editBasicGood;
    }

    public void setEditBasicGood(GoodsBasicBasket editBasicGood) {
        this.editBasicGood = editBasicGood;
    }

    public UIComponent getCreateGoodDiv() {
        return createGoodDiv;
    }

    public void setCreateGoodDiv(UIComponent createGoodDiv) {
        this.createGoodDiv = createGoodDiv;
    }

    public UIComponent getEditGoodDiv() {
        return editGoodDiv;
    }

    public void setEditGoodDiv(UIComponent editGoodDiv) {
        this.editGoodDiv = editGoodDiv;
    }

    public List<SelectItem> getUnitsScaleSelectItemList() {
        if (unitsScaleSelectItemList == null) {
            unitsScaleSelectItemList = new ArrayList<SelectItem>();
            for (int i = 0; i < Good.UNIT_SCALES.length; i++) {
                unitsScaleSelectItemList.add(new SelectItem(i, Good.UNIT_SCALES[i]));
            }
        }
        return unitsScaleSelectItemList;
    }

    public void setUnitsScaleSelectItemList(List<SelectItem> unitsScaleSelectItemList) {
        this.unitsScaleSelectItemList = unitsScaleSelectItemList;
    }

    public long getUnitsScaleSelectItemListSize() {
        return unitsScaleSelectItemList.size();
    }

    public boolean isBasicGoodListEmpty() {
        return basicGoodList.isEmpty();
    }

    public boolean isEditBasicGoodNotNull() {
        return (null != editBasicGood);
    }

    public static String cid(UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        return component.getClientId(context);
    }

}
