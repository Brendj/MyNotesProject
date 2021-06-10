package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.RegularPreorder;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtDish;
import ru.iteco.restservice.servise.data.PreorderAmountData;
import ru.iteco.restservice.servise.data.PreorderComplexAmountData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 29.04.2021.
 */
public class ComplexItem {
    @Schema(description = "Идентификатор комплекса")
    private Long complexId;
    @Schema(description = "Название комплекса")
    private String complexName;
    @Schema(description = "Признак составного комплекса")
    private Boolean composite;
    @Schema(description = "Цена комплекса")
    private Long price;
    @Schema(description = "Вид рациона комплекса")
    private Integer fRation;
    @Schema(description = "Информация о предзаказе на комплекс")
    private PreorderComplexDTO preorderInfo;
    @Schema(description = "Информация о правиле регулярного предзаказа на комплекс")
    private RegularPreorderDTO regularInfo;
    @Schema(description = "Список блюд комплекса")
    private List<DishItem> menuItems;

    public ComplexItem(WtComplex wtComplex, List<WtDish> dishes, PreorderAmountData preorderComplexAmounts,
                       List<RegularPreorder> regulars) {
        this.complexId = wtComplex.getIdOfComplex();
        this.complexName = wtComplex.getName();
        this.composite = wtComplex.getComposite();
        this.price = wtComplex.getPrice() == null ? 0 : wtComplex.getPrice().longValue();
        PreorderComplexAmountData data = PreorderAmountData.getByComplexId(preorderComplexAmounts, wtComplex.getIdOfComplex());
        PreorderComplexDTO preorderInfo = new PreorderComplexDTO();
        preorderInfo.setAmount(data == null ? 0 : data.getAmount());
        preorderInfo.setPreorderId(data == null ? null : data.getIdOfPreorderComplex());
        this.setPreorderInfo(preorderInfo);
        this.fRation = wtComplex.getWtDietType().getIdOfDietType().intValue();
        RegularPreorder regularPreorder = getRegularByComplexId(regulars, wtComplex.getIdOfComplex());
        if (regularPreorder != null && regularPreorder.getIdOfDish() == null) {
            this.setRegularInfo(RegularPreorderDTO.build(regularPreorder));
        }
        this.menuItems = new ArrayList<>();
        for (WtDish wtDish : dishes) {
            DishItem item = new DishItem(wtDish, data, regularPreorder);
            menuItems.add(item);
        }
    }

    private RegularPreorder getRegularByComplexId(List<RegularPreorder> regulars, Long complexId) {
        for (RegularPreorder regularPreorder : regulars) {
            if (complexId.equals(regularPreorder.getIdOfComplex().longValue())) {
                return regularPreorder;
            }
        }
        return null;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    /*public Integer getComplexType() {
        return complexType;
    }

    public void setComplexType(Integer complexType) {
        this.complexType = complexType;
    }*/

    public Integer getfRation() {
        return fRation;
    }

    public void setfRation(Integer fRation) {
        this.fRation = fRation;
    }

    public List<DishItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<DishItem> menuItems) {
        this.menuItems = menuItems;
    }

    public Boolean getComposite() {
        return composite;
    }

    public void setComposite(Boolean composite) {
        this.composite = composite;
    }

    public PreorderComplexDTO getPreorderInfo() {
        return preorderInfo;
    }

    public void setPreorderInfo(PreorderComplexDTO preorderInfo) {
        this.preorderInfo = preorderInfo;
    }

    public RegularPreorderDTO getRegularInfo() {
        return regularInfo;
    }

    public void setRegularInfo(RegularPreorderDTO regularInfo) {
        this.regularInfo = regularInfo;
    }
}
