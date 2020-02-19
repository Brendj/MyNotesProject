/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cf_wt_menu_groups")
public class WtMenuGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "lastUpdate")
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "create_by_id")
    private User createdUser;

    @ManyToOne
    @JoinColumn(name = "update_by_id")
    private User updatedUser;

    @Column(name = "version")
    private Long version;

    @Column(name = "deleteState")
    private Integer deleteState;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private WtMenu wtMenu;

    @ManyToMany
    @JoinTable(name = "cf_wt_menu_group_dish",
            joinColumns = @JoinColumn(name = "menu_group_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id"))
    private List<WtDish> dishes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    public WtMenu getWtMenu() {
        return wtMenu;
    }

    public void setWtMenu(WtMenu wtMenu) {
        this.wtMenu = wtMenu;
    }

    public List<WtDish> getDishes() {
        return dishes;
    }

    public void setDishes(List<WtDish> dishes) {
        this.dishes = dishes;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(User updatedUser) {
        this.updatedUser = updatedUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtMenuGroup that = (WtMenuGroup) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects
                .equals(createDate, that.createDate) && Objects.equals(lastUpdate, that.lastUpdate) && Objects
                .equals(version, that.version) && Objects.equals(deleteState, that.deleteState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createDate, lastUpdate, version, deleteState);
    }

}
