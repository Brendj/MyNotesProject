package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtDish;

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
    //private Integer complexType;
    @Schema(description = "Вид рациона комплекса")
    private Integer fRation;
    @Schema(description = "Список блюд комплекса")
    private List<DishItem> menuItems;

    public ComplexItem(WtComplex wtComplex, List<WtDish> dishes) {
        this.complexId = wtComplex.getIdOfComplex();
        this.complexName = wtComplex.getName();
        this.composite = wtComplex.getComposite();
        this.price = wtComplex.getPrice() == null ? 0 : wtComplex.getPrice().longValue();
        //this.complexType = wtComplex.fr
        this.fRation = wtComplex.getWtDietType().getIdOfDietType().intValue();
        this.menuItems = new ArrayList<>();
        for (WtDish wtDish : dishes) {
            DishItem item = new DishItem(wtDish);
            menuItems.add(item);
        }
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
}
