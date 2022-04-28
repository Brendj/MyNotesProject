package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.io.Serializable;
import java.util.Objects;

public class WtMenuInvisibleDishPK implements Serializable {
    private Long idOfMenu;
    private Long idOfOrg;
    private Long idOfDish;

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    public void setIdOfMenu(Long idOfMenu) {
        this.idOfMenu = idOfMenu;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WtMenuInvisibleDishPK that = (WtMenuInvisibleDishPK) o;
        return Objects.equals(idOfMenu, that.idOfMenu) && Objects.equals(idOfOrg, that.idOfOrg) && Objects.equals(idOfDish, that.idOfDish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfMenu, idOfOrg, idOfDish);
    }
}
